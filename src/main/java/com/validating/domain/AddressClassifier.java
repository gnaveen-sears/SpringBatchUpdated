package com.validating.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.classify.Classifier;

public class AddressClassifier implements Classifier<Address, ItemWriter<? super Address>>{
	private static final Logger logger = LoggerFactory.getLogger(AddressClassifier.class);
	private ItemWriter<Address> trueWriter;
	private ItemWriter<Address> falseWriter;
	 public AddressClassifier(ItemWriter<Address> trueWriter, ItemWriter<Address> falseWriter) {
	        this.trueWriter = trueWriter;
	        this.falseWriter = falseWriter;
	        }
	@Override
	public ItemWriter<? super Address> classify(Address address) {
		logger.debug("Address Classifier");
		if(address.getValid()){
			return trueWriter;
		}
		else
			return falseWriter;
	}

}
