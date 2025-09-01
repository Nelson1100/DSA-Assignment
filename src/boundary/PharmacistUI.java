package boundary;

import control.PharmacistManagement;
import entity.Pharmacist;
import utility.JOptionPaneConsoleIO;
import utility.Validation;
import utility.IDGenerator;
import utility.IDType;

public class PharmacistUI {

    private final PharmacistManagement pharmacistMgmt;
    private static final Validation validate = new Validation();

    // Inject the shared management instance. Do NOT seed here.
    public PharmacistUI(PharmacistManagement pharmacistMgmt) {
        this.pharmacistMgmt = pharmacistMgmt;
    }

    public void run() {
        int choice;
        do {
            choice = JOptionPaneConsoleIO.readOption(
                    "=== PHARMACIST MANAGEMENT MENU ===",
                    "Pharmacist Management",
                    new String[]{
                        "Register New Pharmacist",
                        "View Pharmacist By ID/Name",
                        "View All Pharmacists",
                        "Exit"
                    }
            );

            switch (choice) {
                case 0 ->
                    registerPharmacist();
                case 1 ->
                    viewPharmacist();
                case 2 ->
                    viewAllPharmacists();
                case 3 ->
                    JOptionPaneConsoleIO.showInfo("Returning to main menu.");
                default ->
                    JOptionPaneConsoleIO.showError("Invalid option selected.");
            }
        } while (choice != 3);
    }

    private void registerPharmacist() {
        String name;
        String phone;
        String email;

        // === NAME INPUT ===
        do {
            name = JOptionPaneConsoleIO.readNonEmpty("Enter Pharmacist Name:");
            if (name == null) {
                return; // user pressed cancel
            }
            if (!validate.validName(name)) {
                JOptionPaneConsoleIO.showError("Please enter a valid name.");
                continue;
            }
            name = validate.standardizedName(name);
            if (pharmacistMgmt.getPharmacistByName(name) != null) { // use byName, not byID
                JOptionPaneConsoleIO.showError("Pharmacist name already exists.");
                continue;
            }
            break; // valid input
        } while (true);

        // === PHONE INPUT ===
        do {
            phone = JOptionPaneConsoleIO.readNonEmpty("Enter Phone Number:");
            if (phone == null) {
                return;
            }
            phone = validate.standardizedPhone(phone);
            if (!validate.validPhone(phone)) {
                JOptionPaneConsoleIO.showError("Please enter a valid phone number.");
                continue;
            }
            if (pharmacistMgmt.findPharmacistByPhone(phone) != null) {
                JOptionPaneConsoleIO.showError("Phone number already exists.");
                continue;
            }
            break;
        } while (true);

        // === EMAIL INPUT ===
        do {
            email = JOptionPaneConsoleIO.readNonEmpty("Enter Email:");
            if (email == null) {
                return;
            }
            if (!validate.validEmail(email)) {
                JOptionPaneConsoleIO.showError("Please enter a valid email address.");
                continue;
            }
            break;
        } while (true);

        // === REGISTRATION ===
        String id = IDGenerator.next(IDType.PHARMACIST);
        Pharmacist p = new Pharmacist(id, name, phone, email);
        pharmacistMgmt.addPharmacist(p);
        JOptionPaneConsoleIO.showInfo("Pharmacist registered successfully.");
    }


    private void viewPharmacist() {
        String input = JOptionPaneConsoleIO.readNonEmpty("Enter Pharmacist ID or Name to search:");
        if (input == null) {
            return;
        }

        Pharmacist p = pharmacistMgmt.getPharmacist(input);
        if (p == null) {
            p = pharmacistMgmt.getPharmacistByName(input);
        }

        if (p == null) {
            JOptionPaneConsoleIO.showError("Pharmacist not found.");
            return;
        }

        JOptionPaneConsoleIO.showInfo("=== Pharmacist Details ===\n" + p);

        int action = JOptionPaneConsoleIO.readOption(
                "Select an action to perform:",
                "Pharmacist Action",
                new String[]{"Update", "Remove", "Cancel"}
        );

        if (action == 0) { // Update
            String name = JOptionPaneConsoleIO.readOptional("Enter new name (leave blank to keep unchanged):");
            if (name == null) {
                return;
            }
            if (!name.isEmpty() && !validate.validName(name)) {
                JOptionPaneConsoleIO.showError("Invalid name format.");
                return;
            }

            String phone = JOptionPaneConsoleIO.readOptional("Enter new phone (leave blank to keep unchanged):");
            if (phone == null) {
                return;
            }
            if (!phone.isEmpty()) {
                phone = validate.standardizedPhone(phone);
                if (!validate.validPhone(phone)) {
                    JOptionPaneConsoleIO.showError("Invalid phone format.");
                    return;
                }
            }

            String email = JOptionPaneConsoleIO.readOptional("Enter new email (leave blank to keep unchanged):");
            if (email == null) {
                return;
            }
            if (!email.isEmpty() && !validate.validEmail(email)) {
                JOptionPaneConsoleIO.showError("Invalid email format.");
                return;
            }

            String finalName = name.isEmpty() ? p.getPharmacistName() : validate.standardizedName(name);
            String finalPhone = phone.isEmpty() ? p.getPharmacistPhone() : phone;
            String finalEmail = email.isEmpty() ? p.getPharmacistEmail() : email;

            boolean updated = pharmacistMgmt.updatePharmacist(p.getPharmacistID(), finalName, finalPhone, finalEmail);
            if (updated) {
                JOptionPaneConsoleIO.showInfo("Pharmacist updated successfully.");
            } else {
                JOptionPaneConsoleIO.showInfo("No changes were made.");
            }
        } else if (action == 1) { // Remove
            boolean confirm = JOptionPaneConsoleIO.confirmDialog(
                    "Are you sure you want to remove this pharmacist?\n" + p.getPharmacistName(),
                    "Confirm Removal"
            );

            if (confirm) {
                boolean removed = pharmacistMgmt.removePharmacist(p.getPharmacistID());
                if (removed) {
                    JOptionPaneConsoleIO.showInfo("Pharmacist removed successfully.");
                } else {
                    JOptionPaneConsoleIO.showError("Failed to remove pharmacist.");
                }
            } else {
                JOptionPaneConsoleIO.showInfo("Removal cancelled.");
            }
        } else {
            JOptionPaneConsoleIO.showInfo("Action cancelled.");
        }
    }

    private void viewAllPharmacists() {
        String report = pharmacistMgmt.ViewAllPharmacistReport();
        if (report.isEmpty()) {
            JOptionPaneConsoleIO.showError("No pharmacists available.");
        } else {
            JOptionPaneConsoleIO.showInfo(report);
        }
    }
}
