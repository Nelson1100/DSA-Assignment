package control;


import entity.MedicineName;
import entity.StockBatch;
import utility.JOptionPaneConsoleIO;


import java.time.LocalDate;


public class StockReportGenerator {
private final StockMaintenance stock;
private static final int WIDTH = 90;


public StockReportGenerator(StockMaintenance stock) {
this.stock = stock;
}


public String stockBalanceSummary(int threshold) {
StringBuilder sb = new StringBuilder();
sb.append(JOptionPaneConsoleIO.reportHeader("Pharmacy Management Module", "Pharmacy Stock Report", WIDTH));
sb.append("Low Stock Threshold: ").append(threshold).append("\n\n");


sb.append(String.format("%-15s %-10s %-15s %-6s%n", "Medicine", "TotalQty", "EarliestExpiry", "Flag"));
sb.append("-".repeat(WIDTH)).append("\n");


int total = 0;
for (MedicineName name : MedicineName.values()) {
int qty = stock.totalBalance(name);
String expiry = "-";
String flag = (qty < threshold) ? "LOW" : "OK";


StockBatch earliest = stock.earliestBatch(name);
if (earliest != null) {
expiry = earliest.getExpiryDate().toString();
}


sb.append(String.format("%-15s %-10d %-15s %-6s%n", name.name(), qty, expiry, flag));
total += qty;
}


sb.append("-".repeat(WIDTH)).append("\n");
sb.append(String.format("%-15s %-10d%n", "TOTAL UNITS", total));
sb.append(JOptionPaneConsoleIO.reportFooter(WIDTH));


return sb.toString();
}


public String expiringMedicinesSummary(int days) {
StringBuilder sb = new StringBuilder();
sb.append(JOptionPaneConsoleIO.reportHeader("Pharmacy Management Module", "Expiring Medicines Report", WIDTH));
sb.append("Expiry Range: Next ").append(days).append(" day(s)\n\n");


sb.append(String.format("%-15s %-10s %-8s %-12s%n", "Medicine", "BatchID", "Qty", "Expiry"));
sb.append("-".repeat(WIDTH)).append("\n");


String[][] expiring = stock.expiringWithin(days);
int total = 0;
for (String[] row : expiring) {
sb.append(String.format("%-15s %-10s %-8s %-12s%n",
row[0], row[1], row[2], row[3]));
total += Integer.parseInt(row[2]);
}


sb.append("-".repeat(WIDTH)).append("\n");
sb.append(String.format("%-15s %-10d%n", "TOTAL UNITS", total));
sb.append(JOptionPaneConsoleIO.reportFooter(WIDTH));


return sb.toString();
}
}