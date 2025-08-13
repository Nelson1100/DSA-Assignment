package utility;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public final class IDGenerator {
    private static final DateTimeFormatter YMD = DateTimeFormatter.BASIC_ISO_DATE; // yyyyMMdd
    private static final int N = IDType.values().length; // total enum constants
    
    // One slot per type
    private static final LocalDate[] lastDate = new LocalDate[N];
    private static final int[]       seq      = new int[N];
    
    private static final int DEFAULT_PAD = 4;
    
    private IDGenerator() {} // prevent instantiation of this utility class
    
    public static synchronized String next(IDType type) { // default 4 digits
        return next(type, DEFAULT_PAD);
    }
    
    public static synchronized String next(IDType type, int padWidth) {
        int i = type.ordinal(); // 0 for PATIENT, 1 for PRESCRIPTION, etc
        LocalDate today = LocalDate.now();
        
        // Daily reset (per type)
        if (lastDate[i] == null || !today.equals(lastDate[i])) {
            lastDate[i] = today;
            seq[i] = 0;
        }
        seq[i]++; // increment that type's counter
        
        String date  = today.format(YMD);                            // "20250811"
        String count = String.format("%0" + padWidth + "d", seq[i]); // "0001"
        return type.getPrefix() + date + count;                      // "P202508110001
    }
}
