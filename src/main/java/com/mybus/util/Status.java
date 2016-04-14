package com.mybus.util;

import lombok.Getter;
import lombok.Setter;

public class Status {
	
	@Setter
	@Getter
	private boolean success;
	
	@Setter
	@Getter
	private int statusCode;
	
	@Setter
	@Getter
	private String statusMessage;
}
