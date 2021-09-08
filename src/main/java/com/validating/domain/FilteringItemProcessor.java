package com.validating.domain;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.batch.item.ItemProcessor;


public class FilteringItemProcessor implements ItemProcessor<Contacts, Contacts> {
	
	 int count=0;
	 private static final Logger logger = LoggerFactory.getLogger(FilteringItemProcessor.class);
     @Override
     
    public Contacts process(Contacts p) throws Exception {
    	 logger.debug("Item Processor");
    	 p.setValid(true);
    	 System.out.println("Phone number"+p.getPhone().trim());
    	
    	 if(!StringUtils.isNumeric(p.getPhone().trim()))
    	 {
    		 
    		 p.setValid(false);
    		 String error = "Phone Number is not numeric";
    		 p.setError(error);
    	  }
    	  int length = StringUtils.length(p.getPhone());
    	  if(length<10)
    	  {
    		  System.out.println("Invalid Phone number"+p.getPhone()); 
    		  p.setValid(false);
    		  System.out.println("Valid:"+p.isValid());
    		  String error = "Phone number is less than 10 digits";
    		  p.setError(error);
    		  System.out.println("Error:"+p.getError());
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




