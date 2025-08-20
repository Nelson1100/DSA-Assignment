package boundary;

import utility.JOptionPaneConsoleIO;
import control.DoctorManagement;
import entity.Doctor;
import utility.*;

public class DoctorUI {
    DoctorManagement dm = new DoctorManagement();
    Doctor doctor = new Doctor();
    Validation validate = new Validation();
    
    public void taskSelection(){
        boolean newTask = true;
        String[] menu = {"Register", "Search", "Update", "Remove", "Back", "Cancel"};
        
        do {
            int choice = JOptionPaneConsoleIO.readOption("Which task would you like to perform?", "Doctor Management Module", menu);

            if (choice == -1)
                break;
            
            switch (choice){
                case 0:
                    // Register new doctor
                    doctor = newDetailsPrompt();
                    if (doctor == null)
                        break;
                    
                    dm.registerDoctor(doctor);
                    break;
                case 1:
                    // Search doctor
                    searchDoctor();
                    dm.viewDoctorInfo(doctorID, doctorName, contactNo, email);
                    break;
                case 2:
                    // Update doctor profile
                    break;
                case 3:
                    // Remove a certain doctor
                    break;
                case 4:
                    // Return back to previous page
                    break;
                case 5:
                    // End performing task
                    newTask = false;
                    break;
                default:
                    JOptionPaneConsoleIO.showError("Please enter a valid option.");
            }
        } while (newTask);
    }
    
    private Doctor newDetailsPrompt(){
        boolean valid;
        String name;
        String phone;
        String email;
        String specialization;
        String id = IDGenerator.next(IDType.DOCTOR);
                
        do {
            valid = false;
            name = JOptionPaneConsoleIO.readNonEmpty("Enter Doctor Name: ");
            
            if (name == null)
                return null;
            
            if (validate.validName(name))
                valid = true;
            else 
                JOptionPaneConsoleIO.showError("Please enter a valid name.");
        } while (!valid);
        
        do {
            valid = false;
            phone = JOptionPaneConsoleIO.readNonEmpty("Enter phone number: ");
            
            if (phone == null)
                return null;
            
            if (validate.validPhone(phone))
                valid = true;
            else 
                JOptionPaneConsoleIO.showError("Please enter a valid phone number.");
        } while (!valid);
        
        do {
            valid = false;
            email = JOptionPaneConsoleIO.readNonEmpty("Enter Email Address: ");
            
            if (email == null)
                return null;
            
            if (validate.validEmail(email))
                valid = true;
            else 
                JOptionPaneConsoleIO.showError("Please enter a valid email address.");
        } while (!valid);
        
        do {
            valid = false;
            specialization = JOptionPaneConsoleIO.readNonEmpty("Enter Specialization: ");
            
            if (specialization == null)
                return null;
            
            if (validate.validSpecialization(specialization))
                valid = true;
            else 
                JOptionPaneConsoleIO.showError("Please enter a valid specialization.");
        } while (!valid);
        
        doctor = new Doctor (id, name, phone, email, specialization);
        return doctor;
    }
    
    private Doctor searchDoctor(){
        JOptionPaneConsoleIO.readNonEmpty("Enter Doctor ID / Name / Phone / Email): ");
    }
}