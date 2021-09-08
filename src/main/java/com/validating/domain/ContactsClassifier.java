package com.validating.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.classify.Classifier;

public class ContactsClassifier implements Classifier<Contacts, ItemWriter<? super Contacts>>{
	private static final Logger logger = LoggerFactory.getLogger(ContactsClassifier.class);
	private ItemWriter<Contacts> trueWriter;
	private ItemWriter<Contacts> falseWriter;
	public ContactsClassifier(ItemWriter<Contacts> trueWriter, ItemWriter<Contacts> falseWriter) {
        this.trueWriter = trueWriter;
        this.falseWriter = falseWriter;
        }
	@Override
	public ItemWriter<? super Contacts> classify(Contacts contacts) {
		logger.debug("Contacts Classifier");
		System.out.println("Inside Classifier phone:"+contacts.getPhone()+" Valid:"+contacts.isValid());
		if(contacts.isValid()==true){
			return trueWriter;
		}
		else
			return falseWriter;
	}

}
