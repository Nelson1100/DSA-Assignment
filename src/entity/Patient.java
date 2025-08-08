package entity;

public class Patient {
    private String patientID;
    private String patientName;
    private String contactNo;
    private String email; // dk need ma
    public enum visitType {
        WALK_IN, APPOINTMENT
    }
//    private time arrivalTime;
    
    // getters
    public String getPatientID() {
        return this.patientID;
    }
    
    public String getPatientName() {
        return this.patientName;
    }
    
    public String getContactNo() {
        return this.contactNo;
    }
    
    public String getEmail() {
        return this.email;
    }
    
    // setters
    public void setPatientID(String patientID) {
        this.patientID = patientID;
    }
    
    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }
    
    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    // 2 reporting summary
}
