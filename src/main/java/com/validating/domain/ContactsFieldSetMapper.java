package com.validating.domain;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

//import com.validating.domain.Contacts;

public class ContactsFieldSetMapper implements FieldSetMapper<Contacts> {

	@Override
	public Contacts mapFieldSet(FieldSet fieldSet) throws BindException {
		return new Contacts(fieldSet.readString("Last_Name"),
				fieldSet.readString("First_Name"),
				fieldSet.readString("Phone"),
				fieldSet.readString("Email"),
				fieldSet.readString("Title"),
				fieldSet.readString("Designation"));
	}

}
