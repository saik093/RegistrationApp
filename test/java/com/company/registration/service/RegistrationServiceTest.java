package com.company.registration.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import com.company.registration.entity.RegistrationDetails;
import com.company.registration.response.MSPResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

@ExtendWith(MockitoExtension.class)
public class RegistrationServiceTest {

	@InjectMocks
	private RegistrationService registrationService;
	
	@BeforeEach
	public void init() {
		ReflectionTestUtils.setField(registrationService, "iPApiUrlString", "http://ip-api.com/json/{ip}");
	}
	
	@Test 
	public void testgetIpDetails() {
		
		Map response = registrationService.getIpDetails("24.48.0.1");
		assertNotNull(response);
		assertEquals(response.get("country"), "Canada");
		
	    response = registrationService.getIpDetails("100.200.300.22");
	    assertEquals(response.get("status"), "fail");
		
	    response = registrationService.getIpDetails("101.0.127.255");
		assertEquals(response.get("country"), "Australia");
	}
	
	
	@Test
	public void testProcessRegistration() throws JsonMappingException, JsonProcessingException {
		
		ResponseEntity<MSPResponse> responseEntity = registrationService.processRegistration("john", "Test$323", "24.48.0.1");
		
		MSPResponse responseBody = responseEntity.getBody();
		
		assertNotNull(responseBody);
		assertEquals(responseBody.getErrors().size(), 0);
		assertNotNull(responseBody.getBody());
		assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
		
		RegistrationDetails registrationDetails = (RegistrationDetails) responseBody.getBody();
		assertEquals(registrationDetails.getCountry(), "Canada");
		assertEquals(registrationDetails.getUserName(), "john");
		assertEquals(registrationDetails.getPassword(), "Test$323");
		
		responseEntity = registrationService.processRegistration(" ", "Test323", "214.48.0.1");
		responseBody = responseEntity.getBody();
		
		assertEquals(responseBody.getErrors().size(), 3);
		assertEquals(responseBody.getErrors().get(0), "user is not eligible to register");
		assertEquals(responseBody.getErrors().get(1), "password should contain atleast 1 uppercase, 1 number and 1 special character in this set _#$%.");
		assertEquals(responseBody.getErrors().get(2), "User Name should not be null");
		assertEquals(responseEntity.getStatusCode(), HttpStatus.NOT_ACCEPTABLE);
		
		responseEntity = registrationService.processRegistration(" ", "Test323", "");
		responseBody = responseEntity.getBody();
		
		assertEquals(responseBody.getErrors().size(), 3);
		assertEquals(responseBody.getErrors().get(0), "Invalid Geo Location");
		assertEquals(responseBody.getErrors().get(1), "password should contain atleast 1 uppercase, 1 number and 1 special character in this set _#$%.");
		assertEquals(responseBody.getErrors().get(2), "User Name should not be null");
		assertEquals(responseEntity.getStatusCode(), HttpStatus.NOT_ACCEPTABLE);
	}
	
}
