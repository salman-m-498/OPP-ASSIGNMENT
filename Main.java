import com.rentalapp.App;

/**
 * Main class - Entry point of the Car Rental Application
 */
public class Main {
    public static void main(String[] args) {
        try {
            System.out.println("=== Welcome to Car Rental System ===");
            System.out.println("Starting application...");
            
            // Create and start the application
            App app = new App();
            app.start();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
