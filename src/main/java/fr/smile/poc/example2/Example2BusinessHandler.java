package fr.smile.poc.example2;

import org.springframework.integration.handler.GenericHandler;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;

@Component(value = "businessHandler")
public class Example2BusinessHandler implements GenericHandler<byte[]> {
	@Override
	public Object handle(byte[] payload, MessageHeaders headers) {
		System.out.println("------------> i'm from a business handler: example 2");
		return payload;
	}
}
