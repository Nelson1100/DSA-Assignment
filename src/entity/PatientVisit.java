package entity;

import java.time.LocalDateTime;

public class PatientVisit {
    private Patient patient;
    private final LocalDateTime arrivalDateTime;
    private final VisitType visitType;
    private VisitStatus status;
    
    public PatientVisit(Patient patient, VisitType visitType, LocalDateTime arrivalDateTime) {
        this.patient = patient;
        this.arrivalDateTime = arrivalDateTime;
        this.visitType = visitType;
        this.status = VisitStatus.WAITING;
    }
    
    public Patient getPatient() {
        return patient;
    }
    
    public VisitType getVisitType() {
        return visitType;
    }
    
    public LocalDateTime getArrivalDateTime() {
        return arrivalDateTime;
    }
    
    public VisitStatus getStatus() {
        return status;
    }
    
    public void setStatus(VisitStatus status) {
        this.status = status;
    }
    
    public void setPatient(Patient patient) {
        this.patient = patient;
    }
    
    @Override
    public String toString() {
        return String.format("Visit:%nID: %s | Name: %s | Type: %s | Arrival: %s | Status: %s",
            patient.getPatientID(), patient.getPatientName(), visitType, arrivalDateTime, status);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof PatientVisit other)) return false;
        return patient != null && arrivalDateTime != null &&
               patient.getPatientID().equals(other.patient.getPatientID()) &&
               arrivalDateTime.equals(other.arrivalDateTime);  // Unique per visit
    }

    @Override
    public int hashCode() {
        return patient.getPatientID().hashCode() + arrivalDateTime.hashCode();
    }
}
