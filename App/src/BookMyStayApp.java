import java.util.LinkedList;
import java.util.Queue;

/**
 * Reservation class represents a guest's intent to book a room.
 * This encapsulates the request details before processing.
 */
class Reservation {
    private String guestName;
    private String requestedRoomType;

    public Reservation(String guestName, String requestedRoomType) {
        this.guestName = guestName;
        this.requestedRoomType = requestedRoomType;
    }

    public String getGuestName() {
        return guestName;
    }

    public String getRequestedRoomType() {
        return requestedRoomType;
    }

    @Override
    public String toString() {
        return "Guest: " + guestName + " | Requested: " + requestedRoomType;
    }
}

/**
 * BookingRequestQueue manages incoming booking requests.
 * Demonstrates the Queue Data Structure and the FIFO Principle.
 * Note: Decouples request intake from allocation (no inventory mutation here).
 */
class BookingRequestQueue {
    // Using a LinkedList as the underlying implementation for the Queue interface
    private Queue<Reservation> queue;

    public BookingRequestQueue() {
        this.queue = new LinkedList<>();
    }

    /**
     * Accepts a booking request and adds it to the end of the queue.
     * Preserves the order in which requests arrive.
     */
    public void addRequest(Reservation reservation) {
        queue.offer(reservation); // .offer() safely adds to the tail of the queue
        System.out.println("[Intake] Received request -> " + reservation.toString());
    }

    /**
     * Displays the current state of the queue to verify FIFO ordering.
     */
    public void displayQueue() {
        System.out.println("\n--- Current Booking Queue (FIFO Order) ---");
        if (queue.isEmpty()) {
            System.out.println("The queue is currently empty.");
            return;
        }

        int position = 1;
        // Iterating through the queue preserves insertion order automatically
        for (Reservation res : queue) {
            System.out.println(position + ". " + res.toString());
            position++;
        }
        System.out.println("------------------------------------------");
        System.out.println("Total requests waiting for processing: " + queue.size());
    }
}

/**
 * The main entry point for Use Case 5.
 * Demonstrates First-Come-First-Served booking request handling.
 * * @author Your Name
 * @version 5.0
 */
public class BookMyStayApp {

    public static void main(String[] args) {
        System.out.println("=========================================");
        System.out.println(" Book My Stay - Booking Request Intake   ");
        System.out.println("=========================================\n");

        // 1. Initialize the Booking Request Queue
        BookingRequestQueue bookingQueue = new BookingRequestQueue();

        // 2. Simulate guests submitting booking requests during peak demand
        System.out.println("Simulating incoming booking requests...");

        Reservation req1 = new Reservation("Alice Smith", "Suite Room");
        Reservation req2 = new Reservation("Bob Johnson", "Single Room");
        Reservation req3 = new Reservation("Charlie Davis", "Double Room");
        Reservation req4 = new Reservation("Diana Prince", "Single Room");

        // 3. Add requests to the queue (Decoupled from allocation)
        bookingQueue.addRequest(req1);
        bookingQueue.addRequest(req2);
        bookingQueue.addRequest(req3);
        bookingQueue.addRequest(req4);

        // 4. Display the queue to prove requests are stored in arrival order
        bookingQueue.displayQueue();

        System.out.println("\nNotice: No inventory mutation has occurred yet.");
        System.out.println("Requests are simply staged in a fair, First-Come-First-Served order.");
        System.out.println("\n=========================================");
        System.out.println("Application terminated.");
    }
}