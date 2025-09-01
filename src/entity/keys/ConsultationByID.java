package entity.keys;

import entity.Consultation;

/**
 *
 * @author Sim Jia Quan
 */

public class ConsultationByID implements Comparable<ConsultationByID> {
    private final String consultationID;
    private final Consultation consultation;
    
    public ConsultationByID(String consultationID, Consultation consultation) {
        this.consultationID = consultationID;
        this.consultation = consultation;
    }
    
    public String getConsultationID() {
        return consultationID;
    }
    
    public Consultation getConsultation() {
        return consultation;
    }
    
    @Override
    public int compareTo(ConsultationByID other) {
        return this.consultationID.compareTo(other.consultationID);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof ConsultationByID other)) return false;
        return consultationID.equals(other.consultationID);
    }
    
    @Override
    public int hashCode() {
        return consultationID.hashCode();
    }
    
    @Override
    public String toString() {
        return "ConsultationByID{" + consultationID + "}";
    }
}