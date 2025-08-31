package dao;

import control.StockMaintenance;
import entity.MedicineName;
import java.time.LocalDate;

public class StockInitializer {

    public static void initialize(StockMaintenance sm) {
        // === EXPIRED BATCHES FIRST (2022–2023 received) ===

        sm.addBatch(MedicineName.PARACETAMOL, "SB202208010001", 500, LocalDate.of(2022, 8, 1), LocalDate.of(2024, 8, 1));
        sm.addBatch(MedicineName.PARACETAMOL, "SB202305150001", 400, LocalDate.of(2023, 5, 15), LocalDate.of(2025, 5, 15));

        sm.addBatch(MedicineName.INSULIN, "SB202207100001", 100, LocalDate.of(2022, 7, 10), LocalDate.of(2024, 7, 10));
        sm.addBatch(MedicineName.INSULIN, "SB202304200001", 80, LocalDate.of(2023, 4, 20), LocalDate.of(2025, 4, 20));

        sm.addBatch(MedicineName.CETIRIZINE, "SB202203050001", 10, LocalDate.of(2022, 3, 5), LocalDate.of(2024, 3, 5));
        sm.addBatch(MedicineName.CETIRIZINE, "SB202302250001", 12, LocalDate.of(2023, 2, 25), LocalDate.of(2025, 2, 25));

        sm.addBatch(MedicineName.ASPIRIN, "SB202201010001", 300, LocalDate.of(2022, 1, 1), LocalDate.of(2024, 1, 1));
        sm.addBatch(MedicineName.ASPIRIN, "SB202303310001", 200, LocalDate.of(2023, 3, 31), LocalDate.of(2025, 3, 31));

        sm.addBatch(MedicineName.IBUPROFEN, "SB202206150001", 250, LocalDate.of(2022, 6, 15), LocalDate.of(2024, 6, 15));
        sm.addBatch(MedicineName.IBUPROFEN, "SB202304010001", 180, LocalDate.of(2023, 4, 1), LocalDate.of(2025, 4, 1));

        sm.addBatch(MedicineName.VITAMIN_C, "SB202205200001", 300, LocalDate.of(2022, 5, 20), LocalDate.of(2024, 5, 20));
        sm.addBatch(MedicineName.VITAMIN_C, "SB202301300001", 200, LocalDate.of(2023, 1, 30), LocalDate.of(2025, 1, 30));

        // === CURRENT VALID BATCHES ===
        LocalDate today = LocalDate.now();

        sm.addBatch(MedicineName.PARACETAMOL, "SB" + today.minusDays(10).format(java.time.format.DateTimeFormatter.BASIC_ISO_DATE) + "0001", 1000, today.minusDays(10), today.plusMonths(4));
        sm.addBatch(MedicineName.PARACETAMOL, "SB" + today.minusDays(5).format(java.time.format.DateTimeFormatter.BASIC_ISO_DATE) + "0001", 600, today.minusDays(5), today.plusMonths(6));

        sm.addBatch(MedicineName.INSULIN, "SB" + today.minusDays(3).format(java.time.format.DateTimeFormatter.BASIC_ISO_DATE) + "0001", 150, today.minusDays(3), today.plusWeeks(3));

        sm.addBatch(MedicineName.CETIRIZINE, "SB" + today.minusDays(2).format(java.time.format.DateTimeFormatter.BASIC_ISO_DATE) + "0001", 8, today.minusDays(2), today.plusDays(5));

        sm.addBatch(MedicineName.ASPIRIN, "SB" + today.minusDays(1).format(java.time.format.DateTimeFormatter.BASIC_ISO_DATE) + "0001", 150, today.minusDays(1), today.plusMonths(2));

        sm.addBatch(MedicineName.IBUPROFEN, "SB" + today.minusDays(3).format(java.time.format.DateTimeFormatter.BASIC_ISO_DATE) + "0001", 200, today.minusDays(3), today.plusDays(20));
        sm.addBatch(MedicineName.IBUPROFEN, "SB" + today.minusDays(3).format(java.time.format.DateTimeFormatter.BASIC_ISO_DATE) + "0002", 300, today.minusDays(3), today.plusDays(5));

        sm.addBatch(MedicineName.VITAMIN_C, "SB" + today.minusDays(5).format(java.time.format.DateTimeFormatter.BASIC_ISO_DATE) + "0001", 500, today.minusDays(5), today.plusMonths(2));
    }
}
