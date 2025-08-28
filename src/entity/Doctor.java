package entity;

import entity.Specialization;
import utility.Validation;

public class Doctor implements Comparable<Doctor> {

    private String doctorID;
    private String doctorName;
    private String contactNo;
    private String email;
    private Specialization specialization;
    private String icNo;
    Validation validate = new Validation();
    
    // Constructor
    public Doctor(){
        this("","","","", null, "");
    }
    
    public Doctor(String doctorID, String doctorName, String contactNo, String email, Specialization specialization, String icNo){
        this.doctorID = doctorID;
        this.doctorName = doctorName;
        this.contactNo = contactNo;
        this.email = email;
        this.specialization = specialization;
        this.icNo = icNo;
    }
    
    // Getter
    public String getDoctorID(){
        return this.doctorID;
    }
    
    public String getDoctorName(){
        return this.doctorName;
    }
    
    public String getContactNo(){
        return this.contactNo;
    }
    
    public String getEmail(){
        return this.email;
    }
    
    public Specialization getSpecialization(){
        return this.specialization;
    }
    
    public String getIcNo(){
        return this.icNo;
    }
    
    // Setter
    public void setDoctorID(String doctorID){
        this.doctorID = doctorID;
    }
    
    public void setDoctorName(String doctorName){
        this.doctorName = doctorName;
    }
    
    public void setContactNo(String contactNo){
        this.contactNo = contactNo;
    }
    
    public void setEmail(String email){
        this.email = email;
    }
    
    public void setSpecialization(Specialization specialization){
        this.specialization = specialization;
    }
    
    public void setIcNo(String icNo){
        this.icNo = icNo;
    }
    
    // Other methods
    public Doctor(Doctor other) {
        this.doctorID = other.doctorID;
        this.doctorName = other.doctorName;
        this.contactNo = other.contactNo;
        this.email = other.email;
        this.specialization = other.specialization;
        this.icNo = other.icNo;
    }
    
    public Doctor clone() {
        return new Doctor(this);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Doctor)) return false;
        Doctor other = (Doctor) o;
        return this.doctorID != null && this.doctorID.equals(other.doctorID);
    }

    @Override
    public int hashCode() {
        return (doctorID == null) ? 0 : doctorID.hashCode();
    }

    @Override
    public String toString() {
        return "Doctor ID: " + doctorID + "\n" +
               "Doctor Name: " + doctorName + "\n" +
               "IC Number: " + icNo + "\n" +
               "Gender: " + validate.getGenderFromIC(icNo) + "\n" +
               "Contact No: " + contactNo + "\n" +
               "Email Address: " + email + "\n" +
               "Specialization: " + specialization;
    }

    @Override
    public int compareTo(Doctor o) {
        return this.doctorID.compareTo(o.doctorID);
    }
}
