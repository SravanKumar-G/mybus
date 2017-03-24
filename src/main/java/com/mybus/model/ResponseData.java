package com.mybus.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.Collection;

/**
 * Created by srinikandula on 3/8/17.
 */
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class ResponseData<T> {
    private long total;
    private Page<T> data;
}
