package control;

import adt.*;
import entity.*;
import entity.keys.PatientByID;
import entity.keys.PatientByName;
import dao.PatientInitializer;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

public class PatientMaintenance {
    private final QueueInterface<PatientVisit> visitQueue;
    private final AVLTree<PatientByID> idxByID = new AVLTree<>();
    private final AVLTree<PatientByName> idxByName = new AVLTree<>();
    
    public PatientMaintenance() {
        visitQueue = new LinkedQueue<>();
        PatientInitializer.initialize(visitQueue); // Pre-load test data
        rebuildIndexesFromQueue();
    }
    
    private void rebuildIndexesFromQueue() {
        QueueIterator<Patient> it = getIterator();
        
        while (it.hasNext()) {
            Patient p = it.getNext();
            indexPatient(p);
        }
    }
    
    private void indexPatient(Patient p) {
        idxByID.insert(new PatientByID(p.getPatientID(), p));
        idxByName.insert(new PatientByName(p.getPatientName(), p.getPatientID(), p));
    }
    
    private void unindexPatient(Patient p) {
        idxByID.delete(new PatientByID(p.getPatientID(), null));
        idxByName.delete(new PatientByName(p.getPatientName(), p.getPatientID(), null));
    }
    
    public void registerPatient(Patient patient) {
        if (existsByID(patient.getPatientID())) 
            return;
        
        visitQueue.enqueue(patient);
        indexPatient(patient);
    }
    
    public Patient serveNextPatient() {
        if (isEmpty())
            return null;
        
        Patient served = visitQueue.dequeue();
        unindexPatient(served);
        
        return served;
    }
    
    public boolean removeByID(String id) {
        if (id == null || isEmpty())
            return false;
        
        QueueInterface<Patient> temp = new LinkedQueue<>();
        boolean removed = false;
        
        while (!isEmpty()) {
            Patient p = visitQueue.dequeue();
            if (!removed && id.equals(p.getPatientID())) {
                unindexPatient(p);
                removed = true;
            } else {
                temp.enqueue(p);
            }
        }
        
        // Rebuild queue
        while(!temp.isEmpty()) {
            visitQueue.enqueue(temp.dequeue());
        }
        
        return removed;
    }
    
    public int findPosition(String id) {
        int pos = 1;
        QueueIterator<Patient> it = getIterator();
        
        while (it.hasNext()) {
            if (it.getNext().getPatientID().equals(id))
                return pos;
            pos++;
        }
        
        return -1;
    }
    
    public boolean isEmpty() {
        return visitQueue.isEmpty();
    }
    
    public int getQueueSize() {
        return visitQueue.size();
    }
    
    public boolean existsByID(String id) {
        if (id == null) 
            return false;
        
        return idxByID.contains(new PatientByID(id, null));
    }
    
    public void viewAllPatients() {
        QueueIterator<Patient> it = getIterator();
        int i = 1;
        
        while (it.hasNext()) {
            Patient p = it.getNext();
            System.out.printf("%2d) %s%n", i++, p);
        }
    }
    
    public int countByVisitType(VisitType type) {
        int count = 0;
        QueueIterator<Patient> it = getIterator();
        
        while (it.hasNext()) {
            Patient p = it.getNext();
            if (p.getVisitType() == type)
                count++;
        }
        
        return count;
    }
    
    public int countByGender(Gender gender) {
        int count = 0;
        QueueIterator<Patient> it = getIterator();
        
        while (it.hasNext()) {
            Patient p = it.getNext();
            if (p.getGender() == gender)
                count++;
        }
        
        return count;
    }
    
    public double avgWaitMinutes(LocalTime now) {
        long total = 0;
        int count = 0;
        QueueIterator<Patient> it = getIterator();
        
        while(it.hasNext()) {
            Patient p = it.getNext();
            long wait = ChronoUnit.MINUTES.between(p.getArrivalTime(), now);
            total += Math.max(wait, 0);
            count++;
        }
        
        return count == 0 ? 0.0 : (double) total / count;
    }
    
    public long maxWaitMinutes(LocalTime now) {
        long max = 0;
        QueueIterator<Patient> it = getIterator();
        
        while (it.hasNext()) {
            Patient p = it.getNext();
            long wait = ChronoUnit.MINUTES.between(p.getArrivalTime(), now);
            if (wait > max)
                max = wait;
        }
        
        return max;
    }
    
    public Patient getNextPatient() {
        QueueIterator<Patient> it = getIterator();
        return it.hasNext() ? it.getNext() : null;
    }
    
    public Patient[] peekNextN(int n) {
        if (n <= 0)
            return new Patient[0];
        
        int limit = Math.min(n, visitQueue.size());
        Patient[] result = new Patient[limit];
        
        QueueIterator<Patient> it = getIterator();
        for (int i = 0; i < limit && it.hasNext(); i++) {
            result[i] = it.getNext();
        }
        
        return result;
    }
    
    public PatientByID[] getPatientsSortedByID() {
        return idxByID.toArrayInorder();
    }

    public PatientByName[] getPatientsSortedByName() {
        return idxByName.toArrayInorder();
    }
    
    // Returns top-K longest waiting patients (based on arrival time)
    public String topKLongestWaiting(int k, LocalTime now) {
        Patient[] snap = peekNextN(100);
        sortByWaitTimeDesc(snap, now);
        StringBuilder sb = new StringBuilder();
        
        sb.append("Top ").append(k).append(" Longest-Waiting Patients\n");
        sb.append("-------------------------------------\n");
        for (int i = 0; i < Math.min(k, snap.length); i++) {
            Patient p = snap[i];
            long wait = ChronoUnit.MINUTES.between(p.getArrivalTime(), now);
            sb.append(String.format("%2d) %s | Wait: %d min%n", i + 1, p.getPatientName(), wait));
        }
        return sb.toString();
    }

    private void sortByWaitTimeDesc(Patient[] arr, LocalTime now) {
        for (int i = 0; i < arr.length - 1; i++) {
            int max = i;
            long wMax = ChronoUnit.MINUTES.between(arr[i].getArrivalTime(), now);
            for (int j = i + 1; j < arr.length; j++) {
                long w = ChronoUnit.MINUTES.between(arr[j].getArrivalTime(), now);
                if (w > wMax) {
                    max = j;
                    wMax = w;
                }
            }
            Patient temp = arr[i]; arr[i] = arr[max]; arr[max] = temp;
        }
    }

    public String queueHealthSnapshot(LocalTime now) {
        int walkIn = countByVisitType(VisitType.WALK_IN);
        int appointment = countByVisitType(VisitType.APPOINTMENT);
        double avg = avgWaitMinutes(now);
        long max = maxWaitMinutes(now);
        Patient next = getNextPatient();
        return String.format("""
                Queue Health Summary
                ---------------------
                Walk-in Patients   : %d
                Appointments       : %d
                Average Wait       : %.1f min
                Max Wait           : %d min
                Next to Serve      : %s (%s)
                """, walkIn, appointment, avg, max,
                next != null ? next.getPatientName() : "(none)",
                next != null ? next.getPatientID() : "(none)");
    }
    
//    public String reportSortedByID() {
//        StringBuilder sb = new StringBuilder();
//        sb.append("Patient Sorted by ID\n");
//        sb.append("----------------------");
//        PatientByID[] nodes = idxByID.toArrayInorder();
//        
//        if (nodes.length == 0)
//            sb.append("(none)\n");
//        else {
//            for (int i = 0; i < nodes.length; i++) {
//                Patient p = nodes[i].ref;
//                sb.append(String.format("%2d) %s%n", i + 1, p));
//            }
//        }
//        sb.append("\nAVL(ID) height=").append(idxByID.height())
//          .append("  valid=").append(idxByID.isValidAVL()).append("\n");
//        
//        return sb.toString();
//    }
//    
//    public String reportSortedByName() {
//        StringBuilder sb = new StringBuilder();
//        sb.append("Patients Sorted by Name (A–Z)\n");
//        sb.append("------------------------------\n");
//        PatientByName[] nodes = idxByName.toArrayInorder();
//        
//        if (nodes.length == 0) 
//            sb.append("(none)\n");
//        else {
//            for (int i = 0; i < nodes.length; i++) {
//                Patient p = nodes[i].ref;
//                sb.append(String.format("%2d) %s%n", i + 1, p));
//            }
//        }
//        sb.append("\nAVL(Name) height=").append(idxByName.height())
//          .append("  valid=").append(idxByName.isValidAVL()).append('\n');
//        
//        return sb.toString();
//    }
    
    // findByID, findByEmail, containID
    // snapshot()
    // listByVisitType(...), listByGender(...)
    // longestWaitingPatient(...)
    // estimateWaitingTimeFor(...)
    
    /* ---------- Iterator Access (read-only) ---------- */

    private QueueIterator<Patient> getIterator() {
        return visitQueue.getIterator();
    }
    
}
