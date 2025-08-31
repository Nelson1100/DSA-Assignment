package control;

import adt.QueueInterface;
import entity.DispensedRecord;
import entity.MedicineName;
import entity.StockBatch;
import utility.JOptionPaneConsoleIO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;

public class PharmacistReportGenerator {

    private static final int WIDTH = 100;

    public static void generateInventoryForecastReport(StockMaintenance stock) {
        StringBuilder sb = new StringBuilder();
        sb.append(JOptionPaneConsoleIO.reportHeader("Pharmacy Management Module", "Advanced Inventory and Demand Forecasting Report", WIDTH));

        sb.append(String.format("%-15s %-8s %-8s %-11s %-12s %-9s %-12s %-20s%n",
                "Medicine", "Stock", "Usage", "Days Left", "Exp. Soon", "Batches", "Risk", "Action"));
        sb.append(JOptionPaneConsoleIO.line('-', WIDTH)).append("\n");

        int criticalCount = 0;
        int totalMed = 0;

        for (MedicineName name : MedicineName.values()) {
            int totalQty = totalNonExpiredQuantity(stock, name);
            int dailyUsage = getEstimatedDailyUsage(name);
            int daysLeft = (dailyUsage == 0 ? 999 : totalQty / dailyUsage);
            int batchCount = countValidBatches(stock, name);
            LocalDate earliestExp = getEarliestExpiry(stock, name);

            String riskLevel;
            String action;

            if (daysLeft <= 3) {
                riskLevel = "\u203C CRITICAL";
                action = "Reorder Immediately";
                criticalCount++;
            } else if (daysLeft <= 7) {
                riskLevel = "HIGH";
                action = "Review Next Week";
            } else if (daysLeft <= 14) {
                riskLevel = "MODERATE";
                action = "Review Next Week";
            } else {
                riskLevel = "LOW";
                action = "Stock Sufficient";
            }

            sb.append(String.format("%-15s %-8d %-8d %-11d %-12s %-9d %-12s %-20s%n",
                    name, totalQty, dailyUsage, daysLeft,
                    (earliestExp != null ? earliestExp.toString() : "-"),
                    batchCount, riskLevel, action));

            totalMed++;
        }

        sb.append(JOptionPaneConsoleIO.line('-', WIDTH)).append("\n");
        sb.append("Total Medicines Analyzed: ").append(totalMed)
                .append("    |  Critical Risk: ").append(criticalCount).append("\n");

        sb.append(JOptionPaneConsoleIO.reportFooter(WIDTH));

        JOptionPaneConsoleIO.showMonospaced(
                "Inventory and Demand Forecasting Report",
                sb.toString()
        );
    }

    public static void generateDispensingSummaryReport(QueueInterface<DispensedRecord> recordLog) {
        int total = 0, success = 0, failed = 0;
        int[] dispensedCount = new int[MedicineName.values().length];
        int[] dispensedQty = new int[MedicineName.values().length];
        String[] reasons = new String[500];
        int[] reasonCounts = new int[500];
        int reasonIndex = 0;

        LocalDateTime first = null, last = null;

        for (DispensedRecord r : recordLog) {
            total++;

            if (r.isDispensed()) {
                success++;
                if (first == null || r.getTimestamp().isBefore(first)) {
                    first = r.getTimestamp();
                }
                if (last == null || r.getTimestamp().isAfter(last)) {
                    last = r.getTimestamp();
                }

                MedicineName[] meds = r.getMedicines();
                int[] qtys = r.getQuantities();
                for (int i = 0; i < meds.length; i++) {
                    int idx = meds[i].ordinal();
                    dispensedCount[idx]++;
                    dispensedQty[idx] += qtys[i];
                }
            } else {
                failed++;
                String reason = r.getRejectionReason();
                if (reason == null || reason.isEmpty()) {
                    reason = "Unknown";
                }

                boolean found = false;
                for (int i = 0; i < reasonIndex; i++) {
                    if (reasons[i].equals(reason)) {
                        reasonCounts[i]++;
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    reasons[reasonIndex] = reason;
                    reasonCounts[reasonIndex] = 1;
                    reasonIndex++;
                }
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append(JOptionPaneConsoleIO.reportHeader("Pharmacy Management Module", "Dispensing Activity Summary Report", WIDTH));
        sb.append(String.format("Total Prescriptions Attempted: %d%n", total));
        sb.append(String.format("  - Successful : %d%n", success));
        sb.append(String.format("  - Failed     : %d%n%n", failed));

        if (first != null) {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a");
            sb.append("Time Range: ").append(first.format(fmt)).append(" to ").append(last.format(fmt)).append("\n\n");
        }

        sb.append(JOptionPaneConsoleIO.sectionTitle("Top 5 Most Dispensed Medicines", WIDTH));
        sb.append(String.format("%-20s %-15s %-15s%n", "Medicine", "Times Dispensed", "Total Qty"));
        sb.append(JOptionPaneConsoleIO.line('-', WIDTH)).append("\n");

        int[] indices = new int[MedicineName.values().length];
        for (int i = 0; i < indices.length; i++) {
            indices[i] = i;
        }

        for (int i = 0; i < indices.length - 1; i++) {
            for (int j = i + 1; j < indices.length; j++) {
                if (dispensedQty[indices[j]] > dispensedQty[indices[i]]) {
                    int temp = indices[i];
                    indices[i] = indices[j];
                    indices[j] = temp;
                }
            }
        }

        for (int i = 0; i < Math.min(5, indices.length); i++) {
            int idx = indices[i];
            if (dispensedQty[idx] == 0) {
                continue;
            }
            sb.append(String.format("%-20s %-15d %-15d%n",
                    MedicineName.values()[idx].name(), dispensedCount[idx], dispensedQty[idx]));
        }

        if (reasonIndex > 0) {
            sb.append("\n").append(JOptionPaneConsoleIO.sectionTitle("Common Rejection Reasons", WIDTH));
            sb.append(String.format("%-40s %-10s%n", "Reason", "Count"));
            sb.append(JOptionPaneConsoleIO.line('-', WIDTH)).append("\n");
            for (int i = 0; i < reasonIndex; i++) {
                sb.append(String.format("%-40s %-10d%n", reasons[i], reasonCounts[i]));
            }
        }

        sb.append(JOptionPaneConsoleIO.reportFooter(WIDTH));
        JOptionPaneConsoleIO.showMonospaced("Dispensing Activity Summary", sb.toString());
    }

    // ==== Private Utilities ====
    private static int getEstimatedDailyUsage(MedicineName name) {
        return switch (name) {
            case PARACETAMOL ->
                10;
            case AMOXICILLIN ->
                6;
            case COUGH_SYRUP ->
                3;
            case INSULIN ->
                2;
            case HYDROCORTISONE ->
                4;
            case IBUPROFEN ->
                8;
            case ASPIRIN ->
                7;
            case CETIRIZINE ->
                5;
            case VITAMIN_C ->
                9;
        };
    }

    private static int totalNonExpiredQuantity(StockMaintenance stock, MedicineName name) {
        int total = 0;
        LocalDate today = LocalDate.now();
        Iterator<StockBatch> it = stock.getAllBatches().iterator();
        while (it.hasNext()) {
            StockBatch b = it.next();
            if (b.getMedicineName() == name && b.getStockQty() > 0 && !b.getExpiryDate().isBefore(today)) {
                total += b.getStockQty();
            }
        }
        return total;
    }

    private static int countValidBatches(StockMaintenance stock, MedicineName name) {
        int count = 0;
        LocalDate today = LocalDate.now();
        Iterator<StockBatch> it = stock.getAllBatches().iterator();
        while (it.hasNext()) {
            StockBatch b = it.next();
            if (b.getMedicineName() == name && b.getStockQty() > 0 && !b.getExpiryDate().isBefore(today)) {
                count++;
            }
        }
        return count;
    }

    private static LocalDate getEarliestExpiry(StockMaintenance stock, MedicineName name) {
        LocalDate today = LocalDate.now();
        LocalDate earliest = null;
        Iterator<StockBatch> it = stock.getAllBatches().iterator();
        while (it.hasNext()) {
            StockBatch b = it.next();
            if (b.getMedicineName() == name && b.getStockQty() > 0 && !b.getExpiryDate().isBefore(today)) {
                if (earliest == null || b.getExpiryDate().isBefore(earliest)) {
                    earliest = b.getExpiryDate();
                }
            }
        }
        return earliest;
    }
}
