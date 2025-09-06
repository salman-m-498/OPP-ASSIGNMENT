package com.rentalapp.vessel;

import com.rentalapp.utils.VesselDataLoader;
import java.util.*;
import java.util.stream.Collectors;
import java.time.Duration;

public class VesselManager {
    private List<Vessel> vessels;
    
    public VesselManager() {
        this.vessels = VesselDataLoader.loadAllVessels();
    }
    
    public List<Vessel> getAllVessels() {
        return new ArrayList<>(vessels);
    }
    
    public List<Vessel> getAvailableVessels() {
        return vessels.stream()
                      .filter(Vessel::isAvailable)
                      .collect(Collectors.toList());
    }
    
    public List<Vessel> getVesselsByCategory(String category) {
        return vessels.stream()
                      .filter(v -> v.getVesselCategory().equalsIgnoreCase(category))
                      .collect(Collectors.toList());
    }
    
    public List<Vessel> getVesselsByLocation(String location) {
        return vessels.stream()
                      .filter(v -> v.getLocation().toLowerCase().contains(location.toLowerCase()))
                      .collect(Collectors.toList());
    }
    
    public List<Vessel> getVesselsByCapacity(int minCapacity) {
        return vessels.stream()
                      .filter(v -> v.getCapacity() >= minCapacity)
                      .collect(Collectors.toList());
    }
    
    public List<Vessel> getVesselsByPriceRange(double minPrice, double maxPrice) {
        return vessels.stream()
                      .filter(v -> v.getBasePrice() >= minPrice && v.getBasePrice() <= maxPrice)
                      .collect(Collectors.toList());
    }
    
    public List<Vessel> getVesselsByPurpose(String purpose) {
        return vessels.stream()
                      .filter(v -> v.getPurpose().toLowerCase().contains(purpose.toLowerCase()))
                      .collect(Collectors.toList());
    }
    
    public Vessel getVesselById(String id) {
        return vessels.stream()
                      .filter(v -> v.getId().equals(id))
                      .findFirst()
                      .orElse(null);
    }
    
    public boolean rentVessel(String vesselId) {
        Vessel vessel = getVesselById(vesselId);
        if (vessel != null && vessel.isAvailable()) {
            vessel.setAvailable(false);
            return true;
        }
        return false;
    }
    
    public boolean returnVessel(String vesselId) {
        Vessel vessel = getVesselById(vesselId);
        if (vessel != null && !vessel.isAvailable()) {
            vessel.setAvailable(true);
            vessel.incrementRentalCount();
            return true;
        }
        return false;
    }
    
    
/**
 * Add a new vessel to the system
 */
public boolean addVessel(String category, String id, String vesselType, String location,
                         String purpose, int capacity, Duration duration, double basePrice, boolean available) {
    try {
        // Directly create a Vessel instance
        Vessel newVessel = new Vessel(id, category, vesselType, location, purpose,
                                      capacity, duration, basePrice, available);
        vessels.add(newVessel);
        return true;
    } catch (Exception e) {
        System.err.println("Error adding vessel: " + e.getMessage());
        return false;
    }
}
    
    /**
     * Update a specific field of a vessel
     */
    public boolean updateVesselField(String id, String field, String newValue) {
        Vessel vessel = getVesselById(id);
        if (vessel == null) return false;
        
        try {
            switch (field.toLowerCase()) {
                case "type":
                    vessel.setVesselType(newValue);
                    break;
                case "location":
                    vessel.setLocation(newValue);
                    break;
                case "purpose":
                    vessel.setPurpose(newValue);
                    break;
                case "capacity":
                    vessel.setCapacity(Integer.parseInt(newValue));
                    break;
                case "price":
                    vessel.setBasePrice(Double.parseDouble(newValue));
                    break;
                case "availability":
                    vessel.setAvailable(Boolean.parseBoolean(newValue));
                    break;
                default:
                    return false;
            }
            return true;
        } catch (Exception e) {
            System.err.println("Error updating vessel field: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Delete a vessel from the system
     */
    public boolean deleteVessel(String id) {
        Vessel vessel = getVesselById(id);
        if (vessel != null) {
            vessels.remove(vessel);
            return true;
        }
        return false;
    }
    
    
    /**
     * Get vessel count by category
     */
    public Map<String, Long> getVesselCountByCategory() {
        return vessels.stream()
                     .collect(Collectors.groupingBy(Vessel::getVesselCategory, Collectors.counting()));
    }
    
    /**
     * Search vessels by multiple criteria
     */
    public List<Vessel> searchVessels(String searchTerm) {
        String term = searchTerm.toLowerCase();
        return vessels.stream()
                     .filter(v -> v.getId().toLowerCase().contains(term) ||
                                 v.getVesselType().toLowerCase().contains(term) ||
                                 v.getLocation().toLowerCase().contains(term) ||
                                 v.getPurpose().toLowerCase().contains(term))
                     .collect(Collectors.toList());
    }
    
    public void displayVessels(List<Vessel> vesselList) {
        if (vesselList.isEmpty()) {
            System.out.println("No vessels found.");
            return;
        }

        // Fix encoding issue in all text
        vesselList.forEach(v -> {
            v.setVesselType(v.getVesselType().replace("â??", "'"));
            v.setLocation(v.getLocation().replace("â??", "'"));
            v.setPurpose(v.getPurpose().replace("â??", "'"));
        });

        // Convert values into table rows first
        List<String[]> rows = new ArrayList<>();
        rows.add(new String[]{"ID", "Category", "Vessel Type", "Location", "Purpose",
                "Cap", "Duration", "Price", "Available"});

        for (Vessel vessel : vesselList) {
            long hours = vessel.getDuration().toHours();
            long minutes = vessel.getDuration().toMinutesPart();
            String durationStr = (minutes > 0)
                    ? String.format("%dh %dm", hours, minutes)
                    : String.format("%dh", hours);

            rows.add(new String[]{
                    vessel.getId(),
                    vessel.getVesselCategory(),
                    vessel.getVesselType(),
                    vessel.getLocation(),
                    vessel.getPurpose(),
                    String.valueOf(vessel.getCapacity()),
                    durationStr,
                    String.format("RM%.2f", vessel.getBasePrice()),
                    vessel.isAvailable() ? "Yes" : "No"
            });
        }

        // Calculate max width for each column
        int[] colWidths = new int[rows.get(0).length];
        for (String[] row : rows) {
            for (int i = 0; i < row.length; i++) {
                colWidths[i] = Math.max(colWidths[i], row[i].length());
            }
        }

        // Print header separator
        int totalWidth = Arrays.stream(colWidths).sum() + (colWidths.length - 1) * 2;
        String line = "=".repeat(totalWidth);
        System.out.println("\n" + line);

        // Print all rows
        for (int r = 0; r < rows.size(); r++) {
            StringBuilder sb = new StringBuilder();
            for (int c = 0; c < rows.get(r).length; c++) {
                String format = (c == 5 || c == 6 || c == 7)  // numbers right-aligned
                        ? "%" + colWidths[c] + "s"
                        : "%-" + colWidths[c] + "s";
                sb.append(String.format(format, rows.get(r)[c]));
                if (c < rows.get(r).length - 1) sb.append("  "); // spacing
            }
            System.out.println(sb.toString());
            if (r == 0) System.out.println(line); // after header
        }

        System.out.println(line);
    }
    
    public void displayPopularVessels() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("MOST RENTED VESSELS");
        System.out.println("=".repeat(80));
        
        vessels.stream()
               .filter(v -> v.getRentalCount() > 0)
               .sorted((v1, v2) -> Integer.compare(v2.getRentalCount(), v1.getRentalCount()))
               .limit(10)
               .forEach(v -> System.out.printf("%-6s %-25s - %d rentals\n", 
                          v.getId(), v.getVesselType(), v.getRentalCount()));
    }
}