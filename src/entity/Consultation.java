package entity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
    
    public Consultation(String consultationID, String patientID, String doctorID, 
                       String symptoms, String diagnosis, String notes) {
        this.consultationID = consultationID;
        this.patientID = patientID;
        this.doctorID = doctorID;
        this.consultationDateTime = LocalDateTime.now();
        this.symptoms = symptoms;
        this.diagnosis = diagnosis;
        this.notes = notes;
        this.status = ConsultationStatus.IN_PROGRESS;
        this.treatmentRecord = null;
    }
    
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