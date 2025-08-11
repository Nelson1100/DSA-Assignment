package entity;

import java.time.LocalTime;

public class Patient {
    private String patientID;
    private String patientName;
    private String contactNo;
    private String email;
    private Gender gender;
    private int age;
    private VisitType visitType;
    private LocalTime arrivalTime;
    
    // constructors
    public Patient() {
        this("", "", "", "", Gender.MALE, 0, VisitType.WALK_IN, LocalTime.now());
    }
    
    public Patient(String patientID, String patientName, String contactNo, String email,
                   Gender gender, int age, VisitType visitType, LocalTime arrivalTime) {
        this.patientID = patientID;
        this.patientName = patientName;
        this.contactNo = contactNo;
        this.email = email;
        this.gender = gender;
        this.age = age;
        this.visitType = visitType;
        this.arrivalTime = arrivalTime;
    }
    
    // getters
    public String getPatientID() {
        return patientID;
    }
    
    public String getPatientName() {
        return patientName;
    }
    
    public String getContactNo() {
        return contactNo;
    }
    
    public String getEmail() {
        return email;
    }
    
    public Gender getGender() {
        return gender;
    }
    
    public int getAge() {
        return age;
    }
    
    public VisitType getVisitType() {
        return visitType;
    }
    
    public LocalTime getArrivalTime() {
        return arrivalTime;
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
    
    public void setGender(Gender gender) {
        this.gender = gender;
    }
    
    public void setAge(int age) {
        this.age = age;
    }
    
    public void setVisitType(VisitType visitType) {
        this.visitType = visitType;
    }
    
    public void setArrivalTime(LocalTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }
    
    // other methods
    @Override
    public String toString() {
//        return String.format("Patient ID: %s | Name: %s | Gender: %s | Age: %d | Email: %s | Contact: %s | Visit Type: %s | Arrival: %s",
//                patientID, patientName, gender, age, email, contactNo, visitType, arrivalTime);
        return String.format(
            "----------------------------------------%n" +
            "Patient ID : %s%n" +
            "Name       : %s%n" +
            "Gender     : %s%n" +
            "Age        : %d%n" +
            "Email      : %s%n" +
            "Contact    : %s%n" +
            "Visit Type : %s%n" +
            "Arrival    : %s%n" +
            "----------------------------------------",
            patientID,
            patientName,
            gender,
            age,
            email,
            contactNo,
            visitType,
            arrivalTime
    );
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) 
            return true;
        
        if (obj == null || getClass() != obj.getClass()) 
            return false;
        
        Patient other = (Patient) obj;
        return patientID.equals(other.patientID);
    }
    
    @Override
    public int hashCode() {
        return patientID != null ? patientID.hashCode() : 0;
    }
}
