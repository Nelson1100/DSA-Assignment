package entity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import adt.QueueInterface;
import adt.QueueIterator;
import adt.LinkedQueue;

public class TreatmentRecord implements Comparable<TreatmentRecord> {

    private String treatmentID;
    private String patientID;
    private String diagnosis;
    private String treatment;
    private LocalDateTime dateTime;

    public TreatmentRecord() {
        this("", "", "", "", LocalDateTime.now());
    }

    public TreatmentRecord(String treatmentID, String patientID, String diagnosis, String treatment, LocalDateTime dateTime) {
        this.treatmentID = treatmentID;
        this.patientID = patientID;
        this.diagnosis = diagnosis;
        this.treatment = treatment;
        this.dateTime = dateTime;
    }

    public String getTreatmentID() { return treatmentID; }
    public String getPatientID()   { return patientID; }
    public String getDiagnosis()   { return diagnosis; }
    public String getTreatment()   { return treatment; }
    public LocalDateTime getDateTime() { return dateTime; }

    public void setTreatmentID(String treatmentID) { this.treatmentID = treatmentID; }
    public void setPatientID(String patientID)     { this.patientID = patientID; }
    public void setDiagnosis(String diagnosis)     { this.diagnosis = diagnosis; }
    public void setTreatment(String treatment)     { this.treatment = treatment; }
    public void setDateTime(LocalDateTime dateTime){ this.dateTime = dateTime; }

    @Override
    public String toString() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        return String.format("[%s] %s | Diagnosis: %s | Treatment: %s",
                treatmentID, dateTime != null ? dateTime.format(fmt) : "N/A", diagnosis, treatment);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TreatmentRecord)) return false;
        TreatmentRecord other = (TreatmentRecord) o;
        return this.treatmentID != null && this.treatmentID.equals(other.treatmentID);
    }

    @Override
    public int hashCode() {
        return (treatmentID == null) ? 0 : treatmentID.hashCode();
    }

    @Override
    public int compareTo(TreatmentRecord o) {
        return this.treatmentID.compareTo(o.treatmentID);
    }

    public static TreatmentRecord findRecordByID(QueueInterface<TreatmentRecord> records, String treatmentID) {
        if (records == null || treatmentID == null) return null;
        QueueIterator<TreatmentRecord> it = ((LinkedQueue<TreatmentRecord>) records).getIterator();
        while (it.hasNext()) {
            TreatmentRecord r = it.getNext();
            if (treatmentID.equals(r.getTreatmentID())) return r;
        }
        return null;
    }
    
}
