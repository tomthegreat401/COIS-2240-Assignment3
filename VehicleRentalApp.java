import java.util.Scanner;
import java.time.LocalDate;

public class VehicleRentalApp {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        RentalSystem system = RentalSystem.getInstance(); // singleton call

        while (true) {
            System.out.println("\n1: Add Vehicle\n2: Add Customer\n3: Rent Vehicle\n4: Return Vehicle\n5: Display Available Vehicles\n6: Show Rental History\n7: Exit");
            System.out.println("Enter a number from the above menu--> ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    System.out.println("  1: Car\n  2: Motorcycle\n  3: Truck\n--> ");
                    int type = scanner.nextInt();
                    scanner.nextLine();

                    System.out.print("Enter license plate: ");
                    String plate = scanner.nextLine();
                    System.out.print("Enter make: ");
                    String make = scanner.nextLine();
                    System.out.print("Enter model: ");
                    String model = scanner.nextLine();
                    System.out.print("Enter year: ");
                    int year = scanner.nextInt();
                    scanner.nextLine();

                    Vehicle vehicle;
                    if (type == 1) {
                        System.out.print("Enter number of seats: ");
                        int seats = scanner.nextInt();
                        vehicle = new Car(make, model, year, seats);
                    } else if (type == 2) {
                        System.out.print("Has sidecar? (true/false): ");
                        boolean sidecar = scanner.nextBoolean();
                        vehicle = new Motorcycle(make, model, year, sidecar);
                    } else if (type == 3) {
                        System.out.print("Enter the cargo capacity: ");
                        double cargoCapacity = scanner.nextDouble();
                        vehicle = new Truck(make, model, year, cargoCapacity);
                    } else {
                        vehicle = null;
                    }

                    if (vehicle != null) {
                        vehicle.setLicensePlate(plate);
                        system.addVehicle(vehicle);
                        System.out.println("Vehicle added.");
                    } else {
                        System.out.println("Vehicle not added.");
                    }
                    break;

                case 2:
                    System.out.print("Enter customer ID: ");
                    String cID = scanner.nextLine();
                    System.out.print("Enter name: ");
                    String cname = scanner.nextLine();

                    system.addCustomer(new Customer(Integer.parseInt(cID), cname));
                    System.out.println("Customer added.");
                    break;

                case 3:
                    System.out.println("List of Available Vehicles:");
                    system.displayVehicles(true);

                    System.out.print("Enter license plate: ");
                    String rentPlate = scanner.nextLine().toUpperCase();

                    System.out.println("Registered Customers:");
                    system.displayAllCustomers();

                    System.out.print("Enter customer ID: ");
                    String cidRent = scanner.nextLine();

                    System.out.print("Enter rental amount: ");
                    double rentAmount = scanner.nextDouble();
                    scanner.nextLine();

                    Vehicle vehicleToRent = system.findVehicleByPlate(rentPlate);
                    Customer customerToRent = system.findCustomerById(cidRent);

                    if (vehicleToRent == null || customerToRent == null) {
                        System.out.println("Vehicle or customer not found.");
                        break;
                    }

                    system.rentVehicle(vehicleToRent, customerToRent, LocalDate.now(), rentAmount);
                    break;

                case 4:
                    System.out.println("List of Vehicles:");
                    system.displayVehicles(false);

                    System.out.print("Enter license plate: ");
                    String returnPlate = scanner.nextLine().toUpperCase();

                    System.out.println("Registered Customers:");
                    system.displayAllCustomers();

                    System.out.print("Enter customer ID: ");
                    String cidReturn = scanner.nextLine();

                    System.out.print("Enter return fees: ");
                    double returnFees = scanner.nextDouble();
                    scanner.nextLine();

                    Vehicle vehicleToReturn = system.findVehicleByPlate(returnPlate);
                    Customer customerToReturn = system.findCustomerById(cidReturn);

                    if (vehicleToReturn == null || customerToReturn == null) {
                        System.out.println("Vehicle or customer not found.");
                        break;
                    }

                    system.returnVehicle(vehicleToReturn, customerToReturn, LocalDate.now(), returnFees);
                    break;

                case 5:
                    system.displayVehicles(true);
                    break;

                case 6:
                    System.out.println("Rental History:");
                    system.displayRentalHistory();
                    break;

                case 7:
                    scanner.close();
                    System.exit(0);
                    break;

                default:
                    System.out.println("Invalid choice. Try again.");
                    break;
            }
        }
    }
}
