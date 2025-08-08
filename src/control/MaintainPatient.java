package control;

import adt.*;
import dao.PatientInitializer;
import entity.*;

public class MaintainPatient {
    private QueueInterface<Patient> patientQueue;
    
    public MaintainPatient() {
        patientQueue = new LinkedQueue<>();
        PatientInitializer.initialize(patientQueue); // Pre-load test data
    }
    
    public void registerPatient(Patient patient) {
        patientQueue.enqueue(patient);
    }
    
    public Patient serveNextPatient() {
        if (!patientQueue.isEmpty()) {
            return patientQueue.dequeue();
        }
        return null;
    }
    
    public void viewAllPatients() {
        QueueInterface<Patient> tempQueue = new LinkedQueue<>();
        
        while (!patientQueue.isEmpty()) {
            Patient p = patientQueue.dequeue();
            System.out.println(p);
            tempQueue.enqueue(p);
        }
        
        // Restore original queue
        while (!tempQueue.isEmpty()) {
            patientQueue.enqueue(tempQueue.dequeue());
        }
    }
    
    public boolean isEmpty() {
        return patientQueue.isEmpty();
    }
    
    public int getQueueSize() {
        return patientQueue.size();
    }
    
    public int countByVisitType(VisitType type) {
        int count = 0;
        QueueInterface<Patient> tempQueue = new LinkedQueue<>();
        
        while (!patientQueue.isEmpty()) {
            Patient p = patientQueue.dequeue();
            if (p.getVisitType() == type) {
                count++;
            }
            tempQueue.enqueue(p);
        }
        
        // Restore original queue
        while (!tempQueue.isEmpty()) {
            patientQueue.enqueue(tempQueue.dequeue());
        }
        
        return count;
    }
    
    // searchByID(String id), removeByID(String ID), filterByGender(Gender g), estimateWaitingTime(), etc
}
