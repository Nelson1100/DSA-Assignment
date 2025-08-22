package utility;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import entity.Gender;

public final class Validation {
    public Validation() {} // prevent instantiation of this utility class
    
    private static final DateTimeFormatter STRICT_ISO = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    // Validation
    public static boolean validName(String name){
        return name.matches("[A-Za-z ]+");
    }
    
    public static boolean validPhone(String phone) {
        phone = standardizedPhone(phone);
        return phone.matches("^011-[0-9]{8}$|^01(0|2|3|4|5|6|7|8|9)-[0-9]{7}$");
    }
    
    public static boolean validEmail(String email){
        return email.matches("^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,6}$");
    }
    
    public static boolean validSpecialization(String specialization){
        return specialization.matches("^[A-Za-z ]+$") 
                && specialization.length() >= 3 
                && specialization.length() <= 50;
    }
    
    public static boolean validGender(String gender) {
        try {
            Gender.valueOf(gender.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
    
    public static boolean validNumber(String value, int min, int max) {
        try {
            int num = Integer.parseInt(value);
            return num >= min && num <= max;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    public static boolean isValidISODate(String dateStr) {
        try {
            LocalDate.parse(dateStr, STRICT_ISO);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
    
    // Formatting
    public static String standardizedPhone(String phoneNo) {
        phoneNo = phoneNo.replaceAll("[-\\s]", "");
        
        if (phoneNo.length() >= 3)
            phoneNo = phoneNo.substring(0, 3) + "-" + phoneNo.substring(3);
        
        return phoneNo;
    }
}