package control;

import adt.*;
import dao.PatientInitializer;
import entity.*;
import entity.keys.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class PatientManagement {
    /* ---------- Fields & Constructor ---------- */
    
    private final QueueInterface<PatientVisit> visitQueue;
    private final AVLTree<PatientByID> idxByID = new AVLTree<>();
    private final AVLTree<PatientByName> idxByName = new AVLTree<>();
    private final AVLTree<PatientByContactNo> idxByContact = new AVLTree<>();
    private final AVLTree<PatientByEmail> idxByEmail = new AVLTree<>();
    
    public PatientManagement() {
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
        if (patient == null) return false;
        
        // Uniqueness checks
        if (existsByID(patient.getPatientID())) return false;
        if (findPatientByPhone(patient.getContactNo()) != null) return false;
        if (findPatientByEmail(patient.getEmail()) != null) return false;
        
        indexPatient(patient);
        return true;
    }
    
    public boolean updatePatient(Patient updatedPatient) {
        String id = updatedPatient.getPatientID();
        if (!existsByID(id)) return false;
        
        Patient old = findPatientByID(id);
        unindexPatient(old);
        indexPatient(updatedPatient);
        updatePatientInVisitQueue(id);
        return true;
    }
    
    public boolean removePatientByID(String id) {
        if (!existsByID(id)) return false;
        
        Patient p = findPatientByID(id);
        unindexPatient(p);
        removeVisitByID(id);
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
    
    /* ---------- Visit Queue Operations ---------- */
    
    public boolean registerVisit(Patient patient, VisitType visitType) {
        if (!existsByID(patient.getPatientID())) {
            return false;
        }
        
        // Check last registration time for this patient
        QueueIterator<PatientVisit> it = getIterator();
        while (it.hasNext()) {
            PatientVisit visit = it.getNext();
            if (visit.getPatient().getPatientID().equals(patient.getPatientID())) {
                long minutesSince = ChronoUnit.MINUTES.between(visit.getArrivalDateTime(), LocalDateTime.now());
                if (minutesSince < 10) { // cooldown window
                    return false; // prevent repeated registration
                }
            }
        }
        
        visitQueue.enqueue(new PatientVisit(patient, visitType, LocalDateTime.now()));
        return true;
    }
    
    public PatientVisit serveNextVisit() {
        if (isEmpty()) return null;
        
        PatientVisit next = visitQueue.dequeue();
        next.setStatus(VisitStatus.SERVED);
        return next;
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
    
    public PatientVisit getNextVisit() {
        QueueIterator<PatientVisit> it = getIterator();
        
        while (it.hasNext()) {
            PatientVisit visit = it.getNext();
            if (visit.getStatus() == VisitStatus.WAITING)
                return visit;
        }
        return null;
    }
    
    public PatientVisit[] peekNextN(int n) {
        int size = Math.min(n, visitQueue.size());
        PatientVisit[] result = new PatientVisit[size];
        
        QueueIterator<PatientVisit> it = getIterator();
        int i = 0;
        
        while (it.hasNext() && i < size) {
            PatientVisit v = it.getNext();
            if (v.getStatus() == VisitStatus.WAITING) 
                result[i++] = v;
        }
        
        // if there are fewer WAITING patients than size
        if (i < size) {
            PatientVisit[] trimmed = new PatientVisit[i];
            for (int j = 0; j < 1; j++) {
                trimmed[j] = result [j];
            }
            return trimmed;
        }
        
        return result;
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
    
    public PatientVisit[] getAllVisits() {
        QueueIterator<PatientVisit> it = getIterator();
        PatientVisit[] temp = new PatientVisit[visitQueue.size()];
        int i = 0;

        while (it.hasNext()) {
            temp[i++] = it.getNext();
        }

        return temp;
    }
    
    public int countVisitsByID(String id) {
        if (id == null) return 0;

        int count = 0;
        QueueIterator<PatientVisit> it = getIterator();

        while (it.hasNext()) {
            PatientVisit v = it.getNext();
            if (id.equals(v.getPatient().getPatientID())) {
                count++;
            }
        }

        return count;
    }
    
    private void updatePatientInVisitQueue(String id) {
        QueueInterface<PatientVisit> temp = new LinkedQueue<>();
        
        while(!visitQueue.isEmpty()) {
            PatientVisit visit = visitQueue.dequeue();
            
            if (visit.getPatient().getPatientID().equals(id)) {
                Patient updated = findPatientByID(id);
                visit.setPatient(updated);
            }
            
            temp.enqueue(visit);
        }
        
        while (!temp.isEmpty()) {
            visitQueue.enqueue(temp.dequeue());
        }
    }
    
    // Queue Helpers
    
    public boolean isEmpty() {
        return visitQueue.isEmpty();
    }
    
    public int getQueueSize() {
        return visitQueue.size();
    }
    
    private QueueIterator<PatientVisit> getIterator() {
        return visitQueue.getIterator();
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
    
    /* ---------- Sorting Patients ---------- */
    
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
    
    /* ---------- Sorting Helpers ---------- */
    
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
}
