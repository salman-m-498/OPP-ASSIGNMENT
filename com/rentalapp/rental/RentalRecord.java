package com.rentalapp.rental;

import java.time.LocalDate;

public class RentalRecord {
    private String rentalId;
    private String customerId;
    private String vehicleId;
    private String pickupLocation;
    private LocalDate pickupDate;
    private LocalDate returnDate;
    private LocalDate actualReturnDate;
    private int rentalDays;
    private double totalCost;
    private RentalStatus status;
    private String vehicleModel;
    private String customerName;
}
