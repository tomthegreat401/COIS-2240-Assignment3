import java.util.*;
import java.time.LocalDate;
import java.io.*;

public class RentalSystem {
    private static RentalSystem instance;

    private List<Vehicle> vehicles;
    private List<Customer> customers;
    private List<RentalRecord> rentalRecords;
    private Map<String, List<RentalRecord>> rentalHistory;

    // Private constructor to prevent instantiation
    private RentalSystem() {
        if (instance != null) {
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }

        vehicles = new ArrayList<>();
        customers = new ArrayList<>();
        rentalRecords = new ArrayList<>();
        rentalHistory = new HashMap<>();
        loadData();
    }

    public static RentalSystem getInstance() {
        if (instance == null) {
            synchronized (RentalSystem.class) {
                if (instance == null) {
                    instance = new RentalSystem();
                }
            }
        }
        return instance;
    }

    // Prevent cloning
    @Override
    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException("Cloning of this singleton class is not allowed.");
    }

    // Prevent deserialization from breaking singleton
    protected Object readResolve() {
        return getInstance();
    }

    // --- Save Methods ---
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

    // --- Load Methods ---
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

                String type = parts[0];
                String plate = parts[1];
                String make = parts[2];
                String model = parts[3];
                int year = Integer.parseInt(parts[4]);

                Vehicle vehicle;
                if (type.equalsIgnoreCase("Car")) {
                    int seats = Integer.parseInt(parts[5]);
                    vehicle = new Car(make, model, year, seats);
                } else if (type.equalsIgnoreCase("Motorcycle")) {
                    boolean hasSidecar = Boolean.parseBoolean(parts[5]);
                    vehicle = new Motorcycle(make, model, year, hasSidecar);
                } else {
                    continue;
                }

                vehicle.setLicensePlate(plate);
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
                int id = Integer.parseInt(parts[0]);
                String name = parts[1];

                Customer customer = new Customer(id, name);
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
                String recordType = parts[0];
                String plate = parts[1];
                String customerId = parts[2];
                LocalDate date = LocalDate.parse(parts[3]);
                double amount = Double.parseDouble(parts[4]);

                Vehicle vehicle = findVehicleByPlate(plate);
                Customer customer = findCustomerById(customerId);

                if (vehicle != null && customer != null) {
                    RentalRecord record = new RentalRecord(vehicle, customer, date, amount, recordType);
                    rentalRecords.add(record);
                    rentalHistory.computeIfAbsent(plate, k -> new ArrayList<>()).add(record);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading rental records: " + e.getMessage());
        }
    }

    // --- Business Logic ---
    public boolean addVehicle(Vehicle vehicle) {
        if (findVehicleByPlate(vehicle.getLicensePlate()) != null) {
            System.out.println("Vehicle with plate " + vehicle.getLicensePlate() + " already exists.");
            return false;
        }
        vehicles.add(vehicle);
        saveVehicle(vehicle);
        return true;
    }

    public boolean addCustomer(Customer customer) {
        if (findCustomerById(String.valueOf(customer.getCustomerId())) != null) {
            System.out.println("Customer with ID " + customer.getCustomerId() + " already exists.");
            return false;
        }
        customers.add(customer);
        saveCustomer(customer);
        return true;
    }

    public boolean rentVehicle(Vehicle vehicle, Customer customer, LocalDate date, double amount) {
        if (vehicle.getStatus() != Vehicle.VehicleStatus.AVAILABLE) {
            return false;
        }
        if (vehicle instanceof Rentable rentableVehicle) {
            rentableVehicle.rentVehicle();
            RentalHistory.getInstance().addRecord(new RentalRecord(vehicle, customer, date, amount, "RENT"));
            return true;
        }
        return false;
    }

    public boolean returnVehicle(Vehicle vehicle, Customer customer, LocalDate date, double amount) {
        if (vehicle.getStatus() != Vehicle.VehicleStatus.RENTED) {
            return false;
        }
        if (vehicle instanceof Rentable rentableVehicle) {
            rentableVehicle.returnVehicle();
            RentalHistory.getInstance().addRecord(new RentalRecord(vehicle, customer, date, amount, "RETURN"));
            return true;
        }
        return false;
    }

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
        for (List<RentalRecord> records : rentalHistory.values()) {
            for (RentalRecord record : records) {
                System.out.println(record.toString());
            }
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
            if (String.valueOf(c.getCustomerId()).equals(id)) {
                return c;
            }
        }
        return null;
    }
}
