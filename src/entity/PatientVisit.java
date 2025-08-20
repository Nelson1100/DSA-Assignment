package entity;

import java.time.LocalTime;

public class PatientVisit {
    private final Patient patient;
    private final LocalTime arrivalTime;
    private final VisitType visitType;
    
    public PatientVisit(Patient patient, VisitType visitType, LocalTime arrivalTime) {
        this.patient = patient;
        this.arrivalTime = arrivalTime;
        this.visitType = visitType;
    }
    
    public Patient getPatient() {
        return patient;
    }
    
    public VisitType getVisitType() {
        return visitType;
    }
    
    public LocalTime getArrivalTime() {
        return arrivalTime;
    }
    
    @Override
    public String toString() {
        return String.format("Visit: %s | ID: %s | Name: %s | Type: %s | Arrival: %s",
            patient.getPatientID(), patient.getPatientName(), visitType, arrivalTime);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof PatientVisit other)) return false;
        return patient != null && arrivalTime != null &&
               patient.getPatientID().equals(other.patient.getPatientID()) &&
               arrivalTime.equals(other.arrivalTime);  // Unique per visit
    }

    @Override
    public int hashCode() {
        return patient.getPatientID().hashCode() + arrivalTime.hashCode();
    }
}
