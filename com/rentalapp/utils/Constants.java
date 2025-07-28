package com.rentalapp.utils;

public class Constants {

    //File paths
    private static final String ECONOMY_FILE = "com/rentalapp/data/economy_vehicles.csv";
    private static final String LUXURY_FILE = "com/rentalapp/data/luxury_vehicles.csv";

     // Business rules
    public static final int MIN_DRIVER_AGE = 18;
    public static final int MAX_DRIVER_AGE = 80;
    public static final int MIN_RENTAL_DAYS = 1;
    public static final int MAX_RENTAL_DAYS = 30;
    public static final double LUXURY_SURCHARGE = 0.1;
    public static final double LATE_RETURN_PENALTY = 50.0;

    //System messages
    public static final String VEHICLE_NOT_AVAILABLE = "Selected vehicle is not available.";
    public static final String RENTAL_SUCCESS = "Vehicle rented successfully!";
    public static final String RETURN_SUCCESS = "Vehicle returned successfully!";
}


