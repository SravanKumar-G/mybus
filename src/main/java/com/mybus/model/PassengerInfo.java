package com.mybus.model;

import java.util.LinkedHashMap;

import org.json.simple.JSONObject;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
public class PassengerInfo {
	
	@Setter
	@Getter
	private String name;
	
	@Setter
	@Getter
	private int age;
	
	@Setter
	@Getter
	private String gender;
	
	@Setter
	@Getter
	private String seatNumber;
	
	@Setter
	@Getter
	private double seatFare;
	
	/**
	 * {seatNumber=L1, gender=male, name=chinna, age=23, seatFare = 100}
	 */
	
	public PassengerInfo(LinkedHashMap passengerInfoMap){
		if(passengerInfoMap.containsKey("name")) {
			this.name = (String)passengerInfoMap.get("name");
		}
		if(passengerInfoMap.containsKey("age")) {
			this.age = Integer.valueOf((String)passengerInfoMap.get("age"));
		}
		if(passengerInfoMap.containsKey("gender")) {
			this.gender = (String)passengerInfoMap.get("gender");
		}
		if(passengerInfoMap.containsKey("seatNumber")) {
			this.seatNumber = (String)passengerInfoMap.get("seatNumber");
		}
		if(passengerInfoMap.containsKey("seatFare")) {
			this.seatFare = Double.valueOf((String)passengerInfoMap.get("seatFare"));
		}
	}
	
}
