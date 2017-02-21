package com.mybus.model;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Collection;
import java.util.Date;

/**
 * Created by skandula on 2/13/16.
 */
@ToString
@ApiModel(value = "ServiceReportStatus")
@Getter
@Setter
public class ServiceReportStatus extends AbstractDocument  {
    private String reportDate;
    public ServiceReportStatus() {

    }
    public ServiceReportStatus(String reportDate) {
        this.reportDate = reportDate;
    }
}
