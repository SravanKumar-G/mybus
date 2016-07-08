package com.mybus.service;

import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 
 * @author yks-srinivas
 *
 */
@Service
public class SendMailServices {

	@Autowired
	private VelocityEngine velocityEngine;
	
	@Autowired
	private CommunicationManager communicationManager; 
	
	public void sendTicketToUser(){
		
	}
	
}
