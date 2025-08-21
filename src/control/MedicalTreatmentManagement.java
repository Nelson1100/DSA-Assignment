package control;

import adt.AVL_Tree;
import adt.LinkedQueue;
import adt.QueueInterface;
import adt.QueueIterator;
import dao.PatientInitializer;
import entity.Patient;
import entity.PatientHistory;
import entity.TreatmentRecord;

import java.time.LocalTime;

public class MedicalTreatmentManagement {

    private final QueueInterface<Patient> waitingQueue = new LinkedQueue<>();        // arrival order
    private final AVL_Tree<PatientHistory> historyIndex = new AVL_Tree<>();         // search by patientID
    private final QueueInterface<PatientHistory> historyList = new LinkedQueue<>(); // iterable list for reports

    /** Load sample patients into waiting queue and index them. */
    public void initializePatients() {
        PatientInitializer.initialize(waitingQueue);
        QueueIterator<Patient> it = ((LinkedQueue<Patient>) waitingQueue).getIterator();
        while (it.hasNext()) {
            Patient p = it.getNext();
            ensureHistoryExists(p);
        }
    }

    /** Add patient to waiting queue and create history index if new. */
    public void addPatient(Patient p) {
        waitingQueue.enqueue(p);
        ensureHistoryExists(p);
    }

    /**
     * Serve next waiting patient and record diagnosis & treatment.
     * Returns the served Patient, or null if queue empty.
     */
    public Patient serveNextPatient(String diagnosis, String treatment) {
        if (waitingQueue.isEmpty()) return null;
        Patient served = waitingQueue.dequeue();
        PatientHistory ph = ensureHistoryExists(served);
        
        ph.addRecord(new TreatmentRecord("T-PLACEHOLDER", served.getPatientID(), diagnosis, treatment, java.time.LocalDateTime.now()));
        return served;
    }

    public boolean addTreatmentById(String patientID, TreatmentRecord record) {
        // find patient history via dummy key
        Patient dummy = new Patient(patientID, "", "", "", null, 0, null, LocalTime.now());
        PatientHistory key = new PatientHistory(dummy);
        PatientHistory found = historyIndex.search(key);
        if (found == null) return false;
        found.addRecord(record);
        return true;
    }

    public PatientHistory findHistory(String patientID) {
        Patient dummy = new Patient(patientID, "", "", "", null, 0, null, LocalTime.now());
        PatientHistory key = new PatientHistory(dummy);
        return historyIndex.search(key);
    }

    public QueueIterator<Patient> waitingIterator() {
        return ((LinkedQueue<Patient>) waitingQueue).getIterator();
    }

    /** Iterator for all patient histories (used by reports). */
    public QueueIterator<PatientHistory> historiesIterator() {
        return ((LinkedQueue<PatientHistory>) historyList).getIterator();
    }

    /** Is waiting queue empty? */
    public boolean isWaitingEmpty() {
        return waitingQueue.isEmpty();
    }

    private PatientHistory ensureHistoryExists(Patient p) {
        // Create a probe PatientHistory using patientID only (compareTo uses ID)
        Patient probePatient = new Patient(p.getPatientID(), "", "", "", p.getGender(), 0, p.getVisitType(), LocalTime.now());
        PatientHistory probe = new PatientHistory(probePatient);
        PatientHistory found = historyIndex.search(probe);
        if (found != null) return found;

        PatientHistory ph = new PatientHistory(p);
        historyIndex.insert(ph);
        historyList.enqueue(ph);
        return ph;
    }
}
