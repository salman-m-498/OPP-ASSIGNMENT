package com.rentalapp.review;

import com.rentalapp.auth.Customer;
import com.rentalapp.auth.MemberCustomer;
import com.rentalapp.loyalty.LoyaltyPointManager;
import com.rentalapp.rental.RentalRecord;
import com.rentalapp.rental.RentalStatus;
import java.util.*;

public class ReviewManager {
    private Map<String, Review> reviews = new HashMap<>();
    private int reviewCounter = 1;
    private LoyaltyPointManager loyaltyPointManager;

    public ReviewManager(LoyaltyPointManager loyaltyPointManager) {
    this.loyaltyPointManager = loyaltyPointManager;
    }

    /**
     * Add a review for a completed rental.
     * Rewards +25 loyalty points if customer is a member.
     */
    public Review addReview(Customer customer, RentalRecord rental, int rating, String comment) {
         if (customer == null || rental == null) return null;

        // Ensure rating is valid
        if (rating < 1 || rating > 5) {
            System.out.println("Rating must be between 1 and 5.");
            return null;
        }

        // Ensure rental is completed
        if (rental.getStatus() != RentalStatus.COMPLETED) {
            System.out.println("You can only review completed rentals.");
            return null;
        }

        // Prevent duplicate reviews
        if (rental.getReview() != null) {
            System.out.println("This rental already has a review.");
            return null;
        }

        String reviewId = "RV" + String.format("%04d", reviewCounter++);

        Review review = new Review(
            reviewId,
            customer.getCustomerId(),
            rental.getRentalId(),
            rental.getVesselType(),
            rating,
            comment
        );

        reviews.put(reviewId, review);

        rental.setReview(review);

        /// Reward loyalty points for review (only if customer is a member)
       if (customer instanceof MemberCustomer && loyaltyPointManager != null) {
       loyaltyPointManager.addReviewBonus(customer.getCustomerId());
       }

        return review;
    }


    /**
     * Get all reviews submitted by a specific customer.
     */
    public List<Review> getCustomerReviews(String customerId) {
        List<Review> list = new ArrayList<>();
        for (Review r : reviews.values()) {
            if (r.getCustomerId().equals(customerId)) {
                list.add(r);
            }
        }
        return list;
    }

    /**
     * Get all reviews for a given vessel type (for admin / average rating).
     */
    public List<Review> getReviewsForVessel(String vesselType) {
        List<Review> list = new ArrayList<>();
        for (Review r : reviews.values()) {
            if (r.getVesselType().equalsIgnoreCase(vesselType)) {
                list.add(r);
            }
        }
        return list;
    }

    /**
     * Get all reviews in the system.
     */
    public List<Review> getAllReviews() {
        return new ArrayList<>(reviews.values());
    }

    /**
 * Display all reviews in the system (for admin).
 */
public void displayAllReviews() {
    if (reviews.isEmpty()) {
        System.out.println("No reviews submitted yet.");
        return;
    }

    System.out.println("\n" + "═".repeat(50));
    System.out.println("                     ALL REVIEWS                       ");
    System.out.println("\n" + "═".repeat(50));

    for (Review r : reviews.values()) {

       String stars = "*".repeat(r.getRating());
    
        System.out.printf(
            "Review ID: %s%nCustomer: %s%nRental ID: %s%nVessel: %s%nRating: %s%nComment: %s%nDate: %s%n",
            r.getReviewId(),
            r.getCustomerId(),
            r.getRentalId(),
            r.getVesselType(),
            stars,
            r.getComment(),
            r.getCreatedAt().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
        );
        System.out.println("-------------------------------------------------------");
    }
}


    /**
     * Get the average rating for a vessel type.
     */
    public Map<String, Double> getAverageRatingsByVesselType() {
        Map<String, List<Review>> grouped = new HashMap<>();

        // Group reviews by vessel type
        for (Review r : reviews.values()) {
            grouped.computeIfAbsent(r.getVesselType(), k -> new ArrayList<>()).add(r);
        }

        // Calculate averages
        Map<String, Double> averages = new HashMap<>();
        for (Map.Entry<String, List<Review>> entry : grouped.entrySet()) {
            int sum = 0;
            for (Review r : entry.getValue()) {
                sum += r.getRating();
            }
            double avg = (double) sum / entry.getValue().size();
            averages.put(entry.getKey(), avg);
        }

        return averages;
    }

    /**
     * Display aggregated reviews (for admin dashboard).
     */
    public void displayAggregatedReviews() {
    Map<String, List<Review>> grouped = new HashMap<>();

    // Group reviews by vessel type
    for (Review r : reviews.values()) {
        grouped.computeIfAbsent(r.getVesselType(), k -> new ArrayList<>()).add(r);
    }

    if (grouped.isEmpty()) {
        System.out.println("No reviews available.");
        return;
    }

    System.out.println("\n" + "═".repeat(50));
    System.out.println("              AGGREGATED REVIEWS (ADMIN)           ");
    System.out.println("\n" + "═".repeat(50));

    // Sort by average rating (highest first)
    grouped.entrySet().stream()
        .sorted((a, b) -> {
            double avgA = a.getValue().stream().mapToInt(Review::getRating).average().orElse(0);
            double avgB = b.getValue().stream().mapToInt(Review::getRating).average().orElse(0);
            return Double.compare(avgB, avgA);
        })
        .forEach(entry -> {
            String vesselType = entry.getKey();
            List<Review> vesselReviews = entry.getValue();

            double avg = vesselReviews.stream().mapToInt(Review::getRating).average().orElse(0);
            int count = vesselReviews.size();

            // Build star visualization
            int rounded = (int) Math.round(avg);
            String stars = "*".repeat(rounded);

            // Get a sample best & worst comment
            Review best = vesselReviews.stream().max(Comparator.comparingInt(Review::getRating)).orElse(null);
            Review worst = vesselReviews.stream().min(Comparator.comparingInt(Review::getRating)).orElse(null);

            System.out.printf("\n-> %s%n", vesselType);
            System.out.printf("   Average Rating : %.2f %s (%d reviews)%n", avg, stars, count);

            if (best != null) {
                System.out.printf("   Highlight   : \"%s\"%n", best.getComment());
            }
            if (worst != null && worst != best) {
                System.out.printf("   Complaint   : \"%s\"%n", worst.getComment());
            }

            System.out.println("------------------------------------------------------");
        });
}
}