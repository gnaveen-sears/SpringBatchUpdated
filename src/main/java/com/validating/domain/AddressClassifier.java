package com.validating.domain;

import org.springframework.batch.item.ItemWriter;
import org.springframework.classify.Classifier;

public class AddressClassifier implements Classifier<Address, ItemWriter<? super Address>>{
	private ItemWriter<Address> trueWriter;
	private ItemWriter<Address> falseWriter;
	 public AddressClassifier(ItemWriter<Address> trueWriter, ItemWriter<Address> falseWriter) {
	        this.trueWriter = trueWriter;
	        this.falseWriter = falseWriter;
	        }
	@Override
	public ItemWriter<? super Address> classify(Address address) {
		return !address.getValid()? falseWriter : trueWriter;
	}

}
