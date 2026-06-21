package service;

import model.Booking;
import model.CargoRequest;
import model.Truck;
import util.IdGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Manages cargo requests and bookings.
 *
 * DSA CHOICE: LinkedList<Booking> for booking history.
 *   WHY LinkedList instead of ArrayList here:
 *     - Booking history is append-heavy (every new booking is an
 *       insertion at the tail, and "most recent first" reporting
 *       benefits from cheap head insertion too). LinkedList gives O(1)
 *       insertion at either end without the occasional O(n) resize-copy
 *       that ArrayList incurs when its backing array fills up.
 *     - We rarely need random access into booking history (we mostly
 *       iterate front-to-back or back-to-front for reports), so we don't
 *       pay for ArrayList's O(1)-random-access advantage anyway, and we
 *       gain cheaper insertions instead.
 */
public class BookingService {

    private List<CargoRequest> cargoRequests = new ArrayList<>();
    private Map<String, CargoRequest> requestById = new HashMap<>();
    private LinkedList<Booking> bookingHistory = new LinkedList<>();
    private TruckService truckService;

    public BookingService(TruckService truckService) {
        this.truckService = truckService;
    }

    public CargoRequest createRequest(String customerName, String pickup, String drop, double weight) {
        String id = IdGenerator.nextRequestId();
        CargoRequest req = new CargoRequest(id, customerName, pickup, drop, weight);
        cargoRequests.add(req);
        requestById.put(id, req);
        return req;
    }

    public List<CargoRequest> getAllRequests() {
        return cargoRequests;
    }

    /**
     * Smart Capacity Allocation + Dynamic Cost Calculation + Booking.
     * Returns the Booking on success, or null if there isn't enough
     * available capacity (booking rejected).
     */
    public Booking bookCargo(Truck truck, String requestId, String customerName, double weightTons) {
        if (truck == null) return null;
        if (weightTons <= 0 || weightTons > truck.getAvailableCapacity()) {
            return null; // reject: not enough space
        }
        double costPerKg = truck.getCostPerKg();
        double weightKg = weightTons * 1000.0;
        double totalCost = weightKg * costPerKg;

        truck.allocate(weightTons);

        String bookingId = IdGenerator.nextBookingId();
        Booking booking = new Booking(bookingId, truck.getTruckId(), requestId, truck.getTruckNumber(),
                customerName, truck.getRouteString(), weightTons, totalCost);
        bookingHistory.addLast(booking);
        return booking;
    }

    public LinkedList<Booking> getBookingHistory() {
        return bookingHistory;
    }

    public int totalBookings() {
        return bookingHistory.size();
    }

    public double totalRevenue() {
        double sum = 0;
        for (Booking b : bookingHistory) {
            sum += b.getTotalCost();
        }
        return sum;
    }

    /**
     * Return-trip optimization: for a truck heading back empty from
     * `arrivalCity`, find cargo requests whose pickup location matches
     * that city (i.e. could be loaded onto the truck for its return
     * leg). Simple O(n) scan over cargo requests.
     */
    public List<CargoRequest> findReturnMatches(String arrivalCity) {
        List<CargoRequest> matches = new ArrayList<>();
        for (CargoRequest req : cargoRequests) {
            if (req.getPickupLocation().equalsIgnoreCase(arrivalCity.trim())) {
                matches.add(req);
            }
        }
        return matches;
    }

    /** Most active route by booking count, for analytics dashboard. */
    public String mostActiveRoute() {
        Map<String, Integer> routeCounts = new HashMap<>();
        for (Booking b : bookingHistory) {
            routeCounts.merge(b.getRoute(), 1, Integer::sum);
        }
        String best = "N/A";
        int max = 0;
        for (Map.Entry<String, Integer> e : routeCounts.entrySet()) {
            if (e.getValue() > max) {
                max = e.getValue();
                best = e.getKey();
            }
        }
        return best;
    }
}
