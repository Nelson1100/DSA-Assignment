package boundary;

import control.PatientManagement;
import entity.*;
import utility.*;

/**
 *
 * @author Ng Wei Jian
 */
public class PatientProfileUI {
    private final PatientManagement pm;
    private final Validation validate = new Validation();
    
    private Patient tempPatient;
    
    public PatientProfileUI(PatientManagement pm) {
        this.pm = pm;
    }
    
    public void run() {
        boolean repeat = true;
        
        String[] menu = {
            "Register",
            "Search & Manage",
            "View All",
            "Back"
        };

        while (repeat) {
            int choice = JOptionPaneConsoleIO.readOption(
                    "Please select a Patient Profile option:",
                    "Patient Profile Management",
                    menu
            );
            
            switch (choice) {
                case 0 -> registerPatient();
                case 1 -> searchPatient();
                case 2 -> viewSortedPatients();
                case 3, -1 -> repeat = false;
                default -> JOptionPaneConsoleIO.showError("Please choose a valid option.");
            }
        }
    }

    private void registerPatient() {
        Patient p = readPatientPrompt();
        
        if (p == null) return;
        
        boolean result = pm.registerPatient(p);
        JOptionPaneConsoleIO.showInfo(result ? "Patient registered." : "Registration failed.");
    }

    private void searchPatient() {
        String detail = JOptionPaneConsoleIO.readNonEmpty("Search by ID / Name / Contact / Email:");
        
        if (detail == null) return;
        detail = detail.trim();
        
        Patient match = null;
        Patient[] matches = null;
        
        // decide the search type
        if (validate.validName(detail)) {
            matches = pm.findPatientsByName(detail);
        } else if (validate.validPhone(validate.standardizedPhone(detail))) {
            String phone = validate.standardizedPhone(detail);
            match = pm.findPatientByPhone(phone);
        } else if (validate.validEmail(detail.toLowerCase())) {
            match = pm.findPatientByEmail(detail.toLowerCase());
        } else {
            match = pm.findPatientByID(detail);
        }
        
        // handle multiple matches (for name search only)
        if (matches != null && matches.length > 1) {
            String[] options = new String[matches.length];
            
            for (int i = 0; i < matches.length; i++) {
                options[i] = String.format("%s (%s, %s, %s)", 
                        matches[i].getPatientName(), 
                        matches[i].getPatientID(), 
                        matches[i].getContactNo(),
                        matches[i].getEmail()
                );
            }
            
            int sel = JOptionPaneConsoleIO.readOption("Multiple matches found:", "Select Patient", options);
            
            if (sel != -1)
                match = matches[sel];
        } else if (matches != null && matches.length == 1) {
            match = matches[0];
        }
        
        if (match == null) {
            JOptionPaneConsoleIO.showError("Patient not found.");
            return;
        }
        
        this.tempPatient = match;
        viewPatientMenu();
    }
    
    private void viewPatientMenu() {
        boolean back = false;
        
        while (!back) {
            String[] menu = {
                "Update",
                "Delete",
                "Cancel"
            };

            int choice = JOptionPaneConsoleIO.readOption(tempPatient.toString(), "Patient Found", menu);

            switch (choice) {
                case 0 -> updatePatient();
                case 1 -> {
                    deletePatient();
                    back = true;
                }
                case 2, -1 -> back = true;
            }
        }
    }
    
    private void updatePatient() {
        int field = -1;
        Integer input;
        
        String msg = """
                         Which field to update?
                         [1] Name
                         [2] Phone
                         [3] Email
                         [4] Gender
                         [5] Age
                         """;
        
        do {
            input = JOptionPaneConsoleIO.readInt(msg);

            if (input == -1) return; 
            
            if (input >= 1 && input <= 5) {
                field = input;
                break;
            } else {
               JOptionPaneConsoleIO.showError("Invalid choice. Please enter a number between 1 and 5."); 
            }
        } while (field == -1);
        
        String newValue;
        boolean valid = false;
        
        do {
            newValue = JOptionPaneConsoleIO.readNonEmpty("Enter new value:");
            
            if (newValue == null) return;
            
            valid = switch (field) {
                case 1 -> validate.validName(newValue);
                case 2 -> validate.validPhone(validate.standardizedPhone(newValue));
                case 3 -> validate.validEmail(newValue.toLowerCase());
                case 4 -> validate.validGender(newValue);
                case 5 -> validate.validNumber(newValue, 0, 120);
                default -> false;
            };
            
            if (!valid) {
                JOptionPaneConsoleIO.showError("Invalid input for the selected field.");
            }
        } while (!valid);
        
        // Standardize name, phone or email
        switch (field) {
            case 1 -> newValue = validate.standardizedName(newValue);
            case 2 -> newValue = validate.standardizedPhone(newValue);
            case 3 -> newValue = newValue.toLowerCase();
            default -> {}
        }
        
        boolean success = pm.updatePatientField(tempPatient.getPatientID(), field, newValue);
        
        if (success) {
            JOptionPaneConsoleIO.showInfo("Updated successfully.");
            tempPatient = pm.findPatientByID(tempPatient.getPatientID()); // Refresh to latest data
        } else {
            JOptionPaneConsoleIO.showInfo("Failed to update.");
        }
    }
    
    private void deletePatient() {
        boolean confirm = JOptionPaneConsoleIO.confirmDialog(
                "Are you sure you want to delete this patient?\n" + tempPatient, 
                "Confirm Deletion"
        );
        
        if (!confirm) {
            viewPatientMenu();
            return;
        }
        
        boolean result = pm.removePatientByID(tempPatient.getPatientID());
        JOptionPaneConsoleIO.showInfo(result ? "Patient deleted." : "Deletion failed.");
        tempPatient = null;
    }

    private void viewSortedPatients() {
        String[] viewOptions = {
            "By ID", 
            "By Name", 
            "By Gender", 
            "By Age"
        };
        String[] orderOptions = {
            "Ascending", 
            "Descending"
        };
        
        int sortChoice = JOptionPaneConsoleIO.readOption("Please select sorting field:", "Patient List Sorting", viewOptions);
        if (sortChoice == -1) return;
        
        int orderChoice = JOptionPaneConsoleIO.readOption("Please select sorting order:", "Sort Order", orderOptions);
        boolean descending = (orderChoice == 1);
        
        Patient[] arr = switch (sortChoice) {
            case 0 -> pm.getAllPatientsSortedByID(descending);
            case 1 -> pm.getAllPatientsSortedByName(descending);
            case 2 -> pm.getAllPatientsSortedByGender(descending);
            case 3 -> pm.getAllPatientsSortedByAge(descending);
            default -> null;
        };
        
        if (arr == null || arr.length == 0) {
            JOptionPaneConsoleIO.showError("No patient data to display.");
            return;
        }
        
        final int width = 100;
        StringBuilder sb = new StringBuilder();
        sb.append(JOptionPaneConsoleIO.line('-', width)).append("\n");
        sb.append(JOptionPaneConsoleIO.sectionTitle("Sorted Patients", width));
        sb.append(String.format("%-15s %-25s %-10s %-5s %-25s %-15s\n", 
                "Patient ID", "Name", "Gender", "Age", "Email", "Contact"));
        sb.append(JOptionPaneConsoleIO.line('-', width)).append("\n");
                
        for (Patient p : arr) {
            sb.append(String.format("%-15s %-25s %-10s %-5s %-25s %-15s\n",
                p.getPatientID(),
                p.getPatientName(),
                p.getGender(),
                p.getAge(),
                p.getEmail(),
                p.getContactNo()
            ));
        }
        
        sb.append(JOptionPaneConsoleIO.line('-', width)).append("\n");

        JOptionPaneConsoleIO.showMonospaced("Sorted Patients", sb.toString());
    }

    private Patient readPatientPrompt() {
        String name = "";
        String contact = "";
        String email = "";
        Gender gender = null;
        int age = 0;
        
        // Name
        boolean valid = false;
        while (!valid) {
            name = JOptionPaneConsoleIO.readNonEmpty("Enter Patient Name:");
            
            if (name == null) return null;
            
            name = validate.standardizedName(name);
            
            if (validate.validName(name)) {
                valid = true;
            } else {
                JOptionPaneConsoleIO.showError("Invalid name.");
            }
        }
        
        // Phone
        valid = false;
        while (!valid) {
            contact = JOptionPaneConsoleIO.readNonEmpty("Enter Contact Number:");
            
            if (contact == null) return null;
            
            contact = validate.standardizedPhone(contact);
            
            if (!validate.validPhone(contact)) {
                JOptionPaneConsoleIO.showError("Please enter a valid Malaysian contact number (+60).");
                continue;
            }
            
            if (pm.findPatientByPhone(contact) != null) {
                JOptionPaneConsoleIO.showError("Contact number is already registered.");
                continue;
            }
            
            valid = true;
        }
        
        // Email
        valid = false;
        while (!valid) {
            email = JOptionPaneConsoleIO.readNonEmpty("Enter Email:");
            
            if (email == null) return null;
            
            if (!validate.validEmail(email)) {
                JOptionPaneConsoleIO.showError("Please enter a valid email.");
                continue;
            }
            
            if (pm.findPatientByEmail(email) != null) {
                JOptionPaneConsoleIO.showError("Email is already registered.");
                continue;
            }
            
            valid = true;
        }
        
        // Gender
        valid = false;
        while (!valid) {
            String g = JOptionPaneConsoleIO.readNonEmpty("Enter Gender (MALE/FEMALE):");
            
            if (g == null) return null;
            
            if (validate.validGender(g)) {
                gender = Gender.valueOf(g.toUpperCase());
                valid = true;
            } else {
                JOptionPaneConsoleIO.showError("Please enter MALE or FEMALE.");
            }
        }
        
        // Age
        valid = false;
        while (!valid) {
            String a = JOptionPaneConsoleIO.readNonEmpty("Enter Age:");
            
            if (a == null) return null;
            
            if (validate.validNumber(a, 0, 120)) {
                age = Integer.parseInt(a);
                valid = true;
            } else {
                JOptionPaneConsoleIO.showError("Please enter a valid age (0–120).");
            }
        }
        
        // Generate ID only after confirming all input
        String id = IDGenerator.next(IDType.PATIENT);
        
        return new Patient(id, name, contact, email, gender, age);
    }
}
