package entity;

public class Doctor implements Comparable<Doctor> {

    private String doctorID;
    private String doctorName;
    private String contactNo;
    private String email;
    private String specialization;
    
    public Doctor(){
        this("","","","","");
    }
    
    public Doctor(String doctorID, String doctorName, String contactNo, String email, String specialization){
        this.doctorID = doctorID;
        this.doctorName = doctorName;
        this.contactNo = contactNo;
        this.email = email;
        this.specialization = specialization;
    }
    
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
    
    public String getSpecialization(){
        return this.specialization;
    }
    
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
    
    public void setSpecialization(String specialization){
        this.specialization = specialization;
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
               "Contact No: " + contactNo + "\n" +
               "Email Address: " + email + "\n" +
               "Specialization: " + specialization;
    }

    @Override
    public int compareTo(Doctor o) {
        return this.doctorID.compareTo(o.doctorID);
    }
}
