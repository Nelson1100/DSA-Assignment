package control;

import entity.Doctor;
import javax.swing.JOptionPane;

public class testing {
    public static void main(String[] args) {
        DoctorManagement dm = new DoctorManagement();
        boolean con = true;
        String id;
        String name;
        String phone;
        String email;
        String specialization;
        String[] options = { "Register New Doctor", "Search Doctor Information", "Update Doctor Information", "Remove Existing Doctor", "Cancel" };
        String[] searchOptions = { "Doctor ID", "Name", "Phone", "Email" };
        
        while (con) {
            int choice = JOptionPane.showOptionDialog(null, "Which task would you like to perform?", "Doctor Management Module", JOptionPane.DEFAULT_OPTION,JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
            
            if (choice == -1 || choice == 4)
                con = false;
            
            switch (choice) {
                case 0:
                    name = JOptionPane.showInputDialog(null, "Enter Doctor Name: ", "Doctor Name", JOptionPane.QUESTION_MESSAGE);
                    phone = JOptionPane.showInputDialog(null, "Enter Phone Number: ", "Phone Number", JOptionPane.QUESTION_MESSAGE);
                    email = JOptionPane.showInputDialog(null, "Enter Email Address: ", "Email Address", JOptionPane.QUESTION_MESSAGE);
                    specialization = JOptionPane.showInputDialog(null, "Enter Specialization: ", "Specialization", JOptionPane.QUESTION_MESSAGE);
                    
                    Doctor newDoc = new Doctor("S0001", name, phone, email, specialization);
                    dm.registerDoctor(newDoc);
                    break;
                case 1:
                    id = "";
                    name = "";
                    email = "";
                    phone = "";
                    boolean conSearch = true;
                    
                    while (conSearch) {
                        int searchChoice = JOptionPane.showOptionDialog(null, "Search by which detail?", "Search Doctor", JOptionPane.DEFAULT_OPTION,JOptionPane.QUESTION_MESSAGE, null, searchOptions, searchOptions[0]);

                        if (searchChoice == -1 || searchChoice == 4)
                            conSearch = false;
                    
                        switch (searchChoice){
                            case 0:
                                id = JOptionPane.showInputDialog(null, "Enter Doctor ID: ", "Search By Doctor ID", JOptionPane.QUESTION_MESSAGE);
                                break;
                            case 1:
                                name = JOptionPane.showInputDialog(null, "Enter Doctor Name: ", "Search By Doctor Name", JOptionPane.QUESTION_MESSAGE);
                                break;
                            case 2:
                                phone = JOptionPane.showInputDialog(null, "Enter Phone Number: ", "Search By Phone Number", JOptionPane.QUESTION_MESSAGE);
                                break;
                            case 3:
                                email = JOptionPane.showInputDialog(null, "Enter Email Address: ", "Search By Email Address", JOptionPane.QUESTION_MESSAGE);
                                break;
                        }
                        dm.viewDoctorInfo(id, name, phone, email);
                    }
            }
        }

        //dm.updateDoctorInfo(doctorID);
        
        
        
        dm.removeDoctor("S0001", "0182284609");
        
        dm.viewDoctorInfo("S0001", "", "", "");
    }
}
