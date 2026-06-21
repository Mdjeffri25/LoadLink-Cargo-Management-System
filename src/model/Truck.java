package model;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a truck registered on the LoadLink platform.
 *
 * route is stored as an ordered List<String> of stop names so that
 * partial-route matching (e.g. Salem -> Bangalore inside
 * Chennai -> Salem -> Bangalore) can be implemented with a simple
 * sub-sequence search instead of string parsing every time.
 */
public class Truck implements Comparable<Truck> {

    private String truckId;
    private String truckNumber;
    private String ownerName;
    private List<String> route;     // ordered stops, e.g. [Chennai, Salem, Bangalore]
    private double totalCapacity;   // in tons
    private double usedCapacity;    // in tons
    private double costPerKg;
        private LocalDateTime availabilityUpdatedAt;

        private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public Truck(String truckId, String truckNumber, String ownerName,
                  List<String> route, double totalCapacity, double usedCapacity,
                  double costPerKg) {
        this.truckId = truckId;
        this.truckNumber = truckNumber;
        this.ownerName = ownerName;
        this.route = new ArrayList<>(route);
        this.totalCapacity = totalCapacity;
        this.usedCapacity = usedCapacity;
        this.costPerKg = costPerKg;
        this.availabilityUpdatedAt = LocalDateTime.now();
    }

    public String getTruckId() { return truckId; }
    public String getTruckNumber() { return truckNumber; }
    public String getOwnerName() { return ownerName; }
    public List<String> getRoute() { return route; }
    public String getSource() { return route.isEmpty() ? "" : route.get(0); }
    public String getDestination() { return route.isEmpty() ? "" : route.get(route.size() - 1); }
    public double getTotalCapacity() { return totalCapacity; }
    public double getUsedCapacity() { return usedCapacity; }
    public double getCostPerKg() { return costPerKg; }
    public LocalDateTime getAvailabilityUpdatedAt() { return availabilityUpdatedAt; }

    public String getAvailabilityUpdatedAtText() {
        return availabilityUpdatedAt.format(DATE_TIME_FORMATTER);
    }

    public double getAvailableCapacity() {
        return totalCapacity - usedCapacity;
    }

    /** Capacity Utilization % = usedCapacity / totalCapacity * 100 */
    public double getUtilizationPercent() {
        if (totalCapacity == 0) return 0;
        return (usedCapacity / totalCapacity) * 100.0;
    }

    /** Books weight (in tons) onto this truck, increasing usedCapacity. */
    public void allocate(double weightTons) {
        this.usedCapacity += weightTons;
        this.availabilityUpdatedAt = LocalDateTime.now();
    }

    /** Frees previously allocated capacity (e.g. booking cancellation). */
    public void release(double weightTons) {
        this.usedCapacity = Math.max(0, this.usedCapacity - weightTons);
        this.availabilityUpdatedAt = LocalDateTime.now();
    }

    public String getRouteString() {
        return String.join(" -> ", route);
    }

    /**
     * Natural ordering used when trucks are placed in a PriorityQueue for
     * "cheapest truck first" recommendations: cheaper cost per kg wins;
     * ties broken by higher remaining capacity.
     */
    @Override
    public int compareTo(Truck other) {
        int costCompare = Double.compare(this.costPerKg, other.costPerKg);
        if (costCompare != 0) return costCompare;
        return Double.compare(other.getAvailableCapacity(), this.getAvailableCapacity());
    }

    @Override
    public String toString() {
        return truckNumber + " [" + getRouteString() + "] avail=" + getAvailableCapacity() + "T"
                + " @ " + getAvailabilityUpdatedAtText();
    }
}
