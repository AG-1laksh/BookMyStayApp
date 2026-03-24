import java.util.*;

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
        System.out.println("\nBooking History Report");

        for (Reservation res : history.getConfirmedReservations()) {
            System.out.println(
                    "Guest: " + res.getGuestName() +
                            ", Room Type: " + res.getRoomType()
            );
        }
    }
}

/**
 * MAIN CLASS - BookMyStay
 */
public class BookMyStayApp {

    public static void main(String[] args) {

        System.out.println("Booking History and Reporting\n");

        // Initialize history
        BookingHistory history = new BookingHistory();

        // Add confirmed reservations
        history.addReservation(new Reservation("Abhi", "Single"));
        history.addReservation(new Reservation("Subha", "Double"));
        history.addReservation(new Reservation("Vanmathi", "Suite"));

        // Generate report
        BookingReportService reportService = new BookingReportService();
        reportService.generateReport(history);
    }
}