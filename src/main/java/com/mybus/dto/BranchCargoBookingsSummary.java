package com.mybus.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BranchCargoBookingsSummary {
    private String branchOfficeId;
    private String branchOfficeName;
    private double paidBookingsTotal;
    private int paidBookingsCount;
    private double topayBookingsTotal;
    private int topayBookingsCount;
    private double onAccountBookingsTotal;
    private int onAccountBookingsCount;

    private double canceledBookingsTotal;
    private int canceledBookingCount;

    private double topayBookingsDeliveredTotal;
}
