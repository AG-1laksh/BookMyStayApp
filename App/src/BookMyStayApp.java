import java.util.*;
import java.util.concurrent.*;

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
        this.isCancelled = false;
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
 * Uses synchronized methods to ensure Thread Safety across the entire app.
 */
class RoomInventory {
    private final Map<String, Integer> inventoryCounts;

    public RoomInventory() {
        inventoryCounts = new HashMap<>();
        inventoryCounts.put("Single", 10);
        inventoryCounts.put("Double", 5);
        inventoryCounts.put("Suite", 2);
    }

    public synchronized boolean isValidRoomType(String roomType) {
        return inventoryCounts.containsKey(roomType);
    }

    public synchronized void decrementInventory(String roomType) {
        if (inventoryCounts.containsKey(roomType) && inventoryCounts.get(roomType) > 0) {
            inventoryCounts.put(roomType, inventoryCounts.get(roomType) - 1);
        }
    }

    public synchronized void incrementInventory(String roomType) {
        if (inventoryCounts.containsKey(roomType)) {
            inventoryCounts.put(roomType, inventoryCounts.get(roomType) + 1);
        }
    }

    /**
     * CRITICAL SECTION: Synchronized to prevent Race Conditions.
     */
    public synchronized boolean allocateRoom(String roomType) {
        int currentCount = inventoryCounts.getOrDefault(roomType, 0);

        if (currentCount > 0) {
            // Simulate processing delay to expose race conditions if synchronization were missing
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            inventoryCounts.put(roomType, currentCount - 1);
            return true;
        }
        return false;
    }

    public synchronized void displayInventory() {
        System.out.println("Current Inventory: " + inventoryCounts);
    }
}

/**
 * CLASS - ReservationValidator
 */
class ReservationValidator {
    public void validate(String guestName, String roomType, RoomInventory inventory) throws InvalidBookingException {
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
 */
class BookingRequestQueue {
    public void addRequest(String guestName, String roomType) {
        System.out.println("Booking request accepted for " + guestName + " (" + roomType + ")");
    }
}

/**
 * CLASS - BookingHistory
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
 */
class CancellationService {
    private BookingHistory history;
    private RoomInventory inventory;
    private Stack<String> releasedRoomIds;

    public CancellationService(BookingHistory history, RoomInventory inventory) {
        this.history = history;
        this.inventory = inventory;
        this.releasedRoomIds = new Stack<>();
    }

    public void cancelBooking(String reservationId) {
        System.out.println("\n[Action] Initiating cancellation for Reservation: " + reservationId);
        Reservation res = history.getReservation(reservationId);

        if (res == null) {
            System.out.println("--> Error: Reservation " + reservationId + " does not exist.");
            return;
        }

        if (res.isCancelled()) {
            System.out.println("--> Error: Reservation " + reservationId + " is already cancelled.");
            return;
        }

        res.setCancelled(true);
        releasedRoomIds.push(res.getRoomId());
        inventory.incrementInventory(res.getRoomType());

        System.out.println("--> Success: Booking cancelled. Room " + res.getRoomId() + " released.");
    }

    public void displayRollbackHistory() {
        System.out.println("Recently released rooms (LIFO Stack): " + releasedRoomIds);
    }
}

/**
 * CLASS - BookingReportService
 */
class BookingReportService {
    public void generateReport(BookingHistory history) {
        System.out.println("Booking History Report");
        for (Reservation res : history.getConfirmedReservations()) {
            String status = res.isCancelled() ? "[CANCELLED] " : "[CONFIRMED] ";
            System.out.println(status + "ID: " + res.getReservationId() + 
                    ", Guest: " + res.getGuestName() + ", Room: " + res.getRoomType());
        }
    }
}

/**
 * CLASS - AddOnService
 */
class AddOnService {
    private String serviceName;
    private double cost;

    public AddOnService(String serviceName, double cost) {
        this.serviceName = serviceName;
        this.cost = cost;
    }

    public String getServiceName() { return serviceName; }
    public double getCost() { return cost; }
}

/**
 * CLASS - AddOnServiceManager
 */
class AddOnServiceManager {
    private Map<String, List<AddOnService>> servicesByReservation;

    public AddOnServiceManager() {
        servicesByReservation = new HashMap<>();
    }

    public void addService(String reservationId, AddOnService service) {
        servicesByReservation.computeIfAbsent(reservationId, k -> new ArrayList<>()).add(service);
    }

    public double calculateTotalServiceCost(String reservationId) {
        List<AddOnService> services = servicesByReservation.get(reservationId);
        if (services == null) return 0.0;
        
        double total = 0.0;
        for (AddOnService service : services) {
            total += service.getCost();
        }
        return total;
    }
}

/**
 * CLASS - BookingRequest (For Concurrency)
 */
class BookingRequest {
    private String guestName;
    private String requestedRoomType;

    public BookingRequest(String guestName, String requestedRoomType) {
        this.guestName = guestName;
        this.requestedRoomType = requestedRoomType;
    }

    public String getGuestName() { return guestName; }
    public String getRequestedRoomType() { return requestedRoomType; }
}

/**
 * CLASS - BookingProcessor (For Concurrency)
 */
class BookingProcessor implements Runnable {
    private BookingRequest request;
    private RoomInventory inventory;

    public BookingProcessor(BookingRequest request, RoomInventory inventory) {
        this.request = request;
        this.inventory = inventory;
    }

    @Override
    public void run() {
        boolean success = inventory.allocateRoom(request.getRequestedRoomType());
        if (success) {
            System.out.println("--> SUCCESS: " + request.getGuestName() + " booked a " + request.getRequestedRoomType());
        } else {
            System.out.println("--> FAILED: " + request.getGuestName() + " couldn't book a " + request.getRequestedRoomType() + " (Sold Out)");
        }
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
        System.out.println("--- PART 1: Booking Validation ---");
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
            scanner.close(); // Closed here; no further console input expected below
        }

        // --- PART 2: Booking History Setup ---
        System.out.println("\n--- PART 2: Booking History Setup ---");
        BookingHistory history = new BookingHistory();
        
        inventory.decrementInventory("Single");
        history.addReservation(new Reservation("RES101", "Alice", "Single", "S-01"));

        inventory.decrementInventory("Double");
        history.addReservation(new Reservation("RES102", "Bob", "Double", "D-05"));

        inventory.decrementInventory("Suite");
        history.addReservation(new Reservation("RES103", "Charlie", "Suite", "ST-02"));

        inventory.displayInventory();

        BookingReportService reportService = new BookingReportService();
        System.out.println();
        reportService.generateReport(history);

        // --- PART 3: Booking Cancellation ---
        System.out.println("\n--- PART 3: Booking Cancellation & Rollback ---");
        CancellationService cancellationService = new CancellationService(history, inventory);

        cancellationService.cancelBooking("RES101"); // Alice cancels (Single)
        cancellationService.cancelBooking("RES103"); // Charlie cancels (Suite)
        
        System.out.println();
        cancellationService.displayRollbackHistory();
        inventory.displayInventory(); // Inventory reflects cancellations

        // --- PART 4: Add-On Services ---
        System.out.println("\n--- PART 4: Add-On Service Selection ---");
        AddOnServiceManager manager = new AddOnServiceManager();
        String activeResId = "RES102"; // Bob

        manager.addService(activeResId, new AddOnService("Breakfast", 500.0));
        manager.addService(activeResId, new AddOnService("Spa", 1000.0));

        System.out.println("Reservation ID: " + activeResId);
        System.out.println("Total Add-On Cost: " + manager.calculateTotalServiceCost(activeResId));

        // --- PART 5: Concurrent Booking Simulation ---
        System.out.println("\n--- PART 5: Concurrent Booking Simulation ---");
        System.out.println("Initial State before concurrent requests:");
        inventory.displayInventory();
        System.out.println("\nStarting concurrent booking requests...");

        // Generate 12 requests for Singles (Inventory likely has 10 right now)
        List<BookingRequest> requests = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            requests.add(new BookingRequest("Guest_" + i, "Single"));
        }

        ExecutorService executor = Executors.newFixedThreadPool(requests.size());
        for (BookingRequest request : requests) {
            executor.submit(new BookingProcessor(request, inventory));
        }

        executor.shutdown();
        try {
            executor.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            System.err.println("Execution interrupted.");
        }

        System.out.println("\nFinal State after concurrent simulation:");
        inventory.displayInventory();
    }
}