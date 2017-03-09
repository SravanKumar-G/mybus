package com.mybus.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;

/**
 * Created by srinikandula on 3/8/17.
 */
@NoArgsConstructor
@Getter
@Setter
public class ResponseData<T> {
    private long total;
    private Collection<T> data;
}
