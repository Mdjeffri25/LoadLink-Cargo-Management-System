package util;

import model.Truck;
import java.util.List;

/**
 * Implements the partial-route-matching algorithm.
 *
 * A customer search of (source -> destination) matches a truck whenever
 * BOTH locations appear in the truck's ordered stop list AND source
 * occurs at or before destination in that list. This allows a customer
 * travelling Salem -> Bangalore to match a truck registered for the
 * longer haul Chennai -> Salem -> Bangalore.
 *
 * Complexity: O(R) per truck where R = number of stops on that truck's
 * route (R is small/bounded in practice), so checking N trucks is O(N*R).
 */
public class RouteMatcher {

    public static boolean matches(Truck truck, String source, String destination) {
        List<String> route = truck.getRoute();
        int sourceIndex = indexOfIgnoreCase(route, source);
        int destIndex = indexOfIgnoreCase(route, destination);
        return sourceIndex != -1 && destIndex != -1 && sourceIndex < destIndex;
    }

    private static int indexOfIgnoreCase(List<String> route, String stop) {
        for (int i = 0; i < route.size(); i++) {
            if (route.get(i).equalsIgnoreCase(stop.trim())) {
                return i;
            }
        }
        return -1;
    }
}
