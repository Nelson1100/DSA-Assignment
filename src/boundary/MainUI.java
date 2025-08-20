package boundary;

import control.*;

import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class MainUI {
    public static void main(String[] args) {
        String welcome = """
        ╔═════════════════════════════════════════════════╗
        ║       🏥 Clinic Management System        ║
        ╠═════════════════════════════════════════════════╣
        ║          Developed by: Group X           ║
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
        
        try {
            ImageIcon logo = new ImageIcon("logo.png");
            JLabel label = new JLabel("Welcome to Clinic Management System", logo, JLabel.CENTER);
            label.setHorizontalTextPosition(JLabel.CENTER);
            label.setVerticalTextPosition(JLabel.BOTTOM);
            JOptionPane.showMessageDialog(null, label, "Welcome", JOptionPane.PLAIN_MESSAGE);
        } catch (Exception e) {
            System.out.println("Logo image not found. Skipping image flash.");
        }
        
        PatientMaintenance patientController = new PatientMaintenance();
        
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
            choice = JOptionPane.showOptionDialog(
                    null,
                    "Welcome to Hospital Management System\nSelect a module continue:",
                    "Main Menu",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    modules,
                    modules[0]
            );
            
            switch (choice) {
                case 0 -> launchPatientModule(patientController);
                case 1 -> JOptionPane.showMessageDialog(null, "Doctor Module: Not yet implemented.");
                case 2 -> JOptionPane.showMessageDialog(null, "Consultation Module: Not yet implemented.");
                case 3 -> JOptionPane.showMessageDialog(null, "Medical Treatment Module: Not yet implemented.");
                case 4 -> JOptionPane.showMessageDialog(null, "Pharmacy Module: Not yet implemented.");
            }
        } while (choice != 5);
    }
    
    private static void launchPatientModule(PatientMaintenance controller) {
        String[] patientOptions = {
            "Visit Queue",
            "Profiles",
            "Reports",
            "Back"
        };
        
        int choice;
        do {
            choice = JOptionPane.showOptionDialog(
                    null,
                    "Patient Management Module:\n- Visit Queue: manage visit registrations\n- Profiles: add/edit/remove patients\n- Reports: view summaries",
                    "Patient Module",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    patientOptions,
                    patientOptions[0]
            );
            
            switch (choice) {
//                case 0 -> new PatientUI(controller).run();
                case 1 -> new PatientProfileUI(controller).run();
//                case 2 -> new PatientReportUI(controller).run();
            }
        } while (choice != 3);
    }
}
