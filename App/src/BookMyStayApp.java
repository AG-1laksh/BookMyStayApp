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
 * Represents a confirmed booking.
 */
class Reservation {
    private String guestName;
    private String roomType;

    public Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getGuestName() {
        return guestName;
    }

    public String getRoomType() {
        return roomType;
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
 * CLASS - BookingHistory
 * Stores confirmed reservations in order.
 */
class BookingHistory {
    private List<Reservation> confirmedReservations;

    public BookingHistory() {
        confirmedReservations = new ArrayList<>();
    }

    public void addReservation(Reservation reservation) {
        confirmedReservations.add(reservation);
    }

    public List<Reservation> getConfirmedReservations() {
        return confirmedReservations;
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
            System.out.println(
                    "Guest: " + res.getGuestName() +
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
 * MAIN CLASS - BookMyStay
 */
public class BookMyStayApp {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // --- PART 1: Booking Validation ---
        System.out.println("--- Booking Validation ---");
        RoomInventory inventory = new RoomInventory();
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

        // --- PART 2: Booking History Reporting ---
        System.out.println("\n--- Booking History and Reporting ---");
        BookingHistory history = new BookingHistory();
        history.addReservation(new Reservation("Abhi", "Single"));
        history.addReservation(new Reservation("Subha", "Double"));
        history.addReservation(new Reservation("Vanmathi", "Suite"));

        BookingReportService reportService = new BookingReportService();
        reportService.generateReport(history);

        // --- PART 3: Add-On Service Selection ---
        System.out.println("\n--- Add-On Service Selection ---");
        AddOnServiceManager manager = new AddOnServiceManager();
        String reservationId = "RES123";

        AddOnService breakfast = new AddOnService("Breakfast", 500.0);
        AddOnService spa = new AddOnService("Spa", 1000.0);

        manager.addService(reservationId, breakfast);
        manager.addService(reservationId, spa);

        double totalCost = manager.calculateTotalServiceCost(reservationId);

        System.out.println("Reservation ID: " + reservationId);
        System.out.println("Total Add-On Cost: " + totalCost);
    }
}