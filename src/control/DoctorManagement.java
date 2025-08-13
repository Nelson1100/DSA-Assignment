package control;

// Manage doctor information, duty schedules and availability tracking.

import adt.AVL_Implementation;
import adt.AVL_Tree;
import entity.Doctor;
import javax.swing.JOptionPane;

public class DoctorManagement {
    
    AVL_Implementation<Doctor> doctorTree = new AVL_Tree<>();
    
    public boolean validName(String name){
        return name.matches("[A-Za-z ]+");
    }
    
    public boolean validPhone(String phone){
        return phone.matches("^011[0-9]{8}$|^01(0|2|3|4|5|6|7|8|9)[0-9]{7}$");
    }
    
    public boolean validEmail(String email){
        return email.matches("^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,6}$");
    }
    
    public boolean validSpecialization(String specialization){
        return specialization.matches("^[A-Za-z ]+$") && specialization.length() >= 3 && specialization.length() <= 50;
    }
    
    public void registerDoctor(Doctor doctor){
        String result = doctorRegistration(doctor);
        JOptionPane.showMessageDialog(null, result, "Doctor Registration", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private String doctorRegistration(Doctor doctor){
        String phone = doctor.getContactNo().replaceAll("[-\\s]", "");
        
        if (!doctor.getDoctorID().isEmpty() && !doctor.getDoctorName().isEmpty() &&
            !doctor.getContactNo().isEmpty() && !doctor.getEmail().isEmpty() &&
            !doctor.getSpecialization().isEmpty()){
            if (validName(doctor.getDoctorName())) {
                if (validPhone(phone)){
                    phone = phone.substring(0, 3) + "-" + phone.substring(3);
                    doctor.setContactNo(phone);
                    if (validEmail(doctor.getEmail())) {
                        if (validSpecialization(doctor.getSpecialization())) {
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
    
    public void viewDoctorInfo(String doctorID){
        String result = showDoctorInfo(doctorID);
        
        if (result.startsWith("No doctor"))
            JOptionPane.showMessageDialog(null, result, "Doctor Information", JOptionPane.ERROR_MESSAGE);
        else
            JOptionPane.showMessageDialog(null, result, "Doctor Information", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private String showDoctorInfo(String doctorID){
        if (doctorID == null || doctorID.isEmpty())
            return "Invalid DoctorID Entered.";
        
        Doctor searchKey = new Doctor(doctorID, "", "", "", "");
        Doctor found = doctorTree.find(searchKey);
        
        if (found == null)
            return "No doctor (" + doctorID + ") found.";
        else
            return found.toString();
    }
    
    public void updateDoctorInfo(String doctorID){
        Doctor searchKey = new Doctor(doctorID, "", "", "", "");
        Doctor doctor = doctorTree.find(searchKey);
        String dividerLine = "-".repeat(50);
        boolean valid = false;
        
        if (doctor == null)
            JOptionPane.showMessageDialog(null, "No doctor found.", "Invalid Doctor", JOptionPane.ERROR_MESSAGE);
        else {
            String infoDisplay = "Profile Information\n" + dividerLine + "\n" + showDoctorInfo(doctorID) + "\n" +
                                  dividerLine + "\n[1] Doctor Name\n[2] Contact Number\n[3] Email Address\n[4] Specialization\n";
            String choice = JOptionPane.showInputDialog(null, infoDisplay, "Modify Information", JOptionPane.INFORMATION_MESSAGE);

            switch(choice){
                case "1":
                    while (!valid) {
                        String newName = JOptionPane.showInputDialog(null, "Enter new name: ", "Doctor Name", JOptionPane.INFORMATION_MESSAGE);

                        if (validName(newName)) {
                            doctor.setDoctorName(newName);
                            valid = true;
                        }
                        else
                            JOptionPane.showMessageDialog(null, "\"" + newName + "\" is not valid. Please try again.", "Invalid Name", JOptionPane.ERROR_MESSAGE);
                    }
                    break;
                default:
                    JOptionPane.showMessageDialog(null, "Please enter a valid choice.", "Invalid Option Selected", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
//    public boolean RemoveDoctor(){
//        return false;
//    }
//    
//    public
    
}
