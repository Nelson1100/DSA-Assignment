package boundary;

import utility.JOptionPaneConsoleIO;

public class PatientUI {
    public void run() {
        boolean repeat = true;
        
        String[] menu = {
            "Profiles",
            "Visit Queue",
            "Reports",
            "Back"
        };
        
        while (repeat) {
            int choice = JOptionPaneConsoleIO.readOption(
                    "Select a module:", 
                    "Patient Module", 
                    menu
            );
            switch (choice) {
                case 0 -> new PatientProfileUI().run();
//                case 1 -> new PatientQueueUI().run();
//                case 2 -> new PatientReportUI().run();
                case 3, -1 -> repeat = false;
                default -> JOptionPaneConsoleIO.showError("Please choose a valid option.");            }
        }
    }
}
