package com.company.registration.service;

import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.company.registration.entity.RegistrationDetails;
import com.company.registration.response.MSPResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class RegistrationService {

	final RestTemplate restTemplate = new RestTemplate();

	final ObjectMapper mapper = new ObjectMapper();

	@Value("${iPApiUrl}")
	public String iPApiUrlString;

	public Map getIpDetails(String ipAddress) {

		String URL = iPApiUrlString.replace("{ip}", ipAddress);

		String response = restTemplate.getForObject(URL, String.class);

		try {
			return mapper.readValue(response, Map.class);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		return null;
	}

	public boolean validatePassword(String password) {

		Pattern pattern = Pattern.compile("^(?=.*?[A-Z])(?=.*?[0-9])(?=.*?[_#$%.]).{8,}$");

		Matcher match = pattern.matcher(password);

		return match.find();
	}

	public boolean validateCountry(String country) {

		return country != null && country.equals("Canada");
	}

	/**
	 * Register user
	 * @param userName
	 * @param password
	 * @param ipaddress
	 * @return
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
	public ResponseEntity<MSPResponse> processRegistration(String userName, String password, String ipaddress)
			throws JsonMappingException, JsonProcessingException {

		MSPResponse mspResponse = new MSPResponse();

		String country = null;

		if (ipaddress == null || ipaddress.trim().length() == 0) {
			mspResponse.addError("Invalid Geo Location");
		} else {
			Map ipDetailsMap = getIpDetails(ipaddress);

			if (ipDetailsMap != null)
				country = (String) ipDetailsMap.get("country");

			if (!validateCountry(country))
				mspResponse.addError("user is not eligible to register");
		}

		if (password != null && !validatePassword(password))
			mspResponse.addError("password should contain atleast 1 uppercase, "
					+ "1 number and 1 special character in this set _#$%.");

		if (userName == null || userName.trim().length() == 0)
			mspResponse.addError("User Name should not be null");

		if (!mspResponse.getErrors().isEmpty()) {
			mspResponse.setMessage("Registration Failed");
			return new ResponseEntity<MSPResponse>(mspResponse, HttpStatus.NOT_ACCEPTABLE);
		}

		RegistrationDetails registrationDetails = new RegistrationDetails();
		registrationDetails.setUserName(userName);
		registrationDetails.setPassword(password);
		registrationDetails.setCountry(country);
		registrationDetails.setRegistrationId(UUID.randomUUID().toString());
		mspResponse.setBody(registrationDetails);
		mspResponse.setMessage("Welcome " + userName + " from " + country + " and your registration id is "
				+ registrationDetails.getRegistrationId());

		return new ResponseEntity<MSPResponse>(mspResponse, HttpStatus.OK);
	}
}
