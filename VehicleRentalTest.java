import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

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
}