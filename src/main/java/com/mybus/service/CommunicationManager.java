package com.mybus.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

/**
 * @author yks-Srinivas
 */

@Service
public class CommunicationManager {

	private Logger LOGGER = LoggerFactory.getLogger(CommunicationManager.class);
	@Autowired
	private TaskExecutor etsTaskExecutor;

	public void sendSMS(final String to,final String  body) {
		LOGGER.info("Sending SMS Asynchronously [{}] to: {},body :{}",new Object[] { to, body});
		Runnable runnable = new Runnable() {
			public void run() {
				String urlString = null;
				BufferedReader in = null;
				try {
					urlString = "http://alerts.solutionsinfini.com/api/web2sms.php?user="+URLEncoder.encode("XXXXX", "UTF-8")+"&password="+URLEncoder.encode("XXXXXX","UTF-8")+"&mobile="+URLEncoder.encode("91"+to, "UTF-8")+"&message="+URLEncoder.encode(body,"UTF-8")+"&sender="+URLEncoder.encode("TestID", "UTF-8")+"&type=3";
					LOGGER.info(to+":"+urlString);
					URL url = new URL(urlString);
					HttpURLConnection urlconnection = (HttpURLConnection) url.openConnection();
					urlconnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
					urlconnection.setDoOutput(true);
					in = new BufferedReader(	new InputStreamReader(urlconnection.getInputStream()));
					String decodedString;
					String retval = "";
					while ((decodedString = in.readLine()) != null) {
						retval += decodedString;
					}
					LOGGER.info(to+":Response from sms service provider:"+retval);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}finally {
					if(in!=null)
						try {
							in.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
				}
			};
		};
		etsTaskExecutor.execute(runnable);
	}
}