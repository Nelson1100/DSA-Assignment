package control;

// Manage doctor information, duty schedules and availability tracking.

import adt.AVLInterface;
import adt.AVLTree;
import entity.Doctor;
import entity.Specialization;
import utility.*;

public class DoctorManagement {
    AVLInterface<Doctor> doctorTree = new AVLTree<>();
    Validation validate = new Validation();
    
    public boolean isEmptyTree() {
        return doctorTree.isEmpty();
    }
        
    // Abstract Classes
    public boolean registerDoctor(Doctor doctor){
        return doctorRegistration(doctor);
    }
    
    public Doctor findDoctor(Doctor doctor){
        Doctor found = searchByKey(doctor);
        
        if (found == null)
            return null;
        else
            return found;
    }
    
    public boolean updateDoctor(Doctor doctor, int infoSelected, String newName, String newPhone, String newEmail, Specialization newSpecialization){
        Doctor selectedDoc = searchByKey(doctor);
        
        switch (infoSelected){
            case 1:
                return modifyName(selectedDoc, newName);
            case 2:
                return modifyPhone(selectedDoc, newPhone);
            case 3:
                return modifyEmail(selectedDoc, newEmail);
            case 4:
                return modifySpecialization(selectedDoc, newSpecialization);
        }
        return false;
    }
    
    public boolean removeDoctor(Doctor doctor){
        Doctor selectedDoc = searchByKey(doctor);
        return eraseDoctor(selectedDoc);
    }
    
    // Implementation Classes
    private boolean doctorRegistration(Doctor doctor){
        return doctorTree.insert(doctor);
    }
    
    private Doctor searchByKey(Doctor doctor){
        for (Doctor doc : getAllDoctor()) {
            if (!doctor.getDoctorID().isEmpty() && doctor.getDoctorID().equals(doc.getDoctorID()))
                return doc;
            if (!doctor.getDoctorName().isEmpty() && doctor.getDoctorName().equals(doc.getDoctorName()))
                return doc;
            if (!doctor.getContactNo().isEmpty() && doctor.getContactNo().equals(doc.getContactNo()))
                return doc;
            if (!doctor.getEmail().isEmpty() && doctor.getEmail().equals(doc.getEmail()))
                return doc;
        }
        return null;
    }
    
    private Doctor[] getAllDoctor() {
        Doctor[] doctors = new Doctor[doctorTree.size()];
        return doctorTree.toArrayInorder(doctors);
    }
    
    private boolean modifyName(Doctor doctor, String newName){
        if (validate.validName(newName) && !newName.equals(doctor.getDoctorName())){
            doctor.setDoctorName(newName);
            return true;
        }
        return false;
    }
    
    private boolean modifyPhone(Doctor doctor, String newPhone){
        if (validate.validPhone(newPhone) && !newPhone.equals(doctor.getContactNo())){
            doctor.setContactNo(newPhone);
            return true;
        }
        return false;
    }
    
    private boolean modifyEmail(Doctor doctor, String newEmail){
        if (validate.validEmail(newEmail) && !newEmail.equals(doctor.getEmail())){
            doctor.setEmail(newEmail);
            return true;
        }
        return false;
    }
    
    private boolean modifySpecialization(Doctor doctor, Specialization newSpecialization){
        if (!newSpecialization.equals(doctor.getSpecialization())){
            doctor.setSpecialization(newSpecialization);
            return true;
        }
        return false;
    }
    
    private boolean eraseDoctor(Doctor doctor){
        return doctorTree.delete(doctor);
    }
}