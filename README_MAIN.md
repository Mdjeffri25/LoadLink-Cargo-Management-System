# LoadLink - Cargo Management System

A comprehensive Java-based cargo and truck management system designed to streamline logistics operations. LoadLink enables customers to book cargo shipments and truck owners to manage their fleet efficiently.

## 📋 Features

- **User Management**: Role-based access control for Customers and Truck Owners
- **Booking System**: Easy cargo booking with route matching and availability checking
- **Truck Management**: Fleet management with real-time availability tracking
- **Route Optimization**: Smart route matching between cargo requests and available trucks
- **Analytics Dashboard**: Comprehensive analytics and reporting capabilities
- **Return Trip Management**: Track and manage return trips for trucks
- **Search & Filter**: Advanced search functionality for routes and available trucks

## 🏗️ Project Structure

```
LoadLink/
├── src/
│   ├── Main.java                    # Application entry point
│   ├── model/                       # Data models
│   │   ├── User.java               # User entity (Customer/Owner)
│   │   ├── Truck.java              # Truck entity
│   │   ├── Booking.java            # Booking entity
│   │   └── CargoRequest.java       # Cargo request entity
│   ├── service/                     # Business logic services
│   │   ├── UserService.java        # User management
│   │   ├── TruckService.java       # Truck management
│   │   ├── BookingService.java     # Booking operations
│   │   ├── AnalyticsService.java   # Analytics and reporting
│   │   └── AppContext.java         # Application context manager
│   ├── ui/                          # User interface components
│   │   ├── LoginScreen.java        # Login/Authentication screen
│   │   ├── MainDashboard.java      # Main dashboard controller
│   │   ├── HomePanel.java          # Home panel
│   │   ├── CustomerDashboard.java  # Customer-specific dashboard
│   │   ├── OwnerDashboard.java     # Truck owner dashboard
│   │   ├── BookCargoPanel.java     # Cargo booking interface
│   │   ├── SearchRoutePanel.java   # Route search interface
│   │   ├── AddTruckPanel.java      # Truck registration
│   │   ├── ReturnTripPanel.java    # Return trip management
│   │   ├── AnalyticsPanel.java     # Analytics display
│   │   └── ReportsPanel.java       # Reports interface
│   └── util/                        # Utility classes
│       ├── IdGenerator.java        # Unique ID generation
│       ├── RouteMatcher.java       # Route matching algorithm
│       ├── SampleDataLoader.java   # Sample data initialization
│       └── UIConstants.java        # UI configuration constants
├── README_MAIN.md                   # This file
└── DSA_JAVA_INTERVIEW_GUIDE.md     # Data structures reference

```

## 🚀 Getting Started

### Prerequisites

- **Java**: JDK 11 or higher
- **IDE**: IntelliJ IDEA or Eclipse (optional but recommended)
- **Git**: For version control

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/Mdjeffri25/LoadLink-Cargo-Management-System.git
   cd LoadLink
   ```

2. **Compile the project**
   ```bash
   cd src
   javac -d . Main.java
   ```

3. **Run the application**
   ```bash
   java Main
   ```

## 🎯 Usage

### Login
- **Customer Mode**: Access customer dashboard to book cargo
- **Truck Owner Mode**: Manage trucks and view bookings

### Key Operations

#### For Customers
1. Log in with customer credentials
2. Navigate to "Book Cargo" panel
3. Enter cargo details and desired route
4. View matching available trucks
5. Complete booking

#### For Truck Owners
1. Log in with owner credentials
2. Add trucks to fleet via "Add Truck" panel
3. Monitor available bookings
4. Manage return trips
5. View analytics and earnings

### Search & Filter
- Search for available trucks by route
- Filter by capacity, availability, and price
- View detailed truck information

## 🏛️ Architecture

### Service Layer
- Handles all business logic and data operations
- Manages booking workflows and route matching
- Performs analytics calculations

### UI Layer
- Swing-based user interface
- Panel-based navigation system
- Role-specific dashboards

### Data Model
- User (Customer/Truck Owner)
- Truck (Fleet management)
- Booking (Transaction records)
- CargoRequest (Shipment details)

### Utility Functions
- ID generation for unique identifiers
- Route matching algorithm
- Sample data initialization for testing

## 📊 Analytics & Reports

The Analytics panel provides:
- Total bookings and revenue
- Fleet utilization metrics
- Popular routes
- Customer activity reports
- Earnings breakdowns

## 🔐 Security Features

- Role-based access control
- User authentication
- Password protection (basic implementation)
- Session management

## 🛠️ Development

### Adding New Features

1. Create models in `src/model/`
2. Implement services in `src/service/`
3. Build UI components in `src/ui/`
4. Update `AppContext.java` with new services
5. Add utilities to `src/util/` as needed

### Code Style
- Follow Java naming conventions
- Use descriptive variable and method names
- Comment complex business logic
- Keep methods focused and single-responsibility

## 📝 Sample Data

The application includes a `SampleDataLoader` utility that populates the system with sample data for testing purposes. This includes:
- Test users (Customers and Truck Owners)
- Sample trucks with various capacities
- Pre-populated bookings for demonstration

## 🐛 Known Issues

- None reported yet

## 🚀 Future Enhancements

- [ ] Database integration (JDBC/JPA)
- [ ] REST API for mobile clients
- [ ] Payment gateway integration
- [ ] Real-time notifications
- [ ] GPS tracking for trucks
- [ ] Machine learning for route optimization
- [ ] Cloud deployment support

## 📞 Contact & Support

- **Project Repository**: [GitHub - LoadLink](https://github.com/Mdjeffri25/LoadLink-Cargo-Management-System)
- **Developer**: Mdjeffri25

## 📄 License

This project is provided as-is for educational and commercial use.

## 🤝 Contributing

Contributions are welcome! Please:
1. Fork the repository
2. Create a feature branch (`git checkout -b feature/YourFeature`)
3. Commit changes (`git commit -m 'Add YourFeature'`)
4. Push to the branch (`git push origin feature/YourFeature`)
5. Open a Pull Request

---

**Version**: 1.0.0  
**Last Updated**: June 21, 2026

