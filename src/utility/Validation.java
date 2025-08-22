package utility;

import entity.Specialization;
import java.time.*;
import java.time.format.*;
import entity.Gender;

public class Validation {
    private static final DateTimeFormatter STRICT_ISO = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    // Validation
    public boolean validName(String name){
        return name.matches("[A-Za-z ]+");
    }
    
    public boolean validPhone(String phone) {
        phone = standardizedPhone(phone);
        return phone.matches("^011-[0-9]{8}$|^01(0|2|3|4|5|6|7|8|9)-[0-9]{7}$");
    }
    
    public boolean validEmail(String email){
        return email.matches("^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,6}$");
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
    
    public boolean isWeekday(LocalDate date){
        DayOfWeek day = date.getDayOfWeek();
        return day != DayOfWeek.SATURDAY && day != DayOfWeek.SUNDAY;
    }
    
    // Formatting
    public String standardizedPhone(String phoneNo) {
        phoneNo = phoneNo.replaceAll("[-\\s]", "");
        
        if (phoneNo.length() >= 3)
            phoneNo = phoneNo.substring(0, 3) + "-" + phoneNo.substring(3);
        
        return phoneNo;
    }
}