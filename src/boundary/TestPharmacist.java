package boundary; 

import control.MaintainPharmacistAccount;
import entity.Pharmacist;

import java.util.Scanner;

public class TestPharmacist {
    public static void main(String[] args) {
        MaintainPharmacistAccount manager = new MaintainPharmacistAccount();
        Scanner sc = new Scanner(System.in);
        int choice;

        do {
            System.out.println("\n=== Pharmacist Management ===");
            System.out.println("1. Register Pharmacist");
            System.out.println("2. View All Pharmacists");
            System.out.println("3. Search Pharmacist");
            System.out.println("4. Remove Pharmacist");
            System.out.println("0. Exit");
            System.out.print("Enter choice: ");
            while (!sc.hasNextInt()) {
                System.out.print("Please enter a number: ");
                sc.next();
            }
            choice = sc.nextInt();
            sc.nextLine(); // consume newline

            switch (choice) {
                case 1:
                    System.out.print("Enter Name: ");
                    String name = sc.nextLine();
                    System.out.print("Enter Phone: ");
                    String phone = sc.nextLine();

                    boolean added = manager.registerPharmacist(name, phone);
                    if (added)
                        System.out.println("Pharmacist registered successfully.");
                    else
                        System.out.println("Pharmacist already exists.");
                    break;

                case 2:
                    System.out.println("\nAll Pharmacists:");
                    manager.viewPharmacist();
                    break;

                case 3:
                    System.out.print("Enter ID to search: ");
                    String searchID = sc.nextLine();
                    Pharmacist found = manager.searchPharmacist(searchID);
                    if (found != null)
                        System.out.println("Found: " + found);
                    else
                        System.out.println("Pharmacist not found.");
                    break;

                case 4:
                    System.out.print("Enter ID to remove: ");
                    String removeID = sc.nextLine();
                    boolean removed = manager.removePharmacist(removeID);
                    if (removed)
                        System.out.println("Pharmacist removed successfully.");
                    else
                        System.out.println("Pharmacist not found.");
                    break;

                case 0:
                    System.out.println("Exiting program.");
                    break;

                default:
                    System.out.println("Invalid choice!");
            }

        } while (choice != 0);

        sc.close();
    }
}
