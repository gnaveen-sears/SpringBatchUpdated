package com.validating.domain;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
//import org.springframework.validation.BindException;

public class AddressRowMapper implements FieldSetMapper<Address>{

	@Override
	public Address mapFieldSet(FieldSet fieldSet)  {
		Address address = new Address();
		
		address.setCustomerPhone(fieldSet.readString("customerPhone"));
		address.setAddressType(fieldSet.readString("addressType"));
		address.setAddressLine1(fieldSet.readString("addressLine1"));
		address.setAddressLine2(fieldSet.readString("addressLine2"));
		address.setCity(fieldSet.readString("city"));
		address.setStateCode(fieldSet.readString("stateCode"));
		address.setZipcode(fieldSet.readString("zipcode"));
		address.setZipplus4(fieldSet.readString("zipplus4"));
		address.setAddressType2(fieldSet.readString("addressType2"));
		address.setAddressLine12(fieldSet.readString("addressLine12"));
		address.setAddressLine22(fieldSet.readString("addressLine22"));
		address.setCity2(fieldSet.readString("city2"));
		address.setStateCode2(fieldSet.readString("stateCode2"));
		address.setZipcode2(fieldSet.readString("zipcode2"));
		address.setZipplus42(fieldSet.readString("zipplus42"));
		
		
		

		return address;
	}
	

}
