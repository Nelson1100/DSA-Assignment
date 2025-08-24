package control;

import adt.AVLTree;
import control.StockMaintenance;
import entity.Medicine;
import entity.StockBatch;

import java.time.LocalDate;

public class StockMaintenanceTest {
    public static void main(String[] args) {
        StockMaintenance stock = new StockMaintenance();

        // Add medicines
        stock.addMedicine(new Medicine("MED001", "Paracetamol", "Painkiller", "500mg", 2.00));
        stock.addMedicine(new Medicine("MED002", "Amoxicillin", "Antibiotic", "250mg", 1.50));

        // Add stock batches
        stock.addStockBatch(new StockBatch("MED001", "B001", 100, LocalDate.of(2025, 10, 1), LocalDate.of(2025, 7, 1)));
        stock.addStockBatch(new StockBatch("MED001", "B002", 80, LocalDate.of(2025, 9, 1), LocalDate.of(2025, 7, 15)));
        stock.addStockBatch(new StockBatch("MED002", "B101", 50, LocalDate.of(2025, 8, 15), LocalDate.of(2025, 7, 10)));

        // List all batches
        System.out.println("\nAll batches:");
        stock.listAllBatches();

        // Get next batch for MED001 (should be B002 due to earlier expiry)
        System.out.println("\nNext batch to dispense for MED001:");
        StockBatch next = stock.getNextBatchToDispense("MED001");
        if (next != null) {
            System.out.println(next);
        } else {
            System.out.println("No batch available.");
        }

        // Simulate dispensing 30 units from that batch
        if (next != null) {
            next.deduct(30);
            System.out.println("\nAfter dispensing 30 units:");
            System.out.println(next);
        }

        // Remove expired (simulate today = 2025-08-20)
        System.out.println("\nRemoving expired stock as of 2025-08-20...");
        stock.removeExpiredStock(LocalDate.of(2025, 8, 20));

        System.out.println("\nRemaining batches:");
        stock.listAllBatches();
    }
}
