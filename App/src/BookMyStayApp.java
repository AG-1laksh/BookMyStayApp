import java.util.*;

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
 * CLASS - BookingHistory
 * Stores all reservations mapped by their Reservation ID for quick O(1) lookups.
 */
class BookingHistory {
    private Map<String, Reservation> reservations;

    public BookingHistory() {
        reservations = new HashMap<>();
    }

    public void addReservation(Reservation reservation) {
        reservations.put(reservation.getReservationId(), reservation);
    }

    public Reservation getReservation(String reservationId) {
        return reservations.get(reservationId);
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
        System.out.println("\nRecently released rooms (Rollback Stack LIFO): " + releasedRoomIds);
    }
}

/**
 * MAIN CLASS - UseCase10BookingCancellation
 */
public class BookMyStayApp {

    public static void main(String[] args) {
        System.out.println("--- Book My Stay: Use Case 10 (Cancellation & Rollback) ---\n");

        // 1. Initialize core system components
        RoomInventory inventory = new RoomInventory();
        BookingHistory history = new BookingHistory();
        CancellationService cancellationService = new CancellationService(history, inventory);

        // 2. Setup initial state (Simulate previously confirmed bookings)
        System.out.println("Setting up initial confirmed reservations...");

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

        // 3. Test valid cancellations
        cancellationService.cancelBooking("RES101"); // Alice cancels
        inventory.displayInventory(); // Should show Single incremented

        cancellationService.cancelBooking("RES103"); // Charlie cancels
        inventory.displayInventory(); // Should show Suite incremented

        // 4. Test validation limits (Error handling)
        // Attempt to cancel an already cancelled booking
        cancellationService.cancelBooking("RES101");

        // Attempt to cancel a non-existent booking
        cancellationService.cancelBooking("RES999");

        // 5. Verify the LIFO Stack
        cancellationService.displayRollbackHistory();
    }
}