package control;

import adt.*;
import entity.*;
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
    private final MedicalTreatmentManagement treatmentManagement;
    
    public ConsultationManagement(PatientManagement patientManagement, 
                                 DoctorManagement doctorManagement,
                                 MedicalTreatmentManagement treatmentManagement) {
        this.patientManagement = patientManagement;
        this.doctorManagement = doctorManagement;
        this.treatmentManagement = treatmentManagement;
    }
    
    private void indexConsultation(Consultation consultation) {
        idxByID.insert(new ConsultationByID(consultation.getConsultationID(), consultation));
        idxByPatientID.insert(new ConsultationByPatientID(consultation.getPatientID(), 
                                                         consultation.getConsultationID(), consultation));
        idxByDoctorID.insert(new ConsultationByDoctorID(consultation.getDoctorID(), 
                                                       consultation.getConsultationID(), consultation));
    }
    
    private void unindexConsultation(Consultation consultation) {
        idxByID.delete(new ConsultationByID(consultation.getConsultationID(), null));
        idxByPatientID.delete(new ConsultationByPatientID(consultation.getPatientID(), 
                                                         consultation.getConsultationID(), null));
        idxByDoctorID.delete(new ConsultationByDoctorID(consultation.getDoctorID(), 
                                                       consultation.getConsultationID(), null));
    }
    
    /* ---------- Core Operations ---------- */
    
    public String startConsultation(String patientID, String doctorID) {
        Patient patient = patientManagement.findPatientByID(patientID);
        if (patient == null) {
            return "Error: Patient not found with ID: " + patientID;
        }
        
        Doctor doctor = getDoctorByID(doctorID);
        if (doctor == null) {
            return "Error: Doctor not found with ID: " + doctorID;
        }

        String consultationID = IDGenerator.next(IDType.CONSULTATION);
        
        Consultation consultation = new Consultation(consultationID, patientID, doctorID);
        
        indexConsultation(consultation);
        
        return "Consultation started successfully. Consultation ID: " + consultationID;
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
    
    public boolean hasActiveConsultation(String patientID) {
        AVLTree<Consultation> patientConsultations = getConsultationsByPatientID(patientID);
        for (Consultation consultation : patientConsultations) {
            if (consultation.isInProgress()) {
                return true;
            }
        }
        return false;
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
        Consultation consultation = getConsultationByID(consultationID);
        if (consultation == null) {
            return "Error: Consultation not found with ID: " + consultationID;
        }
        
        if (!consultation.isCompleted()) {
            return "Error: Can only schedule follow-up appointments for completed consultations.";
        }
        
        Doctor doctor = getDoctorByID(consultation.getDoctorID());
        if (doctor == null) {
            return "Error: Doctor not found for consultation.";
        }
        
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
        
        return "Follow-up appointment scheduled successfully. Appointment ID: " + appointmentID;
    }
    
    public String scheduleAppointment(String patientID, String doctorID, LocalDateTime appointmentDateTime, String purpose) {
        Patient patient = patientManagement.findPatientByID(patientID);
        if (patient == null) {
            return "Error: Patient not found with ID: " + patientID;
        }
        
        Doctor doctor = getDoctorByID(doctorID);
        if (doctor == null) {
            return "Error: Doctor not found with ID: " + doctorID;
        }
        
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
        
        return "Appointment scheduled successfully. Appointment ID: " + appointmentID;
    }
    
    public AVLTree<Appointment> getAppointmentsByPatient(String patientID) {
        AVLTree<Appointment> patientAppointments = new AVLTree<>();
        for (Appointment apt : appointments) {
            if (apt.getPatientID().equals(patientID)) {
                patientAppointments.insert(apt);
            }
        }
        return patientAppointments;
    }
    
    public AVLTree<Appointment> getAppointmentsByDoctor(String doctorID) {
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
    
    public Appointment getAppointmentByID(String appointmentID) {
        for (Appointment apt : appointments) {
            if (apt.getAppointmentID().equals(appointmentID)) {
                return apt;
            }
        }
        return null;
    }
}