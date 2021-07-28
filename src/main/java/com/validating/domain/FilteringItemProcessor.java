package com.validating.domain;

import org.apache.commons.lang3.StringUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;


public class FilteringItemProcessor implements ItemProcessor<Contacts, Contacts> {
	
	 int count=0;
//	 private static final Logger LOGGER = LoggerFactory.getLogger(FilteringItemProcessor.class);
    @Override
     
    public Contacts process(Contacts p) throws Exception {
//    	    	Contacts contacts = new Contacts();
    	p.setValid(true);
    	
    	if(!StringUtils.isNumeric(p.getPhone()))
    	{
    		p.setValid(false);
    		String error = "Phone Number is not numeric";
    		p.setError(error);
    	}
    	int length = StringUtils.length(p.getPhone());
    	if(length<10)
    	{
    		p.setValid(false);
    		String error = "Phone number is less than 10 digits";
    		p.setError(error);
    	}
    	if(p.getFirst_Name()==null)
    	{
    		p.setValid(false);
    		String error = "First Name is not mentioned";
    		p.setError(error);
    	}
    	if(p.getLast_Name()==null)
    	{
    		p.setValid(false);
    		String error = "Last Name is not mentioned";
    		p.setError(error);
    	}
    	if(p.getEmail()==null)
    	{
    		p.setValid(false);
    		String error = "Email is not mentioned";
    		p.setError(error);
    	}
		return p;
    	
    }
}




