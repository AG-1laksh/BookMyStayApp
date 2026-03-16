import java.util.HashMap;
import java.util.Map;

/**
 * Abstract class representing a generalized Room.
 * Maintains domain characteristics separately from system state (inventory).
 */
abstract class Room {
    private String roomType;
    private int numberOfBeds;
    private double price;

    public Room(String roomType, int numberOfBeds, double price) {
        this.roomType = roomType;
        this.numberOfBeds = numberOfBeds;
        this.price = price;
    }

    public String getRoomType() { return roomType; }
    public void displayDetails() {
        System.out.printf("%-15s | Beds: %d | Price: $%.2f%n", roomType, numberOfBeds, price);
    }
}

class SingleRoom extends Room {
    public SingleRoom() { super("Single Room", 1, 100.00); }
}

class DoubleRoom extends Room {
    public DoubleRoom() { super("Double Room", 2, 150.00); }
}

class SuiteRoom extends Room {
    public SuiteRoom() { super("Suite Room", 3, 350.00); }
}

/**
 * RoomInventory acts as the Single Source of Truth for room availability.
 * Demonstrates Encapsulation of Inventory Logic and HashMap usage.
 */
class RoomInventory {
    // HashMap provides O(1) average time complexity for lookups and updates
    private Map<String, Integer> availabilityMap;

    public RoomInventory() {
        this.availabilityMap = new HashMap<>();
    }

    /**
     * Registers a room type with an initial available count.
     */
    public void registerRoomType(String roomType, int count) {
        availabilityMap.put(roomType, count);
    }

    /**
     * Retrieves the current availability for a specific room type.
     */
    public int getAvailability(String roomType) {
        return availabilityMap.getOrDefault(roomType, 0);
    }

    /**
     * Controlled update to room availability. Ensures state consistency.
     */
    public boolean updateAvailability(String roomType, int countChange) {
        if (!availabilityMap.containsKey(roomType)) {
            System.out.println("Error: Room type not found in inventory.");
            return false;
        }

        int currentAvailability = availabilityMap.get(roomType);
        int newAvailability = currentAvailability + countChange;

        if (newAvailability < 0) {
            System.out.println("Error: Cannot reduce availability below zero for " + roomType);
            return false;
        }

        availabilityMap.put(roomType, newAvailability);
        return true;
    }

    /**
     * Displays the centralized inventory state.
     */
    public void displayInventory() {
        System.out.println("\n--- Current Centralized Inventory ---");
        for (Map.Entry<String, Integer> entry : availabilityMap.entrySet()) {
            System.out.printf("%-15s : %d available%n", entry.getKey(), entry.getValue());
        }
    }
}

/**
 * The main entry point for Use Case 3.
 * Demonstrates the transition to a centralized HashMap-based inventory.
 * * @author Your Name
 * @version 3.0
 */
public class BookMyStayApp {

    public static void main(String[] args) {
        System.out.println("=========================================");
        System.out.println(" Book My Stay - Centralized Inventory    ");
        System.out.println("=========================================\n");

        // 1. Initialize domain models (What a room is)
        Room singleRoom = new SingleRoom();
        Room doubleRoom = new DoubleRoom();
        Room suiteRoom = new SuiteRoom();

        // 2. Initialize inventory component (How many are available)
        RoomInventory inventory = new RoomInventory();

        // Register room types into the centralized HashMap
        inventory.registerRoomType(singleRoom.getRoomType(), 10);
        inventory.registerRoomType(doubleRoom.getRoomType(), 5);
        inventory.registerRoomType(suiteRoom.getRoomType(), 2);

        // Display initial state
        inventory.displayInventory();

        System.out.println("\n--- Processing Booking Updates ---");
        System.out.println("Booking 2 Single Rooms...");
        // Controlled update: decreasing availability by 2
        inventory.updateAvailability(singleRoom.getRoomType(), -2);

        System.out.println("Booking 1 Suite Room...");
        inventory.updateAvailability(suiteRoom.getRoomType(), -1);

        // Display updated state to prove single source of truth
        inventory.displayInventory();

        System.out.println("\n=========================================");
        System.out.println("Application terminated.");
    }
}