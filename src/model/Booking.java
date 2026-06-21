package model;

import java.time.LocalDateTime;

/**
 * Represents a confirmed booking that links a Truck to a CargoRequest
 * (or a direct manual booking), recording the weight booked and the
 * total cost charged.
 */
public class Booking {

    private String bookingId;
    private String truckId;
    private String requestId; // may be null for direct/manual bookings
    private String truckNumber;
    private String customerName;
    private String route;
    private double bookedWeight;
    private double totalCost;
    private LocalDateTime bookedAt;

    public Booking(String bookingId, String truckId, String requestId, String truckNumber,
                    String customerName, String route, double bookedWeight, double totalCost) {
        this.bookingId = bookingId;
        this.truckId = truckId;
        this.requestId = requestId;
        this.truckNumber = truckNumber;
        this.customerName = customerName;
        this.route = route;
        this.bookedWeight = bookedWeight;
        this.totalCost = totalCost;
        this.bookedAt = LocalDateTime.now();
    }

    public String getBookingId() { return bookingId; }
    public String getTruckId() { return truckId; }
    public String getRequestId() { return requestId; }
    public String getTruckNumber() { return truckNumber; }
    public String getCustomerName() { return customerName; }
    public String getRoute() { return route; }
    public double getBookedWeight() { return bookedWeight; }
    public double getTotalCost() { return totalCost; }
    public LocalDateTime getBookedAt() { return bookedAt; }

    @Override
    public String toString() {
        return bookingId + ": " + customerName + " on " + truckNumber + " (" + bookedWeight
                + "T) = Rs." + totalCost;
    }
}
