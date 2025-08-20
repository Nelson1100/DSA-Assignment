package utility;

public class Validation {

    // Formatting and Validation
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
    
}
