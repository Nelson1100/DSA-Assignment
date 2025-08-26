package control;

import adt.AVLInterface;
import adt.AVLTree;
import adt.LinkedQueue;
import adt.QueueIterator;
import entity.Patient;
import entity.PatientHistory;
import entity.TreatmentRecord;
import utility.Validation;

import javax.swing.JOptionPane;
import java.time.LocalDateTime;

public class MedicalTreatmentManagement {

    private final AVLInterface<PatientHistory> historyTree = new AVLTree<>();
    private final Validation validate = new Validation();

    // Public API

    public void registerTreatment(Patient patient, String diagnosis, String treatment) {
        String result = treatmentRegistration(patient, diagnosis, treatment);
        JOptionPane.showMessageDialog(
            null, result, "Treatment Registration",
            result.startsWith("Treatment (") ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE
        );
    }

    public void viewTreatments(String patientID) {
        String result = showTreatmentInfo(patientID);
        JOptionPane.showMessageDialog(
            null, result, "Treatment History",
            result.startsWith("No ") || result.startsWith("Please") ? JOptionPane.ERROR_MESSAGE : JOptionPane.INFORMATION_MESSAGE
        );
    }

    public void updateTreatment(String patientID, String treatmentID) {
        String result = treatmentModification(patientID, treatmentID);
        JOptionPane.showMessageDialog(
            null, result, "Update Treatment",
            result.contains("successfully") ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE
        );
    }

    public void removeTreatment(String patientID, String treatmentID) {
        boolean ok = treatmentRemover(patientID, treatmentID);
        JOptionPane.showMessageDialog(
            null,
            ok ? "Treatment (" + treatmentID + ") successfully removed." : "Treatment not found. Please retry.",
            ok ? "Treatment Removed" : "Treatment Remove Unsuccessful",
            ok ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE
        );
    }

    // Implementations

    private String treatmentRegistration(Patient patient, String diagnosis, String treatment) {
        if (patient == null || patient.getPatientID() == null || patient.getPatientID().isEmpty())
            return "Invalid patient.";

        if (diagnosis == null || diagnosis.trim().isEmpty())
            return "Diagnosis cannot be empty.";
        if (treatment == null || treatment.trim().isEmpty())
            return "Treatment cannot be empty.";

        String dx = diagnosis.trim();
        String tx = treatment.trim();

        boolean diagOK = validate.validSpecialization(dx) || validate.validName(dx);
        boolean trtOK  = validate.validSpecialization(tx) || validate.validName(tx);
        if (!diagOK) return "Invalid diagnosis. Only alphabets and spaces are allowed.";
        if (!trtOK)  return "Invalid treatment. Only alphabets and spaces are allowed.";

        PatientHistory history = ensureHistoryExists(patient);

        String treatmentID = "TR" + String.format("%03d", history.getRecords().size() + 1);
        TreatmentRecord tr = new TreatmentRecord(
            treatmentID, patient.getPatientID(), dx, tx, LocalDateTime.now()
        );
        history.addRecord(tr);

        return "Treatment (" + treatmentID + ") successfully registered for patient (" + patient.getPatientID() + ").";
    }
    
    private String showTreatmentInfo(String patientID) {
        if (patientID == null || patientID.isEmpty())
            return "Please enter a valid patient ID.";

        Patient probe = new Patient(); // set only the ID for search
        probe.setPatientID(patientID);

        PatientHistory ph = historyTree.find(new PatientHistory(probe));
        if (ph == null || ph.getRecords().isEmpty())
            return "No treatment history found for patient (" + patientID + ").";

        StringBuilder sb = new StringBuilder("Treatment History for Patient " + patientID + ":\n");
        QueueIterator<TreatmentRecord> it = ((LinkedQueue<TreatmentRecord>) ph.getRecords()).getIterator();
        int n = 0;
        while (it.hasNext()) {
            sb.append(++n).append(". ").append(it.getNext().toString()).append("\n");
        }
        return sb.toString();
    }
    
    // Edit diagnosis or treatment
    private String treatmentModification(String patientID, String treatmentID) {
        if (patientID == null || patientID.isEmpty() || treatmentID == null || treatmentID.isEmpty())
            return "Please provide patient ID and treatment ID.";

        Patient probe = new Patient();
        probe.setPatientID(patientID);
        PatientHistory ph = historyTree.find(new PatientHistory(probe));
        if (ph == null)
            return "No patient found with ID (" + patientID + ").";

        QueueIterator<TreatmentRecord> finder = ((LinkedQueue<TreatmentRecord>) ph.getRecords()).getIterator();
        TreatmentRecord found = null;
        while (finder.hasNext()) {
            TreatmentRecord r = finder.getNext();
            if (treatmentID.equals(r.getTreatmentID())) { found = r; break; }
        }
        if (found == null)
            return "No treatment found with ID (" + treatmentID + ").";

        String choice = JOptionPane.showInputDialog(
            null,
            "Modify Treatment (" + treatmentID + "):\n[1] Diagnosis\n[2] Treatment",
            "Update Treatment",
            JOptionPane.INFORMATION_MESSAGE
        );
        if (choice == null) return "Update cancelled.";

        switch (choice) {
            case "1": {
                String newDiag = JOptionPane.showInputDialog(null, "Enter new diagnosis:", "Update Diagnosis", JOptionPane.INFORMATION_MESSAGE);
                if (newDiag == null || newDiag.trim().isEmpty()) return "Update cancelled.";
                String dx = newDiag.trim();
                boolean ok = validate.validSpecialization(dx) || validate.validName(dx);
                if (!ok) return "Invalid diagnosis. Only alphabets and spaces are allowed.";
                found.setDiagnosis(dx);
                return "Diagnosis successfully updated.";
            }
            case "2": {
                String newTreat = JOptionPane.showInputDialog(null, "Enter new treatment:", "Update Treatment", JOptionPane.INFORMATION_MESSAGE);
                if (newTreat == null || newTreat.trim().isEmpty()) return "Update cancelled.";
                String tx = newTreat.trim();
                boolean ok = validate.validSpecialization(tx) || validate.validName(tx);
                if (!ok) return "Invalid treatment. Only alphabets and spaces are allowed.";
                found.setTreatment(tx);
                return "Treatment successfully updated.";
            }
            default:
                return "Invalid choice.";
        }
    }

    private boolean treatmentRemover(String patientID, String treatmentID) {
        if (patientID == null || patientID.isEmpty() || treatmentID == null || treatmentID.isEmpty())
            return false;

        Patient probe = new Patient();
        probe.setPatientID(patientID);

        PatientHistory ph = historyTree.find(new PatientHistory(probe));
        if (ph == null) return false;

        // rebuild queue without the target record (no change to PatientHistory class)
        LinkedQueue<TreatmentRecord> tmp = new LinkedQueue<>();
        boolean removed = false;

        QueueIterator<TreatmentRecord> it = ((LinkedQueue<TreatmentRecord>) ph.getRecords()).getIterator();
        while (it.hasNext()) {
            TreatmentRecord r = it.getNext();
            if (!removed && treatmentID.equals(r.getTreatmentID())) {
                removed = true; // skip this one
            } else {
                tmp.enqueue(r);
            }
        }

        // restore original queue contents
        ((LinkedQueue<TreatmentRecord>) ph.getRecords()).clear();
        QueueIterator<TreatmentRecord> it2 = tmp.getIterator();
        while (it2.hasNext()) {
            ph.getRecords().enqueue(it2.getNext());
        }

        return removed;
    }

    // Utility

    private PatientHistory ensureHistoryExists(Patient patient) {
        Patient probe = new Patient();
        probe.setPatientID(patient.getPatientID());

        PatientHistory existing = historyTree.find(new PatientHistory(probe));
        if (existing != null) return existing;

        PatientHistory created = new PatientHistory(patient);
        historyTree.insert(created);
        return created;
    }
    
    public PatientHistory[] listAllHistories() {
        return historyTree.toArrayInorder(); 
    }

}
