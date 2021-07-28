package com.validating.domain;

public class Contacts {
	 private  String Last_Name;
	 private  String First_Name;
	 private  String Phone;
	 private  String Email;
	 private  String Title;
	 private  String Designation;
	 private boolean valid;
	 private String error;
	 
	 
	 public Contacts()
		{
			super();
		}
		

	public Contacts(String Last_Name, String First_Name, String Phone ,String Email, String Title, String Designation) {
		
		this.Last_Name= Last_Name;
		this.First_Name = First_Name;
		this.Phone=Phone;
		this.Email=Email;
		this.Title=Title;
		this.Designation=Designation;
	}

	public String getLast_Name() {
		return Last_Name;
	}

	public String getFirst_Name() {
		return First_Name;
	}

	public String getPhone() {
		return Phone;
	}



	public String getEmail() {
		return Email;
	}



	public String getTitle() {
		return Title;
	}


	public String getDesignation() {
		return Designation;
	}
	public boolean isValid() {
		return valid;
	}


	public void setValid(boolean valid) {
		this.valid = valid;
	}


	public String getError() {
		return error;
	}


	public void setError(String error) {
		this.error = error;
	}




	@Override
	public String toString() {
		return "Contacts [ Last_Name =" + Last_Name + ", First_Name =" + First_Name+ ", Phone =" + Phone+ ", Email =" + Email+ ", Title ="+ Title+ ", Designation =" + Designation
				
	  + "]";
	}
	}

