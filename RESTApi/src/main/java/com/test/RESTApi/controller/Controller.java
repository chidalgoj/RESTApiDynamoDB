package com.test.RESTApi.controller;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeAction;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.AttributeValueUpdate;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException;
import com.test.RESTApi.model.Person;

@RestController
public class Controller {

	private final AtomicInteger counter = new AtomicInteger();
	final AmazonDynamoDB ddb = AmazonDynamoDBClientBuilder.defaultClient();

	@RequestMapping("/add")
	public int addPerson(@RequestParam(value = "name") String name,
			@RequestParam(value = "dob") @DateTimeFormat(pattern = "ddMMyyyy") Date dob) {
		Person person = new Person(counter.incrementAndGet(), name, dob);
		try {

			HashMap<String, AttributeValue> itemValues = new HashMap<String, AttributeValue>();

			itemValues.put("id", new AttributeValue(String.valueOf(person.getId())));
			itemValues.put("name", new AttributeValue(person.getName()));
			itemValues.put("dob", new AttributeValue(Person.DATE_FORMAT.format(person.getDateOfBirth())));

			ddb.putItem("Person", itemValues);
		} catch (ResourceNotFoundException e) {
			System.err.format("Error: The table \"%s\" can't be found.\n", "Person");
			System.err.println("Be sure that it exists and that you've typed its name correctly!");
			return -1;

		} catch (AmazonServiceException e) {
			System.err.println(e.getMessage());
			return -1;
		}
		return person.getId();
	}

	@RequestMapping("/get")
	public Person getPerson(@RequestParam(value = "id") int id) {
		String name = null;
		Date dob = null;

		try {
			HashMap<String, AttributeValue> keyToGet = new HashMap<String, AttributeValue>();

			keyToGet.put("id", new AttributeValue(String.valueOf(id)));
			GetItemRequest request = new GetItemRequest().withKey(keyToGet).withTableName("Person");

			Map<String, AttributeValue> returnedItem = ddb.getItem(request).getItem();
			if (returnedItem != null) {
				Set<String> keys = returnedItem.keySet();
				for (String key : keys) {
					switch (key) {
					case "name":
						name = returnedItem.get(key).getS();
						break;
					case "dob":
						dob = Person.DATE_FORMAT.parse(returnedItem.get(key).getS());
						break;

					}
				}
			} else {
				System.out.format("No item found with the key %s!\n", id);
				return null;
			}
		} catch (AmazonServiceException e) {
			System.err.println(e.getErrorMessage());
			return null;
		} catch (ParseException e) {
			System.err.println(e.getMessage());
			return null;
		}

		return new Person(id, name, dob);
	}

	@RequestMapping("/update")
	public boolean updatePerson(@RequestParam(value = "id") int id, @RequestParam(value = "name") String name,
			@RequestParam(value = "dob") @DateTimeFormat(pattern = "ddMMyyyy") Date dob) {
		try {
			HashMap<String, AttributeValue> itemKey = new HashMap<String, AttributeValue>();

			itemKey.put("id", new AttributeValue(String.valueOf(id)));

			HashMap<String, AttributeValueUpdate> updatedValues = new HashMap<String, AttributeValueUpdate>();

			updatedValues.put("name", new AttributeValueUpdate(new AttributeValue(name), AttributeAction.PUT));
			updatedValues.put("dob",
					new AttributeValueUpdate(new AttributeValue(Person.DATE_FORMAT.format(dob)), AttributeAction.PUT));

			ddb.updateItem("Person", itemKey, updatedValues);
			return true;
		} catch (ResourceNotFoundException e) {
			System.err.println(e.getMessage());

		} catch (AmazonServiceException e) {
			System.err.println(e.getMessage());
		}
		return false;
	}

	@RequestMapping("/delete")
	public boolean deletePerson(@RequestParam(value = "id") int id) {
		try {
			HashMap<String, AttributeValue> itemKey = new HashMap<String, AttributeValue>();

			itemKey.put("id", new AttributeValue(String.valueOf(id)));

			ddb.deleteItem("Person", itemKey);
		} catch (ResourceNotFoundException e) {
			System.err.println(e.getMessage());

		} catch (AmazonServiceException e) {
			System.err.println(e.getMessage());
		}

		return true;
	}
}
