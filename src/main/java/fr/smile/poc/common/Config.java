package fr.smile.poc.common;

import java.io.File;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.context.IntegrationContextUtils;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.file.FileHeaders;
import org.springframework.integration.file.FileReadingMessageSource;
import org.springframework.integration.file.FileWritingMessageHandler;
import org.springframework.integration.file.transformer.FileToByteArrayTransformer;
import org.springframework.integration.handler.GenericHandler;
import org.springframework.integration.zip.splitter.UnZipResultSplitter;
import org.springframework.integration.zip.transformer.UnZipTransformer;
import org.springframework.integration.zip.transformer.ZipResultType;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class Config {

	private static final String INPUT_BYTE_ARRAY_CHANNEL = "inputByteArrayChannel";

	@Value("${poc.app-name}")
	private String appName;

	@Value("${poc.input-dir}")
	private String inputDirectory;

	@Value("${poc.output-dir}")
	private String outputDirectory;

	@Autowired
	private GenericHandler<byte[]> businessHandler;

	@Bean
	public MeterRegistry meterRegistry() {
		return new SimpleMeterRegistry();
	}

	@Bean
	public IntegrationFlow errorMonitoring() {
		return IntegrationFlows //
				.from(IntegrationContextUtils.ERROR_CHANNEL_BEAN_NAME) //
				.handle(message -> {
					System.out.println("------------> got a message from error");
					System.out.println("---> headers");
					for (Entry<String, Object> headerEntry : message.getHeaders().entrySet()) {
						System.out.println("     " + headerEntry.getKey() + "=" + headerEntry.getValue());
					}
				}).get();
	}

	@Bean
	public IntegrationFlow loadFile() {
		log.trace("[{}] loadFile", appName);
		// source
		FileReadingMessageSource sourceDirectory = new FileReadingMessageSource();
		sourceDirectory.setDirectory(new File(inputDirectory));
		// transformer
		FileToByteArrayTransformer fileToByteArrayTransformer = new FileToByteArrayTransformer();
		// log handler
		GenericHandler<byte[]> logHandler = new LogHandler("from input", false);
		// flow
		return IntegrationFlows //
				.from(sourceDirectory, configurer -> configurer.poller(Pollers.fixedDelay(5000))) //
				.transform(fileToByteArrayTransformer) //
				.channel(INPUT_BYTE_ARRAY_CHANNEL) //
				.handle(logHandler) //
				.route("T(org.apache.commons.io.FilenameUtils).getExtension(headers['" + FileHeaders.FILENAME + "'])",
						routerConfigurer -> {
							routerConfigurer.resolutionRequired(false);
							routerConfigurer.subFlowMapping("csv", csvSubFlow());
							routerConfigurer.subFlowMapping("zip", zipSubFlow());
							routerConfigurer.defaultOutputChannel(IntegrationContextUtils.ERROR_CHANNEL_BEAN_NAME);
						}) //
				.get();
	}

	private IntegrationFlow csvSubFlow() {
		log.trace("[{}] csvSubFlow", appName);
		// log handler
		GenericHandler<byte[]> logHandler = new LogHandler("from csv", true);
		// output
		FileWritingMessageHandler targetDirectoryHandler = new FileWritingMessageHandler(new File(outputDirectory));
		targetDirectoryHandler.setExpectReply(false);
		// flow
		return mapping -> {
			mapping //
					.handle(logHandler) //
					.enrichHeaders(h -> h.headerExpression(FileHeaders.FILENAME,
							"'" + appName.replace(" ", "_") + "-' + headers['" + FileHeaders.FILENAME + "']", true)) //
					.handle(businessHandler) //
					.handle(targetDirectoryHandler);
		};
	}

	private IntegrationFlow zipSubFlow() {
		log.trace("[{}] zipSubFlow", appName);
		// transformer
		UnZipTransformer unZipTransformer = new UnZipTransformer();
		unZipTransformer.setZipResultType(ZipResultType.BYTE_ARRAY);
		// splitter
		UnZipResultSplitter unZipSplitter = new UnZipResultSplitter();
		// flow
		return mapping -> {
			mapping //
					.transform(unZipTransformer) //
					.split(unZipSplitter) //
					.filter("!headers['" + FileHeaders.FILENAME + "'].startsWith('.')") //
					.channel(INPUT_BYTE_ARRAY_CHANNEL);
		};
	}
}
