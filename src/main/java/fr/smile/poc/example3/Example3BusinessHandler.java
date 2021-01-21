package fr.smile.poc.example3;

import org.springframework.integration.handler.GenericHandler;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;

@Component
public class Example3BusinessHandler implements GenericHandler<byte[]> {
	@Override
	public Object handle(byte[] payload, MessageHeaders headers) {
		System.out.println("------------> i'm from a business handler: example 3");
		return payload;
	}
}
