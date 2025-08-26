package entity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Appointment implements Comparable<Appointment> {
    private final String appointmentID;
    private final String patientID;
    private final String doctorID;
    private final String consultationID; // Related consultation
    private final LocalDateTime appointmentDateTime;
    private String purpose;
    private AppointmentStatus status;
    private String notes;
    
    public Appointment(String appointmentID, String patientID, String doctorID, 
                      String consultationID, LocalDateTime appointmentDateTime, String purpose) {
        this.appointmentID = appointmentID;
        this.patientID = patientID;
        this.doctorID = doctorID;
        this.consultationID = consultationID;
        this.appointmentDateTime = appointmentDateTime;
        this.purpose = purpose;
        this.status = AppointmentStatus.SCHEDULED;
        this.notes = "";
    }
    
    // Getters
    public String getAppointmentID() { return appointmentID; }
    public String getPatientID() { return patientID; }
    public String getDoctorID() { return doctorID; }
    public String getConsultationID() { return consultationID; }
    public LocalDateTime getAppointmentDateTime() { return appointmentDateTime; }
    public String getPurpose() { return purpose; }
    public AppointmentStatus getStatus() { return status; }
    public String getNotes() { return notes; }
    
    // Setters
    public void setPurpose(String purpose) { this.purpose = purpose; }
    public void setStatus(AppointmentStatus status) { this.status = status; }
    public void setNotes(String notes) { this.notes = notes; }
    
    // Business methods
    public void confirmAppointment() {
        this.status = AppointmentStatus.CONFIRMED;
    }
    
    public void cancelAppointment(String reason) {
        this.status = AppointmentStatus.CANCELLED;
        this.notes = (notes.isEmpty() ? "" : notes + "\n") + "Cancellation reason: " + reason;
    }
    
    public void completeAppointment() {
        this.status = AppointmentStatus.COMPLETED;
    }
    
    public boolean isScheduled() { return status == AppointmentStatus.SCHEDULED; }
    public boolean isConfirmed() { return status == AppointmentStatus.CONFIRMED; }
    public boolean isCancelled() { return status == AppointmentStatus.CANCELLED; }
    public boolean isCompleted() { return status == AppointmentStatus.COMPLETED; }
    
    @Override
    public String toString() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        return String.format(
            "=== Appointment Details ===\n" +
            "Appointment ID  : %s\n" +
            "Patient ID      : %s\n" +
            "Doctor ID       : %s\n" +
            "Consultation ID : %s\n" +
            "Date & Time     : %s\n" +
            "Purpose         : %s\n" +
            "Status          : %s\n" +
            "Notes           : %s\n",
            appointmentID, patientID, doctorID, consultationID,
            appointmentDateTime.format(dtf), purpose, status,
            notes.isEmpty() ? "None" : notes
        );
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Appointment other)) return false;
        return appointmentID.equals(other.appointmentID);
    }
    
    @Override
    public int hashCode() {
        return appointmentID.hashCode();
    }
    
    @Override
    public int compareTo(Appointment other) {
        return this.appointmentDateTime.compareTo(other.appointmentDateTime);
    }
}