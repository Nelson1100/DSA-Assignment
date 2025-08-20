package control;

// Manage doctor information, duty schedules and availability tracking.

import adt.AVLInterface;
import adt.AVLTree;
import entity.Doctor;
import javax.swing.JOptionPane;
import utility.Validation;

public class DoctorManagement {
    AVLInterface<Doctor> doctorTree = new AVLTree<>();
    Validation validate = new Validation();
    
    // Abstract Classes
    public void registerDoctor(Doctor doctor){
        String result = doctorRegistration(doctor);
        JOptionPane.showMessageDialog(null, result, "Doctor Registration", JOptionPane.INFORMATION_MESSAGE);
    }
    
    public void viewDoctorInfo(String doctorID, String doctorName, String contactNo, String email){
        String result = showDoctorInfo(doctorID, doctorName, contactNo, email);
        
        if (result.startsWith("No doctor"))
            JOptionPane.showMessageDialog(null, result, "Doctor Information", JOptionPane.ERROR_MESSAGE);
        else
            JOptionPane.showMessageDialog(null, result, "Doctor Information", JOptionPane.INFORMATION_MESSAGE);
    }
    
    public void updateDoctorInfo(String doctorID){
        int result = doctorInfoModification(doctorID);
        
        switch (result){
            case 0:
                JOptionPane.showMessageDialog(null, "No doctor found.", "Invalid Doctor", JOptionPane.ERROR_MESSAGE);
                break;
            case 1:
                JOptionPane.showMessageDialog(null, "Doctor name is successfully changed.", "Doctor Name Changed", JOptionPane.INFORMATION_MESSAGE);
                break;
            case 2:
                JOptionPane.showMessageDialog(null, "Contact number is successfully changed.", "Contact Number Changed", JOptionPane.INFORMATION_MESSAGE);
                break;
            case 3:
                JOptionPane.showMessageDialog(null, "Email Address is successfully changed.", "Email Address Changed", JOptionPane.INFORMATION_MESSAGE);
                break;
            case 4:
                JOptionPane.showMessageDialog(null, "Specialization is successfully changed.", "Specialization Changed", JOptionPane.INFORMATION_MESSAGE);
                break;
            default:
                JOptionPane.showMessageDialog(null, "Please try again.", "System Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void removeDoctor(String doctorID, String contactNo){
        boolean result = doctorRemover(doctorID, contactNo);
        
        if (result == true)
            JOptionPane.showMessageDialog(null, "Doctor (" + doctorID + ") is successfully removed.", "Doctor Removed", JOptionPane.INFORMATION_MESSAGE);
        else
            JOptionPane.showMessageDialog(null, "Please retry again.", "Doctor Remove Unsuccessful", JOptionPane.ERROR_MESSAGE);
    }
    
    // Implementation Classes
    private String doctorRegistration(Doctor doctor){
        String phone = doctor.getContactNo().replaceAll("[-\\s]", "");
        
        if (!doctor.getDoctorID().isEmpty() && !doctor.getDoctorName().isEmpty() &&
            !doctor.getContactNo().isEmpty() && !doctor.getEmail().isEmpty() &&
            !doctor.getSpecialization().isEmpty()){
            if (validate.validName(doctor.getDoctorName())) {
                if (validate.validPhone(phone)){
                    phone = phone.substring(0, 3) + "-" + phone.substring(3);
                    doctor.setContactNo(phone);
                    if (validate.validEmail(doctor.getEmail())) {
                        if (validate.validSpecialization(doctor.getSpecialization())) {
                            doctorTree.insert(doctor);
                            return "Doctor (" + doctor.getDoctorID() + ") is successfully registered.";
                        }
                        else
                            return "Please enter a valid specialization.";
                    }
                    else
                        return "Invalid Email Address Format.";
                }
                else
                    return "Invalid Contact Number. It should be in format of 01X-XXXXXXXX or 01XXXXXXXXX.";
            }
            else
                return "Invalid Doctor Name. Only Alphabets are allowed.";
        }
        return "Ensure all fields are properly filled before proceeding.";
    }
    
    private String showDoctorInfo(String doctorID, String doctorName, String contactNo, String email){
        Doctor found = null;
        Doctor searchKey = null;
        
        if (doctorTree.isEmpty())
            return "No Doctor Record.";
        
        if (!doctorID.isEmpty())
            searchKey = new Doctor(doctorID, "", "", "", "");
        else if (!doctorName.isEmpty())
            searchKey = new Doctor("", doctorName, "", "", "");
        else if (!contactNo.isEmpty())
            searchKey = new Doctor("", "", contactNo, "", "");
        else if (!email.isEmpty())
            searchKey = new Doctor("", "", "", email, "");
        else
            return "Please enter a valid detail.";
        
        found = doctorTree.find(searchKey);
        
        if (found == null) {
            String result = !doctorID.isEmpty() ? doctorID :
                    !doctorName.isEmpty() ? doctorName :
                    !contactNo.isEmpty() ? contactNo :
                    !email.isEmpty() ? email : "unknown";
            return "No doctor (" + result + ") found.";
        } else
            return found.toString();
    }
    
    private int doctorInfoModification(String doctorID){
        Doctor searchKey = new Doctor(doctorID, "", "", "", "");
        Doctor doctor = doctorTree.find(searchKey);
        String dividerLine = "-".repeat(50);
        boolean valid = false;
        int result = -1;
        
        if (doctor == null)
            result = 0;
        else {
            String infoDisplay = "Profile Information\n" + dividerLine + "\n" + showDoctorInfo(doctorID, "", "", "") + "\n" +
                                  dividerLine + "\n[1] Doctor Name\n[2] Contact Number\n[3] Email Address\n[4] Specialization\n";
            String choice = JOptionPane.showInputDialog(null, infoDisplay, "Modify Information", JOptionPane.INFORMATION_MESSAGE);

            switch(choice){
                case "1":
                    while (!valid) {
                        String newName = JOptionPane.showInputDialog(null, "Enter new name: ", "New Doctor Name", JOptionPane.INFORMATION_MESSAGE);

                        if (validate.validName(newName)) {
                            doctor.setDoctorName(newName);
                            valid = true;
                        }
                        else
                            JOptionPane.showMessageDialog(null, "\"" + newName + "\" is not valid. Please try again.", "Invalid Name", JOptionPane.ERROR_MESSAGE);
                    }
                    result = 1;
                    break;
                case "2":
                    while (!valid) {
                        String newContact = JOptionPane.showInputDialog(null, "Enter new contact number: ", "New Contact Number", JOptionPane.INFORMATION_MESSAGE);
                        
                        if (validate.validPhone(newContact)) {
                            doctor.setContactNo(newContact);
                            valid = true;
                        }
                        else
                            JOptionPane.showMessageDialog(null, "\"" + newContact + "\" is not valid. Please try again.", "Invalid Contact Number", JOptionPane.ERROR_MESSAGE);
                    }
                    result = 2;
                    break;
                case "3":
                    while (!valid) {
                        String newEmail = JOptionPane.showInputDialog(null, "Enter new email address: ", "New Email Address", JOptionPane.INFORMATION_MESSAGE);
                        
                        if (validate.validEmail(newEmail)) {
                            doctor.setEmail(newEmail);
                            valid = true;
                        }
                        else
                            JOptionPane.showMessageDialog(null, "\"" + newEmail + "\" is not valid. Please try again.", "Invalid Email Address", JOptionPane.ERROR_MESSAGE);
                    }
                    result = 3;
                    break;
                case "4":
                    while (!valid) {
                        String newSpecialization = JOptionPane.showInputDialog(null, "Enter new specialization: ", "New Specialization", JOptionPane.INFORMATION_MESSAGE);
                        
                        if (validate.validSpecialization(newSpecialization)) {
                            doctor.setSpecialization(newSpecialization);
                            valid = true;
                        }
                        else
                            JOptionPane.showMessageDialog(null, "\"" + newSpecialization + "\" is not valid. Please try again.", "Invalid Specialization", JOptionPane.ERROR_MESSAGE);
                    }
                    result = 4;
                    break;
                default:
                    JOptionPane.showMessageDialog(null, "Please enter a valid choice.", "Invalid Option Selected", JOptionPane.ERROR_MESSAGE);
            }
        }
        return result;
    }
    
    public boolean doctorRemover(String doctorID, String contactNo){
        Doctor searchKey = new Doctor (doctorID, "", contactNo, "", "");
        Doctor found = doctorTree.find(searchKey);
        
        if (found == null)
            return false;
        else {
            doctorTree.delete(found);
            return true;
        }
    }
}