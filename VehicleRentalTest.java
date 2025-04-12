import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

class VehicleRentalTest {
    private Vehicle testVehicle;

    @BeforeEach
    void setUp() {
        testVehicle = new Vehicle("Toyota", "Camry", 2020) {
            @Override
            public String toFileString() {
                return "";
            }
        };
    }

    @Test
    void testLicensePlateValidation() {
        // Test valid plates (3 letters + 3 numbers)
        String[] validPlates = {"AAA100", "ABC567", "ZZZ999"};
        for (String plate : validPlates) {
            assertDoesNotThrow(() -> testVehicle.setLicensePlate(plate));
            assertEquals(plate.toUpperCase(), testVehicle.getLicensePlate());
        }

        // Test invalid plates
        assertThrows(IllegalArgumentException.class, () -> testVehicle.setLicensePlate(null));
        assertThrows(IllegalArgumentException.class, () -> testVehicle.setLicensePlate(""));
        assertThrows(IllegalArgumentException.class, () -> testVehicle.setLicensePlate("   "));
        assertThrows(IllegalArgumentException.class, () -> testVehicle.setLicensePlate("AAA1000"));
        assertThrows(IllegalArgumentException.class, () -> testVehicle.setLicensePlate("ZZZ99"));
        assertThrows(IllegalArgumentException.class, () -> testVehicle.setLicensePlate("123ABC"));
        assertThrows(IllegalArgumentException.class, () -> testVehicle.setLicensePlate("A1B2C3"));
        
        // Verify exception message
        Exception exception = assertThrows(IllegalArgumentException.class, 
            () -> testVehicle.setLicensePlate("INVALID"));
        assertTrue(exception.getMessage().contains("Invalid license plate format"));
    }

    @Test
    void testLicensePlateCaseConversion() {
        testVehicle.setLicensePlate("abc123");
        assertEquals("ABC123", testVehicle.getLicensePlate());
    }
    
    @Test
    void testRentAndReturnVehicle() {
        RentalSystem rentalSystem = RentalSystem.getInstance();

        // Create a rentable vehicle and a customer
        Vehicle car = new Car("Toyota", "Camry", 2020, 5);
        car.setLicensePlate("CAR123");

        Customer customer = new Customer(1, "John Doe");

        // Add to system
        rentalSystem.addVehicle(car);
        rentalSystem.addCustomer(customer);

        // Initial state should be AVAILABLE
        assertEquals(Vehicle.VehicleStatus.AVAILABLE, car.getStatus());

        // Rent vehicle - should succeed
        boolean rentSuccess = rentalSystem.rentVehicle(car, customer, LocalDate.now(), 100.0);
        assertTrue(rentSuccess);
        assertEquals(Vehicle.VehicleStatus.RENTED, car.getStatus());

        // Try renting again - should fail
        boolean secondRentAttempt = rentalSystem.rentVehicle(car, customer, LocalDate.now(), 100.0);
        assertFalse(secondRentAttempt);

        // Return vehicle - should succeed
        boolean returnSuccess = rentalSystem.returnVehicle(car, customer, LocalDate.now(), 20.0);
        assertTrue(returnSuccess);
        assertEquals(Vehicle.VehicleStatus.AVAILABLE, car.getStatus());

        // Try returning again - should fail
        boolean secondReturnAttempt = rentalSystem.returnVehicle(car, customer, LocalDate.now(), 20.0);
        assertFalse(secondReturnAttempt);
    }
}