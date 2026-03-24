import java.io.*;
import java.util.*;

/**
 * CLASS - Reservation
 * Implements Serializable so its state can be written to a file.
 */
class Reservation implements Serializable {
    private static final long serialVersionUID = 1L; // Ensures version compatibility during deserialization

    private String reservationId;
    private String guestName;
    private String roomType;

    public Reservation(String reservationId, String guestName, String roomType) {
        this.reservationId = reservationId;
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getReservationId() { return reservationId; }
    public String getGuestName() { return guestName; }
    public String getRoomType() { return roomType; }

    @Override
    public String toString() {
        return "Reservation[ID=" + reservationId + ", Guest=" + guestName + ", Room=" + roomType + "]";
    }
}

/**
 * CLASS - RoomInventory
 * Implements Serializable to persist available room counts.
 */
class RoomInventory implements Serializable {
    private static final long serialVersionUID = 1L;

    private Map<String, Integer> inventoryCounts;

    public RoomInventory() {
        inventoryCounts = new HashMap<>();
        inventoryCounts.put("Single", 10);
        inventoryCounts.put("Double", 5);
        inventoryCounts.put("Suite", 2);
    }

    public void decrementInventory(String roomType) {
        if (inventoryCounts.containsKey(roomType) && inventoryCounts.get(roomType) > 0) {
            inventoryCounts.put(roomType, inventoryCounts.get(roomType) - 1);
        }
    }

    public void displayInventory() {
        System.out.println("Current Inventory: " + inventoryCounts);
    }
}

/**
 * CLASS - BookingHistory
 * Implements Serializable to persist the list of confirmed reservations.
 */
class BookingHistory implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<Reservation> reservations;

    public BookingHistory() {
        reservations = new ArrayList<>();
    }

    public void addReservation(Reservation reservation) {
        reservations.add(reservation);
    }

    public void displayHistory() {
        System.out.println("Booking History:");
        if (reservations.isEmpty()) {
            System.out.println(" (No bookings found)");
        } else {
            for (Reservation res : reservations) {
                System.out.println(" - " + res.toString());
            }
        }
    }
}

/**
 * CLASS - SystemState
 * A wrapper class that holds all critical data needing persistence.
 */
class SystemState implements Serializable {
    private static final long serialVersionUID = 1L;

    private RoomInventory inventory;
    private BookingHistory history;

    public SystemState(RoomInventory inventory, BookingHistory history) {
        this.inventory = inventory;
        this.history = history;
    }

    public RoomInventory getInventory() { return inventory; }
    public BookingHistory getHistory() { return history; }
}

/**
 * CLASS - PersistenceService
 * Handles writing the SystemState to a file and reading it back.
 */
class PersistenceService {
    private static final String DATA_FILE = "hotel_data.ser";

    // Saves the system state to a file
    public void saveState(SystemState state) {
        System.out.println("\n[System] Initiating shutdown sequence. Saving state...");
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(state);
            System.out.println("[System] State successfully saved to " + DATA_FILE);
        } catch (IOException e) {
            System.err.println("[Error] Failed to save system state: " + e.getMessage());
        }
    }

    // Loads the system state from a file
    public SystemState loadState() {
        System.out.println("\n[System] Booting up. Attempting to restore previous state...");
        File file = new File(DATA_FILE);

        if (!file.exists()) {
            System.out.println("[System] No previous state found. Initializing fresh system.");
            return null; // Graceful handling if the file doesn't exist
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            SystemState state = (SystemState) ois.readObject();
            System.out.println("[System] State successfully restored from " + DATA_FILE);
            return state;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("[Error] Data file corrupted or missing class. Initializing fresh system.");
            return null; // Graceful handling on corruption
        }
    }
}

/**
 * MAIN CLASS - UseCase12DataPersistenceRecovery
 */
public class BookMyStayApp {

    public static void main(String[] args) {
        System.out.println("--- Book My Stay: Use Case 12 (Data Persistence & Recovery) ---\n");

        PersistenceService persistenceService = new PersistenceService();
        RoomInventory inventory;
        BookingHistory history;

        // --- PHASE 1: Try to load existing state ---
        SystemState recoveredState = persistenceService.loadState();

        if (recoveredState != null) {
            // State recovered successfully
            inventory = recoveredState.getInventory();
            history = recoveredState.getHistory();
        } else {
            // First run or file missing: Initialize default state
            inventory = new RoomInventory();
            history = new BookingHistory();
        }

        System.out.println("\n--- Current System State ---");
        inventory.displayInventory();
        history.displayHistory();

        // --- PHASE 2: Simulate Business Operations ---
        // We only add new data if it's a fresh system, to clearly see the before/after effect.
        if (recoveredState == null) {
            System.out.println("\n[Action] Processing new reservations...");

            inventory.decrementInventory("Single");
            history.addReservation(new Reservation("RES101", "Alice", "Single"));

            inventory.decrementInventory("Double");
            history.addReservation(new Reservation("RES102", "Bob", "Double"));

            System.out.println("\n--- State After Operations ---");
            inventory.displayInventory();
            history.displayHistory();

            // --- PHASE 3: Save state (Simulate shutdown) ---
            SystemState currentState = new SystemState(inventory, history);
            persistenceService.saveState(currentState);

            System.out.println("\n[Note] Run the program again to see the state instantly recover instead of resetting!");
        } else {
            System.out.println("\n[Note] You are looking at recovered data! The application successfully " +
                    "remembered the state from the previous run.");
        }
    }
}