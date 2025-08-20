package entity;

import java.time.LocalDateTime;

public class PatientVisit {
    private final Patient patient;
    private final LocalDateTime arrivalDateTime;
    private final VisitType visitType;
    
    public PatientVisit(Patient patient, VisitType visitType, LocalDateTime arrivalDateTime) {
        this.patient = patient;
        this.arrivalDateTime = arrivalDateTime;
        this.visitType = visitType;
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
    
    @Override
    public String toString() {
        return String.format("Visit: %s | ID: %s | Name: %s | Type: %s | Arrival: %s",
            patient.getPatientID(), patient.getPatientName(), visitType, arrivalDateTime);
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
