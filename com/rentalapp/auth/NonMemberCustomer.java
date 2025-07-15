package com.rentalapp.auth;

public class NonMemberCustomer extends Customer {
    @Override
    public void addLoyaltyPoints(){
        return;
    }

    @Override
    public boolean isEligibleForPromo() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'isEligibleForPromo'");
    }
    
}