package service;

import model.User;

/**
 * Simple shared application context (in-memory, no DB) holding the
 * service singletons and the currently logged-in user, so every UI
 * panel can access the same data without re-instantiating services.
 */
public class AppContext {

    private static AppContext instance;

    private final UserService userService;
    private final TruckService truckService;
    private final BookingService bookingService;
    private final AnalyticsService analyticsService;
    private User currentUser;

    private AppContext() {
        userService = new UserService();
        truckService = new TruckService();
        bookingService = new BookingService(truckService);
        analyticsService = new AnalyticsService(truckService, bookingService);
    }

    public static AppContext getInstance() {
        if (instance == null) {
            instance = new AppContext();
        }
        return instance;
    }

    public UserService getUserService() { return userService; }
    public TruckService getTruckService() { return truckService; }
    public BookingService getBookingService() { return bookingService; }
    public AnalyticsService getAnalyticsService() { return analyticsService; }

    public User getCurrentUser() { return currentUser; }
    public void setCurrentUser(User user) { this.currentUser = user; }
}
