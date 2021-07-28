package com.validating.domain;

import org.springframework.batch.item.ItemWriter;
import org.springframework.classify.Classifier;

public class ContactsClassifier implements Classifier<Contacts, ItemWriter<? super Contacts>>{
	private ItemWriter<Contacts> trueWriter;
	private ItemWriter<Contacts> falseWriter;
	public ContactsClassifier(ItemWriter<Contacts> trueWriter, ItemWriter<Contacts> falseWriter) {
        this.trueWriter = trueWriter;
        this.falseWriter = falseWriter;
        }
	@Override
	public ItemWriter<? super Contacts> classify(Contacts contacts) {
		return !contacts.isValid()  ? falseWriter : trueWriter;
	}

}
