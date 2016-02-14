package com.mybus.model;

import io.swagger.annotations.ApiModel;
import lombok.*;

import java.util.List;

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

	@Getter
	@Setter
	private int totalRows;
	
	@Getter
	@Setter
	private int seatsPerRow;
	
	@Getter
	@Setter
	private int middleRowPosition;

	@Getter
	@Setter
	private boolean middleRowLastSeat;
	
	@Getter
	@Setter
	private boolean active = true;
	
}
