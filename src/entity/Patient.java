package entity;

public class Patient {
    private String patientID;
    private String patientName;
    private String contactNo;
    private String email;
    private Gender gender;
    private int age;
    
    // constructors
    public Patient() {
        this("", "", "", "", Gender.MALE, 0);
    }
    
    public Patient(String patientID, String patientName, String contactNo, String email,
                   Gender gender, int age) {
        this.patientID = patientID;
        this.patientName = patientName;
        this.contactNo = contactNo;
        this.email = email;
        this.gender = gender;
        this.age = age;
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
    
    // other methods
    @Override
    public String toString() {
        return String.format(
            "Patient ID : %s%n" +
            "Name       : %s%n" +
            "Gender     : %s%n" +
            "Age        : %d%n" +
            "Email      : %s%n" +
            "Contact    : %s%n",
            patientID,
            patientName,
            gender,
            age,
            email,
            contactNo
        );
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Patient other)) return false;
        return patientID.equals(other.patientID);
    }
    
    @Override
    public int hashCode() {
        return patientID != null ? patientID.hashCode() : 0;
    } 
}
