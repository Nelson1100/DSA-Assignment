package control;

import entity.MedicineName;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class StockReportGenerator {

    private static final int WIDTH = 80;
    private final StockMaintenance stock;
    private final String clinicName;

    public StockReportGenerator(StockMaintenance stock) {
        this(stock, "Clinic Pharmacy");
    }

    public StockReportGenerator(StockMaintenance stock, String clinicName) {
        this.stock = stock;
        this.clinicName = (clinicName == null ? "Clinic Pharmacy" : clinicName);
    }

    /* ===================== Report 1 ===================== */
    /** STOCK BALANCE SUMMARY (By Medicine) */
    public String stockBalanceSummary(int lowThreshold) {
        MedicineName[] meds = MedicineName.values();
        int n = meds.length;

        int[] totals = new int[n];
        LocalDate[] earliest = new LocalDate[n];
        for (int i = 0; i < n; i++) earliest[i] = LocalDate.MAX;

        String[][] rows = safeRows(stock.listAll()); // [Medicine, BatchID, Qty, Expiry, Received]
        for (int i = 0; i < rows.length; i++) {
            MedicineName m = parseMedicine(rows[i][0]);
            if (m == null) continue;
            int idx = m.ordinal();

            int qty = parseIntSafe(rows[i][2]);
            totals[idx] += qty;

            LocalDate exp = parseDateSafe(rows[i][3]);
            if (exp != null && exp.isBefore(earliest[idx])) earliest[idx] = exp;
        }

        // Build
        StringBuilder sb = new StringBuilder(1024);
        banner(sb, "STOCK BALANCE SUMMARY (BY MEDICINE)");
        headerBlock(sb, "Low Stock Threshold: " + lowThreshold);

        // Table header
        sb.append(padRight("Medicine", 22))
          .append(padRight("TotalQty", 10))
          .append(padRight("EarliestExpiry", 16))
          .append(padRight("Flag", 8))
          .append('\n');
        sb.append(line('-')).append('\n');

        // Rows
        int grandTotal = 0;
        for (int i = 0; i < n; i++) {
            grandTotal += totals[i];
            String earliestStr = (totals[i] == 0 || earliest[i] == LocalDate.MAX) ? "-" : earliest[i].toString();
            String flag = (totals[i] < lowThreshold) ? "LOW" : "";

            sb.append(padRight(meds[i].name(), 22))
              .append(padLeft(Integer.toString(totals[i]), 10))
              .append(padRight(earliestStr, 16))
              .append(padRight(flag, 8))
              .append('\n');
        }

        sb.append(line('-')).append('\n');
        sb.append(padRight("TOTAL UNITS", 22))
          .append(padLeft(Integer.toString(grandTotal), 10))
          .append('\n');

        footer(sb);
        return sb.toString();
    }

    /* ===================== Report 2 ===================== */
    /** EXPIRING MEDICINES SUMMARY (≤ days) */
    public String expiringMedicinesSummary(int days) {
        if (days < 0) return "Days must be >= 0.";

        MedicineName[] meds = MedicineName.values();
        int n = meds.length;

        int[] qtySoon = new int[n];
        int batchCount = 0;

        String[][] rows = safeRows(stock.expiringWithin(days)); // [Medicine, BatchID, Qty, Expiry]
        for (int i = 0; i < rows.length; i++) {
            MedicineName m = parseMedicine(rows[i][0]);
            if (m == null) continue;
            int idx = m.ordinal();

            int qty = parseIntSafe(rows[i][2]);
            if (qty > 0) {
                qtySoon[idx] += qty;
                batchCount++;
            }
        }

        // Find highest risk medicine
        int maxIdx = -1;
        int maxQty = -1;
        int totalSoon = 0;
        for (int i = 0; i < n; i++) {
            totalSoon += qtySoon[i];
            if (qtySoon[i] > maxQty) { maxQty = qtySoon[i]; maxIdx = i; }
        }

        // Build
        String title = "EXPIRING MEDICINES SUMMARY (≤ " + days + " DAYS)";
        StringBuilder sb = new StringBuilder(1024);
        banner(sb, title);
        headerBlock(sb, "Window: ≤ " + days + " days",
                        "Expiring Batches: " + batchCount);

        // Table header
        sb.append(padRight("Medicine", 22))
          .append(padRight("QtyExpiring", 12))
          .append('\n');
        sb.append(line('-')).append('\n');

        boolean any = false;
        for (int i = 0; i < n; i++) {
            if (qtySoon[i] > 0) {
                any = true;
                sb.append(padRight(meds[i].name(), 22))
                  .append(padLeft(Integer.toString(qtySoon[i]), 12))
                  .append('\n');
            }
        }
        if (!any) sb.append("(none)\n");

        sb.append(line('-')).append('\n');
        sb.append(padRight("TOTAL EXPIRING QTY", 22))
          .append(padLeft(Integer.toString(totalSoon), 12))
          .append('\n');

        if (maxIdx >= 0 && maxQty > 0) {
            sb.append("\nHIGHEST RISK : ").append(meds[maxIdx].name())
              .append("  (").append(maxQty).append(" units)\n");
        } else {
            sb.append("\nHIGHEST RISK : -\n");
        }

        footer(sb);
        return sb.toString();
    }

    /* ===================== Formatting helpers ===================== */

    private void banner(StringBuilder sb, String title) {
        sb.append(line('=')).append('\n');
        sb.append(center(clinicName.toUpperCase(), WIDTH)).append('\n');
        sb.append(center("PHARMACY STOCK REPORT", WIDTH)).append('\n');
        sb.append(center(title, WIDTH)).append('\n');
        sb.append(line('=')).append('\n');
    }

    private void headerBlock(StringBuilder sb, String a) {
        sb.append(a).append('\n');
        sb.append(line('-')).append('\n');
    }

    private void headerBlock(StringBuilder sb, String a, String b) {
        sb.append(a).append('\n');
        sb.append(b).append('\n');
        sb.append(line('-')).append('\n');
    }

    private void footer(StringBuilder sb) {
        sb.append(line('=')).append('\n');
        sb.append(padRight("Generated at: " + LocalDateTime.now(), WIDTH)).append('\n');
        sb.append(line('=')).append('\n');
    }

    private String line(char ch) {
        StringBuilder s = new StringBuilder(WIDTH);
        for (int i = 0; i < WIDTH; i++) s.append(ch);
        return s.toString();
    }

    private static String padRight(String s, int w) {
        if (s == null) s = "";
        if (s.length() >= w) return s.substring(0, w);
        StringBuilder b = new StringBuilder(w);
        b.append(s);
        for (int i = s.length(); i < w; i++) b.append(' ');
        return b.toString();
    }

    private static String padLeft(String s, int w) {
        if (s == null) s = "";
        if (s.length() >= w) return s.substring(0, w);
        StringBuilder b = new StringBuilder(w);
        for (int i = s.length(); i < w; i++) b.append(' ');
        b.append(s);
        return b.toString();
    }

    private static String center(String s, int w) {
        if (s == null) s = "";
        if (s.length() >= w) return s.substring(0, w);
        int left = (w - s.length()) / 2;
        int right = w - s.length() - left;
        StringBuilder b = new StringBuilder(w);
        for (int i = 0; i < left; i++) b.append(' ');
        b.append(s);
        for (int i = 0; i < right; i++) b.append(' ');
        return b.toString();
    }

    /* ===================== Parsers (safe) ===================== */

    private static String[][] safeRows(String[][] rows) {
        return rows == null ? new String[0][0] : rows;
    }

    private static MedicineName parseMedicine(String s) {
        try { return MedicineName.valueOf(s); } catch (Exception e) { return null; }
    }

    private static int parseIntSafe(String s) {
        try { return Integer.parseInt(s.trim()); } catch (Exception e) { return 0; }
    }

    private static LocalDate parseDateSafe(String s) {
        try { return LocalDate.parse(s.trim()); } catch (Exception e) { return null; }
    }
}
