package control;

import adt.LinkedQueue;
import adt.QueueIterator;
import entity.Patient;
import entity.PatientHistory;
import entity.TreatmentRecord;
import utility.Validation;
import utility.IDGenerator;
import utility.IDType;

import javax.swing.JOptionPane;
import java.time.LocalDateTime;

public class MedicalTreatmentManagement {
    private final PatientManagement patientMgmt;
    private final PatientHistoryManagement historyMgmt;
    private final Validation validate = new Validation();

    public MedicalTreatmentManagement(PatientManagement pm, PatientHistoryManagement phm) {
        this.patientMgmt = pm;
        this.historyMgmt = phm;
    }

    public MedicalTreatmentManagement(PatientManagement pm) {
        this(pm, new PatientHistoryManagement());
    }

    public MedicalTreatmentManagement() {
        this(new PatientManagement(), new PatientHistoryManagement());
    }

    public void preloadSampleTreatmentsFromPatients() {
        historyMgmt.preloadSampleTreatments(patientMgmt);
    }

    public boolean registerTreatment(Patient p, String diagnosis, String treatment) {
        if (p == null || p.getPatientID() == null || p.getPatientName() == null) {
            JOptionPane.showMessageDialog(null, "Provide patient ID and name.");
            return false;
        }
        if (!validate.validId(p.getPatientID()) || !validate.validName(p.getPatientName())) {
            JOptionPane.showMessageDialog(null, "Invalid ID or name format.");
            return false;
        }
        Patient reg = patientMgmt.findPatientByID(p.getPatientID());
        if (reg == null) {
            JOptionPane.showMessageDialog(null, "Patient ID not found. Register first in Patient module.");
            return false;
        }
        if (!reg.getPatientName().equalsIgnoreCase(p.getPatientName())) {
            JOptionPane.showMessageDialog(null, "Patient name does not match registered record.");
            return false;
        }
        if (diagnosis == null || diagnosis.trim().isEmpty() || treatment == null || treatment.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Diagnosis and treatment cannot be empty.");
            return false;
        }
        String tid;
        try { tid = IDGenerator.next(IDType.TREATMENT); } catch (Exception e) {
            tid = java.util.UUID.randomUUID().toString().substring(0,8).toUpperCase();
        }
        TreatmentRecord tr = new TreatmentRecord(tid, reg.getPatientID(), diagnosis.trim(), treatment.trim(), LocalDateTime.now());
        historyMgmt.addRecordByPatient(reg, tr);
        JOptionPane.showMessageDialog(null, "Treatment recorded: " + tid);
        return true;
    }

    public void viewTreatments(String patientID) {
        if (!validate.validId(patientID)) {
            JOptionPane.showMessageDialog(null, "Invalid Patient ID.");
            return;
        }
        PatientHistory ph = historyMgmt.findByPatientID(patientID);
        if (ph == null) {
            JOptionPane.showMessageDialog(null, "No history found for " + patientID);
            return;
        }
        if (ph.getRecords().isEmpty()) {
            JOptionPane.showMessageDialog(null, "No treatments found for " + patientID);
            return;
        }
        StringBuilder sb = new StringBuilder("Treatment History for " + patientID + ":\n");
        QueueIterator<TreatmentRecord> it = ((LinkedQueue<TreatmentRecord>) ph.getRecords()).getIterator();
        int i = 0;
        while (it.hasNext()) sb.append(++i).append(". ").append(it.getNext()).append("\n");
        JOptionPane.showMessageDialog(null, sb.toString());
    }

    public boolean updateTreatment(String patientID, String treatmentID, String newDiagnosis, String newTreatment) {
        if (!validate.validId(patientID) || treatmentID == null || treatmentID.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Invalid input.");
            return false;
        }
        PatientHistory ph = historyMgmt.findByPatientID(patientID);
        if (ph == null) {
            JOptionPane.showMessageDialog(null, "No history for " + patientID);
            return false;
        }
        boolean ok = historyMgmt.updateRecord(patientID, treatmentID, newDiagnosis, newTreatment);
        JOptionPane.showMessageDialog(null, ok ? "Updated." : "Update failed (not found).");
        return ok;
    }

    public boolean removeTreatment(String patientID, String treatmentID) {
        if (!validate.validId(patientID) || treatmentID == null || treatmentID.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Invalid input.");
            return false;
        }
        boolean ok = historyMgmt.removeRecord(patientID, treatmentID);
        JOptionPane.showMessageDialog(null, ok ? "Removed." : "Not found.");
        return ok;
    }

    public PatientHistory[] listAllHistories() {
        return historyMgmt.listAllHistories();
    }

    public TreatmentRecord[] listAllRecords() {
        return historyMgmt.listAllRecords();
    }

    public PatientHistory findHistory(String patientID) {
        return historyMgmt.findByPatientID(patientID);
    }
}
