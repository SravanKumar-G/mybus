package com.mybus.model;

import io.swagger.annotations.ApiModel;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by schanda on 01/14/16.
 */
@ToString
@ApiModel(value = "Layout")
@AllArgsConstructor
@NoArgsConstructor
public class Layout extends AbstractDocument {

	@Getter
	@Setter
	private String name;

	@Getter
	@Setter
	private LayoutType type;

	@Getter
	@Setter
	private Integer totalSeats;

	@Getter
	@Setter
	private List<Row> rows;

//	@Getter
//	@Setter
//	private int rows;
	
//	@Getter
//	@Setter
//	private int columns;

	@Getter
	@Setter
	private boolean active = true;
	
}
