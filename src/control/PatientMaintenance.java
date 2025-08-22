package control;

import adt.*;
import dao.PatientInitializer;
import entity.*;
import entity.keys.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class PatientMaintenance {
    /* ---------- Data Structures ---------- */
    
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
    
    /* ---------- CRUD Operations ---------- */
    
    public boolean registerPatient(Patient patient) {
        if (existsByID(patient.getPatientID())) return false;
        
        indexPatient(patient);
        return true;
    }
    
    public boolean updatePatient(Patient updatedPatient) {
        String id = updatedPatient.getPatientID();
        if (!existsByID(id)) return false;
        
        Patient old = findPatientByID(id);
        unindexPatient(old);
        indexPatient(updatedPatient);
        return true;
    }
    
    public boolean removePatientByID(String id) {
        if (!existsByID(id)) return false;
        
        Patient p = findPatientByID(id);
        unindexPatient(p);
        return true;
    }
    
    public boolean updatePatientField(String id, int fieldCode, String newValue) {
        Patient old = findPatientByID(id);
        if (old == null || newValue == null) return false;
        
        String name = old.getPatientName();
        String phone = old.getContactNo();
        String email = old.getEmail();
        Gender gender = old.getGender();
        int age = old.getAge();
        
        switch (fieldCode) {
            case 1 -> name = newValue.trim();
            case 2 -> phone = newValue.trim();
            case 3 -> email = newValue.trim();
            case 4 -> {
                try {
                    gender = Gender.valueOf(newValue.trim().toUpperCase());
                } catch (IllegalArgumentException e) {
                    return false;
                }
            }
            case 5 -> {
                try {
                    age = Integer.parseInt(newValue.trim());
                    if (age < 1 || age > 120) return false;
                } catch (NumberFormatException e) {
                    return false;
                }
            }
            default -> { return false; }
        }
        
        Patient updated = new Patient(id, name, phone, email, gender, age);
        return updatePatient(updated);
    }
    
    /* ---------- Find Operations ---------- */
    
    public boolean existsByID(String id) {
        if (id == null) return false;
        
        return idxByID.contains(new PatientByID(id, null));
    }
    
    public Patient findPatientByID(String id) {
        PatientByID wrapper = idxByID.find(new PatientByID(id, null));
        return (wrapper != null) ? wrapper.ref : null;
    }
    
    public Patient[] findPatientsByName(String name) {
        PatientByName[] all = idxByName.toArrayInorder(new PatientByName[idxByName.size()]);
        int count = 0;
        
        // count matches
        for (PatientByName p : all) {
            if (p.getPatient().getPatientName().equalsIgnoreCase(name)) {
                count++;
            }
        }
        
        // collect matches
        Patient[] matches = new Patient[count];
        int i = 0;
        for (PatientByName p : all) {
            if (p.getPatient().getPatientName().equalsIgnoreCase(name)) {
                matches[i++] = p.getPatient();
            }
        }
        
        return matches;
    }
    
    public Patient findPatientByPhone(String phone) {
        PatientByContactNo wrapper = idxByContact.find(new PatientByContactNo(phone, null));
        return wrapper != null ? wrapper.ref : null;
    }
    
    public Patient findPatientByEmail(String email) {
        PatientByEmail wrapper = idxByEmail.find(new PatientByEmail(email, null));
        return wrapper != null ? wrapper.ref : null;
    }
    
    /* ---------- Visit Queue ---------- */
    
    public void registerVisit(Patient patient, VisitType visitType) {
        if (existsByID(patient.getPatientID())) {
            patient = findPatientByID(patient.getPatientID());
        } else {
            indexPatient(patient);
        }

        visitQueue.enqueue(new PatientVisit(patient, visitType, LocalDateTime.now()));
    }
    
    public PatientVisit serveNextVisit() {
        return !isEmpty() ? visitQueue.dequeue() : null;
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
    
    // Queue Access
    
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
    
    // Queue Statistics 
    
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
    
    // need modification gua
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
    
    /* ---------- Patient Sorting ---------- */
    
    public Patient[] getAllPatientsSortedByID(boolean descending) {
        PatientByID[] wrapped = idxByID.toArrayInorder(new PatientByID[idxByID.size()]);
        
        if (descending)
            reverseArray(wrapped);
        
        Patient[] result = new Patient[wrapped.length];
        
        for (int i = 0; i < wrapped.length; i++) {
            result[i] = wrapped[i].ref;
        }
        
        return result;
    }

    public Patient[] getAllPatientsSortedByName(boolean descending) {
        PatientByName[] wrapped = idxByName.toArrayInorder(new PatientByName[idxByName.size()]);
        
        if (descending)
            reverseArray(wrapped);
        
        Patient[] result = new Patient[wrapped.length];
        
        for (int i = 0; i < wrapped.length; i++)  {
            result[i] = wrapped[i].ref;
        }
        
        return result; 
    }
    
    public Patient[] getAllPatientsSortedByGender(boolean descending) {
        Patient[] arr = getAllPatientsSortedByName(false);
        selectionSortByGender(arr, descending);
        return arr;
    }
    
    public Patient[] getAllPatientsSortedByAge(boolean descending) {
        Patient[] arr = getAllPatientsSortedByName(false);
        selectionSortByAge(arr, descending);
        return arr;
    }
    
    /* ---------- Sort Helpers ---------- */
    
    private void selectionSortByGender(Patient[] a, boolean descending) {
        for (int i = 0; i < a.length - 1; i++) {
            int target = i;
            
            for (int j = i + 1; j < a.length; j++) {
                int cmp = a[j].getGender().compareTo(a[target].getGender());
                
                if (cmp == 0) 
                    cmp = a[j].getPatientName().compareToIgnoreCase(a[target].getPatientName());
                
                if ((descending && cmp > 0) || (!descending && cmp < 0)) 
                    target = j;
            }
            
            swap(a, i, target);
        }
    }


    private void selectionSortByAge(Patient[] a, boolean descending) {
        for (int i = 0; i < a.length - 1; i++) {
            int target = i;
            
            for (int j = i + 1; j < a.length; j++) {
                int cmp = Integer.compare(a[j].getAge(), a[target].getAge());
                
                if (cmp == 0) 
                    cmp = a[j].getPatientName().compareToIgnoreCase(a[target].getPatientName());
                
                if ((descending && cmp > 0) || (!descending && cmp < 0)) 
                    target = j;
            }
            
            swap(a, i, target);
        }
    }
    
    private <T> void reverseArray(T[] array) {
        for (int i = 0, j = array.length - 1; i < j; i++, j--) {
            T temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }
    }


    private void swap(Patient[] a, int i, int j) {
        if (i == j) return;
        
        Patient temp = a[i];
        a[i] = a[j];
        a[j] = temp;
    }
    
    /* ---------- Reports ---------- */
    
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
}
