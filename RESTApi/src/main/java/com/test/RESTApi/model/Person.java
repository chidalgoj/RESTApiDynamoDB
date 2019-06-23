package com.test.RESTApi.model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Person {
	private int id;
	private String name;
	private Date dateOfBirth;
	public static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("ddMMyyyy");
	
	
	public Person(int id, String name, Date dateOfBirth) {
		super();
		this.id = id;
		this.name = name;
		this.dateOfBirth = dateOfBirth;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Date getDateOfBirth() {
		return dateOfBirth;
	}
	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

}
