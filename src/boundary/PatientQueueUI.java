package boundary;

import control.PatientMaintenance;
import entity.*;
import utility.*;

import java.time.LocalDateTime;

public class PatientQueueUI {
    private final PatientMaintenance pm = new PatientMaintenance();
    
    public void run() {
        boolean repeat = true;
        
        String[] menu = {
            "Register",
            "Serve Next",
            "Remove",
            "Find Postion",
            "View Queue",
            "Queue Summary",
            "Top-K Wait",
            "Back"
        };
        
        while (repeat) {
            int choice = JOptionPaneConsoleIO.readOption(
                    "Visit Queue Management", 
                    "Patient Queue", 
                    menu
            );
            
            switch (choice) {
//                case 0 -> registerVisit();
//                case 1 -> serveNext();
//                case 2 -> removeVisit();
//                case 3 -> viewSummary();
//                case 4 -> viewTopWaiting();
                case 5, -1 -> repeat = false;
                default -> JOptionPaneConsoleIO.showError("Please choose a valid option.");
            }
        }
    }
}
