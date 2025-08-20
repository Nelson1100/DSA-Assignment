package boundary;

import control.PatientMaintenance;
import entity.*;
import utility.*;

import javax.swing.JOptionPane;

public class PatientProfileUI {
    private final PatientMaintenance controller;
    
    public PatientProfileUI(PatientMaintenance controller) {
        this.controller = controller;
    }
    
    public void run() {
        String[] options = {
            "Register New Patient",
            "Update Existing Patient",
            "Remove Patient by ID",
            "Search Patient by ID",
            "View All Patients (Sorted by ID)",
            "View All Patients (Sorted by Name)",
            "Back to Main Menu"
        };
        
        int choice;
//        do {
//            choice = JOptionPane.showOptionDialog(
//                    null,
//                    "Choose an option:",
//                    
//            );
//        }
    }
}
