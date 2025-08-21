package boundary;

import control.PatientMaintenance;
import entity.*;
import utility.*;

import javax.swing.JOptionPane;

public class PatientProfileUI {
    private final PatientMaintenance pm = new PatientMaintenance();
    private final Validation validate = new Validation();
    
    private Patient patient; // scratch
    
    public void taskSelection() {
        boolean newTask = true;
        String[] menu = {
            "Register",
            "Search",
            "View",
            "Back"
        };
        String[] searchOptions = {
            "Update",
            "Remove",
            "Back"
        };

        do {
            int choice = JOptionPaneConsoleIO.readOption(
                    "Which task would you like to perform?",
                    "Patient Management Module — Profiles",
                    menu
            );
            
            if (choice == -1) break;
            
            switch (choice) {
                case 0 -> { // Register
//                    patient = newDetailsPrompt();
                    if (patient == null) break;
                    
                    boolean success = pm.registerPatient(patient);
                    if (success)
                        JOptionPaneConsoleIO.showInfo("Patient successfully registered.");
                    else
                        JOptionPaneConsoleIO.showError("Unsuccessful. Patient ID already exists or invalid.");
                }
                case 1 -> { // Search -> Update | Remove
                    
                }
            }
        } while (newTask);
    }
}
