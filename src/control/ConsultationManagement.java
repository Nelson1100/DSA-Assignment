package control;

import adt.*;
import entity.*;
import static entity.AppointmentStatus.CANCELLED;
import static entity.AppointmentStatus.COMPLETED;
import static entity.AppointmentStatus.CONFIRMED;
import static entity.AppointmentStatus.NO_SHOW;
import static entity.AppointmentStatus.SCHEDULED;
import entity.keys.*;
import utility.IDGenerator;
import utility.IDType;

import java.time.LocalDateTime;

public class ConsultationManagement {
    private final AVLTree<ConsultationByID> idxByID = new AVLTree<>();
    private final AVLTree<ConsultationByPatientID> idxByPatientID = new AVLTree<>();
    private final AVLTree<ConsultationByDoctorID> idxByDoctorID = new AVLTree<>();
    
    private final AVLTree<Appointment> appointments = new AVLTree<>();
    
    private final PatientManagement patientManagement;
    private final DoctorManagement doctorManagement;
    
    public ConsultationManagement(PatientManagement patientManagement, 
                                 DoctorManagement doctorManagement) {
        this.patientManagement = patientManagement;
        this.doctorManagement = doctorManagement;
    }
    
    /* ---------- Validation Methods ---------- */
    /*Validates if a patient ID exists in the Patient Management module*/
    public boolean isValidPatientID(String patientID) {
        if (patientID == null || patientID.trim().isEmpty()) {
            return false;
        }
        
        // Verify the patient exists in the Patient Management module
        return patientManagement.existsByID(patientID);
    }
    
    /*Validates if a doctor ID exists in the Doctor Management module*/
    public boolean isValidDoctorID(String doctorID) {
        if (doctorID == null || doctorID.trim().isEmpty()) {
            return false;
        }
        
        /* Verify the doctor exists in the Doctor Management module */
        Doctor searchKey = new Doctor();
        searchKey.setDoctorID(doctorID);
        Doctor foundDoctor = doctorManagement.findDoctor(searchKey);
        return foundDoctor != null;
    }
    
    /*Validates both patient and doctor IDs for consultation creation*/
    public ValidationResult validateConsultationIDs(String patientID, String doctorID) {
        ValidationResult result = new ValidationResult();
        
        // Validate patient ID
        if (!isValidPatientID(patientID)) {
            result.addError("Invalid patient ID: " + patientID + 
                          ". Patient ID must exist in the Patient Management module and follow format PYYYYMMDD####");
        }
        
        // Validate doctor ID
        if (!isValidDoctorID(doctorID)) {
            result.addError("Invalid doctor ID: " + doctorID + 
                          ". Doctor ID must exist in the Doctor Management module and follow format DYYYYMMDD####");
        }
        
        return result;
    }
    
    /*Gets detailed information about a patient for consultation purposes*/
    public String getPatientInfoForConsultation(String patientID) {
        if (!isValidPatientID(patientID)) {
            return "Error: Invalid patient ID - " + patientID;
        }
        
        Patient patient = patientManagement.findPatientByID(patientID);
        if (patient == null) {
            return "Error: Patient not found - " + patientID;
        }
        
        return String.format(
            "Patient ID: %s\n" +
            "Name: %s\n" +
            "Age: %d\n" +
            "Gender: %s\n" +
            "Contact: %s\n" +
            "Email: %s",
            patient.getPatientID(),
            patient.getPatientName(),
            patient.getAge(),
            patient.getGender(),
            patient.getContactNo(),
            patient.getEmail()
        );
    }
    
    /*Gets detailed information about a doctor for consultation purposes*/
    public String getDoctorInfoForConsultation(String doctorID) {
        if (!isValidDoctorID(doctorID)) {
            return "Error: Invalid doctor ID - " + doctorID;
        }
        
        Doctor doctor = getDoctorByID(doctorID);
        if (doctor == null) {
            return "Error: Doctor not found - " + doctorID;
        }
        
        return String.format(
            "Doctor ID: %s\n" +
            "Name: %s\n" +
            "Specialization: %s\n" +
            "Contact: %s\n" +
            "Email: %s",
            doctor.getDoctorID(),
            doctor.getDoctorName(),
            doctor.getSpecialization(),
            doctor.getContactNo(),
            doctor.getEmail()
        );
    }
    
    /*Gets detailed information about an appointment */
    public String getAppointmentInfo(String appointmentID) {
        if (appointmentID == null || appointmentID.trim().isEmpty()) {
            return "Error: Appointment ID cannot be null or empty.";
        }
        
        Appointment appointment = getAppointmentByID(appointmentID);
        if (appointment == null) {
            return "Error: Appointment not found - " + appointmentID;
        }
        
        /* Validate appointment ID format using entity method */
        if (!appointment.hasValidAppointmentID()) {
            return "Error: Invalid appointment ID format - " + appointmentID;
        }
        
        /* Get patient and doctor information */
        Patient patient = patientManagement.findPatientByID(appointment.getPatientID());
        Doctor doctor = getDoctorByID(appointment.getDoctorID());
        
        String patientName = (patient != null) ? patient.getPatientName() : "Unknown Patient";
        String doctorName = (doctor != null) ? doctor.getDoctorName() : "Unknown Doctor";
        
        return String.format(
            "Appointment ID: %s\n" +
            "Patient: %s (%s)\n" +
            "Doctor: %s (%s)\n" +
            "Date/Time: %s\n" +
            "Purpose: %s\n" +
            "Status: %s\n" +
            "Notes: %s",
            appointmentID,
            patientName,
            appointment.getPatientID(),
            doctorName,
            appointment.getDoctorID(),
            appointment.getAppointmentDateTime().toString(),
            appointment.getPurpose(),
            appointment.getStatus(),
            appointment.getNotes().isEmpty() ? "None" : appointment.getNotes()
        );
    }
    
    /*Checks if a patient has any active consultations*/
    public boolean hasActiveConsultation(String patientID) {
        if (!isValidPatientID(patientID)) {
            return false;
        }
        
        AVLTree<Consultation> patientConsultations = getConsultationsByPatientID(patientID);
        for (Consultation consultation : patientConsultations) {
            if (consultation.isInProgress()) {
                return true;
            }
        }
        return false;
    }
    
    /*Checks if a doctor has any active consultations*/
    public boolean hasActiveConsultationByDoctor(String doctorID) {
        if (!isValidDoctorID(doctorID)) {
            return false;
        }
        
        AVLTree<Consultation> doctorConsultations = getConsultationsByDoctorID(doctorID);
        for (Consultation consultation : doctorConsultations) {
            if (consultation.isInProgress()) {
                return true;
            }
        }
        return false;
    }
    
    /*Validates appointment status transitions*/
    public ValidationResult validateAppointmentStatusTransition(Appointment appointment, AppointmentStatus newStatus) {
        ValidationResult result = new ValidationResult();
        
        if (appointment == null) {
            result.addError("Appointment cannot be null.");
            return result;
        }
        
        AppointmentStatus currentStatus = appointment.getStatus();
        
        /* Define valid transitions */
        switch (currentStatus) {
            case SCHEDULED:
                if (newStatus != AppointmentStatus.CONFIRMED && 
                    newStatus != AppointmentStatus.CANCELLED) {
                    result.addError("Scheduled appointments can only be confirmed or cancelled.");
                }
                break;
            case CONFIRMED:
                if (newStatus != AppointmentStatus.COMPLETED && 
                    newStatus != AppointmentStatus.CANCELLED &&
                    newStatus != AppointmentStatus.NO_SHOW) {
                    result.addError("Confirmed appointments can only be completed, cancelled, or marked as no-show.");
                }
                break;
            case COMPLETED:
                result.addError("Completed appointments cannot be modified.");
                break;
            case CANCELLED:
                result.addError("Cancelled appointments cannot be modified.");
                break;
            case NO_SHOW:
                result.addError("No-show appointments cannot be modified.");
                break;
        }
        
        return result;
    }
    
    /* ---------- Indexing Methods ---------- */
    
    private void indexConsultation(Consultation consultation) {
        idxByID.insert(new ConsultationByID(consultation.getConsultationID(), consultation));
        idxByPatientID.insert(new ConsultationByPatientID(consultation.getPatientID(), 
                                                         consultation.getConsultationID(), consultation));
        idxByDoctorID.insert(new ConsultationByDoctorID(consultation.getDoctorID(), 
                                                       consultation.getConsultationID(), consultation));
    }
    
    /* ---------- Core Operations ---------- */
    
    public String startConsultation(String patientID, String doctorID) {
        ValidationResult validationResult = validateConsultationIDs(patientID, doctorID);
        if (!validationResult.isValid()) {
            return "Validation Error:\n" + validationResult.getErrorMessage();
        }
        
        /* Check for active consultations */
        if (hasActiveConsultation(patientID)) {
            return "Error: Patient " + patientID + " already has an active consultation.";
        }
        
        Patient patient = patientManagement.findPatientByID(patientID);
        Doctor doctor = getDoctorByID(doctorID);

        String consultationID = IDGenerator.next(IDType.CONSULTATION);
        
        Consultation consultation = new Consultation(consultationID, patientID, doctorID);
        
        indexConsultation(consultation);
        
        return String.format(
            "Consultation started successfully.\n" +
            "Consultation ID: %s\n" +
            "Patient: %s (%s)\n" +
            "Doctor: %s (%s)",
            consultationID,
            patient.getPatientName(),
            patientID,
            doctor.getDoctorName(),
            doctorID
        );
    }
    
    public String updateConsultationDetails(String consultationID, String symptoms, 
                                           String diagnosis, String notes) {
        Consultation consultation = getConsultationByID(consultationID);
        if (consultation == null) {
            return "Error: Consultation not found with ID: " + consultationID;
        }
        
        if (consultation.isCompleted() || consultation.isCancelled()) {
            return "Error: Cannot update a " + consultation.getStatus().toString().toLowerCase() + " consultation.";
        }
        
        consultation.setSymptoms(symptoms);
        consultation.setDiagnosis(diagnosis);
        consultation.setNotes(notes);
        
        return "Consultation details updated successfully.";
    }
    
    public String completeConsultation(String consultationID, String finalDiagnosis, String treatmentNotes) {
        Consultation consultation = getConsultationByID(consultationID);
        if (consultation == null) {
            return "Error: Consultation not found with ID: " + consultationID;
        }
        
        if (consultation.isCompleted()) {
            return "Error: Consultation is already completed.";
        }
        
        if (consultation.isCancelled()) {
            return "Error: Cannot complete a cancelled consultation.";
        }
        
        consultation.completeConsultation(finalDiagnosis, treatmentNotes);
        
        return "Consultation completed successfully.";
    }
    
    public String cancelConsultation(String consultationID, String reason) {
        Consultation consultation = getConsultationByID(consultationID);
        if (consultation == null) {
            return "Error: Consultation not found with ID: " + consultationID;
        }
        
        if (consultation.isCompleted()) {
            return "Error: Cannot cancel a completed consultation.";
        }
        
        consultation.cancelConsultation();
        consultation.setNotes(consultation.getNotes() + "\nCancellation Reason: " + reason);
        
        return "Consultation cancelled successfully.";
    }
    
    /* ---------- Search Operations ---------- */
    
    public Consultation getConsultationByID(String consultationID) {
        ConsultationByID key = new ConsultationByID(consultationID, null);
        ConsultationByID result = idxByID.find(key);
        return result != null ? result.getConsultation() : null;
    }
    
    public AVLTree<Consultation> getConsultationsByPatientID(String patientID) {
        AVLTree<Consultation> consultations = new AVLTree<>();
        
        for (ConsultationByPatientID consultation : idxByPatientID) {
            if (consultation.getPatientID().equals(patientID)) {
                consultations.insert(consultation.getConsultation());
            }
        }
        
        return consultations;
    }
    
    public AVLTree<Consultation> getConsultationsByDoctorID(String doctorID) {
        AVLTree<Consultation> consultations = new AVLTree<>();
        
        for (ConsultationByDoctorID consultation : idxByDoctorID) {
            if (consultation.getDoctorID().equals(doctorID)) {
                consultations.insert(consultation.getConsultation());
            }
        }
        
        return consultations;
    }
    
    public AVLTree<Consultation> getAllConsultations() {
        AVLTree<Consultation> consultations = new AVLTree<>();
        
        for (ConsultationByID consultation : idxByID) {
            consultations.insert(consultation.getConsultation());
        }
        
        return consultations;
    }
    
    public AVLTree<Consultation> getActiveConsultations() {
        AVLTree<Consultation> activeConsultations = new AVLTree<>();
        AVLTree<Consultation> allConsultations = getAllConsultations();
        
        for (Consultation consultation : allConsultations) {
            if (consultation.isInProgress()) {
                activeConsultations.insert(consultation);
            }
        }
        
        return activeConsultations;
    }
    
    public AVLTree<Consultation> getCompletedConsultations() {
        AVLTree<Consultation> completedConsultations = new AVLTree<>();
        AVLTree<Consultation> allConsultations = getAllConsultations();
        
        for (Consultation consultation : allConsultations) {
            if (consultation.isCompleted()) {
                completedConsultations.insert(consultation);
            }
        }
        
        return completedConsultations;
    }
    
    /* ---------- Statistics and Reports ---------- */
    
    public int getTotalConsultationsCount() {
        int count = 0;
        for (ConsultationByID consultation : idxByID) {
            count++;
        }
        return count;
    }
    
    public int getActiveConsultationsCount() {
        int count = 0;
        AVLTree<Consultation> activeConsultations = getActiveConsultations();
        for (Consultation consultation : activeConsultations) {
            count++;
        }
        return count;
    }
    
    public int getCompletedConsultationsCount() {
        int count = 0;
        AVLTree<Consultation> completedConsultations = getCompletedConsultations();
        for (Consultation consultation : completedConsultations) {
            count++;
        }
        return count;
    }
    
    public int getConsultationsCountByDoctor(String doctorID) {
        int count = 0;
        AVLTree<Consultation> doctorConsultations = getConsultationsByDoctorID(doctorID);
        for (Consultation consultation : doctorConsultations) {
            count++;
        }
        return count;
    }
    
    public int getConsultationsCountByPatient(String patientID) {
        int count = 0;
        AVLTree<Consultation> patientConsultations = getConsultationsByPatientID(patientID);
        for (Consultation consultation : patientConsultations) {
            count++;
        }
        return count;
    }
    
    /* ---------- Helper Methods ---------- */
    
    private Doctor getDoctorByID(String doctorID) {
        Doctor searchKey = new Doctor();
        searchKey.setDoctorID(doctorID);
        return doctorManagement.findDoctor(searchKey);
    }
    
    /* ---------- Utility Methods ---------- */
    
    public String getConsultationSummary() {
        int total = getTotalConsultationsCount();
        int active = getActiveConsultationsCount();
        int completed = getCompletedConsultationsCount();
        int cancelled = total - active - completed;
        
        return String.format(
            "=== Consultation Summary ===\n" +
            "Total Consultations : %d\n" +
            "Active              : %d\n" +
            "Completed           : %d\n" +
            "Cancelled           : %d\n",
            total, active, completed, cancelled
        );
    }
    
    public Consultation getActiveConsultationByPatient(String patientID) {
        AVLTree<Consultation> patientConsultations = getConsultationsByPatientID(patientID);
        for (Consultation consultation : patientConsultations) {
            if (consultation.isInProgress()) {
                return consultation;
            }
        }
        return null;
    }
    
    /* ---------- Appointment Management ---------- */
    
    public String scheduleFollowUpAppointment(String consultationID, LocalDateTime appointmentDateTime, String purpose) {
        /* Validate consultation ID format */
        if (consultationID == null || consultationID.trim().isEmpty()) {
            return "Error: Consultation ID cannot be null or empty.";
        }
        
        Consultation consultation = getConsultationByID(consultationID);
        if (consultation == null) {
            return "Error: Consultation not found with ID: " + consultationID;
        }
        
        /* Validate consultation ID format using entity method */
        if (!consultation.hasValidConsultationID()) {
            return "Error: Invalid consultation ID format. Expected format: CYYYYMMDD####";
        }
        
        if (!consultation.isCompleted()) {
            return "Error: Can only schedule follow-up appointments for completed consultations.";
        }
        
        /* Validate that the consultation's patient and doctor still exist */
        if (!isValidPatientID(consultation.getPatientID())) {
            return "Error: Patient from consultation no longer exists in the system.";
        }
        
        if (!isValidDoctorID(consultation.getDoctorID())) {
            return "Error: Doctor from consultation no longer exists in the system.";
        }
        
        Doctor doctor = getDoctorByID(consultation.getDoctorID());
        Patient patient = patientManagement.findPatientByID(consultation.getPatientID());
        
        String appointmentID = IDGenerator.next(IDType.APPOINTMENT);
        
        Appointment appointment = new Appointment(
            appointmentID,
            consultation.getPatientID(),
            consultation.getDoctorID(),
            consultationID,
            appointmentDateTime,
            purpose
        );
        
        appointments.insert(appointment);
        
        return String.format(
            "Follow-up appointment scheduled successfully.\n" +
            "Appointment ID: %s\n" +
            "Patient: %s (%s)\n" +
            "Doctor: %s (%s)\n" +
            "Date/Time: %s\n" +
            "Purpose: %s",
            appointmentID,
            patient.getPatientName(),
            consultation.getPatientID(),
            doctor.getDoctorName(),
            consultation.getDoctorID(),
            appointmentDateTime.toString(),
            purpose
        );
    }
    
    public String scheduleAppointment(String patientID, String doctorID, LocalDateTime appointmentDateTime, String purpose) {
        ValidationResult validationResult = validateConsultationIDs(patientID, doctorID);
        if (!validationResult.isValid()) {
            return "Validation Error:\n" + validationResult.getErrorMessage();
        }
        
        Patient patient = patientManagement.findPatientByID(patientID);
        Doctor doctor = getDoctorByID(doctorID);
        
        String appointmentID = IDGenerator.next(IDType.APPOINTMENT);
        
        Appointment appointment = new Appointment(
            appointmentID,
            patientID,
            doctorID,
            null, 
            appointmentDateTime,
            purpose
        );
        
        appointments.insert(appointment);
        
        return String.format(
            "Appointment scheduled successfully.\n" +
            "Appointment ID: %s\n" +
            "Patient: %s (%s)\n" +
            "Doctor: %s (%s)\n" +
            "Date/Time: %s\n" +
            "Purpose: %s",
            appointmentID,
            patient.getPatientName(),
            patientID,
            doctor.getDoctorName(),
            doctorID,
            appointmentDateTime.toString(),
            purpose
        );
    }
    
    public String updateAppointmentStatus(String appointmentID, AppointmentStatus newStatus, String notes) {
        if (appointmentID == null || appointmentID.trim().isEmpty()) {
            return "Error: Appointment ID cannot be null or empty.";
        }
        
        Appointment appointment = getAppointmentByID(appointmentID);
        if (appointment == null) {
            return "Error: Appointment not found - " + appointmentID;
        }
        
        /* Validate appointment ID format using entity method */
        if (!appointment.hasValidAppointmentID()) {
            return "Error: Invalid appointment ID format - " + appointmentID;
        }
        
        ValidationResult validation = validateAppointmentStatusTransition(appointment, newStatus);
        if (!validation.isValid()) {
            return "Error: " + validation.getErrorMessage();
        }
        
        /* Update the appointment status */
        appointment.setStatus(newStatus);
        if (notes != null && !notes.trim().isEmpty()) {
            String currentNotes = appointment.getNotes();
            String updatedNotes = currentNotes.isEmpty() ? notes : currentNotes + "\n" + notes;
            appointment.setNotes(updatedNotes);
        }
        
        return String.format(
            "Appointment status updated successfully.\n" +
            "Appointment ID: %s\n" +
            "New Status: %s\n" +
            "Previous Status: %s",
            appointmentID,
            newStatus,
            appointment.getStatus()
        );
    }
    
    public Appointment getAppointmentByID(String appointmentID) {
        if (appointmentID == null || appointmentID.trim().isEmpty()) {
            return null; // Return null for invalid appointment ID
        }
        
        for (Appointment apt : appointments) {
            if (apt.getAppointmentID().equals(appointmentID)) {
                return apt;
            }
        }
        return null;
    }
    
    public AVLTree<Appointment> getAppointmentsByPatient(String patientID) {
        if (!isValidPatientID(patientID)) {
            return new AVLTree<>(); // Return empty tree for invalid patient ID
        }
        
        AVLTree<Appointment> patientAppointments = new AVLTree<>();
        for (Appointment apt : appointments) {
            if (apt.getPatientID().equals(patientID)) {
                patientAppointments.insert(apt);
            }
        }
        return patientAppointments;
    }
    
    public AVLTree<Appointment> getAppointmentsByDoctor(String doctorID) {
        if (!isValidDoctorID(doctorID)) {
            return new AVLTree<>(); // Return empty tree for invalid doctor ID
        }
        
        AVLTree<Appointment> doctorAppointments = new AVLTree<>();
        for (Appointment apt : appointments) {
            if (apt.getDoctorID().equals(doctorID)) {
                doctorAppointments.insert(apt);
            }
        }
        return doctorAppointments;
    }
    
    public AVLTree<Appointment> getUpcomingAppointments() {
        AVLTree<Appointment> upcomingAppointments = new AVLTree<>();
        LocalDateTime now = LocalDateTime.now();
        
        for (Appointment apt : appointments) {
            if (apt.getAppointmentDateTime().isAfter(now) && !apt.isCancelled()) {
                upcomingAppointments.insert(apt);
            }
        }
        return upcomingAppointments;
    }
    
    /* ---------- Validation Result Inner Class ---------- */
    
    /* Hold validation results with error messages */
    public static class ValidationResult {
        private boolean valid = true;
        private StringBuilder errorMessages = new StringBuilder();
        
        public boolean isValid() {
            return valid;
        }
        
        public void addError(String error) {
            valid = false;
            if (errorMessages.length() > 0) {
                errorMessages.append("\n");
            }
            errorMessages.append(error);
        }
        
        public String getErrorMessage() {
            return errorMessages.toString();
        }
    }
}