package com.mybus.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

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
