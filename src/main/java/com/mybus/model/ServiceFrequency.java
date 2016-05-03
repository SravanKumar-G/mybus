package com.mybus.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;

/**
 * Created by schanda on 02/02/16.
 */
public enum ServiceFrequency {
	DAILY,
	WEEKLY,
	SPECIAL;
	@Override
	public String toString() {
		return name();
	}
}
