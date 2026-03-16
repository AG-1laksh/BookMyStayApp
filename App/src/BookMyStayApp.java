/**
 * Abstract class representing a generalized Room.
 * Demonstrates Abstraction and Encapsulation.
 */
abstract class Room {
    // Encapsulated attributes
    private String roomType;
    private int numberOfBeds;
    private double price;

    public Room(String roomType, int numberOfBeds, double price) {
        this.roomType = roomType;
        this.numberOfBeds = numberOfBeds;
        this.price = price;
    }

    // Getters for encapsulated fields
    public String getRoomType() { return roomType; }
    public int getNumberOfBeds() { return numberOfBeds; }
    public double getPrice() { return price; }

    // Common behavior for all room types
    public void displayDetails() {
        System.out.printf("%-15s | Beds: %d | Price: $%.2f%n", roomType, numberOfBeds, price);
    }
}

/**
 * Concrete implementation of a Single Room.
 * Demonstrates Inheritance.
 */
class SingleRoom extends Room {
    public SingleRoom() {
        super("Single Room", 1, 100.00);
    }
}

/**
 * Concrete implementation of a Double Room.
 */
class DoubleRoom extends Room {
    public DoubleRoom() {
        super("Double Room", 2, 150.00);
    }
}

/**
 * Concrete implementation of a Suite Room.
 */
class SuiteRoom extends Room {
    public SuiteRoom() {
        super("Suite Room", 3, 350.00);
    }
}

/**
 * The main entry point for Use Case 2.
 * Initializes room types and manages static availability.
 * * @author Your Name
 * @version 2.0
 */
public class BookMyStayApp {

    public static void main(String[] args) {
        System.out.println("=========================================");
        System.out.println("      Book My Stay - Room Inventory      ");
        System.out.println("=========================================\n");

        // 1. Initialize room objects using Polymorphism
        // The reference type is Room, but the object is a specific subclass.
        Room singleRoom = new SingleRoom();
        Room doubleRoom = new DoubleRoom();
        Room suiteRoom = new SuiteRoom();

        // 2. Static Availability Representation
        // Storing availability in simple variables instead of data structures
        int singleRoomAvailability = 10;
        int doubleRoomAvailability = 5;
        int suiteRoomAvailability = 2;

        // 3. Display room details and their corresponding hardcoded availability
        System.out.println("--- Room Types & Pricing ---");
        singleRoom.displayDetails();
        doubleRoom.displayDetails();
        suiteRoom.displayDetails();

        System.out.println("\n--- Current Availability ---");
        System.out.println(singleRoom.getRoomType() + "s available: " + singleRoomAvailability);
        System.out.println(doubleRoom.getRoomType() + "s available: " + doubleRoomAvailability);
        System.out.println(suiteRoom.getRoomType() + "s available: " + suiteRoomAvailability);

        System.out.println("\n=========================================");
        System.out.println("Application terminated.");
    }
}