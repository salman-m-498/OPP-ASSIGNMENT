package com.rentalapp.rental;

import java.time.LocalDate;

public class RentalRequest {
    private String customerId;
    private String vehicleId;
    private String pickupLocation;
    private LocalDate pickupDate;
    private LocalDate returnDate;
    private int driverAge;
    private int rentalDays;
}
