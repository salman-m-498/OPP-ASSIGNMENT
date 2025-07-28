package com.rentalapp.rental;

import java.time.LocalDate;

public class RentalHistoryRecord {
    private String rentalId;
    private String customerId;
    private String customerName;
    private String vehicleId;
    private String vehicleModel;
    private LocalDate rentalDate;
    private LocalDate returnDate;
    private int rentalDays;
    private double totalAmount;
    private String status;
    private String paymentMethod;
    private int loyaltyPointsEarned;
    private String pickupLocation;
    private String notes;

    public RentalHistoryRecord(String rentalId, String customerId, String customerName,
                              String vehicleId, String vehicleModel, LocalDate rentalDate,
                              LocalDate returnDate, int rentalDays, double totalAmount,
                              String status, String paymentMethod, int loyaltyPointsEarned,
                              String pickupLocation) {
        this.rentalId = rentalId;
        this.customerId = customerId;
        this.customerName = customerName;
        this.vehicleId = vehicleId;
        this.vehicleModel = vehicleModel;
        this.rentalDate = rentalDate;
        this.returnDate = returnDate;
        this.rentalDays = rentalDays;
        this.totalAmount = totalAmount;
        this.status = status;
        this.paymentMethod = paymentMethod;
        this.loyaltyPointsEarned = loyaltyPointsEarned;
        this.pickupLocation = pickupLocation;
        this.notes = "";
    }

    // Constructor with notes
    public RentalHistoryRecord(String rentalId, String customerId, String customerName,
                              String vehicleId, String vehicleModel, LocalDate rentalDate,
                              LocalDate returnDate, int rentalDays, double totalAmount,
                              String status, String paymentMethod, int loyaltyPointsEarned,
                              String pickupLocation, String notes) {
        this(rentalId, customerId, customerName, vehicleId, vehicleModel, rentalDate,
             returnDate, rentalDays, totalAmount, status, paymentMethod, 
             loyaltyPointsEarned, pickupLocation);
        this.notes = notes;
    }

    // Getters
    public String getRentalId() { return rentalId; }
    public String getCustomerId() { return customerId; }
    public String getCustomerName() { return customerName; }
    public String getVehicleId() { return vehicleId; }
    public String getVehicleModel() { return vehicleModel; }
    public LocalDate getRentalDate() { return rentalDate; }
    public LocalDate getReturnDate() { return returnDate; }
    public int getRentalDays() { return rentalDays; }
    public double getTotalAmount() { return totalAmount; }
    public String getStatus() { return status; }
    public String getPaymentMethod() { return paymentMethod; }
    public int getLoyaltyPointsEarned() { return loyaltyPointsEarned; }
    public String getPickupLocation() { return pickupLocation; }
    public String getNotes() { return notes; }

    // Setters
    public void setRentalId(String rentalId) { this.rentalId = rentalId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public void setVehicleId(String vehicleId) { this.vehicleId = vehicleId; }
    public void setVehicleModel(String vehicleModel) { this.vehicleModel = vehicleModel; }
    public void setRentalDate(LocalDate rentalDate) { this.rentalDate = rentalDate; }
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }
    public void setRentalDays(int rentalDays) { this.rentalDays = rentalDays; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    public void setStatus(String status) { this.status = status; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public void setLoyaltyPointsEarned(int loyaltyPointsEarned) { this.loyaltyPointsEarned = loyaltyPointsEarned; }
    public void setPickupLocation(String pickupLocation) { this.pickupLocation = pickupLocation; }
    public void setNotes(String notes) { this.notes = notes; }

    public void printRecord() {
        System.out.println("\n==================== RENTAL RECORD ====================");
        System.out.println("Rental ID: " + rentalId);
        System.out.println("Customer: " + customerName + " (" + customerId + ")");
        System.out.println("Vehicle: " + vehicleModel + " (" + vehicleId + ")");
        System.out.println("Pickup Location: " + pickupLocation);
        System.out.println("Rental Period: " + rentalDate + " to " + returnDate);
        System.out.println("Duration: " + rentalDays + " days");
        System.out.println("Total Amount: RM " + String.format("%.2f", totalAmount));
        System.out.println("Payment Method: " + paymentMethod);
        System.out.println("Status: " + status);
        System.out.println("Loyalty Points Earned: " + loyaltyPointsEarned);
        
        if (notes != null && !notes.trim().isEmpty()) {
            System.out.println("Notes: " + notes);
        }
        
        System.out.println("========================================================\n");
    }

    public void printSummary() {
        System.out.println(rentalId + " | " + customerName + " | " + vehicleModel + 
                          " | " + rentalDate + " | " + rentalDays + " days | " +
                          "RM " + String.format("%.2f", totalAmount) + " | " + status);
    }

    public boolean isOverdue() {
        return "OVERDUE".equalsIgnoreCase(status);
    }

    public boolean isCompleted() {
        return "RETURNED".equalsIgnoreCase(status) || "COMPLETED".equalsIgnoreCase(status);
    }

    public boolean isCancelled() {
        return "CANCELLED".equalsIgnoreCase(status);
    }

    public long getDaysFromRental() {
        return java.time.temporal.ChronoUnit.DAYS.between(rentalDate, LocalDate.now());
    }

    public double getAverageRatePerDay() {
        return rentalDays > 0 ? totalAmount / rentalDays : 0.0;
    }

    @Override
    public String toString() {
        return "RentalHistoryRecord{" +
                "rentalId='" + rentalId + '\'' +
                ", customerName='" + customerName + '\'' +
                ", vehicleModel='" + vehicleModel + '\'' +
                ", rentalDate=" + rentalDate +
                ", rentalDays=" + rentalDays +
                ", totalAmount=" + totalAmount +
                ", status='" + status + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        RentalHistoryRecord that = (RentalHistoryRecord) obj;
        return rentalId != null ? rentalId.equals(that.rentalId) : that.rentalId == null;
    }

    @Override
    public int hashCode() {
        return rentalId != null ? rentalId.hashCode() : 0;
    }
}
