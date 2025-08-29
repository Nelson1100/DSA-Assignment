package boundary;

import control.StockMaintenance;
import control.StockReportGenerator;

import entity.MedicineName;
import entity.StockBatch;

import utility.*;

import javax.swing.JOptionPane;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class StockMaintenanceUI {

    private  StockMaintenance stock = new StockMaintenance();
    private  StockReportGenerator reports;
    private static final Validation validate = new Validation();

    public StockMaintenanceUI(StockMaintenance stock) {
        this.stock = stock;
        this.reports = new StockReportGenerator(stock);
    }

    public void run() {
        int choice;
        do {              
             choice = JOptionPaneConsoleIO.readOption(
                "=== STOCK MAINTENANCE MENU ===",
                "Stock Maintenance",    
                new String[]{
                    "Add Stock Batch",
                    "Search Batch",
                    "Search Medicine",
                    "View All Batches",
                    "Reports",
                    "Exit"
                }
            );

            switch (choice) {
                case 0 -> addStockBatch();
                case 1 -> searchBatch();
                case 2 -> searchByMedicine();
                case 3 -> viewAllBatches();
                case 4 -> showReports();
                case 5 -> JOptionPaneConsoleIO.showInfo("Exiting Stock Maintenance.");
                default -> JOptionPaneConsoleIO.showError("Invalid option selected.");
            }
        } while (choice != 5);
    }

    // ========== OPTION 1 ==========
    private void addStockBatch() {
        MedicineName med = JOptionPaneConsoleIO.readEnum(
                "Select the medicine for this batch:",
                MedicineName.class,
                getMedicineNameOptions()
        );
        
        if (med == null)
            return;

        String qtyStr = JOptionPaneConsoleIO.readNonEmpty("Enter Quantity:");
        if (!validate.validNumber(qtyStr,1, Integer.MAX_VALUE)) {
            JOptionPaneConsoleIO.showError("Invalid quantity. Must be a positive integer.");
            return;
        }
        int qty = Integer.parseInt(qtyStr);

        LocalDate received = LocalDate.now();

        LocalDate expiry = received.plusYears(2);
        
        String id = IDGenerator.next(IDType.STOCKBATCH);
        boolean success = stock.addBatch(med, id, qty, received, expiry);
        if (success)
            JOptionPaneConsoleIO.showInfo("Batch added successfully.");
        else
            JOptionPaneConsoleIO.showError("Failed to add batch (duplicate Batch ID?).");
    }

    // ========== OPTION 2 ==========
    private void searchBatch() {
        String batchID = JOptionPaneConsoleIO.readNonEmpty("Enter Batch ID to search:");

        // Remove format validation, search directly
        StockBatch result = stock.findBatchByID(batchID);

        if (result == null) {
            JOptionPaneConsoleIO.showError("Batch not found.");
            return;
        }

        String display = String.format("""
        === BATCH FOUND ===
        Medicine    : %s
        Batch ID    : %s
        Quantity    : %d
        Received    : %s
        Expiry      : %s
        """,
                result.getMedicineName(),
                result.getBatchID(),
                result.getStockQty(),
                result.getReceivedDate(),
                result.getExpiryDate()
        );

        JOptionPaneConsoleIO.showMonospaced("Batch Info", display);
    }
    // ========== OPTION 3 ==========
    private void searchByMedicine() {
        MedicineName selected = JOptionPaneConsoleIO.readEnum(
                "Select a medicine to search:",
                MedicineName.class,
                getMedicineNameOptions()
        );
 
        if (selected == null) {
            return; // User cancelled
        }
        String[][] records = stock.listByMedicine(selected);

        if (records.length == 0) {
            JOptionPaneConsoleIO.showInfo("No stock found for " + selected.name());
            return;
        }

        // Format monospaced table
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-15s %-18s %-6s %-12s %-12s%n", "Medicine", "BatchID", "Qty", "Expiry", "Received"));
        sb.append("----------------------------------------------------------------------\n");

        for (String[] row : records) {
            sb.append(String.format("%-15s %-18s %-6s %-12s %-12s%n",
                    row[0], row[1], row[2], row[3], row[4]));
        }

        JOptionPaneConsoleIO.showMonospaced("Search Result",sb.toString());
    }
    
    // ========== OPTION 4 ==========
    private void viewAllBatches() {
        String report = stock.viewAllBatches();

        if (report == null || report.trim().isEmpty()) {
            JOptionPaneConsoleIO.showInfo("No batches available.");
        } else {
            JOptionPaneConsoleIO.showMonospaced("Search Result",report);
        }
    }

    // ========== OPTION 5 ==========
    private void showReports() {
        int choice;
        do {
            choice = JOptionPaneConsoleIO.readOption(
                "=== REPORT MENU ===",
                "Reports Selection",    
                new String[]{
                    "Low Stock Summary",
                    "Expiring Medicines Summary",
                    "Back"
                }
            );

            switch (choice) {
                case 0 -> showLowStockReport();
                case 1 -> showExpiringMedicinesReport();
                case 2 -> JOptionPaneConsoleIO.showInfo("Exiting Report Selection.");
                default -> JOptionPaneConsoleIO.showError("Invalid option selected.");
            }
        } while (choice != 2);
    }

    private void showLowStockReport() {
        String thresholdStr = JOptionPaneConsoleIO.readNonEmpty("Enter low stock threshold (default 50):");
        int threshold = 50;
        if (!thresholdStr.isEmpty()) {
            if (!validate.validQuantity(thresholdStr)) {
                JOptionPaneConsoleIO.showError("Invalid threshold. Using default (50).");
            } else {
                threshold = Integer.parseInt(thresholdStr);
            }
        }
        String report = reports.stockBalanceSummary(threshold);
        JOptionPaneConsoleIO.showMonospaced("Search Result",report);
    }

    private void showExpiringMedicinesReport() {
        String daysStr = JOptionPaneConsoleIO.readNonEmpty("Enter number of days to check expiry (default 30):");
        int days = 30;
        if (!daysStr.isEmpty()) {
            if (!validate.isValidISODate(daysStr)) {
                JOptionPaneConsoleIO.showError("Invalid number. Using default (30).");
            } else {
                days = Integer.parseInt(daysStr);
            }
        }
        String report = reports.expiringMedicinesSummary(days);
        JOptionPaneConsoleIO.showMonospaced("Search Result",report);
    }

    // ========== SHARED ==========
    private MedicineName chooseMedicineByInput() {
        StringBuilder sb = new StringBuilder("Available Medicines:\n");
        for (MedicineName m : MedicineName.values()) {
            sb.append("- ").append(m.name()).append(" (")
                    .append(m.getType().name()).append(")\n");
        }
        JOptionPaneConsoleIO.showInfo(sb.toString());

        String input = JOptionPaneConsoleIO.readNonEmpty("Enter Medicine Name (e.g., PARACETAMOL):");
        if (input == null) {
            return null;
        }

        try {
            return MedicineName.valueOf(input.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            JOptionPaneConsoleIO.showError("Invalid medicine name. Please try again.");
            return null;
        }
    }
    
    // helpter method
    private String[] getMedicineNameOptions() {
        MedicineName[] names = MedicineName.values();
        String[] options = new String[names.length];
        for (int i = 0; i < names.length; i++) {
            options[i] = names[i].name();
        }
        return options;
    }
}
