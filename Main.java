import com.rentalapp.App;

/**
 * Main class - Entry point of the Vessel Rental Application
 */
public class Main {
    public static void main(String[] args) {
        try {
            System.out.println("=== Welcome to Vessel Rental System ===");
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
