package com.rentalapp.rental;

import java.util.List;

public class AddOn {
    private String name;
    private String description;
    private double price;
    private String unit;        // Unit type (e.g., "pax", "set"), optional
    private int count;          // Number of units, optional
    private List<String> suitableFor; 

    // ================= Constructors =================
    // Flat price add-ons
    public AddOn(String name, String description, double price, List<String> suitableFor) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.unit = null;
        this.count = 1;
        this.suitableFor = suitableFor;
    }

    // Per-unit add-ons
    public AddOn(String name, String description, double price, String unit, int count, List<String> suitableFor) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.unit = unit;
        this.count = count;
        this.suitableFor = suitableFor;
    }


    // ================= Getters =================
    public String getName() { return name; }
    public String getDescription() { return description; }
    public double getPrice() { return price; }
    public String getUnit() { return unit; }
    public int getCount() { return count; }
    public List<String> getSuitableFor() { return suitableFor; }

    // ================= Setters =================
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setPrice(double price) { this.price = price; }
    public void setUnit(String unit) { this.unit = unit; }
    public void setCount(int count) { this.count = count; }
    public void setSuitableFor(List<String> suitableFor) { this.suitableFor = suitableFor;}

    // ================= Utility Methods =================
    public double getTotalPrice() {
        return price * count;
    }

    @Override
    public String toString() {
        if (unit != null && count > 1) {
            return String.format("%s (%s) - RM%.2f x %d %s = RM%.2f", 
                name, description, price, count, unit, getTotalPrice());
        } else {
            return String.format("%s (%s) - RM%.2f", name, description, getTotalPrice());
        }
    }
}

