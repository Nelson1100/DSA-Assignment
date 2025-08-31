package boundary;

import control.TreatmentReport;

import javax.swing.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class TreatmentReportUI {
    private final TreatmentReport report;

    public TreatmentReportUI(TreatmentReport report) {
        this.report = report;
    }

    public void run() {
        String[] opts = {"Daily Summary", "Diagnosis Frequency", "Back"};
        while (true) {
            String choice = (String) JOptionPane.showInputDialog(null, "Select report:",
                    "Reports", JOptionPane.PLAIN_MESSAGE, null, opts, opts[0]);
            if (choice == null || choice.equals("Back")) break;
            switch (choice) {
                case "Daily Summary" -> {
                    String input = JOptionPane.showInputDialog("Enter date (YYYY-MM-DD):");
                    if (input == null) break;
                    try {
                        LocalDate d = LocalDate.parse(input.trim());
                        String out = report.reportDailyTreatmentSummary(d);
                        JTextArea area = new JTextArea(out);
                        area.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 12));
                        area.setEditable(false);
                        JOptionPane.showMessageDialog(null, new JScrollPane(area), "Daily Summary", JOptionPane.INFORMATION_MESSAGE);
                    } catch (DateTimeParseException e) {
                        JOptionPane.showMessageDialog(null, "Invalid date.");
                    }
                }
                case "Diagnosis Frequency" -> {
                    String out = report.reportDiagnosisFrequency();
                    JTextArea area = new JTextArea(out);
                    area.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 12));
                    area.setEditable(false);
                    JOptionPane.showMessageDialog(null, new JScrollPane(area), "Diagnosis Frequency", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        }
    }
}
