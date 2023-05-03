package com.company.registration.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.company.registration.response.MSPResponse;
import com.company.registration.service.RegistrationService;

@RestController
public class RegistrationController {

	@Autowired
	private RegistrationService registrationService;

	@PostMapping("/registration/add")
	public ResponseEntity<MSPResponse> addRegistration(
			@RequestParam(name = "username", required = true) String userName,
			@RequestParam(name = "password", required = true) String password,
			@RequestParam(name = "ipaddress", required = true) String ipAddress) {

		try {
			return registrationService.processRegistration(userName, password, ipAddress);
		}

		catch (Exception e) {
			return new ResponseEntity<MSPResponse>(new MSPResponse(), HttpStatus.BAD_REQUEST);
		}
	}

}
