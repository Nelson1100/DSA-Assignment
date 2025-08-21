package entity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TreatmentRecord {

    private final String treatmentID;
    private final String patientID;
    private final String diagnosis;
    private final String treatment;
    private final LocalDateTime dateTime;

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

    @Override
    public String toString() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        return String.format("[%s] %s | Diagnosis: %s | Treatment: %s",
                treatmentID, dateTime.format(fmt), diagnosis, treatment);
    }
}
