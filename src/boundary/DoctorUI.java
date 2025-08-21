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
        String[] menu = {"Register", "Search", "Cancel"};
        String[] updateOption = {"Update", "Remove", "Cancel"};
        
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
                    
                    boolean success = dm.registerDoctor(doctor);
                    
                    if (success)
                        JOptionPaneConsoleIO.showInfo("Doctor is successfully registered.");
                    else
                        JOptionPaneConsoleIO.showError("Unsuccessful action. Please try again.");                    
                    break;
                case 1:
                    // Search doctor
                    int updateChoice = -1;
                    
                    if (dm.isEmptyTree()) {
                        JOptionPaneConsoleIO.showError("Empty doctor record.");
                        break;
                    }
                    
                    doctor = searchDoctor();
                    if (doctor == null)
                        break;
                    
                    String result = dm.findDoctor(doctor);
                    if (!result.equals("No doctor record is found."))
                        updateChoice = JOptionPaneConsoleIO.readOption(result, "Doctor Information", updateOption);
                    else {
                        JOptionPaneConsoleIO.showError(result);
                        break;
                    }
                    
                    int modifyChoice = 0;
                    String newName = null;
                    String newPhone = null;
                    String newEmail = null;
                    String newSpecialization = null;
                    
                    if (updateChoice == 0) {
                        // Update doctor profile
                        modifyChoice = infoModification();
                        
                        if (modifyChoice == -1)
                            break;
                        
                        switch (modifyChoice){
                            case 1:
                                // Modify name
                                newName = JOptionPaneConsoleIO.readNonEmpty("Enter new name: ");
                                break;
                            case 2:
                                // Modify phone number
                                newPhone = JOptionPaneConsoleIO.readNonEmpty("Enter new phone number: ");
                                newPhone = validate.standardizedPhone(newPhone);
                                break;
                            case 3:
                                // Modify email address
                                newEmail = JOptionPaneConsoleIO.readNonEmpty("Enter new email address: ");
                                break;
                            case 4:
                                // Modify specialization
                                newSpecialization = JOptionPaneConsoleIO.readNonEmpty("Enter new specialization: ");
                                break;
                            default:
                                JOptionPaneConsoleIO.showError("Please enter a valid option.");
                        }
                        
                        if (newName == null && newPhone == null && newEmail == null && newSpecialization == null)
                            break;
                        
                        boolean updateResult = dm.updateDoctor(doctor, modifyChoice, newName, newPhone, newEmail, newSpecialization);
                        
                        if (updateResult)
                            JOptionPaneConsoleIO.showInfo("Information updated.");
                        else
                            JOptionPaneConsoleIO.showError("Update unsuccessfully. Please try again.");
                    } else if (updateChoice == 1){
                        // Remove doctor
                        boolean removeResult = dm.removeDoctor(doctor);
                        
                        if (removeResult)
                            JOptionPaneConsoleIO.showInfo("Doctor removed.");
                        else
                            JOptionPaneConsoleIO.showError("Remove unsuccessfully. Please try again.");
                    }
                        
                    break;
                case 2:
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
            
            phone = validate.standardizedPhone(phone);
            
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
        String id = "";
        String name = "";
        String phone = "";
        String email = "";
        String detail = JOptionPaneConsoleIO.readNonEmpty("Enter Doctor ID / Name / Phone / Email): ");
        
        if (detail == null)
            return null;
        
        if (validate.validName(detail))
            name = detail.trim();
        else if (validate.validPhone(detail)) {
            detail = validate.standardizedPhone(phone);
            phone = detail.trim();
        }
        else if (validate.validEmail(detail))
            email = detail.trim();
        
        doctor = new Doctor(id, name, phone, email, "");
        return doctor;
    }
    
    private int infoModification(){
        String infoSelection = "Which information would you like to update?\n[1] Name\n[2] Phone Number\n[3] Email Address\n[4] Specialization\n";
        return JOptionPaneConsoleIO.readInt(infoSelection);
    }
    
}