package control;

import entity.MedicineName;
import utility.JOptionPaneConsoleIO;

import java.util.*;

public class StockReportGenerator {

    private final StockMaintenance stock;
    private static final int WIDTH = 100;

    public StockReportGenerator(StockMaintenance stock) {
        this.stock = stock;
    }

    public String stockBalanceSummary(int threshold) {
        StringBuilder sb = new StringBuilder();
        sb.append(JOptionPaneConsoleIO.reportHeader("Pharmacy Management Module", "Low Stock Summary", WIDTH));
        sb.append(JOptionPaneConsoleIO.sectionTitle("Medicine Stock Balance", WIDTH));

        boolean hasLowStock = false;

        sb.append(String.format("%-20s %-15s %-10s\n", "Medicine", "Type", "Qty"));
        sb.append("-".repeat(WIDTH)).append("\n");

        for (MedicineName name : MedicineName.values()) {
            int balance = stock.totalBalance(name);
            if (balance < threshold) {
                hasLowStock = true;
                sb.append(String.format("%-20s %-15s %-10d\n",
                        name.name(), name.getType(), balance));
            }
        }

        if (!hasLowStock) {
            sb.append("All medicines are above the threshold.\n");
        }

        sb.append(JOptionPaneConsoleIO.reportFooter(WIDTH));
        return sb.toString();
    }

    public String expiringMedicinesSummary(int days) {
        StringBuilder sb = new StringBuilder();
        sb.append(JOptionPaneConsoleIO.reportHeader("Pharmacy Management Module", "Expiring Medicines Summary", WIDTH));
        sb.append(JOptionPaneConsoleIO.sectionTitle("Expiring within " + days + " days", WIDTH));

        String[][] records = stock.expiringWithin(days);

        if (records.length == 0) {
            sb.append("No medicines are expiring within the given period.\n");
        } else {
            sb.append(String.format("%-20s %-15s %-10s %-12s\n", "Medicine", "Batch ID", "Qty", "Expiry"));
            sb.append("-".repeat(WIDTH)).append("\n");
            for (String[] row : records) {
                sb.append(String.format("%-20s %-15s %-10s %-12s\n",
                        row[0], row[1], row[2], row[3]));
            }
        }

        sb.append(JOptionPaneConsoleIO.reportFooter(WIDTH));
        return sb.toString();
    }
}
