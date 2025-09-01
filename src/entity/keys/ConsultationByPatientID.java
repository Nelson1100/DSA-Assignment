package entity.keys;

import entity.Consultation;

/**
 *
 * @author Sim Jia Quan
 */

public class ConsultationByPatientID implements Comparable<ConsultationByPatientID> {
    private final String patientID;
    private final String consultationID; 
    private final Consultation consultation;
    
    public ConsultationByPatientID(String patientID, String consultationID, Consultation consultation) {
        this.patientID = patientID;
        this.consultationID = consultationID;
        this.consultation = consultation;
    }
    
    public String getPatientID() {
        return patientID;
    }
    
    public String getConsultationID() {
        return consultationID;
    }
    
    public Consultation getConsultation() {
        return consultation;
    }
    
    @Override
    public int compareTo(ConsultationByPatientID other) {
        int result = this.patientID.compareTo(other.patientID);
        if (result == 0) {
            result = this.consultationID.compareTo(other.consultationID);
        }
        return result;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof ConsultationByPatientID other)) return false;
        return patientID.equals(other.patientID) && consultationID.equals(other.consultationID);
    }
    
    @Override
    public int hashCode() {
        return patientID.hashCode() + consultationID.hashCode();
    }
    
    @Override
    public String toString() {
        return "ConsultationByPatientID{" + patientID + ", " + consultationID + "}";
    }
}