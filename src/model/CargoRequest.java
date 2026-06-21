package model;

/**
 * Represents a customer's request to ship cargo from pickupLocation to
 * dropLocation. Used both for normal bookings and for matching against
 * empty return-trip trucks.
 */
public class CargoRequest {

    private String requestId;
    private String customerName;
    private String pickupLocation;
    private String dropLocation;
    private double cargoWeight; // in tons

    public CargoRequest(String requestId, String customerName, String pickupLocation,
                         String dropLocation, double cargoWeight) {
        this.requestId = requestId;
        this.customerName = customerName;
        this.pickupLocation = pickupLocation;
        this.dropLocation = dropLocation;
        this.cargoWeight = cargoWeight;
    }

    public String getRequestId() { return requestId; }
    public String getCustomerName() { return customerName; }
    public String getPickupLocation() { return pickupLocation; }
    public String getDropLocation() { return dropLocation; }
    public double getCargoWeight() { return cargoWeight; }

    @Override
    public String toString() {
        return requestId + ": " + customerName + " (" + pickupLocation + " -> " + dropLocation
                + ") " + cargoWeight + "T";
    }
}
