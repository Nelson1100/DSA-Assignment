package entity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Appointment implements Comparable<Appointment> {
    private final String appointmentID;
    private final String patientID;
    private final String doctorID;
    private final String consultationID; 
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
    
    /* ---------- Getters ---------- */
    
    public String getAppointmentID() { return appointmentID; }
    public String getPatientID() { return patientID; }
    public String getDoctorID() { return doctorID; }
    public String getConsultationID() { return consultationID; }
    public LocalDateTime getAppointmentDateTime() { return appointmentDateTime; }
    public String getPurpose() { return purpose; }
    public AppointmentStatus getStatus() { return status; }
    public String getNotes() { return notes; }
    
    /* ---------- Setters ---------- */
    
    public void setPurpose(String purpose) { this.purpose = purpose; }
    public void setStatus(AppointmentStatus status) { this.status = status; }
    public void setNotes(String notes) { this.notes = notes; }
    
    /* ---------- Business Logic Methods ---------- */
    
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
    
    /* ---------- ID Validation Methods ---------- */
    
    /* Validates if the appointment ID follows the expected format */
    public boolean hasValidAppointmentID() {
        return appointmentID != null && appointmentID.matches("^APT\\d{12}$");
    }
    
    /* Validates if the patient ID follows the expected format */
    public boolean hasValidPatientID() {
        return patientID != null && patientID.matches("^P\\d{12}$");
    }
    
    /* Validates if the doctor ID follows the expected format */
    public boolean hasValidDoctorID() {
        return doctorID != null && doctorID.matches("^D\\d{12}$");
    }
    
    /* Validates if the consultation ID follows the expected format */
    public boolean hasValidConsultationID() {
        return consultationID == null || consultationID.matches("^C\\d{12}$");
    }
    
    /* Validates if all IDs in the appointment are properly formatted */
    public boolean hasValidIDs() {
        return hasValidAppointmentID() && hasValidPatientID() && hasValidDoctorID() && hasValidConsultationID();
    }
    
    /* Gets a summary of ID validation status*/
    public String getIDValidationSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("=== Appointment ID Validation Summary ===\n");
        summary.append("Appointment ID: ").append(hasValidAppointmentID() ? "✓ Valid" : "✗ Invalid").append("\n");
        summary.append("Patient ID: ").append(hasValidPatientID() ? "✓ Valid" : "✗ Invalid").append("\n");
        summary.append("Doctor ID: ").append(hasValidDoctorID() ? "✓ Valid" : "✗ Invalid").append("\n");
        summary.append("Consultation ID: ").append(hasValidConsultationID() ? "✓ Valid" : "✗ Invalid").append("\n");
        return summary.toString();
    }
    
    /* ---------- Utility Methods ---------- */
    
    /* Checks if the appointment is in the future */
    public boolean isUpcoming() {
        return appointmentDateTime.isAfter(LocalDateTime.now());
    }
    
    /* Checks if the appointment is overdue (past scheduled time but not completed/cancelled)*/
    public boolean isOverdue() {
        return appointmentDateTime.isBefore(LocalDateTime.now()) && 
               (status == AppointmentStatus.SCHEDULED || status == AppointmentStatus.CONFIRMED);
    }
    
    /* Gets the time until appointment in minutes (negative if overdue)*/
    public long getMinutesUntilAppointment() {
        return java.time.Duration.between(LocalDateTime.now(), appointmentDateTime).toMinutes();
    }
    
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