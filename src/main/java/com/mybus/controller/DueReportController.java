package com.mybus.controller;

import com.mybus.controller.util.ControllerUtils;
import com.mybus.model.BranchOfficeDue;
import com.mybus.service.BookingManager;
import com.mybus.service.DueReportManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by srinikandula on 12/11/16.
 */
@RestController
@RequestMapping(value = "/api/v1/")
@Api(value="DueReportController", description="DueReportController management APIs")
public class DueReportController extends MyBusBaseController{

    private static final Logger logger = LoggerFactory.getLogger(DueReportController.class);

    @Autowired
    private DueReportManager dueReportManager;

    @Autowired
    private BookingManager bookingManager;


    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "dueReports", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
    @ApiOperation(value = "Get all the due reports for branch offices", response = BranchOfficeDue.class, responseContainer = "List")
    public Iterable<BranchOfficeDue> getAllDueReports(HttpServletRequest request,
                                         final Pageable pageable) {
        return dueReportManager.getBranchOfficeDueReports();
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "dueReport/office/{id}", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
    @ApiOperation(value = "Get branch office due report", response = BranchOfficeDue.class )
    public BranchOfficeDue getBranchDueReport(HttpServletRequest request,
                               @ApiParam(value = "Id of the BranchOffice") @PathVariable final String id,
                                            final Pageable pageable) {
        return dueReportManager.findOfficeDuesGroupByDate(id);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "dueReport/office/{id}/{date}", method = RequestMethod.GET, produces = ControllerUtils.JSON_UTF8)
    @ApiOperation(value = "Get branch office due report", response = BranchOfficeDue.class )
    public BranchOfficeDue getBranchDueReportByDate(HttpServletRequest request,
                                              @ApiParam(value = "Id of the BranchOffice") @PathVariable final String id,
                                              @PathVariable(value = "date", required = true) String date,
                                              final Pageable pageable) {
        return dueReportManager.getOfficeDuesByDate(id, date);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "dueReport/payBookingDue/{id}", method = RequestMethod.PUT, produces = ControllerUtils.JSON_UTF8)
    @ApiOperation(value = "Record due payment", response = BranchOfficeDue.class )
    public boolean recordDuePayment(HttpServletRequest request,
                                              @ApiParam(value = "Id of the booking") @PathVariable final String id,
                                              final Pageable pageable) {
        return bookingManager.payBookingDue(id);
    }

}
