package com.rentalapp.review;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Review {
    private String reviewId;
    private String customerId;
    private String rentalId;
    private String vesselType;
    private int rating; // 1–5 
    private String comment;
    private LocalDateTime createdAt;

    public Review(String reviewId, String customerId, String rentalId, String vesselType, int rating, String comment) {
        this.reviewId = reviewId;
        this.customerId = customerId;
        this.rentalId = rentalId;
        this.vesselType = vesselType;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = LocalDateTime.now();
    }

    // Getters
    public String getReviewId() {
        return reviewId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getRentalId() {
        return rentalId;
    }

    public String getVesselType() {
        return vesselType;
    }

    public int getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public String toString() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        return String.format(
            "Review[%s] for %s (Rental %s): %d - \"%s\" (by Customer %s on %s)",
            reviewId, vesselType, rentalId, rating, comment, customerId, createdAt.toLocalDate().format(dtf)
        );
    }
    
}
