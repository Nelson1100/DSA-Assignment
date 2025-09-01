package boundary;

import control.*;

import javax.swing.JOptionPane;
import javax.swing.JTextArea;

import utility.JOptionPaneConsoleIO;

public class MainUI {
    public static void main(String[] args) {
        PatientManagement pm = new PatientManagement();
        DoctorManagement dm = new DoctorManagement();
        //MedicalTreatmentManagement tm = new MedicalTreatmentManagement();
        ConsultationManagement cm = new ConsultationManagement(pm, dm);
        cm.initializeData(); // Initialize consultation data
        
        String welcome = 
                """
                ╔═════════════════════════════════════════════════╗
                ║       🏥 Clinic Management System        ║
                ╠═════════════════════════════════════════════════╣
                ║          Developed by: Group 3           ║
                ║                                          ║
                ║          Welcome to the system!          ║
                ║     Please proceed to the main menu.     ║
                ╚═════════════════════════════════════════════════╝
                """;
        
        JTextArea textArea = new JTextArea(welcome);
        textArea.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 12));
        textArea.setEditable(false);
        textArea.setOpaque(false);
        
        JOptionPane.showMessageDialog(null, textArea, "Welcome", JOptionPane.PLAIN_MESSAGE);
        
        String[] modules = {
            "Patient",
            "Doctor",
            "Consultation",
            "Treatment",
            "Pharmacy",
            "Exit"
        };
        
        int choice;
        do {
            choice = JOptionPaneConsoleIO.readOption(
                    "Please select a module option:", 
                    "Main Menu", 
                    modules
            );
            
            switch (choice) {
                case 0 -> new PatientUI(pm).run();
                case 1 -> new DoctorUI().taskSelection();
                case 2 -> new ConsultationUI(cm, pm, dm).run();
                case 3 -> { 
                    PatientHistoryManagement phm = new PatientHistoryManagement(); 
                    MedicalTreatmentManagement mtm = new MedicalTreatmentManagement(pm, phm); 
                    phm.preloadSampleTreatments(pm); new MedicalTreatmentUI(mtm).run(); 
                }
                case 4 -> new PharmacyModuleUI().run();
            }
        } while (choice != 5 && choice != -1);
    }
}
