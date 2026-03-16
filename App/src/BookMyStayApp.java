import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 * Represents a guest's intent to book a room.
 */
class Reservation {
    private String guestName;
    private String requestedRoomType;

    public Reservation(String guestName, String requestedRoomType) {
        this.guestName = guestName;
        this.requestedRoomType = requestedRoomType;
    }

    public String getGuestName() { return guestName; }
    public String getRequestedRoomType() { return requestedRoomType; }
}

/**
 * Inventory Service manages room availability state and assigned rooms.
 * Demonstrates mapping Room Types to Assigned Rooms using Sets to prevent double-booking.
 */
class InventoryService {
    private Map<String, Integer> availableCounts;
    // Map to track allocated room IDs per room type using a Set for uniqueness
    private Map<String, Set<String>> allocatedRooms;

    public InventoryService() {
        this.availableCounts = new HashMap<>();
        this.allocatedRooms = new HashMap<>();
    }

    public void registerRoomType(String roomType, int count) {
        availableCounts.put(roomType, count);
        allocatedRooms.put(roomType, new HashSet<>());
    }

    public boolean isAvailable(String roomType) {
        return availableCounts.getOrDefault(roomType, 0) > 0;
    }

    /**
     * Atomic Logical Operation: Generates ID, updates Set, and decrements inventory.
     */
    public String allocateRoom(String roomType) {
        if (!isAvailable(roomType)) {
            return null; // Allocation failed due to lack of availability
        }

        Set<String> assignedIds = allocatedRooms.get(roomType);

        // Generate a unique room ID (e.g., "Single Room-101")
        int roomNumber = 101 + assignedIds.size();
        String uniqueRoomId = roomType.split(" ")[0] + "-" + roomNumber;

        // Uniqueness Enforcement: Add to Set and decrement inventory immediately
        assignedIds.add(uniqueRoomId);
        availableCounts.put(roomType, availableCounts.get(roomType) - 1);

        return uniqueRoomId;
    }

    public void displayInventoryState() {
        System.out.println("\n--- Current Inventory State ---");
        for (String roomType : availableCounts.keySet()) {
            System.out.printf("%-15s | Available: %d | Allocated IDs: %s%n",
                    roomType, availableCounts.get(roomType), allocatedRooms.get(roomType));
        }
    }
}

/**
 * Booking Service processes queued requests and performs room allocation.
 */
class BookingService {
    private InventoryService inventory;

    public BookingService(InventoryService inventory) {
        this.inventory = inventory;
    }

    /**
     * Processes requests from the queue in FIFO order.
     */
    public void processQueue(Queue<Reservation> queue) {
        System.out.println("\n--- Processing Booking Queue ---");

        if (queue.isEmpty()) {
            System.out.println("No requests to process.");
            return;
        }

        while (!queue.isEmpty()) {
            // Dequeue the next request (FIFO)
            Reservation request = queue.poll();
            String roomType = request.getRequestedRoomType();

            System.out.print("Processing request for " + request.getGuestName() + " (" + roomType + ")... ");

            // Attempt allocation
            String assignedRoomId = inventory.allocateRoom(roomType);

            if (assignedRoomId != null) {
                System.out.println("SUCCESS! Assigned Room ID: " + assignedRoomId);
            } else {
                System.out.println("FAILED. No available rooms of this type.");
            }
        }
    }
}

/**
 * The main entry point for Use Case 6.
 * * @author Your Name
 * @version 6.0
 */
public class BookMyStayApp {

    public static void main(String[] args) {
        System.out.println("=========================================");
        System.out.println(" Book My Stay - Reservation & Allocation ");
        System.out.println("=========================================\n");

        // 1. Initialize Inventory
        InventoryService inventory = new InventoryService();
        inventory.registerRoomType("Single Room", 2); // Only 2 single rooms available
        inventory.registerRoomType("Double Room", 1);
        inventory.registerRoomType("Suite Room", 1);

        // Display initial inventory
        inventory.displayInventoryState();

        // 2. Initialize Queue and Load Requests (Simulating Use Case 5)
        Queue<Reservation> bookingQueue = new LinkedList<>();
        bookingQueue.offer(new Reservation("Alice Smith", "Single Room"));
        bookingQueue.offer(new Reservation("Bob Johnson", "Single Room"));
        bookingQueue.offer(new Reservation("Charlie Davis", "Single Room")); // Will fail (only 2 available)
        bookingQueue.offer(new Reservation("Diana Prince", "Suite Room"));

        // 3. Initialize Booking Service and Process Queue
        BookingService bookingService = new BookingService(inventory);
        bookingService.processQueue(bookingQueue);

        // 4. Display Final Inventory State to prove synchronization
        inventory.displayInventoryState();

        System.out.println("\n=========================================");
        System.out.println("Application terminated.");
    }
}