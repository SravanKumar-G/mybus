package com.mybus.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by skandula on 3/31/15.
 */
@ToString
@Getter
@Setter
public class BranchwiseCargoBookingSummary  {
    public static final String COLLECTION_NAME="brachwiseCargoBookingSummary";
    private List<BranchCargoBookingsSummary> branchCargoBookings;
    private List<UserCargoBookingsSummary> userCargoBookingsSummaries;
    private double paidBookingsTotal;
    private double toPayBookingsTotal;
    private double onAccountBookingsTotal;
    private double cancelledTotal;

    private double toPayDeliveryTotal;

    public BranchwiseCargoBookingSummary(){
        this.branchCargoBookings = new ArrayList<>();
        this.userCargoBookingsSummaries = new ArrayList<>();
    }

}

