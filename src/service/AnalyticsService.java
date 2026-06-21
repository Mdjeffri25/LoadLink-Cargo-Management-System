package service;

import model.Truck;

import java.util.List;

/**
 * Aggregates analytics across TruckService and BookingService.
 * All computations are simple O(n) traversals since they run on-demand
 * when the Analytics screen is opened, not on every UI repaint.
 */
public class AnalyticsService {

    private TruckService truckService;
    private BookingService bookingService;

    public AnalyticsService(TruckService truckService, BookingService bookingService) {
        this.truckService = truckService;
        this.bookingService = bookingService;
    }

    public int totalTrucks() {
        return truckService.totalTrucks();
    }

    public int totalBookings() {
        return bookingService.totalBookings();
    }

    public double totalRevenue() {
        return bookingService.totalRevenue();
    }

    public String mostActiveRoute() {
        return bookingService.mostActiveRoute();
    }

    public double averageUtilization() {
        List<Truck> trucks = truckService.getAllTrucks();
        if (trucks.isEmpty()) return 0;
        double sum = 0;
        for (Truck t : trucks) {
            sum += t.getUtilizationPercent();
        }
        return sum / trucks.size();
    }
}
