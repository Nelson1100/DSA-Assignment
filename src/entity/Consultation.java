package entity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author Sim Jia Quan
 */

public class Consultation implements Comparable<Consultation> {
    private final String consultationID;
    private final String patientID;
    private final String doctorID;
    private final LocalDateTime consultationDateTime;
    private String symptoms;
    private String diagnosis;
    private String notes;
    private ConsultationStatus status;
    private TreatmentRecord treatmentRecord;
    
    public Consultation(String consultationID, String patientID, String doctorID) {
        this.consultationID = consultationID;
        this.patientID = patientID;
        this.doctorID = doctorID;
        this.consultationDateTime = LocalDateTime.now();
        this.symptoms = "";
        this.diagnosis = "";
        this.notes = "";
        this.status = ConsultationStatus.IN_PROGRESS;
        this.treatmentRecord = null;
    }
    
    /* ---------- Getters ---------- */
    
    public String getConsultationID() {
        return consultationID;
    }
    
    public String getPatientID() {
        return patientID;
    }
    
    public String getDoctorID() {
        return doctorID;
    }
    
    public LocalDateTime getConsultationDateTime() {
        return consultationDateTime;
    }
    
    public String getSymptoms() {
        return symptoms;
    }
    
    public String getDiagnosis() {
        return diagnosis;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public ConsultationStatus getStatus() {
        return status;
    }
    
    public TreatmentRecord getTreatmentRecord() {
        return treatmentRecord;
    }
    
    /* ---------- Setters ---------- */
    
    public void setSymptoms(String symptoms) {
        this.symptoms = symptoms;
    }
    
    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public void setStatus(ConsultationStatus status) {
        this.status = status;
    }
    
    public void setTreatmentRecord(TreatmentRecord treatmentRecord) {
        this.treatmentRecord = treatmentRecord;
        if (treatmentRecord != null) {
            this.status = ConsultationStatus.COMPLETED;
        }
    }
    
    /* ---------- Business Logic Methods ---------- */
    
    public void completeConsultation(String diagnosis, String notes) {
        this.diagnosis = diagnosis;
        this.notes = notes;
        this.status = ConsultationStatus.COMPLETED;
    }
    
    public void cancelConsultation() {
        this.status = ConsultationStatus.CANCELLED;
    }
    
    public boolean isCompleted() {
        return status == ConsultationStatus.COMPLETED;
    }
    
    public boolean isCancelled() {
        return status == ConsultationStatus.CANCELLED;
    }
    
    public boolean isInProgress() {
        return status == ConsultationStatus.IN_PROGRESS;
    }
    
    /* ---------- ID Validation Methods ---------- */
    /* Validates if the consultation ID follows the expected format */
    public boolean hasValidConsultationID() {
        return consultationID != null && consultationID.matches("^C\\d{12}$");
    }
    
    /* Validates if the patient ID follows the expected format */
    public boolean hasValidPatientID() {
        return patientID != null && patientID.matches("^P\\d{12}$");
    }
    
    /* Validates if the doctor ID follows the expected format */
    public boolean hasValidDoctorID() {
        return doctorID != null && doctorID.matches("^D\\d{12}$");
    }
    
    /* Validates if all IDs in the consultation are properly formatted */
    public boolean hasValidIDs() {
        return hasValidConsultationID() && hasValidPatientID() && hasValidDoctorID();
    }
    
    /* Gets a summary of ID validation status*/
    public String getIDValidationSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("=== ID Validation Summary ===\n");
        summary.append("Consultation ID: ").append(hasValidConsultationID() ? "✓ Valid" : "✗ Invalid").append("\n");
        summary.append("Patient ID: ").append(hasValidPatientID() ? "✓ Valid" : "✗ Invalid").append("\n");
        summary.append("Doctor ID: ").append(hasValidDoctorID() ? "✓ Valid" : "✗ Invalid").append("\n");
        return summary.toString();
    }
    
    /* ---------- Utility Methods ---------- */
    /* Gets the consultation duration in minutes */
    public long getDurationInMinutes() {
        if (isInProgress()) {
            return 0; // Still ongoing
        }
        
        LocalDateTime endTime = consultationDateTime;
        if (treatmentRecord != null) {
            // Use treatment record completion time if available
            endTime = treatmentRecord.getDateTime();
        }
        
        return java.time.Duration.between(consultationDateTime, endTime).toMinutes();
    }
    
    /* Checks if the consultation has any symptoms recorded*/
    public boolean hasSymptoms() {
        return symptoms != null && !symptoms.trim().isEmpty();
    }
    
    /* Checks if the consultation has a diagnosis */
    public boolean hasDiagnosis() {
        return diagnosis != null && !diagnosis.trim().isEmpty();
    }
    
    /* Checks if the consultation has any notes */
    public boolean hasNotes() {
        return notes != null && !notes.trim().isEmpty();
    }
    
    @Override
    public String toString() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        return String.format(
            "=== Consultation Record ===\n" +
            "Consultation ID : %s\n" +
            "Patient ID      : %s\n" +
            "Doctor ID       : %s\n" +
            "Date & Time     : %s\n" +
            "Status          : %s\n" +
            "Symptoms        : %s\n" +
            "Diagnosis       : %s\n" +
            "Notes           : %s\n",
            consultationID,
            patientID,
            doctorID,
            consultationDateTime.format(dtf),
            status,
            symptoms.isEmpty() ? "Not recorded" : symptoms,
            diagnosis.isEmpty() ? "Not diagnosed" : diagnosis,
            notes.isEmpty() ? "No notes" : notes
        );
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Consultation other)) return false;
        return consultationID.equals(other.consultationID);
    }
    
    @Override
    public int hashCode() {
        return consultationID.hashCode();
    }
    
    @Override
    public int compareTo(Consultation other) {
        return this.consultationID.compareTo(other.consultationID);
    }
}