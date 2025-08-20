package control;

import adt.*;
import entity.*;
import entity.keys.*;
import dao.PatientInitializer;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class PatientMaintenance {
    private final QueueInterface<PatientVisit> visitQueue;
    
    private final AVLTree<PatientByID> idxByID = new AVLTree<>();
    private final AVLTree<PatientByName> idxByName = new AVLTree<>();
    private final AVLTree<PatientByContactNo> idxByContact = new AVLTree<>();
    private final AVLTree<PatientByEmail> idxByEmail = new AVLTree<>();
    
    public PatientMaintenance() {
        visitQueue = new LinkedQueue<>();
        PatientInitializer.initialize(visitQueue); // Pre-load test data
        rebuildIndexesFromQueue();
    }
    
    /* ---------- Index Handling ---------- */
    
    private void rebuildIndexesFromQueue() {
        QueueIterator<PatientVisit> it = getIterator();
        
        while (it.hasNext()) {
            indexPatient(it.getNext().getPatient());
        }
    }
    
    private void indexPatient(Patient p) {
        idxByID.insert(new PatientByID(p.getPatientID(), p));
        idxByName.insert(new PatientByName(p.getPatientName(), p.getPatientID(), p));
        idxByContact.insert(new PatientByContactNo(p.getContactNo(), p));
        idxByEmail.insert(new PatientByEmail(p.getEmail(), p));
    }
    
    private void unindexPatient(Patient p) {
        idxByID.delete(new PatientByID(p.getPatientID(), null));
        idxByName.delete(new PatientByName(p.getPatientName(), p.getPatientID(), null));
        idxByContact.delete(new PatientByContactNo(p.getContactNo(), null));
        idxByEmail.delete(new PatientByEmail(p.getEmail(), null));
    }
    
    /* ---------- Patient CRUD ---------- */
    
    public boolean registerPatient(Patient patient) {
        if (existsByID(patient.getPatientID())) return false;
        
        indexPatient(patient);
        return true;
    }
    
    public Patient getPatientByID(String id) {
        PatientByID wrapper = idxByID.find(new PatientByID(id, null));
        return (wrapper != null) ? wrapper.ref : null;
    }
    
    public boolean updatePatient(Patient updatedPatient) {
        String id = updatedPatient.getPatientID();
        if (!existsByID(id)) return false;
        
        Patient old = getPatientByID(id);
        unindexPatient(old);
        indexPatient(updatedPatient);
        return true;
    }
    
    public boolean removePatientByID(String id) {
        if (!existsByID(id)) return false;
        
        Patient p = getPatientByID(id);
        unindexPatient(p);
        return true;
    }
    
    public boolean existsByID(String id) {
        if (id == null) return false;
        
        return idxByID.contains(new PatientByID(id, null));
    }
    
    /* ---------- Visit Management ---------- */
    
    public void registerVisit(Patient patient, VisitType visitType) {
        String id = patient.getPatientID();
        
        if (existsByID(id)) {
            Patient existing = getPatientByID(id);
            patient = existing;
        } else {
            indexPatient(patient);
        }
        
        PatientVisit visit = new PatientVisit(patient, visitType, LocalDateTime.now());
        visitQueue.enqueue(visit);
    }
    
    public PatientVisit serveNextVisit() {
        if (isEmpty()) return null;
        
        return visitQueue.dequeue();
    }
    
    public boolean removeVisitByID(String id) {
        if (id == null || isEmpty()) return false;
        
        QueueInterface<PatientVisit> temp = new LinkedQueue<>();
        boolean removed = false;
        
        while (!visitQueue.isEmpty()) {
            PatientVisit v = visitQueue.dequeue();
            
            if (!removed && id.equals(v.getPatient().getPatientID())) {
                removed = true;
            } else {
                temp.enqueue(v);
            }
        }
        
        // Rebuild queue
        while(!temp.isEmpty()) {
            visitQueue.enqueue(temp.dequeue());
        }
        
        return removed;
    }
    
    /* ---------- Queue Access ---------- */
    
    public boolean isEmpty() {
        return visitQueue.isEmpty();
    }
    
    public int getQueueSize() {
        return visitQueue.size();
    }
    
    private QueueIterator<PatientVisit> getIterator() {
        return visitQueue.getIterator();
    }
    
    public PatientVisit getNextVisit() {
        QueueIterator<PatientVisit> it = getIterator();
        return it.hasNext() ? it.getNext() : null;
    }
    
    public PatientVisit[] peekNextN(int n) {
        int size = Math.min(n, visitQueue.size());
        PatientVisit[] arr = new PatientVisit[size];
        QueueIterator<PatientVisit> it = getIterator();
        
        for (int i = 0; i < size && it.hasNext(); i++) {
            arr[i] = it.getNext();
        }
        
        return arr;
    }
    
    public int findPosition(String id) {
        int pos = 1;
        QueueIterator<PatientVisit> it = getIterator();
        
        while (it.hasNext()) {
            if (it.getNext().getPatient().getPatientID().equals(id))
                return pos;
            pos++;
        }
        
        return -1;
    }
    
    /* ---------- Queue Statistics ---------- */
    
    public int countByVisitType(VisitType type) {
        int count = 0;
        QueueIterator<PatientVisit> it = getIterator();
        
        while (it.hasNext()) {
            if (it.getNext().getVisitType() == type)
                count++;
        }
        
        return count;
    }
    
    public int countByGender(Gender gender) {
        int count = 0;
        QueueIterator<PatientVisit> it = getIterator();
        
        while (it.hasNext()) {
            if (it.getNext().getPatient().getGender() == gender)
                count++;
        }
        
        return count;
    }
    
    public double avgWaitMinutes(LocalDateTime now) {
        long total = 0;
        int count = 0;
        QueueIterator<PatientVisit> it = getIterator();
        
        while(it.hasNext()) {
            long wait = ChronoUnit.MINUTES.between(it.getNext().getArrivalDateTime(), now);
            total += Math.max(wait, 0);
            count++;
        }
        
        return count == 0 ? 0.0 : (double) total / count;
    }
    
    public long maxWaitMinutes(LocalDateTime now) {
        long max = 0;
        QueueIterator<PatientVisit> it = getIterator();
        
        while (it.hasNext()) {
            long wait = ChronoUnit.MINUTES.between(it.getNext().getArrivalDateTime(), now);
            if (wait > max)
                max = wait;
        }
        
        return max;
    }
    
    // need modification
    public String queueHealthSnapshot(LocalDateTime now) {
        int walkIn = countByVisitType(VisitType.WALK_IN);
        int appointment = countByVisitType(VisitType.APPOINTMENT);
        double avg = avgWaitMinutes(now);
        long max = maxWaitMinutes(now);
        PatientVisit next = getNextVisit();
        
        return String.format("""
                Queue Health Summary
                ---------------------
                Walk-in Patients   : %d
                Appointments       : %d
                Average Wait       : %.1f min
                Max Wait           : %d min
                Next to Serve      : %s (%s)
                """, 
                walkIn, appointment, avg, max,
                next != null ? next.getPatient().getPatientName() : "(none)",
                next != null ? next.getPatient().getPatientID() : "(none)");
    }
    
    /* ---------- Reporting & Sorting ---------- */
    
    public PatientByID[] getPatientsSortedByID() {
        return idxByID.toArrayInorder();
    }

    public PatientByName[] getPatientsSortedByName() {
        return idxByName.toArrayInorder();
    }
    
    // Returns top-K longest waiting patients (based on arrival time)
    public String topKLongestWaiting(int k, LocalDateTime now) {
        PatientVisit[] snap = peekNextN(100);
        sortByWaitTimeDesc(snap, now);
        StringBuilder sb = new StringBuilder();
        
        sb.append("Top ").append(k).append(" Longest-Waiting Patients\n");
        sb.append("-------------------------------------\n");
        
        for (int i = 0; i < Math.min(k, snap.length); i++) {
            PatientVisit v = snap[i];
            long wait = ChronoUnit.MINUTES.between(v.getArrivalDateTime(), now);
            sb.append(String.format("%2d) %s | Wait: %d min%n", i + 1, v.getPatient().getPatientName(), wait));
        }
        
        return sb.toString();
    }

    private void sortByWaitTimeDesc(PatientVisit[] arr, LocalDateTime now) {
        for (int i = 0; i < arr.length - 1; i++) {
            int max = i;
            long wMax = ChronoUnit.MINUTES.between(arr[i].getArrivalDateTime(), now);
            
            for (int j = i + 1; j < arr.length; j++) {
                long w = ChronoUnit.MINUTES.between(arr[j].getArrivalDateTime(), now);
                if (w > wMax) {
                    max = j;
                    wMax = w;
                }
            }
            
            PatientVisit temp = arr[i]; 
            arr[i] = arr[max]; 
            arr[max] = temp;
        }
    }
    
    /* ---------- Patient Listing ---------- */
    
    public Patient[] getAllPatientsSortedByID() {
        PatientByID[] wrapped = idxByID.toArrayInorder();
        Patient[] result = new Patient[wrapped.length];
        
        for (int i = 0; i < wrapped.length; i++) {
            result[i] = wrapped[i].ref;
        }
        
        return result;
    }
    
    public Patient[] getAllPatientsSortedByName() {
        PatientByName[] wrapped = idxByName.toArrayInorder();
        Patient[] result = new Patient[wrapped.length];
        
        for (int i = 0; i < wrapped.length; i++) {
            result[i] = wrapped[i].ref;
        }
        
        return result;
    } 
}
