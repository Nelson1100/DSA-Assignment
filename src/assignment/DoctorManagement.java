package assignment;

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
    
}
