package entity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class TreatmentRecord {
    private final String treatmentID;
    private final String patientID;
    private String diagnosis;
    private String treatment;
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

    public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }
    public void setTreatment(String treatment) { this.treatment = treatment; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TreatmentRecord)) return false;
        TreatmentRecord that = (TreatmentRecord) o;
        return Objects.equals(treatmentID, that.treatmentID);
    }

    @Override
    public int hashCode() {
        return treatmentID != null ? treatmentID.hashCode() : 0;
    }

    @Override
    public String toString() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        return String.format("[%s] %s | Dx: %s | Tx: %s",
                treatmentID,
                dateTime.format(fmt),
                diagnosis == null ? "" : diagnosis,
                treatment == null ? "" : treatment);
    }
}
