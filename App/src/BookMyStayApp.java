import java.util.*;
import java.util.concurrent.*;

/**
 * CLASS - RoomInventory
 * Maintains available room types and their current availability counts.
 * Uses synchronized methods to ensure Thread Safety.
 */
class RoomInventory {
    private final Map<String, Integer> inventoryCounts;

    public RoomInventory() {
        inventoryCounts = new HashMap<>();
        // Intentionally low inventory to force contention among multiple threads
        inventoryCounts.put("Single", 3);
        inventoryCounts.put("Double", 2);
        inventoryCounts.put("Suite", 1);
    }

    /**
     * CRITICAL SECTION: Synchronized to prevent Race Conditions.
     * Only one thread can execute this method at a time for a given RoomInventory instance.
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
 * CLASS - BookingRequest
 * Represents an individual guest's request to book a room.
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
 * CLASS - BookingProcessor
 * A Runnable task that processes a booking request concurrently.
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
        System.out.println(Thread.currentThread().getName() + " processing request for "
                + request.getGuestName() + " (" + request.getRequestedRoomType() + ")");

        boolean success = inventory.allocateRoom(request.getRequestedRoomType());

        if (success) {
            System.out.println("--> SUCCESS: " + request.getGuestName() + " successfully booked a "
                    + request.getRequestedRoomType() + " room.");
        } else {
            System.out.println("--> FAILED: " + request.getGuestName() + " could not book a "
                    + request.getRequestedRoomType() + " room. (Sold Out)");
        }
    }
}

/**
 * MAIN CLASS - UseCase11ConcurrentBookingSimulation
 */
public class BookMyStayApp {

    public static void main(String[] args) {
        System.out.println("--- Book My Stay: Use Case 11 (Concurrent Booking Simulation) ---\n");

        RoomInventory sharedInventory = new RoomInventory();
        System.out.println("Initial State:");
        sharedInventory.displayInventory();
        System.out.println("\nStarting concurrent booking requests...\n");

        // Create a list of concurrent booking requests (More requests than available inventory)
        List<BookingRequest> requests = Arrays.asList(
                new BookingRequest("Alice", "Single"),
                new BookingRequest("Bob", "Single"),
                new BookingRequest("Charlie", "Single"),
                new BookingRequest("Diana", "Single"), // This one should fail (Only 3 Singles)
                new BookingRequest("Eve", "Suite"),
                new BookingRequest("Frank", "Suite")   // This one should fail (Only 1 Suite)
        );

        // Using an ExecutorService to manage a thread pool for our concurrent requests
        ExecutorService executor = Executors.newFixedThreadPool(requests.size());

        for (BookingRequest request : requests) {
            BookingProcessor processor = new BookingProcessor(request, sharedInventory);
            executor.submit(processor);
        }

        // Shut down the executor and wait for all threads to finish
        executor.shutdown();
        try {
            executor.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            System.err.println("Execution interrupted.");
        }

        System.out.println("\nAll concurrent requests processed.");
        System.out.println("\nFinal State:");
        sharedInventory.displayInventory();
    }
}