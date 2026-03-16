import java.util.HashMap;
import java.util.Map;

/**
 * Domain Models representing 'What a room is'.
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
    public int getNumberOfBeds() { return numberOfBeds; }
    public double getPrice() { return price; }
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
 * RoomInventory acts as the Single Source of Truth for system state.
 */
class RoomInventory {
    private Map<String, Integer> availabilityMap;

    public RoomInventory() {
        this.availabilityMap = new HashMap<>();
    }

    public void registerRoomType(String roomType, int count) {
        availabilityMap.put(roomType, count);
    }

    // Read-only method used by SearchService
    public int getAvailability(String roomType) {
        return availabilityMap.getOrDefault(roomType, 0);
    }

    // Write method (Intentionally NOT used by SearchService)
    public boolean updateAvailability(String roomType, int countChange) {
        int currentAvailability = getAvailability(roomType);
        int newAvailability = currentAvailability + countChange;
        if (newAvailability < 0) return false;

        availabilityMap.put(roomType, newAvailability);
        return true;
    }
}

/**
 * SearchService handles read-only access to inventory and room information.
 * Demonstrates Separation of Concerns and Defensive Programming.
 */
class SearchService {
    private RoomInventory inventory;
    private Room[] availableRoomModels;

    public SearchService(RoomInventory inventory, Room[] availableRoomModels) {
        this.inventory = inventory;
        this.availableRoomModels = availableRoomModels;
    }

    /**
     * Searches and displays rooms that have an availability greater than zero.
     * System state remains completely unchanged.
     */
    public void displayAvailableRooms() {
        System.out.println("--- Search Results: Available Rooms ---");

        boolean foundAnyRoom = false;

        // Defensive Programming: Check against null arrays
        if (availableRoomModels == null || availableRoomModels.length == 0) {
            System.out.println("System Error: No room models configured.");
            return;
        }

        for (Room room : availableRoomModels) {
            // Read-Only Access
            int availableCount = inventory.getAvailability(room.getRoomType());

            // Validation Logic: Exclude room types with zero availability
            if (availableCount > 0) {
                System.out.printf("%-15s | Available: %d | Beds: %d | Price: $%.2f%n",
                        room.getRoomType(), availableCount, room.getNumberOfBeds(), room.getPrice());
                foundAnyRoom = true;
            }
        }

        if (!foundAnyRoom) {
            System.out.println("We're sorry, there are currently no rooms available.");
        }
    }
}

/**
 * The main entry point for Use Case 4.
 * Demonstrates searching for rooms without mutating state.
 * * @author Your Name
 * @version 4.0
 */
public class BookMyStayApp {

    public static void main(String[] args) {
        System.out.println("=========================================");
        System.out.println(" Book My Stay - Room Search Service      ");
        System.out.println("=========================================\n");

        // 1. Initialize Domain Models
        Room[] hotelRooms = {
                new SingleRoom(),
                new DoubleRoom(),
                new SuiteRoom()
        };

        // 2. Initialize and Populate Inventory
        RoomInventory inventory = new RoomInventory();
        inventory.registerRoomType("Single Room", 5);
        inventory.registerRoomType("Double Room", 0); // Intentionally set to 0 to test filtering
        inventory.registerRoomType("Suite Room", 2);

        // 3. Initialize Search Service (injecting dependencies)
        SearchService searchService = new SearchService(inventory, hotelRooms);

        // Guest initiates a room search request
        System.out.println("Guest Request: \"Show me available rooms.\"\n");

        // 4. Perform Search
        searchService.displayAvailableRooms();

        System.out.println("\n=========================================");
        System.out.println("Application terminated.");
    }
}