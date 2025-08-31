package entity.keys;

import entity.Consultation;

public class ConsultationByDoctorID implements Comparable<ConsultationByDoctorID> {
    private final String doctorID;
    private final String consultationID; 
    private final Consultation consultation;
    
    public ConsultationByDoctorID(String doctorID, String consultationID, Consultation consultation) {
        this.doctorID = doctorID;
        this.consultationID = consultationID;
        this.consultation = consultation;
    }
    
    public String getDoctorID() {
        return doctorID;
    }
    
    public String getConsultationID() {
        return consultationID;
    }
    
    public Consultation getConsultation() {
        return consultation;
    }
    
    @Override
    public int compareTo(ConsultationByDoctorID other) {
        int result = this.doctorID.compareTo(other.doctorID);
        if (result == 0) {
            result = this.consultationID.compareTo(other.consultationID);
        }
        return result;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof ConsultationByDoctorID other)) return false;
        return doctorID.equals(other.doctorID) && consultationID.equals(other.consultationID);
    }
    
    @Override
    public int hashCode() {
        return doctorID.hashCode() + consultationID.hashCode();
    }
    
    @Override
    public String toString() {
        return "ConsultationByDoctorID{" + doctorID + ", " + consultationID + "}";
    }
}