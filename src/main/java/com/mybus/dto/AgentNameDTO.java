package com.mybus.dto;

import com.mybus.model.AbstractDocument;
import io.swagger.annotations.ApiModel;
import lombok.*;
import org.springframework.data.mongodb.core.index.Indexed;

/**
 * Created by skandula on 5/7/16.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AgentNameDTO {
    private String id;
    private String username;
}
