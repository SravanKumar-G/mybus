package com.mybus.model;

import io.swagger.annotations.ApiModel;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@ApiModel(value = "Row")
@AllArgsConstructor
@NoArgsConstructor
public class Row {
	
	@Getter
	@Setter
	private List<Seat> seats;
	
	@Getter
	@Setter
	private boolean middleRow;
	
    @Getter
    @Setter
    private boolean window;

    @Getter
    @Setter
    private boolean sideSleeper;

}
