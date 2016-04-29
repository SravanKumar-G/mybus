package com.mybus.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @author yks-Srinivas
 */

@Service
public class CommunicationManager {

	private Logger LOGGER = LoggerFactory.getLogger(CommunicationManager.class);


	public void sendSMS(final String to,final String  body) {
	}
}