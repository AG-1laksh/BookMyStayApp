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
 * CLASS - RoomInventory
 * Maintains available room types.
 */
class RoomInventory {

    private Set<String> availableRoomTypes;

    public RoomInventory() {
        availableRoomTypes = new HashSet<>();
        availableRoomTypes.add("Single");
        availableRoomTypes.add("Double");
        availableRoomTypes.add("Suite");
    }

    public boolean isValidRoomType(String roomType) {
        return availableRoomTypes.contains(roomType);
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
 * MAIN CLASS - BookMyStay
 */
public class BookMyStayApp {

    public static void main(String[] args) {

        System.out.println("Booking Validation");

        Scanner scanner = new Scanner(System.in);

        RoomInventory inventory = new RoomInventory();
        ReservationValidator validator = new ReservationValidator();
        BookingRequestQueue bookingQueue = new BookingRequestQueue();

        try {
            // User input
            System.out.print("Enter guest name: ");
            String guestName = scanner.nextLine();

            System.out.print("Enter room type (Single/Double/Suite): ");
            String roomType = scanner.nextLine();

            // Validate input
            validator.validate(guestName, roomType, inventory);

            // If valid → process request
            bookingQueue.addRequest(guestName, roomType);

        } catch (InvalidBookingException e) {
            // Graceful failure
            System.out.println("Booking failed: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }
}