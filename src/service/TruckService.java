package service;

import model.Truck;
import util.IdGenerator;
import util.RouteMatcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * Core service for truck registration, route search and cheapest-truck
 * recommendation.
 *
 * DSA CHOICE #1: ArrayList<Truck> as the master list of all trucks.
 *   WHY ArrayList instead of a plain array:
 *     - The number of trucks registered on the platform is not known
 *       up-front and grows continuously; ArrayList resizes dynamically
 *       (capacity doubles internally) so we never need to manually
 *       reallocate/copy a fixed-size array ourselves.
 *     - Backed by a contiguous array internally, so index-based access
 *       and full traversal (for search/analytics) remain O(1) / O(n)
 *       respectively, same as a raw array, with none of the manual
 *       resize bookkeeping.
 *
 * DSA CHOICE #2: HashMap<String, Truck> keyed by truckId for direct
 * lookups (e.g. when a Booking needs to update a specific truck).
 *   WHY HashMap instead of linear search through the ArrayList:
 *     - O(1) average lookup vs O(n) scanning every truck to find a
 *       matching ID. This matters a lot once hundreds of trucks are
 *       registered and bookings are happening frequently.
 *
 * DSA CHOICE #3: PriorityQueue<Truck> for cheapest-truck recommendation.
 *   WHY PriorityQueue:
 *     - Truck implements Comparable ordering by (lowest cost per kg,
 *       then highest remaining capacity). Pushing all route-matching
 *       trucks into a PriorityQueue and polling gives the best truck
 *       first in O(log n) per insertion/removal, rather than sorting
 *       the whole list (O(n log n)) every single search.
 *
 * MEMORY NOTE: Using ArrayList + HashMap means total memory grows only
 * with the actual number of trucks (load-factor aware for HashMap),
 * instead of pre-allocating a large fixed-size array "just in case",
 * which would waste memory when usage is low and would still fail to
 * scale once usage is high.
 */
public class TruckService {

    private List<Truck> allTrucks = new ArrayList<>();
    private Map<String, Truck> truckById = new HashMap<>();

    public Truck registerTruck(String truckNumber, String ownerName, List<String> route,
                                double totalCapacity, double usedCapacity, double costPerKg) {
        String id = IdGenerator.nextTruckId();
        Truck truck = new Truck(id, truckNumber, ownerName, route, totalCapacity, usedCapacity, costPerKg);
        allTrucks.add(truck);
        truckById.put(id, truck);
        return truck;
    }

    public List<Truck> getAllTrucks() {
        return allTrucks;
    }

    public Truck findById(String truckId) {
        return truckById.get(truckId);
    }

    /**
     * Returns only trucks that still have spare capacity (i.e. not
     * fully booked). Used anywhere a truck needs to be offered for a
     * new booking — a fully-filled truck should never appear as an
     * option to a customer.
     */
    public List<Truck> getAvailableTrucks() {
        List<Truck> available = new ArrayList<>();
        for (Truck truck : allTrucks) {
            if (truck.getAvailableCapacity() > 0) {
                available.add(truck);
            }
        }
        return available;
    }

    /**
     * Removes a truck from the platform (Admin only — enforced in the UI
     * layer). Removes it from both the ArrayList and the HashMap so the
     * two stay in sync; O(n) for the ArrayList removal (must shift
     * elements), O(1) for the HashMap removal.
     *
     * Returns true if a truck was found and removed.
     */
    public boolean removeTruck(String truckId) {
        Truck truck = truckById.remove(truckId);
        if (truck == null) {
            return false;
        }
        allTrucks.remove(truck);
        return true;
    }

    /**
     * Route Search Engine: returns all trucks whose route contains the
     * (source -> destination) segment, in cheapest-first order.
     *
     * Algorithm:
     *   1. Traversal: O(n) over allTrucks, applying RouteMatcher (partial
     *      route matching) to filter candidates.
     *   2. Ranking: push matches into a PriorityQueue<Truck> ordered by
     *      cost then capacity, then drain it -> O(m log m) for m matches.
     * Overall: O(n + m log m).
     */
    public List<Truck> searchRoute(String source, String destination) {
        PriorityQueue<Truck> pq = new PriorityQueue<>();
        for (Truck truck : allTrucks) {
            if (RouteMatcher.matches(truck, source, destination) && truck.getAvailableCapacity() > 0) {
                pq.offer(truck);
            }
        }
        List<Truck> ranked = new ArrayList<>();
        while (!pq.isEmpty()) {
            ranked.add(pq.poll());
        }
        return ranked;
    }

    /** Cheapest single recommendation for a route, or null if none match. */
    public Truck recommendCheapest(String source, String destination) {
        List<Truck> ranked = searchRoute(source, destination);
        return ranked.isEmpty() ? null : ranked.get(0);
    }

    /**
     * Return-trip optimization: trucks whose destination equals the
     * given city and which are currently empty (or near-empty) are
     * candidates for a reverse-route cargo match. We then look at the
     * reverse path (destination -> source) for matching cargo requests
     * elsewhere in the system (see BookingService.findReturnMatches).
     */
    public List<Truck> findEmptyReturnTrucks(String arrivalCity) {
        List<Truck> results = new ArrayList<>();
        for (Truck truck : allTrucks) {
            if (truck.getDestination().equalsIgnoreCase(arrivalCity.trim())
                    && truck.getUtilizationPercent() < 20.0) {
                results.add(truck);
            }
        }
        return results;
    }

    public int totalTrucks() {
        return allTrucks.size();
    }
}