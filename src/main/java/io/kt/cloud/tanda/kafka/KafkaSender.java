package io.kt.cloud.tanda.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.util.MimeTypeUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.kt.cloud.tanda.App;

public class KafkaSender {
	
	private static final Processor processor = App.applicationContext.getBean(Processor.class);
	private static final Logger logger = LoggerFactory.getLogger(KafkaSender.class);

	
	public static void pub(Object event) {
		
		ObjectMapper objectMapper = new ObjectMapper();
		String json = null;
		
		try {
			json = objectMapper.writeValueAsString(event);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("JSON format exception", e);
		}
		
	    MessageChannel outputChannel = processor.output();

	    outputChannel.send(MessageBuilder
	            .withPayload(json)
	            .setHeader("type", event.getClass().getSimpleName())
	            .setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON)
	            .build());	
	    
	    logger.info("[PUB] " + json);		
	}
	
}
