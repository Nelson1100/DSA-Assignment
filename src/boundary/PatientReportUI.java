package boundary;

import control.PatientManagement;
import control.PatientReportGenerator;
import utility.JOptionPaneConsoleIO;

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
                    "Patient Reports Menu", 
                    "Patient Reports", 
                    menu
            );

            switch (choice) {
                case 0 -> JOptionPaneConsoleIO.showPlain("<html><pre style='font-family:monospace'>" + reportGen.generateVisitQueueAnalysisReport() + "</pre></html>", "Visit Queue Analysis Report");
                case 1 -> JOptionPaneConsoleIO.showPlain("<html><pre style='font-family:monospace'>" + reportGen.generatePatientSummaryReport() + "</pre></html>", "Patient Summary Report");
                case 2, -1 -> repeat = false;
                default -> JOptionPaneConsoleIO.showError("Please choose a valid option.");
            }
        }
    }
}
