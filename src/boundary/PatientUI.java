package boundary;

import control.PatientManagement;
import utility.JOptionPaneConsoleIO;

/**
 *
 * @author Ng Wei Jian
 */
public class PatientUI {
    private final PatientManagement pm;
    
    public PatientUI(PatientManagement pm) {
        this.pm = pm;
    }
    
    public void run() {
        boolean repeat = true;
        
        String[] menu = {
            "Patient Profiles",
            "Visit Queue",
            "Reports",
            "Back"
        };
        
        while (repeat) {
            int choice = JOptionPaneConsoleIO.readOption(
                    "Please select a Patient Management option:", 
                    "Patient Management Module", 
                    menu
            );
            switch (choice) {
                case 0 -> new PatientProfileUI(pm).run();
                case 1 -> new PatientQueueUI(pm).run();
                case 2 -> new PatientReportUI(pm).run();
                case 3, -1 -> repeat = false;
                default -> JOptionPaneConsoleIO.showError("Please choose a valid option.");            }
        }
    }
}
