import java.util.*;

/**
 * CLASS - InvalidBookingException
 * Custom exception for invalid booking scenarios.
 */
class InvalidBookingException extends Exception {
    public InvalidBookingException(String message) {
        super(message);
    }
}

/**
 * CLASS - Reservation
 * Represents a confirmed booking, now with a cancellation status and Room ID.
 */
class Reservation {
    private String reservationId;
    private String guestName;
    private String roomType;
    private String roomId;
    private boolean isCancelled;

    public Reservation(String reservationId, String guestName, String roomType, String roomId) {
        this.reservationId = reservationId;
        this.guestName = guestName;
        this.roomType = roomType;
        this.roomId = roomId;
        this.isCancelled = false; // By default, a new reservation is confirmed (not cancelled)
    }

    public String getReservationId() { return reservationId; }
    public String getGuestName() { return guestName; }
    public String getRoomType() { return roomType; }
    public String getRoomId() { return roomId; }
    public boolean isCancelled() { return isCancelled; }

    public void setCancelled(boolean cancelled) {
        this.isCancelled = cancelled;
    }
}

/**
 * CLASS - RoomInventory
 * Maintains available room types and their current availability counts.
 */
class RoomInventory {
    private Map<String, Integer> inventoryCounts;

    public RoomInventory() {
        inventoryCounts = new HashMap<>();
        // Initialize with default inventory
        inventoryCounts.put("Single", 10);
        inventoryCounts.put("Double", 5);
        inventoryCounts.put("Suite", 2);
    }

    public boolean isValidRoomType(String roomType) {
        return inventoryCounts.containsKey(roomType);
    }

    public void decrementInventory(String roomType) {
        if (inventoryCounts.containsKey(roomType) && inventoryCounts.get(roomType) > 0) {
            inventoryCounts.put(roomType, inventoryCounts.get(roomType) - 1);
        }
    }

    public void incrementInventory(String roomType) {
        if (inventoryCounts.containsKey(roomType)) {
            inventoryCounts.put(roomType, inventoryCounts.get(roomType) + 1);
        }
    }

    public void displayInventory() {
        System.out.println("Current Inventory: " + inventoryCounts);
    }
}

/**
 * CLASS - ReservationValidator
 * Validates booking input.
 */
class ReservationValidator {
    public void validate(
            String guestName,
            String roomType,
            RoomInventory inventory
    ) throws InvalidBookingException {

        if (guestName == null || guestName.trim().isEmpty()) {
            throw new InvalidBookingException("Guest name cannot be empty.");
        }

        if (!inventory.isValidRoomType(roomType)) {
            throw new InvalidBookingException("Invalid room type selected.");
        }
    }
}

/**
 * CLASS - BookingRequestQueue
 * Dummy queue to simulate request handling.
 */
class BookingRequestQueue {
    public void addRequest(String guestName, String roomType) {
        System.out.println("Booking request accepted for " + guestName +
                " (" + roomType + ")");
    }
}

/**
 * CLASS - BookingHistory
 * Stores all reservations using a LinkedHashMap to allow quick O(1) lookups 
 * while preserving insertion order for reporting.
 */
class BookingHistory {
    private Map<String, Reservation> reservations;

    public BookingHistory() {
        reservations = new LinkedHashMap<>();
    }

    public void addReservation(Reservation reservation) {
        reservations.put(reservation.getReservationId(), reservation);
    }

    public Reservation getReservation(String reservationId) {
        return reservations.get(reservationId);
    }

    public List<Reservation> getConfirmedReservations() {
        return new ArrayList<>(reservations.values());
    }
}

/**
 * CLASS - CancellationService
 * Validates cancellations and performs controlled rollback operations using a Stack.
 */
class CancellationService {
    private BookingHistory history;
    private RoomInventory inventory;
    // Stack used to track recently released room IDs for LIFO rollback logic
    private Stack<String> releasedRoomIds;

    public CancellationService(BookingHistory history, RoomInventory inventory) {
        this.history = history;
        this.inventory = inventory;
        this.releasedRoomIds = new Stack<>();
    }

    public void cancelBooking(String reservationId) {
        System.out.println("\n[Action] Initiating cancellation for Reservation: " + reservationId);
        Reservation res = history.getReservation(reservationId);

        // 1. Validate reservation exists
        if (res == null) {
            System.out.println("--> Error: Reservation " + reservationId + " does not exist.");
            return;
        }

        // 2. Validate it is not already cancelled
        if (res.isCancelled()) {
            System.out.println("--> Error: Reservation " + reservationId + " is already cancelled.");
            return;
        }

        // 3. Perform controlled rollback
        res.setCancelled(true);
        releasedRoomIds.push(res.getRoomId()); // Push to LIFO Stack
        inventory.incrementInventory(res.getRoomType()); // Restore Inventory

        System.out.println("--> Success: Booking cancelled. Room " + res.getRoomId() + " (" + res.getRoomType() + ") has been released to availability pool.");
    }

    public void displayRollbackHistory() {
        System.out.println("Recently released rooms (Rollback Stack LIFO): " + releasedRoomIds);
    }
}

/**
 * CLASS - BookingReportService
 * Generates reports from booking history.
 */
class BookingReportService {
    public void generateReport(BookingHistory history) {
        System.out.println("Booking History Report");
        for (Reservation res : history.getConfirmedReservations()) {
            String status = res.isCancelled() ? "[CANCELLED] " : "[CONFIRMED] ";
            System.out.println(
                    status + "ID: " + res.getReservationId() + 
                    ", Guest: " + res.getGuestName() +
                    ", Room Type: " + res.getRoomType()
            );
        }
    }
}

/**
 * CLASS - AddOnService
 * Represents an optional service that can be added to a reservation.
 */
class AddOnService {
    private String serviceName;
    private double cost;

    public AddOnService(String serviceName, double cost) {
        this.serviceName = serviceName;
        this.cost = cost;
    }

    public String getServiceName() {
        return serviceName;
    }

    public double getCost() {
        return cost;
    }
}

/**
 * CLASS - AddOnServiceManager
 * Manages services mapped to reservation IDs.
 */
class AddOnServiceManager {
    private Map<String, List<AddOnService>> servicesByReservation;

    public AddOnServiceManager() {
        servicesByReservation = new HashMap<>();
    }

    // Attach service to a reservation
    public void addService(String reservationId, AddOnService service) {
        servicesByReservation
                .computeIfAbsent(reservationId, k -> new ArrayList<>())
                .add(service);
    }

    // Calculate total cost of services for a reservation
    public double calculateTotalServiceCost(String reservationId) {
        List<AddOnService> services = servicesByReservation.get(reservationId);

        if (services == null) {
            return 0.0;
        }

        double total = 0.0;
        for (AddOnService service : services) {
            total += service.getCost();
        }
        return total;
    }
}

/**
 * MAIN CLASS - BookMyStayApp
 */
public class BookMyStayApp {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        RoomInventory inventory = new RoomInventory();

        // --- PART 1: Booking Validation ---
        System.out.println("--- Booking Validation ---");
        ReservationValidator validator = new ReservationValidator();
        BookingRequestQueue bookingQueue = new BookingRequestQueue();

        try {
            System.out.print("Enter guest name: ");
            String guestName = scanner.nextLine();

            System.out.print("Enter room type (Single/Double/Suite): ");
            String roomType = scanner.nextLine();

            validator.validate(guestName, roomType, inventory);
            bookingQueue.addRequest(guestName, roomType);

        } catch (InvalidBookingException e) {
            System.out.println("Booking failed: " + e.getMessage());
        } finally {
            scanner.close();
        }

        // --- PART 2: Booking History and Initial Setup ---
        System.out.println("\n--- Booking History Setup ---");
        BookingHistory history = new BookingHistory();
        
        inventory.decrementInventory("Single");
        Reservation r1 = new Reservation("RES101", "Alice", "Single", "S-01");
        history.addReservation(r1);

        inventory.decrementInventory("Double");
        Reservation r2 = new Reservation("RES102", "Bob", "Double", "D-05");
        history.addReservation(r2);

        inventory.decrementInventory("Suite");
        Reservation r3 = new Reservation("RES103", "Charlie", "Suite", "ST-02");
        history.addReservation(r3);

        inventory.displayInventory();

        BookingReportService reportService = new BookingReportService();
        System.out.println();
        reportService.generateReport(history);

        // --- PART 3: Booking Cancellation & Rollback ---
        System.out.println("\n--- Book My Stay: Use Case 10 (Cancellation & Rollback) ---");
        CancellationService cancellationService = new CancellationService(history, inventory);

        cancellationService.cancelBooking("RES101"); // Alice cancels
        cancellationService.cancelBooking("RES103"); // Charlie cancels
        
        // Error handling tests
        cancellationService.cancelBooking("RES101"); // Duplicate cancel attempt
        cancellationService.cancelBooking("RES999"); // Non-existent cancel attempt

        System.out.println();
        cancellationService.displayRollbackHistory();
        inventory.displayInventory();

        // --- PART 4: Add-On Service Selection ---
        System.out.println("\n--- Add-On Service Selection ---");
        AddOnServiceManager manager = new AddOnServiceManager();
        String activeReservationId = "RES102"; // Bob's active reservation

        AddOnService breakfast = new AddOnService("Breakfast", 500.0);
        AddOnService spa = new AddOnService("Spa", 1000.0);

        manager.addService(activeReservationId, breakfast);
        manager.addService(activeReservationId, spa);

        double totalCost = manager.calculateTotalServiceCost(activeReservationId);

        System.out.println("Reservation ID: " + activeReservationId);
        System.out.println("Total Add-On Cost: " + totalCost);
    }
}