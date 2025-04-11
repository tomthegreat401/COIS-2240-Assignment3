import java.util.List;
import java.time.LocalDate;
import java.util.ArrayList;
import java.io.*;

public class RentalSystem {
    // Singleton instance
    private static RentalSystem instance;

    // Data collections
    private List<Vehicle> vehicles = new ArrayList<>();
    private List<Customer> customers = new ArrayList<>();
    private List<RentalRecord> rentalRecords = new ArrayList<>(); // Added to track all records
    private RentalHistory rentalHistory = new RentalHistory();

    private RentalSystem() {
    	vehicles = new ArrayList<>();
        customers = new ArrayList<>();
        rentalHistory = new HashMap<>();
        loadData(); // Load saved data on startup
    }       

    public static RentalSystem getInstance() {
        if (instance == null) {
            instance = new RentalSystem();
        }
        return instance;
    }

    // Save methods
    private void saveVehicle(Vehicle vehicle) {
        try (PrintWriter out = new PrintWriter(new FileWriter("vehicles.txt", true))) {
            out.println(vehicle.toFileString());
        } catch (IOException e) {
            System.err.println("Error saving vehicle: " + e.getMessage());
        }
    }

    private void saveCustomer(Customer customer) {
        try (PrintWriter out = new PrintWriter(new FileWriter("customers.txt", true))) {
            out.println(customer.toFileString());
        } catch (IOException e) {
            System.err.println("Error saving customer: " + e.getMessage());
        }
    }

    private void saveRecord(RentalRecord record) {
        try (PrintWriter out = new PrintWriter(new FileWriter("rental_records.txt", true))) {
            out.println(record.toFileString());
        } catch (IOException e) {
            System.err.println("Error saving rental record: " + e.getMessage());
        }
    }
    
    private void loadData() {
        loadVehicles();
        loadCustomers();
        loadRentalRecords();
    }

    private void loadVehicles() {
        File file = new File("vehicles.txt");
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                // Assuming: id, make, model, year
                String id = parts[0];
                String make = parts[1];
                String model = parts[2];
                int year = Integer.parseInt(parts[3]);

                Vehicle vehicle = new Vehicle(id, make, model, year);
                vehicles.add(vehicle);
            }
        } catch (IOException e) {
            System.err.println("Error loading vehicles: " + e.getMessage());
        }
    }
    
    private void loadCustomers() {
        File file = new File("customers.txt");
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                // Assuming: id, name, phoneNumber
                String id = parts[0];
                String name = parts[1];
                String phone = parts[2];

                Customer customer = new Customer(id, name, phone);
                customers.add(customer);
            }
        } catch (IOException e) {
            System.err.println("Error loading customers: " + e.getMessage());
        }
    }

    private void loadRentalRecords() {
        File file = new File("rental_records.txt");
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                // Assuming: vehicleId, customerId, rentDate, returnDate
                String vehicleId = parts[0];
                String customerId = parts[1];
                String rentDate = parts[2];
                String returnDate = parts[3];

                RentalRecord record = new RentalRecord(vehicleId, customerId, rentDate, returnDate);

                rentalHistory.computeIfAbsent(vehicleId, k -> new ArrayList<>()).add(record);
            }
        } catch (IOException e) {
            System.err.println("Error loading rental records: " + e.getMessage());
        }
    }


    // Business methods
    public void addVehicle(Vehicle vehicle) {
        vehicles.add(vehicle);
        saveVehicle(vehicle);
    }

    public void addCustomer(Customer customer) {
        customers.add(customer);
        saveCustomer(customer);
    }

    public void rentVehicle(Vehicle vehicle, Customer customer, LocalDate date, double amount) {
        if (vehicle.getStatus() == Vehicle.VehicleStatus.AVAILABLE) {
            vehicle.setStatus(Vehicle.VehicleStatus.RENTED);
            RentalRecord record = new RentalRecord(vehicle, customer, date, amount, "RENT");
            rentalRecords.add(record); // Store the record
            rentalHistory.addRecord(record);
            saveRecord(record); // Save the specific record
            System.out.println("Vehicle rented to " + customer.getCustomerName());
        } else {
            System.out.println("Vehicle is not available for renting.");
        }
    }

    public void returnVehicle(Vehicle vehicle, Customer customer, LocalDate date, double extraFees) {
        if (vehicle.getStatus() == Vehicle.VehicleStatus.RENTED) {
            vehicle.setStatus(Vehicle.VehicleStatus.AVAILABLE);
            RentalRecord record = new RentalRecord(vehicle, customer, date, extraFees, "RETURN");
            rentalRecords.add(record); // Store the record
            rentalHistory.addRecord(record);
            saveRecord(record); // Save the specific record
            System.out.println("Vehicle returned by " + customer.getCustomerName());
        } else {
            System.out.println("Vehicle is not rented.");
        }
    }

    // Rest of your display methods remain unchanged...
    public void displayVehicles(boolean onlyAvailable) {
        System.out.println("|     Type         |\tPlate\t|\tMake\t|\tModel\t|\tYear\t|");
        System.out.println("---------------------------------------------------------------------------------");

        for (Vehicle v : vehicles) {
            if (!onlyAvailable || v.getStatus() == Vehicle.VehicleStatus.AVAILABLE) {
                System.out.println("|     " + (v instanceof Car ? "Car          " : "Motorcycle   ") + 
                                 "|\t" + v.getLicensePlate() + "\t|\t" + v.getMake() + 
                                 "\t|\t" + v.getModel() + "\t|\t" + v.getYear() + "\t|");
            }
        }
        System.out.println();
    }

    public void displayAllCustomers() {
        for (Customer c : customers) {
            System.out.println("  " + c.toString());
        }
    }

    public void displayRentalHistory() {
        for (RentalRecord record : rentalHistory.getRentalHistory()) {
            System.out.println(record.toString());
        }
    }

    public Vehicle findVehicleByPlate(String plate) {
        for (Vehicle v : vehicles) {
            if (v.getLicensePlate().equalsIgnoreCase(plate)) {
                return v;
            }
        }
        return null;
    }

    public Customer findCustomerById(String id) {
        for (Customer c : customers) {
            if (c.getCustomerId() == Integer.parseInt(id)) {
                return c;
            }
        }
        return null;
    }
}