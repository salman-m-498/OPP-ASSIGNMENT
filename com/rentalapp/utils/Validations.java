package com.rentalapp.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Validations {
    public static boolean isValidAge(int age) {
        return age >= 18 && age <= 80;
    }
    
    public static boolean isValidEmail(String email) {
        return email != null && email.contains("@") && email.contains(".");
    }
    
    public static boolean isValidDate(String dateStr) {
        try {
            LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
    
    public static boolean isValidRentalDays(int days) {
        return days >= 1 && days <= 30;
    }
    
    public static boolean isValidPhoneNumber(String phone) {
        return phone != null && phone.matches("\\d{10,11}");
    }
}
