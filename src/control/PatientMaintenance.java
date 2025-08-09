package control;

import adt.*;
import entity.Patient;
import entity.VisitType;
import entity.Gender;
import dao.PatientInitializer;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

public class PatientMaintenance {
    private QueueInterface<Patient> patientQueue;
    
    public PatientMaintenance() {
        patientQueue = new LinkedQueue<>();
        PatientInitializer.initialize(patientQueue); // Pre-load test data
    }
    
    public void registerPatient(Patient patient) {
        patientQueue.enqueue(patient);
    }
    
    public Patient serveNextPatient() {
        return patientQueue.isEmpty() ? null : patientQueue.dequeue();
    }
    
    public boolean isEmpty() {
        return patientQueue.isEmpty();
    }
    
    public int getQueueSize() {
        return patientQueue.size();
    }
    
    public void viewAllPatients() {
        QueueIterator<Patient> it = getIterator();
        while (it.hasNext()) {
            System.out.println(it.getNext());
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
        if (patientQueue.isEmpty())
            return false;
        
        QueueInterface<Patient> temp = new LinkedQueue<>();
        boolean removed = false;
        
        while (!patientQueue.isEmpty()) {
            Patient patient = patientQueue.dequeue();
            if (!removed && patient.getPatientID().equals(id)) {
                removed = true;
            } else {
                temp.enqueue(patient);
            }
        }
        
        while(!temp.isEmpty()) {
            patientQueue.enqueue(temp.dequeue());
        }
        
        return removed;
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
