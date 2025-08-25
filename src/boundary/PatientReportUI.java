package boundary;

import control.PatientManagement;
import control.PatientReportGenerator;
import utility.JOptionPaneConsoleIO;

/**
 *
 * @author Ng Wei Jian
 */
public class PatientReportUI {
    private final PatientReportGenerator reportGen;
    
    public PatientReportUI(PatientManagement pm) {
        this.reportGen = new PatientReportGenerator(pm);
    }
    
    public void run() {
        boolean repeat = true;
        
        String[] menu = {
            "Visit Queue Analysis",
            "Patient Summary",
            "Back"
        };
        
        while (repeat) {
            int choice = JOptionPaneConsoleIO.readOption(
                    "Please select a Patient Report option:", 
                    "Patient Report Management", 
                    menu
            );

            switch (choice) {
                case 0 -> JOptionPaneConsoleIO.showMonospaced(
                        "Visit Queue Analysis Report", 
                        reportGen.generateVisitQueueAnalysisReport()
                );
                case 1 -> JOptionPaneConsoleIO.showMonospaced(
                        "Patient Summary Report", 
                        reportGen.generatePatientSummaryReport()
                );
                case 2, -1 -> repeat = false;
                default -> JOptionPaneConsoleIO.showError("Please choose a valid option.");
            }
        }
    }
}
