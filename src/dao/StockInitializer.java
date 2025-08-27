package dao;

import entity.MedicineName;
import control.StockMaintenance;
import java.time.LocalDate;

public class StockInitializer {

    public static void initialize(StockMaintenance sm) {
        LocalDate today = LocalDate.now();

        sm.addBatch(MedicineName.PARACETAMOL, "SB0001", 50, LocalDate.of(2025, 9, 15), LocalDate.of(2025, 8, 1));
        sm.addBatch(MedicineName.PARACETAMOL, "SB0002", 30, LocalDate.of(2025, 10, 10), LocalDate.of(2025, 8, 5));
        sm.addBatch(MedicineName.PARACETAMOL, "SB0003", 25, LocalDate.of(2025, 8, 28), LocalDate.of(2025, 7, 20));
        sm.addBatch(MedicineName.PARACETAMOL, "SB0004", 100, LocalDate.of(2025, 12, 5), LocalDate.of(2025, 8, 10));
        sm.addBatch(MedicineName.PARACETAMOL, "SB0005", 10, LocalDate.of(2025, 9, 1), LocalDate.of(2025, 7, 30));
        sm.addBatch(MedicineName.PARACETAMOL, "SB0006", 60, LocalDate.of(2025, 11, 11), LocalDate.of(2025, 8, 15));
        sm.addBatch(MedicineName.PARACETAMOL, "SB0007", 40, LocalDate.of(2025, 10, 1), LocalDate.of(2025, 8, 3));
        sm.addBatch(MedicineName.PARACETAMOL, "SB0008", 15, LocalDate.of(2025, 8, 26), LocalDate.of(2025, 7, 18));
        sm.addBatch(MedicineName.PARACETAMOL, "SB0009", 5, LocalDate.of(2025, 8, 20), LocalDate.of(2025, 7, 10));
        sm.addBatch(MedicineName.PARACETAMOL, "SB0010", 80, LocalDate.of(2025, 12, 20), LocalDate.of(2025, 8, 20));

// AMOXICILLIN
        sm.addBatch(MedicineName.AMOXICILLIN, "SB0011", 35, LocalDate.of(2025, 9, 25), LocalDate.of(2025, 8, 2));
        sm.addBatch(MedicineName.AMOXICILLIN, "SB0012", 20, LocalDate.of(2025, 10, 5), LocalDate.of(2025, 8, 3));
        sm.addBatch(MedicineName.AMOXICILLIN, "SB0013", 45, LocalDate.of(2025, 8, 29), LocalDate.of(2025, 7, 15));
        sm.addBatch(MedicineName.AMOXICILLIN, "SB0014", 70, LocalDate.of(2025, 12, 10), LocalDate.of(2025, 8, 12));
        sm.addBatch(MedicineName.AMOXICILLIN, "SB0015", 18, LocalDate.of(2025, 9, 3), LocalDate.of(2025, 7, 28));
        sm.addBatch(MedicineName.AMOXICILLIN, "SB0016", 55, LocalDate.of(2025, 11, 18), LocalDate.of(2025, 8, 14));
        sm.addBatch(MedicineName.AMOXICILLIN, "SB0017", 28, LocalDate.of(2025, 10, 9), LocalDate.of(2025, 8, 6));
        sm.addBatch(MedicineName.AMOXICILLIN, "SB0018", 12, LocalDate.of(2025, 8, 25), LocalDate.of(2025, 7, 22));
        sm.addBatch(MedicineName.AMOXICILLIN, "SB0019", 6, LocalDate.of(2025, 8, 22), LocalDate.of(2025, 7, 13));
        sm.addBatch(MedicineName.AMOXICILLIN, "SB0020", 90, LocalDate.of(2025, 12, 22), LocalDate.of(2025, 8, 22));
        
        // COUGH_SYRUP
        sm.addBatch(MedicineName.COUGH_SYRUP, "SB00021", 60, LocalDate.now().plusDays(45), LocalDate.now().minusDays(3));
        sm.addBatch(MedicineName.COUGH_SYRUP, "SB00022", 20, LocalDate.now().plusDays(10), LocalDate.now().minusDays(1));
        sm.addBatch(MedicineName.COUGH_SYRUP, "SB00023", 0, LocalDate.now().plusDays(20), LocalDate.now().minusDays(4));
        sm.addBatch(MedicineName.COUGH_SYRUP, "SB00024", 5, LocalDate.now().plusDays(5), LocalDate.now().minusDays(7));
        sm.addBatch(MedicineName.COUGH_SYRUP, "SB00025", 100, LocalDate.now().plusDays(120), LocalDate.now().minusDays(30));
        sm.addBatch(MedicineName.COUGH_SYRUP, "SB00026", 10, LocalDate.now().plusDays(15), LocalDate.now().minusDays(5));
        sm.addBatch(MedicineName.COUGH_SYRUP, "SB00027", 80, LocalDate.now().plusDays(200), LocalDate.now().minusDays(60));
        sm.addBatch(MedicineName.COUGH_SYRUP, "SB00028", 25, LocalDate.now().plusDays(40), LocalDate.now().minusDays(9));
        sm.addBatch(MedicineName.COUGH_SYRUP, "SB00029", 45, LocalDate.now().plusDays(90), LocalDate.now().minusDays(20));
        sm.addBatch(MedicineName.COUGH_SYRUP, "SB00030", 15, LocalDate.now().plusDays(30), LocalDate.now().minusDays(12));

// INSULIN
        sm.addBatch(MedicineName.INSULIN, "SB00031", 30, LocalDate.now().plusDays(60), LocalDate.now().minusDays(10));
        sm.addBatch(MedicineName.INSULIN, "SB00032", 0, LocalDate.now().plusDays(25), LocalDate.now().minusDays(4));
        sm.addBatch(MedicineName.INSULIN, "SB00033", 5, LocalDate.now().plusDays(5), LocalDate.now().minusDays(1));
        sm.addBatch(MedicineName.INSULIN, "SB00034", 100, LocalDate.now().plusDays(180), LocalDate.now().minusDays(40));
        sm.addBatch(MedicineName.INSULIN, "SB00035", 20, LocalDate.now().plusDays(20), LocalDate.now().minusDays(5));
        sm.addBatch(MedicineName.INSULIN, "SB00036", 50, LocalDate.now().plusDays(300), LocalDate.now().minusDays(90));
        sm.addBatch(MedicineName.INSULIN, "SB00037", 70, LocalDate.now().plusDays(100), LocalDate.now().minusDays(30));
        sm.addBatch(MedicineName.INSULIN, "SB00038", 40, LocalDate.now().plusDays(150), LocalDate.now().minusDays(20));
        sm.addBatch(MedicineName.INSULIN, "SB00039", 10, LocalDate.now().plusDays(12), LocalDate.now().minusDays(2));
        sm.addBatch(MedicineName.INSULIN, "SB00040", 35, LocalDate.now().plusDays(90), LocalDate.now().minusDays(15));
        
        sm.addBatch(MedicineName.HYDROCORTISONE, "SB00401", 15, LocalDate.of(2025, 9, 30), LocalDate.of(2025, 6, 1));
        sm.addBatch(MedicineName.HYDROCORTISONE, "SB00402", 5, LocalDate.of(2025, 9, 5), LocalDate.of(2025, 7, 15));      // Low qty, expires soon
        sm.addBatch(MedicineName.HYDROCORTISONE, "SB00403", 0, LocalDate.of(2025, 10, 10), LocalDate.of(2025, 6, 10));     // Zero qty
        sm.addBatch(MedicineName.HYDROCORTISONE, "SB00404", 18, LocalDate.of(2025, 8, 25), LocalDate.of(2025, 5, 20));     // Very soon expiry
        sm.addBatch(MedicineName.HYDROCORTISONE, "SB00405", 22, LocalDate.of(2025, 11, 30), LocalDate.of(2025, 6, 25));
        sm.addBatch(MedicineName.HYDROCORTISONE, "SB00406", 10, LocalDate.of(2026, 1, 15), LocalDate.of(2025, 7, 1));
        sm.addBatch(MedicineName.HYDROCORTISONE, "SB00407", 9, LocalDate.of(2025, 9, 20), LocalDate.of(2025, 6, 30));     // Borderline low
        sm.addBatch(MedicineName.HYDROCORTISONE, "SB00408", 25, LocalDate.of(2025, 12, 10), LocalDate.of(2025, 7, 5));
        sm.addBatch(MedicineName.HYDROCORTISONE, "SB00409", 40, LocalDate.of(2026, 2, 15), LocalDate.of(2025, 7, 6));      // High qty
        sm.addBatch(MedicineName.HYDROCORTISONE, "SB00410", 2, LocalDate.of(2025, 8, 28), LocalDate.of(2025, 6, 29));     // Very low, expires immediately
        
        sm.addBatch(MedicineName.IBUPROFEN, "SB00501", 12, LocalDate.of(2025, 10, 5), LocalDate.of(2025, 7, 1));
        sm.addBatch(MedicineName.IBUPROFEN, "SB00502", 0, LocalDate.of(2025, 11, 20), LocalDate.of(2025, 6, 1));         // Used up
        sm.addBatch(MedicineName.IBUPROFEN, "SB00503", 45, LocalDate.of(2026, 3, 12), LocalDate.of(2025, 8, 1));          // High sm
        sm.addBatch(MedicineName.IBUPROFEN, "SB00504", 3, LocalDate.of(2025, 8, 30), LocalDate.of(2025, 6, 10));         // Very low, soon expiry
        sm.addBatch(MedicineName.IBUPROFEN, "SB00505", 20, LocalDate.of(2025, 10, 20), LocalDate.of(2025, 7, 10));
        sm.addBatch(MedicineName.IBUPROFEN, "SB00506", 18, LocalDate.of(2025, 9, 18), LocalDate.of(2025, 6, 25));
        sm.addBatch(MedicineName.IBUPROFEN, "SB00507", 7, LocalDate.of(2025, 10, 12), LocalDate.of(2025, 7, 5));
        sm.addBatch(MedicineName.IBUPROFEN, "SB00508", 25, LocalDate.of(2026, 1, 25), LocalDate.of(2025, 7, 15));
        sm.addBatch(MedicineName.IBUPROFEN, "SB00509", 1, LocalDate.of(2025, 8, 27), LocalDate.of(2025, 6, 20));         // Edge expiry
        sm.addBatch(MedicineName.IBUPROFEN, "SB00510", 10, LocalDate.of(2025, 9, 5), LocalDate.of(2025, 6, 19));          // Expires soon
        
        sm.addBatch(MedicineName.ASPIRIN, "SB00601", 12, LocalDate.of(2025, 9, 18), LocalDate.of(2025, 6, 20));
        sm.addBatch(MedicineName.ASPIRIN, "SB00602", 0, LocalDate.of(2025, 10, 1), LocalDate.of(2025, 6, 21));          // Zero qty
        sm.addBatch(MedicineName.ASPIRIN, "SB00603", 5, LocalDate.of(2025, 8, 28), LocalDate.of(2025, 6, 22));          // Low qty, expiring soon
        sm.addBatch(MedicineName.ASPIRIN, "SB00604", 18, LocalDate.of(2025, 11, 30), LocalDate.of(2025, 7, 1));
        sm.addBatch(MedicineName.ASPIRIN, "SB00605", 25, LocalDate.of(2026, 2, 1), LocalDate.of(2025, 7, 2));            // High qty
        sm.addBatch(MedicineName.ASPIRIN, "SB00606", 9, LocalDate.of(2025, 9, 25), LocalDate.of(2025, 6, 30));
        sm.addBatch(MedicineName.ASPIRIN, "SB00607", 14, LocalDate.of(2025, 12, 10), LocalDate.of(2025, 7, 4));
        sm.addBatch(MedicineName.ASPIRIN, "SB00608", 2, LocalDate.of(2025, 9, 1), LocalDate.of(2025, 6, 25));           // Very low qty
        sm.addBatch(MedicineName.ASPIRIN, "SB00609", 21, LocalDate.of(2025, 10, 15), LocalDate.of(2025, 7, 6));
        sm.addBatch(MedicineName.ASPIRIN, "SB00610", 7, LocalDate.of(2025, 8, 29), LocalDate.of(2025, 6, 26));          // Short expiry

        sm.addBatch(MedicineName.CETIRIZINE, "SB00701", 16, LocalDate.of(2025, 10, 5), LocalDate.of(2025, 6, 20));
        sm.addBatch(MedicineName.CETIRIZINE, "SB00702", 4, LocalDate.of(2025, 8, 27), LocalDate.of(2025, 6, 21));       // Very low qty, expiring soon
        sm.addBatch(MedicineName.CETIRIZINE, "SB00703", 0, LocalDate.of(2025, 9, 15), LocalDate.of(2025, 6, 22));       // Depleted
        sm.addBatch(MedicineName.CETIRIZINE, "SB00704", 30, LocalDate.of(2025, 12, 1), LocalDate.of(2025, 7, 1));        // High qty
        sm.addBatch(MedicineName.CETIRIZINE, "SB00705", 10, LocalDate.of(2025, 9, 5), LocalDate.of(2025, 6, 25));
        sm.addBatch(MedicineName.CETIRIZINE, "SB00706", 6, LocalDate.of(2025, 9, 10), LocalDate.of(2025, 6, 30));
        sm.addBatch(MedicineName.CETIRIZINE, "SB00707", 20, LocalDate.of(2026, 1, 20), LocalDate.of(2025, 7, 2));
        sm.addBatch(MedicineName.CETIRIZINE, "SB00708", 1, LocalDate.of(2025, 8, 28), LocalDate.of(2025, 6, 23));       // Edge case
        sm.addBatch(MedicineName.CETIRIZINE, "SB00709", 13, LocalDate.of(2025, 10, 15), LocalDate.of(2025, 6, 29));
        sm.addBatch(MedicineName.CETIRIZINE, "SB00710", 8, LocalDate.of(2025, 9, 18), LocalDate.of(2025, 6, 28));

        sm.addBatch(MedicineName.VITAMIN_C, "SB00801", 22, LocalDate.of(2025, 10, 8), LocalDate.of(2025, 6, 21));
        sm.addBatch(MedicineName.VITAMIN_C, "SB00802", 0, LocalDate.of(2025, 11, 20), LocalDate.of(2025, 6, 22));       // Depleted
        sm.addBatch(MedicineName.VITAMIN_C, "SB00803", 12, LocalDate.of(2025, 9, 5), LocalDate.of(2025, 6, 23));
        sm.addBatch(MedicineName.VITAMIN_C, "SB00804", 7, LocalDate.of(2025, 8, 29), LocalDate.of(2025, 6, 25));        // Short expiry
        sm.addBatch(MedicineName.VITAMIN_C, "SB00805", 20, LocalDate.of(2026, 1, 10), LocalDate.of(2025, 7, 1));         // Long expiry
        sm.addBatch(MedicineName.VITAMIN_C, "SB00806", 5, LocalDate.of(2025, 9, 9), LocalDate.of(2025, 6, 26));
        sm.addBatch(MedicineName.VITAMIN_C, "SB00807", 17, LocalDate.of(2025, 12, 12), LocalDate.of(2025, 7, 5));
        sm.addBatch(MedicineName.VITAMIN_C, "SB00808", 3, LocalDate.of(2025, 8, 27), LocalDate.of(2025, 6, 28));        // Edge expiry
        sm.addBatch(MedicineName.VITAMIN_C, "SB00809", 9, LocalDate.of(2025, 9, 10), LocalDate.of(2025, 6, 29));
        sm.addBatch(MedicineName.VITAMIN_C, "SB00810", 14, LocalDate.of(2025, 10, 12), LocalDate.of(2025, 7, 6));


    }
}
    

