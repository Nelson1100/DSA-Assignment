package utility;

import java.time.*;
import java.time.format.*;
import entity.Gender;

public final class Validation {
    public Validation() {} // prevent instantiation of this utility class
    
    private static final DateTimeFormatter STRICT_ISO = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    // Validation
    public static boolean validName(String name){
        if (name == null)
            return false;
        
        return name.matches("[A-Za-z ]+");
    }
    
    public static boolean validPhone(String phone) {
        if (phone == null)
            return false;
        
        phone = standardizedPhone(phone);
        return phone.matches("^011-[0-9]{8}$|^01(0|2|3|4|5|6|7|8|9)-[0-9]{7}$");
    }
    
    public static boolean validEmail(String email){
        if (email == null)
            return false;
        
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
    public static String standardizedPhone(String phoneNo) {
        phoneNo = phoneNo.replaceAll("[-\\s]", "");
        
        if (phoneNo.length() >= 3)
            phoneNo = phoneNo.substring(0, 3) + "-" + phoneNo.substring(3);
        
        return phoneNo;
    }
    
    public String standardizedName(String name){
        if (!validName(name))
            return null;
        
        String[] parts = name.trim().toLowerCase().split("\\s+");
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < parts.length; i++) {
            if (parts[i].length() > 0) {
                sb.append(Character.toUpperCase(parts[i].charAt(0)));
                if (parts[i].length() > 1) {
                    sb.append(parts[i].substring(1));
                }
            }
            if (i < parts.length - 1) {
                sb.append(" ");
            }
        }
        return sb.toString();
    }
    
    public boolean validBatchID(String batchID) {
        return batchID != null && batchID.matches("^SB\\d{5}$");
    }
    
    public boolean validQuantity(String input) {
        try {
            int qty = Integer.parseInt(input);
            return qty >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}