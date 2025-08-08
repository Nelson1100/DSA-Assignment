package entity;

public class DoctorManagement {

    private String doctorID;
    private String doctorName;
    private String contactNo;
    private String email;
    private String specialization;
    
    public DoctorManagement(){
        this("","","","","");
    }
    
    public DoctorManagement(String doctorID, String doctorName, String contactNo, String email, String specialization){
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
    
    // 2 reporting summary
}
