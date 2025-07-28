package com.rentalapp.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class DateUtils {
     private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    public static String formatDate(LocalDate date) {
        return date.format(formatter);
    }
    
    public static LocalDate parseDate(String dateStr) {
        return LocalDate.parse(dateStr, formatter);
    }
    
    public static long calculateDaysBetween(LocalDate startDate, LocalDate endDate) {
        return ChronoUnit.DAYS.between(startDate, endDate);
    }
    
    public static boolean isDateInFuture(LocalDate date) {
        return date.isAfter(LocalDate.now());
    }
    
    public static boolean isValidDateRange(LocalDate startDate, LocalDate endDate) {
        return !endDate.isBefore(startDate);
    }
}
