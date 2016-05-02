package com.mybus.model;

public enum BusServicePublishStatus {

	PUBLISHED,
	PUBLISH,
	ACTIVE,
	IN_ACTIVE;
	
	@Override
	public String toString(){
		return name();
	}
	
}
