package boundary;

import control.MedicalTreatmentManagement;
import control.TreatmentReport;
import control.PatientHistoryManagement;
import entity.Patient;

import javax.swing.*;

public class MedicalTreatmentUI {
    private final MedicalTreatmentManagement mtm;
    private final TreatmentReport report;

    public MedicalTreatmentUI(MedicalTreatmentManagement mtm) {
        this.mtm = mtm;
        this.report = new TreatmentReport(mtm);
    }

    public void run() {
        String[] opts = {
                "Register Treatment",
                "View Treatments",
                "Update Treatment",
                "Remove Treatment",
                "Reports",
                "Back"
        };

        while (true) {
            String choice = (String) JOptionPane.showInputDialog(null, "Medical Treatment Menu",
                    "Treatment", JOptionPane.PLAIN_MESSAGE, null, opts, opts[0]);
            if (choice == null || choice.equals("Back")) break;
            switch (choice) {
                case "Register Treatment" -> register();
                case "View Treatments" -> view();
                case "Update Treatment" -> update();
                case "Remove Treatment" -> remove();
                case "Reports" -> new boundary.TreatmentReportUI(report).run();
            }
        }
    }

    private void register() {
        String pid = JOptionPane.showInputDialog("Patient ID:");
        String pname = JOptionPane.showInputDialog("Patient Name:");
        String dx = JOptionPane.showInputDialog("Diagnosis:");
        String tx = JOptionPane.showInputDialog("Treatment:");
        if (pid == null || pname == null || dx == null || tx == null) return;
        Patient p = new Patient();
        p.setPatientID(pid.trim());
        p.setPatientName(pname.trim());
        mtm.registerTreatment(p, dx.trim(), tx.trim());
    }

    private void view() {
        String pid = JOptionPane.showInputDialog("Patient ID:");
        if (pid == null) return;
        mtm.viewTreatments(pid.trim());
    }

    private void update() {
        String pid = JOptionPane.showInputDialog("Patient ID:");
        String tid = JOptionPane.showInputDialog("Treatment ID:");
        String ndx = JOptionPane.showInputDialog("New Diagnosis (leave blank to keep):");
        String ntx = JOptionPane.showInputDialog("New Treatment (leave blank to keep):");
        if (pid == null || tid == null) return;
        mtm.updateTreatment(pid.trim(), tid.trim(),
                ndx == null || ndx.trim().isEmpty() ? null : ndx.trim(),
                ntx == null || ntx.trim().isEmpty() ? null : ntx.trim());
    }

    private void remove() {
        String pid = JOptionPane.showInputDialog("Patient ID:");
        String tid = JOptionPane.showInputDialog("Treatment ID:");
        if (pid == null || tid == null) return;
        mtm.removeTreatment(pid.trim(), tid.trim());
    }
}
