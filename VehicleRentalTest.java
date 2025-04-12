import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
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
        rentalSystem.resetForTesting(); // Clear data before test

        Vehicle car = new Car("Toyota", "Camry", 2020, 5);
        car.setLicensePlate("CAR123");

        Customer customer = new Customer(1, "John Doe");

        rentalSystem.addVehicle(car);
        rentalSystem.addCustomer(customer);

        assertEquals(Vehicle.VehicleStatus.AVAILABLE, car.getStatus());

        boolean rentSuccess = rentalSystem.rentVehicle(car, customer, LocalDate.now(), 100.0);
        assertTrue(rentSuccess);
        assertEquals(Vehicle.VehicleStatus.RENTED, car.getStatus());

        boolean secondRentAttempt = rentalSystem.rentVehicle(car, customer, LocalDate.now(), 100.0);
        assertFalse(secondRentAttempt);

        boolean returnSuccess = rentalSystem.returnVehicle(car, customer, LocalDate.now(), 20.0);
        assertTrue(returnSuccess);
        assertEquals(Vehicle.VehicleStatus.AVAILABLE, car.getStatus());

        boolean secondReturnAttempt = rentalSystem.returnVehicle(car, customer, LocalDate.now(), 20.0);
        assertFalse(secondReturnAttempt);
    }

    @Test
    void testSingletonRentalSystem() throws Exception {
        Constructor<RentalSystem> constructor = RentalSystem.class.getDeclaredConstructor();
        constructor.setAccessible(true); // allows access to private constructor

        int modifiers = constructor.getModifiers();
        assertTrue(Modifier.isPrivate(modifiers), "Constructor should be private");

        RentalSystem instance = RentalSystem.getInstance();
        assertNotNull(instance, "Instance should not be null");
    }
}