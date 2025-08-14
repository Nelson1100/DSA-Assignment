package control;

import adt.*;
import entity.Patient;
import entity.VisitType;
import entity.Gender;
import entity.keys.PatientByID;
import entity.keys.PatientByName;
import dao.PatientInitializer;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

public class PatientMaintenance {
    private QueueInterface<Patient> patientQueue;
    private final AVLTree<PatientByID> idxByID = new AVLTree<>();
    private final AVLTree<PatientByName> idxByName = new AVLTree<>();
    
    public PatientMaintenance() {
        patientQueue = new LinkedQueue<>();
        PatientInitializer.initialize(patientQueue); // Pre-load test data
        // rebuildIndexesFromQueue();
    }
    
    private void rebuildIndexesFromQueue() {
        QueueIterator<Patient> it = getIterator();
        
        while (it.hasNext()) {
            Patient p = it.getNext();
            idxByID.insert(new PatientByID(p.getPatientID(), p));
            idxByName.insert(new PatientByName(p.getPatientName(), p.getPatientID(), p));
        }
    }
    
    public void registerPatient(Patient patient) {
        if (patient == null || existsByID(patient.getPatientID())) 
            return;
        
        patientQueue.enqueue(patient);
        idxByID.insert(new PatientByID(patient.getPatientID(), patient));
        idxByName.insert(new PatientByName(patient.getPatientName(), patient.getPatientID(), patient));
    }
    
    public Patient serveNextPatient() {
        if (patientQueue.isEmpty())
            return null;
        
        Patient served = patientQueue.dequeue();
        idxByID.delete(new PatientByID(served.getPatientID(), null));
        idxByName.delete(new PatientByName(served.getPatientName(), served.getPatientID(), null));
        
        return served;
    }
    
    public boolean isEmpty() {
        return patientQueue.isEmpty();
    }
    
    public int getQueueSize() {
        return patientQueue.size();
    }
    
    public void viewAllPatients() {
        QueueIterator<Patient> it = getIterator();
        int i = 1;
        
        while (it.hasNext()) {
            System.out.printf("%2d) %s%n", i++, it.getNext());
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
    
    public double avgWaitMinutes(LocalTime now) {
        long total = 0;
        int n = 0;
        QueueIterator<Patient> it = getIterator();
        
        while(it.hasNext()) {
            Patient p = it.getNext();
            long mins = p.getArrivalTime().until(now, ChronoUnit.MINUTES);
            if (mins < 0)
                mins = 0;
            total += mins;
            n++;
        }
        
        return n == 0 ? 0.0 : (double) total / n;
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
    
    public boolean removeByID(String id) {
        if (id == null || patientQueue.isEmpty())
            return false;
        
        QueueInterface<Patient> temp = new LinkedQueue<>();
        boolean removed = false;
        
        while (!patientQueue.isEmpty()) {
            Patient patient = patientQueue.dequeue();
            if (!removed && id.equals(patient.getPatientID())) {
                removed = true;
                idxByID.delete(new PatientByID(patient.getPatientID(), null));
                idxByName.delete(new PatientByName(patient.getPatientName(), patient.getPatientID(), null));
            } else {
                temp.enqueue(patient);
            }
        }
        
        while(!temp.isEmpty()) {
            patientQueue.enqueue(temp.dequeue());
        }
        
        return removed;
    }
    
    public boolean existsByID(String id) {
        return id != null && idxByID.contains(new PatientByID(id, null));
    }
    
    /* ---------- OPTIONAL ---------- */
    
    public Patient[] peekNextN(int n) {
        if (n <= 0)
            return new Patient[0];
        
        int k = Math.min(n, patientQueue.size());
        Patient[] out = new Patient[k];
        
        int i = 0;
        QueueIterator<Patient> it = getIterator();
        
        while (i < k && it.hasNext()) {
            out[i++] = it.getNext();
        }
        
        return out;
    }
    
    public String reportSortedByID() {
        StringBuilder sb = new StringBuilder();
        sb.append("Patient Sorted by ID\n");
        sb.append("----------------------");
        PatientByID[] nodes = idxByID.toArrayInorder();
        
        if (nodes.length == 0)
            sb.append("(none)\n");
        else {
            for (int i = 0; i < nodes.length; i++) {
                Patient p = nodes[i].ref;
                sb.append(String.format("%2d) %s%n", i + 1, p));
            }
        }
        sb.append("\nAVL(ID) height=").append(idxByID.height())
          .append("  valid=").append(idxByID.isValidAVL()).append("\n");
        
        return sb.toString();
    }
    
    public String reportSortedByName() {
        StringBuilder sb = new StringBuilder();
        sb.append("Patients Sorted by Name (A–Z)\n");
        sb.append("------------------------------\n");
        PatientByName[] nodes = idxByName.toArrayInorder();
        
        if (nodes.length == 0) 
            sb.append("(none)\n");
        else {
            for (int i = 0; i < nodes.length; i++) {
                Patient p = nodes[i].ref;
                sb.append(String.format("%2d) %s%n", i + 1, p));
            }
        }
        sb.append("\nAVL(Name) height=").append(idxByName.height())
          .append("  valid=").append(idxByName.isValidAVL()).append('\n');
        
        return sb.toString();
    }
    
    // findByID, findByEmail, containID
    // snapshot()
    // listByVisitType(...), listByGender(...)
    // longestWaitingPatient(...)
    // estimateWaitingTimeFor(...)
    
    /* ---------- Iterator Access (read-only) ---------- */

    private QueueIterator<Patient> getIterator() {
        return patientQueue.getIterator();
    }
    
}
