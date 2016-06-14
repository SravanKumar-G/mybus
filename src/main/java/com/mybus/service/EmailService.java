package com.mybus.service;

import org.springframework.stereotype.Service;

import java.util.Properties;

import org.springframework.mail.javamail.JavaMailSenderImpl;

@Service
public class EmailService {
	
	public JavaMailSenderImpl getJavaMailSenderImpl(){
		JavaMailSenderImpl javaMailSenderImpl = new JavaMailSenderImpl();
		javaMailSenderImpl.setHost("****");
		javaMailSenderImpl.setPort(123);
		javaMailSenderImpl.setUsername("srinu.yks@gmail.com");
		javaMailSenderImpl.setPassword("12345");
		Properties javaMailProperties = new Properties();
		javaMailProperties.put("mail.smtp.auth", true);
		javaMailProperties.put("mail.smtp.starttls.enable", true);
		javaMailProperties.put("mail.smtp.quitwait", false);
		javaMailProperties.put("mail.smtp.ssl.trust","*****");
		javaMailSenderImpl.setJavaMailProperties(javaMailProperties);
		return javaMailSenderImpl;
	}
}
