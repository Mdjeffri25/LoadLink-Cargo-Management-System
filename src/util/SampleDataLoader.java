package util;

import model.User;
import service.BookingService;
import service.TruckService;
import service.UserService;

import java.util.Arrays;

/**
 * Loads sample/test data so the application is immediately demo-able
 * without manual data entry. Run once at application startup.
 */
public class SampleDataLoader {

    public static void load(UserService userService, TruckService truckService, BookingService bookingService) {

        // ---- Users ----
        userService.register("Admin Raja", "9000000001", User.Role.ADMIN);
        userService.register("Suresh (Owner)", "9000000002", User.Role.OWNER);
        userService.register("Karthik (Owner)", "9000000003", User.Role.OWNER);
        userService.register("Priya (Customer)", "9000000004", User.Role.CUSTOMER);
        userService.register("Anand (Customer)", "9000000005", User.Role.CUSTOMER);

        // ---- Trucks ----
        truckService.registerTruck("TN-09-AB-1234", "Suresh",
                Arrays.asList("Chennai", "Salem", "Bangalore"), 10, 6, 5.0);

        truckService.registerTruck("TN-37-CD-5678", "Karthik",
                Arrays.asList("Bangalore", "Hosur", "Chennai"), 12, 2, 4.5);

        truckService.registerTruck("TN-14-EF-9012", "Suresh",
                Arrays.asList("Chennai", "Trichy", "Madurai"), 8, 5, 6.0);

        truckService.registerTruck("KA-05-GH-3456", "Karthik",
                Arrays.asList("Bangalore", "Salem", "Coimbatore"), 15, 9, 4.0);

        truckService.registerTruck("TN-22-IJ-7890", "Suresh",
                Arrays.asList("Madurai", "Trichy", "Chennai"), 10, 1, 5.5);

        // ---- Cargo Requests ----
        bookingService.createRequest("Priya", "Salem", "Bangalore", 2.0);
        bookingService.createRequest("Anand", "Chennai", "Salem", 1.5);
        bookingService.createRequest("Ravi Traders", "Bangalore", "Chennai", 3.0);
        bookingService.createRequest("Meena Exports", "Trichy", "Madurai", 1.0);
    }
}
