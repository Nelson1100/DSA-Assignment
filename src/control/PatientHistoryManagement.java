package control;

import adt.AVLTree;
import adt.LinkedQueue;
import adt.QueueInterface;
import adt.QueueIterator;
import entity.Patient;
import entity.PatientHistory;
import entity.TreatmentRecord;
import utility.IDGenerator;
import utility.IDType;

import java.time.LocalDateTime;

public class PatientHistoryManagement {
    private final AVLTree<PatientHistory> tree = new AVLTree<>();

    public boolean createHistoryForPatient(Patient p) {
        if (p == null || p.getPatientID() == null) return false;
        PatientHistory probe = new PatientHistory(p);
        if (tree.find(probe) != null) return false;
        tree.insert(probe);
        return true;
    }

    public PatientHistory findByPatientID(String id) {
        if (id == null) return null;
        Patient probePatient = new Patient();
        probePatient.setPatientID(id);
        return tree.find(new PatientHistory(probePatient));
    }

    public boolean addRecordByPatient(Patient p, TreatmentRecord r) {
        if (p == null || r == null) return false;
        PatientHistory ph = tree.find(new PatientHistory(p));
        if (ph == null) {
            ph = new PatientHistory(p);
            tree.insert(ph);
        }
        ph.addRecord(r);
        return true;
    }

    public boolean addRecordByPatientID(String patientID, TreatmentRecord r) {
        if (patientID == null || r == null) return false;
        PatientHistory ph = findByPatientID(patientID);
        if (ph == null) return false;
        ph.addRecord(r);
        return true;
    }

    public boolean updateRecord(String patientID, String treatmentID, String newDiagnosis, String newTreatment) {
        PatientHistory ph = findByPatientID(patientID);
        if (ph == null) return false;
        TreatmentRecord tr = ph.findRecordByID(treatmentID);
        if (tr == null) return false;
        if (newDiagnosis != null && !newDiagnosis.trim().isEmpty()) tr.setDiagnosis(newDiagnosis.trim());
        if (newTreatment != null && !newTreatment.trim().isEmpty()) tr.setTreatment(newTreatment.trim());
        return true;
    }

    public boolean removeRecord(String patientID, String treatmentID) {
        PatientHistory ph = findByPatientID(patientID);
        if (ph == null) return false;

        LinkedQueue<TreatmentRecord> tmp = new LinkedQueue<>();
        boolean removed = false;
        QueueIterator<TreatmentRecord> it = ((LinkedQueue<TreatmentRecord>) ph.getRecords()).getIterator();
        while (it.hasNext()) {
            TreatmentRecord r = it.getNext();
            if (!removed && r.getTreatmentID().equals(treatmentID)) {
                removed = true;
            } else {
                tmp.enqueue(r);
            }
        }

        ((LinkedQueue<TreatmentRecord>) ph.getRecords()).clear();
        QueueIterator<TreatmentRecord> it2 = tmp.getIterator();
        while (it2.hasNext()) ph.getRecords().enqueue(it2.getNext());
        return removed;
    }

    public PatientHistory[] listAllHistories() {
        if (tree.isEmpty()) return new PatientHistory[0];
        return tree.toArrayInorder(new PatientHistory[tree.size()]);
    }

    public TreatmentRecord[] listAllRecords() {
        if (tree.isEmpty()) return new TreatmentRecord[0];
        LinkedQueue<TreatmentRecord> q = new LinkedQueue<>();
        PatientHistory[] all = listAllHistories();
        for (PatientHistory ph : all) {
            QueueIterator<TreatmentRecord> it = ((LinkedQueue<TreatmentRecord>) ph.getRecords()).getIterator();
            while (it.hasNext()) q.enqueue(it.getNext());
        }
        TreatmentRecord[] arr = new TreatmentRecord[q.size()];
        int i = 0;
        QueueIterator<TreatmentRecord> it2 = q.getIterator();
        while (it2.hasNext()) arr[i++] = it2.getNext();
        return arr;
    }

    // optional helper to preload some sample treatments for testing (uses given PatientManagement)
    public void preloadSampleTreatments(control.PatientManagement pm) {
        if (pm == null) return;
        String[] sampleIDs = {
            "P202508300001","P202508300002","P202508300003","P202508300004","P202508300005",
            "P202508300006","P202508300007","P202508300008","P202508300009","P202508300010","P202508300011"
        };

        for (String pid : sampleIDs) {
            entity.Patient p = pm.findPatientByID(pid);
            if (p == null) continue;
            PatientHistory ph = tree.find(new PatientHistory(p));
            if (ph == null) {
                ph = new PatientHistory(p);
                tree.insert(ph);
            }
            try {
                String tid1 = IDGenerator.next(IDType.TREATMENT);
                String tid2 = IDGenerator.next(IDType.TREATMENT);
                ph.addRecord(new TreatmentRecord(tid1, pid, "General Checkup", "Paracetamol", LocalDateTime.now().minusDays(1)));
                ph.addRecord(new TreatmentRecord(tid2, pid, "Follow-up", "Observation", LocalDateTime.now()));
            } catch (Exception e) {
                // fallback simple id if IDGenerator not available
                String tid1 = java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase();
                String tid2 = java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase();
                ph.addRecord(new TreatmentRecord(tid1, pid, "General Checkup", "Paracetamol", LocalDateTime.now().minusDays(1)));
                ph.addRecord(new TreatmentRecord(tid2, pid, "Follow-up", "Observation", LocalDateTime.now()));
            }
        }
    }
}
