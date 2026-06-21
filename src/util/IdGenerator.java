package util;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Centralized sequential ID generator for in-memory entities.
 * Using AtomicInteger keeps ID generation simple, fast (O(1)) and
 * thread-safe in case Swing background workers are added later.
 */
public class IdGenerator {

    private static final AtomicInteger userSeq = new AtomicInteger(1000);
    private static final AtomicInteger truckSeq = new AtomicInteger(1);
    private static final AtomicInteger requestSeq = new AtomicInteger(1);
    private static final AtomicInteger bookingSeq = new AtomicInteger(1);

    public static String nextUserId() {
        return "U" + userSeq.getAndIncrement();
    }

    public static String nextTruckId() {
        return "TRK" + String.format("%03d", truckSeq.getAndIncrement());
    }

    public static String nextRequestId() {
        return "REQ" + String.format("%03d", requestSeq.getAndIncrement());
    }

    public static String nextBookingId() {
        return "BKG" + String.format("%03d", bookingSeq.getAndIncrement());
    }
}
