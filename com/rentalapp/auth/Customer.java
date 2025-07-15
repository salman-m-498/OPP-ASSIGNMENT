package com.rentalapp.auth;

import java.util.List;

abstract class Customer extends User{
    // protected List<RentalRecord> rentalRecords;

    public Customer(){
        //this.rentalRecords = new ArrayList<>();
    }
    // TODO: #1 Add Functions for Customers rental history here

    public abstract void addLoyaltyPoints();
    public abstract boolean isEligibleForPromo();
}