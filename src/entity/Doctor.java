package entity;

import entity.Specialization;

public class Doctor implements Comparable<Doctor> {

    private String doctorID;
    private String doctorName;
    private String contactNo;
    private String email;
    private Specialization specialization;
    
    // Constructor
    public Doctor(){
        this("","","","", null);
    }
    
    public Doctor(String doctorID, String doctorName, String contactNo, String email, Specialization specialization){
        this.doctorID = doctorID;
        this.doctorName = doctorName;
        this.contactNo = contactNo;
        this.email = email;
        this.specialization = specialization;
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
    
    // Other methods
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
               "Contact No: " + contactNo + "\n" +
               "Email Address: " + email + "\n" +
               "Specialization: " + specialization;
    }

    @Override
    public int compareTo(Doctor o) {
        return this.doctorID.compareTo(o.doctorID);
    }
}
