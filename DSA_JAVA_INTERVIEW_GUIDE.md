# LoadLink Project: DSA & Java Interview Guide

## Table of Contents
1. [Data Structures Used](#data-structures-used)
2. [Algorithms Implemented](#algorithms-implemented)
3. [Java Design Patterns](#java-design-patterns)
4. [Interview Q&A](#interview-qa)

---

## Data Structures Used

### 1. **ArrayList<Truck>** (Dynamic Array)
**File:** `TruckService.java`

**Why ArrayList over Array:**
- Unknown size at compile-time (trucks registered continuously)
- Dynamic resizing (capacity doubles internally, not O(n) per insertion)
- Random access in O(1), traversal in O(n)
- Backed by contiguous array internally

**Code:**
```java
private List<Truck> allTrucks = new ArrayList<>();
```

**Interview Answer:**
> "I chose ArrayList because the number of trucks is unknown upfront and grows dynamically. Arrays require fixed pre-allocation, which wastes memory when usage is low and fails when usage exceeds capacity. ArrayList resizes automatically (doubling capacity), so insertion is amortized O(1) while maintaining O(1) random access."

---

### 2. **HashMap<String, Truck>** (Hash Table)
**File:** `TruckService.java`

**Why HashMap over Linear Search:**
- Direct truck lookup by ID in O(1) average case vs. O(n) scan
- Used when Booking needs to update a specific truck's capacity
- Load-factor aware resizing

**Code:**
```java
private Map<String, Truck> truckById = new HashMap<>();
```

**Interview Answer:**
> "HashMap provides O(1) average-case lookup by truck ID. Instead of scanning all trucks linearly (O(n)), I can directly fetch a truck. When a booking allocates weight, I look up the truck once and update it instantly. The trade-off is O(n) space for the duplicate keys, but the speed gain justifies it for frequent lookups."

---

### 3. **PriorityQueue<Truck>** (Min-Heap)
**File:** `TruckService.java` - `searchRoute()` method

**Why PriorityQueue:**
- Auto-sorts trucks by cost (cheapest first)
- Used to rank matching trucks without full sort O(n log n)
- Each insertion/removal is O(log n)

**Code:**
```java
PriorityQueue<Truck> pq = new PriorityQueue<>();
for (Truck truck : allTrucks) {
    if (RouteMatcher.matches(truck, source, destination) && truck.getAvailableCapacity() > 0) {
        pq.offer(truck);
    }
}
```

**Truck Comparator:**
```java
@Override
public int compareTo(Truck other) {
    int costCompare = Double.compare(this.costPerKg, other.costPerKg);
    if (costCompare != 0) return costCompare;  // Cheapest first
    return Double.compare(other.getAvailableCapacity(), this.getAvailableCapacity());  // Then highest capacity
}
```

**Interview Answer:**
> "PriorityQueue implements a min-heap backed by a binary heap. When I need the cheapest truck for a route, inserting all matches is O(m log m) where m is the number of matches. This is faster than sorting the entire allTrucks list (O(n log n)) every single search. The heap naturally gives me the lowest-cost truck first when I poll."

---

### 4. **List<String> (Route)** (Dynamic Array)
**File:** `Truck.java`

**Why List for Route:**
- Ordered stops (order matters: Chennai → Salem → Bangalore ≠ Bangalore → Salem → Chennai)
- Partial-match search (sub-sequence) is efficient O(n)

**Code:**
```java
private List<String> route;  // ordered stops

public String getRouteString() {
    return String.join(" -> ", route);
}
```

**Interview Answer:**
> "Routes are ordered sequences of cities. I use a List to preserve order and enable sub-sequence matching (e.g., 'Salem → Bangalore' exists within 'Chennai → Salem → Bangalore'). This is simpler than a Set, and the linear O(n) sub-sequence check is acceptable because route lengths are small (typically 2-5 stops)."

---

### 5. **DefaultTableModel** (Mutable Table Data)
**File:** `AddTruckPanel.java`, `BookCargoPanel.java`

**Why DefaultTableModel:**
- JTable needs mutable data source for refresh
- `setRowCount(0)` and `addRow()` allow dynamic updates

**Code:**
```java
DefaultTableModel tableModel = new DefaultTableModel(
    new Object[]{"ID", "Number", "Owner", "Route", ...}, 0);
table = new JTable(tableModel);
tableModel.addRow(new Object[]{...});  // Add rows dynamically
```

**Interview Answer:**
> "DefaultTableModel is a Swing MutableTableModel that lets me refresh table data without recreating the JTable. When trucks are added or bookings completed, I call `setRowCount(0)` to clear and re-populate with new rows. This is O(n) but acceptable for UI updates with hundreds of records."

---

## Algorithms Implemented

### 1. **Route Matching (Partial Sequence Search)**
**File:** `RouteMatcher.java`

**Algorithm:**
```java
public static boolean matches(Truck truck, String source, String destination) {
    List<String> route = truck.getRoute();
    int sourceIdx = -1, destIdx = -1;
    
    for (int i = 0; i < route.size(); i++) {
        if (route.get(i).equalsIgnoreCase(source)) sourceIdx = i;
        if (route.get(i).equalsIgnoreCase(destination)) destIdx = i;
    }
    
    return sourceIdx >= 0 && destIdx > sourceIdx;
}
```

**Complexity:** O(n) where n = route length (typically 2-5)

**Interview Answer:**
> "This is a linear scan to find if source comes before destination in the route. I iterate once through the route, tracking the latest index of source and destination. If source index is found and destination comes after it, the truck matches. Time complexity is O(n) for route length, space O(1)."

---

### 2. **Route Search with Ranking**
**File:** `TruckService.java` - `searchRoute()`

**Algorithm:**
```
1. Traverse allTrucks (O(n))
2. For each truck, check route match (O(route_length))
3. Push matching trucks into PriorityQueue (O(m log m) for m matches)
4. Poll all trucks from queue to get ranked list
```

**Total Complexity:** O(n × route_length + m log m) ≈ O(n + m log m) for small routes

**Interview Answer:**
> "First, I filter trucks by route match in O(n) traversal. Matching trucks go into a PriorityQueue (min-heap ordered by cost). This is O(m log m) for m matches. Finally, I drain the queue to get a sorted list. Overall O(n + m log m). This is faster than sorting all n trucks every time O(n log n)."

---

### 3. **Capacity Allocation & Deallocation**
**File:** `Truck.java`, `BookingService.java`

**Algorithm:**
```java
public void allocate(double weightTons) {
    this.usedCapacity += weightTons;
    this.availabilityUpdatedAt = LocalDateTime.now();  // Track update time
}

public void release(double weightTons) {
    this.usedCapacity = Math.max(0, this.usedCapacity - weightTons);
    this.availabilityUpdatedAt = LocalDateTime.now();
}
```

**Complexity:** O(1) for both operations

**Interview Answer:**
> "Capacity tracking is simple arithmetic O(1). I also update the `availabilityUpdatedAt` timestamp whenever capacity changes, so users/owners know when the truck's availability last changed. This helps with real-time transparency."

---

### 4. **Return Trip Optimization**
**File:** `TruckService.java` - `findEmptyReturnTrucks()`

**Algorithm:**
```java
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
```

**Complexity:** O(n) for traversal

**Interview Answer:**
> "To find empty trucks arriving at a city, I scan all trucks once O(n), checking if their destination matches and utilization is below 20%. This finds candidates for reverse-route cargo (avoiding empty return trips). Simple linear scan since we need to check all trucks anyway."

---

## Java Design Patterns

### 1. **Singleton Pattern (AppContext)**
**File:** `AppContext.java`

**Purpose:** Single shared instance of services across the app

```java
public class AppContext {
    private static AppContext instance;
    
    public static AppContext getInstance() {
        if (instance == null) {
            instance = new AppContext();
        }
        return instance;
    }
}
```

**Interview Answer:**
> "AppContext is a Singleton ensuring one shared instance of all services (UserService, TruckService, BookingService). All UI panels access the same data through `AppContext.getInstance()`. This avoids duplicating services or passing them through constructors. Thread-safety would require synchronized blocks in production."

---

### 2. **MVC Pattern (UI Layers)**
**File:** `LoginScreen.java`, `MainDashboard.java`, `BookCargoPanel.java`

**Structure:**
- **Model:** `User.java`, `Truck.java`, `Booking.java`
- **View:** `*Panel.java`, `*Dashboard.java`
- **Controller:** Service classes (`UserService`, `TruckService`)

**Interview Answer:**
> "The app separates concerns: Models hold data (User, Truck, Booking), Views display UI (JPanel subclasses), Controllers handle business logic (Services). When a user books cargo, the View calls a service method, which updates the Model, then the View refreshes. This makes testing and maintenance easier."

---

### 3. **Observer Pattern (Refresh Interface)**
**File:** `MainDashboard.java`

```java
public interface Refreshable {
    void refresh();
}

// When switching dashboards or adding trucks:
Component current = cards.get(name);
if (current instanceof Refreshable) {
    ((Refreshable) current).refresh();
}
```

**Interview Answer:**
> "Panels implement the Refreshable interface and override `refresh()` to reload their data. When bookings are made or trucks registered, the dashboard calls refresh on the current panel. This is a light Observer pattern without explicit listeners, suitable for simple UI updates."

---

### 4. **Strategy Pattern (Button Styling)**
**File:** `UIConstants.java`

```java
public static void stylePrimaryButton(JButton button) {
    styleButton(button, PRIMARY, Color.WHITE);
}

public static void styleDangerButton(JButton button) {
    styleButton(button, DANGER, Color.WHITE);
}

private static void styleButton(JButton button, Color background, Color foreground) {
    button.setBackground(background);
    button.setForeground(foreground);
    // ... shared styling
}
```

**Interview Answer:**
> "Instead of applying styles inline everywhere, I extracted common button styling into reusable static methods. Different button types (primary, danger, sidebar) follow the same pattern but with different colors. This is the Strategy pattern: same algorithm (styling), different parameters (colors)."

---

### 5. **Factory Pattern (Role-Based Dashboard Creation)**
**File:** `LoginScreen.java`

```java
if (user.getRole() == User.Role.ADMIN) {
    new MainDashboard().setVisible(true);
} else if (user.getRole() == User.Role.OWNER) {
    new OwnerDashboard().setVisible(true);
} else {
    new CustomerDashboard().setVisible(true);
}
```

**Interview Answer:**
> "Based on the user's role, I create the appropriate dashboard. This is a simple Factory pattern where the role determines which concrete class to instantiate. In production, a DashboardFactory class would encapsulate this logic."

---

## Interview Q&A

### Q1: "Why HashMap instead of traversing a list every time?"
**Answer:**
> "HashMaps provide O(1) average-case lookup vs. O(n) linear search. When a booking allocates weight to a truck, I look up that specific truck by ID (not scan all trucks). For 1000 trucks, HashMap is ~1000x faster. The trade-off is O(n) extra space for the hash table, which is acceptable."

---

### Q2: "How does your route search ranking work?"
**Answer:**
> "First, I filter trucks whose route contains the source → destination segment (O(n) traversal). Matching trucks go into a PriorityQueue (min-heap ordered by cost per kg, then by capacity). Finally, I drain the queue in sorted order. This gives the cheapest truck first. Overall O(n + m log m) where m is the number of matches."

---

### Q3: "Why PriorityQueue instead of sorting?"
**Answer:**
> "Because I don't need the entire sorted list; I just need the best truck(s). Inserting m trucks into a PriorityQueue is O(m log m). Sorting all n trucks would be O(n log n). If m << n (few matches), PriorityQueue is faster. Also, I can poll trucks one at a time and stop early if I find a good match."

---

### Q4: "How do you handle concurrency in AppContext?"
**Answer:**
> "Currently, AppContext is not thread-safe. For production, I'd add synchronized blocks to getInstance() and use volatile for the instance variable, or leverage eager initialization. The current single-threaded design is acceptable for a desktop app where one user logs in at a time."

---

### Q5: "Explain your data structure choices for Trucks."
**Answer:**
> "ArrayList stores all trucks because size is unknown upfront and grows dynamically. HashMap by ID provides O(1) lookups for updates. List for routes preserves order for sub-sequence matching. Together, ArrayList + HashMap is a common dual-index pattern: one for iteration, one for direct access."

---

### Q6: "What's the time complexity of booking a truck?"
**Answer:**
> "1. Find truck by ID: O(1) HashMap lookup
> 2. Check available capacity: O(1) arithmetic
> 3. Allocate weight: O(1) add to usedCapacity
> 4. Update timestamp: O(1)
> 5. Add to booking history: O(1) ArrayList append
> Overall: O(1). Booking is constant-time regardless of dataset size."

---

### Q7: "How do you validate that a booking is valid?"
**Answer:**
> "I check: (1) truck exists, (2) weight > 0, (3) weight <= truck.getAvailableCapacity(). If invalid, the booking returns null and the UI shows an error. This is fail-safe: reject invalid bookings upfront rather than corrupting data."

---

### Q8: "Explain your UI refresh mechanism."
**Answer:**
> "UI panels implement the Refreshable interface. When data changes (new booking, truck added), the dashboard calls refresh() on the current panel. The panel reloads data from services and updates tables/labels. This is simpler than reactive frameworks but works for this use case."

---

### Q9: "Why separate admin and owner dashboards?"
**Answer:**
> "Role-based separation enforces permissions: admins see all trucks and can delete; owners see only their trucks; customers see available trucks. Separation makes the code clearer and reduces accidental exposure of sensitive data."

---

### Q10: "What would you improve for scalability?"
**Answer:**
> "1. Replace in-memory data with a database (SQL or NoSQL)
> 2. Add connection pooling for concurrent requests
> 3. Implement caching (Redis) for frequently accessed trucks
> 4. Use pagination instead of loading all trucks into memory
> 5. Add API endpoints and move business logic to backend
> 6. Use proper logging (SLF4J) and monitoring"

---

## Time & Space Complexity Summary

| Operation | Data Structure | Time | Space |
|-----------|----------------|------|-------|
| Register truck | ArrayList + HashMap | O(1) | O(1) |
| Find truck by ID | HashMap | O(1) avg | O(n) total |
| Search route | ArrayList + PriorityQueue | O(n + m log m) | O(m) |
| Get available trucks | ArrayList | O(n) | O(k) |
| Book cargo | HashMap | O(1) | O(1) |
| Find empty returns | ArrayList | O(n) | O(k) |
| Route match | List | O(route_length) | O(1) |

---

## Key Takeaways for Interviews

1. **Explain Trade-offs:** HashMap uses extra space for O(1) lookup
2. **Know Complexity:** Mention O(n), O(log n), O(1)
3. **Use Real Examples:** "In TruckService, I used..."
4. **Discuss Alternatives:** "I considered Arrays, but ArrayList scales better"
5. **Production Ready:** Mention what you'd improve (databases, caching, concurrency)
6. **Show Understanding:** Explain *why*, not just *what*
