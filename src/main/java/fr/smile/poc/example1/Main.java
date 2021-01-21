package fr.smile.poc.example1;

import java.util.Properties;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import fr.smile.poc.common.Config;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication(scanBasePackageClasses = { Config.class, Main.class })
public class Main {
	public static void main(String[] args) {
		String appName = "Example 1";
		log.trace("[{}] main", appName);
		Properties props = new Properties();
		props.put("poc.app-name", appName);
		props.put("poc.input-dir", "data/source1");
		new SpringApplicationBuilder(Main.class).properties(props).run(args);
	}
}
