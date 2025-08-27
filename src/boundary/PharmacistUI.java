package boundary;

import control.PharmacistManagement;
import entity.Pharmacist;
import utility.JOptionPaneConsoleIO;
import utility.Validation;

public class PharmacistUI {
    private PharmacistManagement pharmacistMgmt = new PharmacistManagement();
    private static final Validation validator = new Validation();

    public void run() {
        int choice;

        do {
            choice = JOptionPaneConsoleIO.readOption(
                "=== PHARMACIST MANAGEMENT MENU ===",
                "Pharmacist Management",
                new String[]{
                    "Register New Pharmacist",
                    "View Pharmacist By ID",
                    "View All Pharmacists",
                    "Update Pharmacist",
                    "Remove Pharmacist",
                    "Exit"
                }
            );

            switch (choice) {
                case 0 -> registerPharmacist();
                case 1 -> viewPharmacist();
                case 2 -> viewAllPharmacists();
                case 3 -> updatePharmacist();
                case 4 -> removePharmacist();
                case 5 -> JOptionPaneConsoleIO.showInfo("Returning to main menu.");
                default -> JOptionPaneConsoleIO.showError("Invalid option selected.");
            }
        } while (choice != 5);
    }

    private void registerPharmacist() {
        String id = JOptionPaneConsoleIO.readNonEmpty("Enter Pharmacist ID:");
        if (pharmacistMgmt.getPharmacist(id) != null) {
            JOptionPaneConsoleIO.showError("Pharmacist ID already exists.");
            return;
        }

        String name = JOptionPaneConsoleIO.readNonEmpty("Enter Pharmacist Name:");
        if (!validator.validName(name)) {
            JOptionPaneConsoleIO.showError("Invalid name format.");
            return;
        }

        String phone = JOptionPaneConsoleIO.readNonEmpty("Enter Phone Number:");
        if (!validator.validPhone(phone)) {
            JOptionPaneConsoleIO.showError("Invalid phone number format.");
            return;
        }

        String email = JOptionPaneConsoleIO.readNonEmpty("Enter Email:");
        if (!validator.validEmail(email)) {
            JOptionPaneConsoleIO.showError("Invalid email format.");
            return;
        }

        Pharmacist p = new Pharmacist(id, name, phone, email);
        pharmacistMgmt.addPharmacist(p);
        JOptionPaneConsoleIO.showInfo("Pharmacist registered successfully.");
    }

    private void viewPharmacist() {
        String id = JOptionPaneConsoleIO.readNonEmpty("Enter Pharmacist ID to search:");
        Pharmacist p = pharmacistMgmt.getPharmacist(id);

        if (p == null)
            JOptionPaneConsoleIO.showError("Pharmacist not found.");
        else
            JOptionPaneConsoleIO.showInfo(p.toString());
    }

    private void viewAllPharmacists() {
        String report = pharmacistMgmt.ViewAllPharmacistReport();
        if (report.isEmpty()) {
            JOptionPaneConsoleIO.showError("No pharmacists available.");
        } else {
            JOptionPaneConsoleIO.showInfo(report);
        }
    }

    private void updatePharmacist() {
        String id = JOptionPaneConsoleIO.readNonEmpty("Enter Pharmacist ID to update:");
        Pharmacist p = pharmacistMgmt.getPharmacist(id);

        if (p == null) {
            JOptionPaneConsoleIO.showError("Pharmacist not found.");
            return;
        }

        String name = JOptionPaneConsoleIO.readNonEmpty("Enter new name (leave blank to keep unchanged):");
        if (!name.isEmpty() && !validator.validName(name)) {
            JOptionPaneConsoleIO.showError("Invalid name format.");
            return;
        }

        String phone = JOptionPaneConsoleIO.readNonEmpty("Enter new phone (leave blank to keep unchanged):");
        if (!phone.isEmpty() && !validator.validPhone(phone)) {
            JOptionPaneConsoleIO.showError("Invalid phone format.");
            return;
        }

        String email = JOptionPaneConsoleIO.readNonEmpty("Enter new email (leave blank to keep unchanged):");
        if (!email.isEmpty() && !validator.validEmail(email)) {
            JOptionPaneConsoleIO.showError("Invalid email format.");
            return;
        }

        boolean updated = pharmacistMgmt.updatePharmacist(id, name, phone, email);
        if (updated)
            JOptionPaneConsoleIO.showInfo("Pharmacist updated successfully.");
        else
            JOptionPaneConsoleIO.showInfo("No changes were made.");
    }

    private void removePharmacist() {
        String id = JOptionPaneConsoleIO.readNonEmpty("Enter Pharmacist ID to remove:");
        boolean removed = pharmacistMgmt.removePharmacist(id);

        if (removed)
            JOptionPaneConsoleIO.showInfo("Pharmacist removed successfully.");
        else
            JOptionPaneConsoleIO.showError("Pharmacist not found.");
    }
}
