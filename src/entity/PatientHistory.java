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

    public void addRecord(TreatmentRecord record) { records.enqueue(record); }

    public int countRecordsOn(LocalDate date) {
        int c = 0;
        QueueIterator<TreatmentRecord> it = ((LinkedQueue<TreatmentRecord>) records).getIterator();
        while (it.hasNext()) {
            TreatmentRecord tr = it.getNext();
            if (tr.getDateTime().toLocalDate().equals(date)) c++;
        }
        return c;
    }

    public TreatmentRecord findRecordByID(String treatmentID) {
        QueueIterator<TreatmentRecord> it = ((LinkedQueue<TreatmentRecord>) records).getIterator();
        while (it.hasNext()) {
            TreatmentRecord tr = it.getNext();
            if (treatmentID.equals(tr.getTreatmentID())) return tr;
        }
        return null;
    }

    @Override
    public int compareTo(PatientHistory other) {
        if (other == null || other.patient == null) return 1;
        return this.patient.getPatientID().compareTo(other.patient.getPatientID());
    }

    @Override
    public String toString() {
        return patient.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PatientHistory)) return false;
        PatientHistory ph = (PatientHistory) o;
        return patient.getPatientID() != null && patient.getPatientID().equals(ph.getPatient().getPatientID());
    }

    @Override
    public int hashCode() {
        return patient.getPatientID() != null ? patient.getPatientID().hashCode() : 0;
    }
}
