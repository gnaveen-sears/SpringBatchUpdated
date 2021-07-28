package com.validating.domain;

import org.springframework.batch.item.file.transform.LineAggregator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

//import com.validating.domain.Contacts;
public class ContactsLineAggregator implements LineAggregator<Contacts> {

	private ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public String aggregate(Contacts item) {
		try {
			return objectMapper.writeValueAsString(item);
		}
		catch (JsonProcessingException e) {
			throw new RuntimeException("Unable to serialize Contacts", e);
		}
	}
}
