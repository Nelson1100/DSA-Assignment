package entity;

import adt.LinkedQueue;
import adt.QueueInterface;
import adt.QueueIterator;

import java.time.LocalDate;

public class PatientHistory implements Comparable<PatientHistory> {
    private final Patient patient;                             
    private final QueueInterface<TreatmentRecord> records;     

    public PatientHistory(Patient patient) {
        this.patient = patient;
        this.records = new LinkedQueue<>();
    }

    public Patient getPatient() { return patient; }
    public QueueInterface<TreatmentRecord> getRecords() { return records; }

    public void addRecord(TreatmentRecord record) {
        records.enqueue(record);
    }

    public int countRecordsOn(LocalDate date) {
        int count = 0;
        QueueIterator<TreatmentRecord> it = ((LinkedQueue<TreatmentRecord>) records).getIterator();
        while (it.hasNext()) {
            TreatmentRecord tr = it.getNext();
            if (tr.getDateTime().toLocalDate().equals(date)) count++;
        }
        return count;
    }

    @Override
    public int compareTo(PatientHistory other) {
        return this.patient.getPatientID().compareTo(other.patient.getPatientID());
    }

    @Override
    public String toString() {
        return patient.toString();
    }
}
