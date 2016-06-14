package com.mybus.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

/**
 * @author yks-Srinivas
 */

@Service
public class CommunicationManager {

	private Logger LOGGER = LoggerFactory.getLogger(CommunicationManager.class);
	
	@Autowired
	private EmailService mailSender;
	
	public void sendEmail(String toAddress, String fromAddress, String subject, String msgBody){
		SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
		simpleMailMessage.setFrom(fromAddress);
		simpleMailMessage.setTo(toAddress);
		simpleMailMessage.setSubject(subject);
		simpleMailMessage.setText(msgBody);
		mailSender.getJavaMailSenderImpl().send(simpleMailMessage);
	}
}