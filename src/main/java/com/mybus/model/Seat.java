package com.mybus.model;

import io.swagger.annotations.ApiModel;
import lombok.*;

/**
 * Created by schanda on 01/14/16.
 */
@ToString
@ApiModel(value = "Seat")
@AllArgsConstructor
@NoArgsConstructor
public class Seat extends AbstractDocument{

    @Getter
    @Setter
    private String number;
    
    @Getter
    @Setter
    private String displayName;

    @Getter
    @Setter
    private boolean display;
 
    @Getter
    @Setter
    private boolean sleeper;
   
    @Getter
    @Setter
    private boolean upperDeck;
    
    @Getter
    @Setter
    private boolean active = true;
}
