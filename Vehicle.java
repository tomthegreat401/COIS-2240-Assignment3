public abstract class Vehicle {
    private String licensePlate;
    private String make;
    private String model;
    private int year;
    private VehicleStatus status;

    public enum VehicleStatus { AVAILABLE, RESERVED, RENTED, MAINTENANCE, OUTOFSERVICE }

    public Vehicle(String make, String model, int year) {
        this.make = capitalize(make);
        this.model = capitalize(model);
        this.year = year;
        this.status = VehicleStatus.AVAILABLE;
        this.licensePlate = null;
    }

    public Vehicle() {
        this(null, null, 0);
    }

    // Helper method
    private String capitalize(String input) {
        if (input == null || input.isEmpty()) {
            return null;
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }
    
    public void setLicensePlate(String plate) {
        this.licensePlate = plate == null ? null : plate.toUpperCase();
        if (!isValidPlate(plate)) {
            throw new IllegalArgumentException("Invalid license plate format");
        }
        this.licensePlate = plate == null ? null : plate.toUpperCase();
    }
    private boolean isValidPlate(String plate) {
        if (plate == null || plate.trim().isEmpty()) {
            return false;
        }
        // Pattern: exactly 3 letters followed by exactly 3 numbers
        return plate.matches("[A-Za-z]{3}\\d{3}$");
    }

    public void setStatus(VehicleStatus status) {
    	this.status = status;
    }

    public String getLicensePlate() { return licensePlate; }

    public String getMake() { return make; }

    public String getModel() { return model;}

    public int getYear() { return year; }

    public VehicleStatus getStatus() { return status; }

    public String getInfo() {
        return "| " + licensePlate + " | " + make + " | " + model + " | " + year + " | " + status + " |";
    }
    
    public abstract String toFileString();
    
    public void rentVehicle() {
        setStatus(VehicleStatus.RENTED);
        System.out.println("Truck " + getLicensePlate() + " has been rented.");
    }

    public void returnVehicle() {
        setStatus(VehicleStatus.AVAILABLE);
        System.out.println("Truck " + getLicensePlate() + " has been returned.");
    }


}
