package com.mybus.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class Row {
	
	@Getter
	@Setter
	private List<Seat> seats;
	
	@Getter
	@Setter
	private boolean middleRow;
}
