package com.mybus.model;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.Date;

/**
 * Created by skandula on 2/13/16.
 */
@ToString
@ApiModel(value = "ServiceReportStatus")
@Getter
@Setter
public class ServiceReportStatus extends AbstractDocument  {
    @Indexed
    private Date reportDate;
    private ReportDownloadStatus status;
    public ServiceReportStatus() {

    }
    public ServiceReportStatus(Date reportDate) {
        this.reportDate = reportDate;
    }
}
