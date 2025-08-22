package boundary;

import control.*;

import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

import utility.JOptionPaneConsoleIO;

public class MainUI {
    public static void main(String[] args) {
        String welcome = 
                """
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
                    "Welcome to Hospital Management System\n\nSelect a module continue:", 
                    "Main Menu", 
                    modules
            );
            
            switch (choice) {
                case 0 -> new PatientUI().run();
                case 1 -> JOptionPane.showMessageDialog(null, "Doctor Module: Not yet implemented.");
                case 2 -> JOptionPane.showMessageDialog(null, "Consultation Module: Not yet implemented.");
                case 3 -> JOptionPane.showMessageDialog(null, "Medical Treatment Module: Not yet implemented.");
                case 4 -> JOptionPane.showMessageDialog(null, "Pharmacy Module: Not yet implemented.");
            }
        } while (choice != 5 && choice != -1);
    }
}
