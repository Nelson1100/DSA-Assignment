package control;

import adt.*;
import entity.*;
import entity.keys.*;
import utility.IDGenerator;
import utility.IDType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ConsultationManagement {
    
    private final AVLTree<ConsultationByID> idxByID = new AVLTree<>();
    private final AVLTree<ConsultationByPatientID> idxByPatientID = new AVLTree<>();
    private final AVLTree<ConsultationByDoctorID> idxByDoctorID = new AVLTree<>();
    
    private final List<Appointment> appointments = new ArrayList<>();
    
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
    
    public List<Consultation> getConsultationsByPatientID(String patientID) {
        List<Consultation> consultations = new ArrayList<>();
        
        for (ConsultationByPatientID consultation : idxByPatientID) {
            if (consultation.getPatientID().equals(patientID)) {
                consultations.add(consultation.getConsultation());
            }
        }
        
        return consultations;
    }
    
    public List<Consultation> getConsultationsByDoctorID(String doctorID) {
        List<Consultation> consultations = new ArrayList<>();
        
        for (ConsultationByDoctorID consultation : idxByDoctorID) {
            if (consultation.getDoctorID().equals(doctorID)) {
                consultations.add(consultation.getConsultation());
            }
        }
        
        return consultations;
    }
    
    public List<Consultation> getAllConsultations() {
        List<Consultation> consultations = new ArrayList<>();
        
        for (ConsultationByID consultation : idxByID) {
            consultations.add(consultation.getConsultation());
        }
        
        return consultations;
    }
    
    public List<Consultation> getActiveConsultations() {
        List<Consultation> activeConsultations = new ArrayList<>();
        List<Consultation> allConsultations = getAllConsultations();
        
        for (Consultation consultation : allConsultations) {
            if (consultation.isInProgress()) {
                activeConsultations.add(consultation);
            }
        }
        
        return activeConsultations;
    }
    
    public List<Consultation> getCompletedConsultations() {
        List<Consultation> completedConsultations = new ArrayList<>();
        List<Consultation> allConsultations = getAllConsultations();
        
        for (Consultation consultation : allConsultations) {
            if (consultation.isCompleted()) {
                completedConsultations.add(consultation);
            }
        }
        
        return completedConsultations;
    }
    
    /* ---------- Statistics and Reports ---------- */
    
    public int getTotalConsultationsCount() {
        return getAllConsultations().size();
    }
    
    public int getActiveConsultationsCount() {
        return getActiveConsultations().size();
    }
    
    public int getCompletedConsultationsCount() {
        return getCompletedConsultations().size();
    }
    
    public int getConsultationsCountByDoctor(String doctorID) {
        return getConsultationsByDoctorID(doctorID).size();
    }
    
    public int getConsultationsCountByPatient(String patientID) {
        return getConsultationsByPatientID(patientID).size();
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
        List<Consultation> patientConsultations = getConsultationsByPatientID(patientID);
        return patientConsultations.stream().anyMatch(Consultation::isInProgress);
    }
    
    public Consultation getActiveConsultationByPatient(String patientID) {
        List<Consultation> patientConsultations = getConsultationsByPatientID(patientID);
        return patientConsultations.stream()
                .filter(Consultation::isInProgress)
                .findFirst()
                .orElse(null);
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
        
        appointments.add(appointment);
        
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
        
        appointments.add(appointment);
        
        return "Appointment scheduled successfully. Appointment ID: " + appointmentID;
    }
    
    public List<Appointment> getAppointmentsByPatient(String patientID) {
        return appointments.stream()
                .filter(apt -> apt.getPatientID().equals(patientID))
                .toList();
    }
    
    public List<Appointment> getAppointmentsByDoctor(String doctorID) {
        return appointments.stream()
                .filter(apt -> apt.getDoctorID().equals(doctorID))
                .toList();
    }
    
    public List<Appointment> getUpcomingAppointments() {
        LocalDateTime now = LocalDateTime.now();
        return appointments.stream()
                .filter(apt -> apt.getAppointmentDateTime().isAfter(now))
                .filter(apt -> !apt.isCancelled())
                .sorted()
                .toList();
    }
    
    public Appointment getAppointmentByID(String appointmentID) {
        return appointments.stream()
                .filter(apt -> apt.getAppointmentID().equals(appointmentID))
                .findFirst()
                .orElse(null);
    }
    
    /* ---------- Enhanced Reports ---------- */
    
    public String getConsultationTrendsReport() {
        StringBuilder sb = new StringBuilder("=== Consultation Trends Report ===\n\n");
        
        int total = getTotalConsultationsCount();
        int active = getActiveConsultationsCount();
        int completed = getCompletedConsultationsCount();
        int cancelled = total - active - completed;
        
        sb.append("Overall Statistics:\n");
        sb.append(String.format("  Total Consultations: %d\n", total));
        sb.append(String.format("  Completion Rate: %.1f%%\n", total > 0 ? (completed * 100.0 / total) : 0));
        sb.append(String.format("  Cancellation Rate: %.1f%%\n", total > 0 ? (cancelled * 100.0 / total) : 0));
        sb.append(String.format("  Active Consultations: %d\n\n", active));
        
        sb.append("Doctor Performance Summary:\n");
        Doctor[] doctors = doctorManagement.getAllDoctor();
        for (Doctor doctor : doctors) {
            int doctorConsultations = getConsultationsCountByDoctor(doctor.getDoctorID());
            if (doctorConsultations > 0) {
                sb.append(String.format("  %s (%s): %d consultations\n", 
                    doctor.getDoctorName(), doctor.getSpecialization(), doctorConsultations));
            }
        }
        
        List<Appointment> upcoming = getUpcomingAppointments();
        sb.append(String.format("\nUpcoming Appointments: %d\n", upcoming.size()));
        
        return sb.toString();
    }
    
    public String getAppointmentSummaryReport() {
        StringBuilder sb = new StringBuilder("=== Appointment Summary Report ===\n\n");
        
        long totalAppointments = appointments.size();
        long scheduledAppointments = appointments.stream().filter(Appointment::isScheduled).count();
        long confirmedAppointments = appointments.stream().filter(Appointment::isConfirmed).count();
        long completedAppointments = appointments.stream().filter(Appointment::isCompleted).count();
        long cancelledAppointments = appointments.stream().filter(Appointment::isCancelled).count();
        
        sb.append("Appointment Statistics:\n");
        sb.append(String.format("  Total Appointments: %d\n", totalAppointments));
        sb.append(String.format("  Scheduled: %d\n", scheduledAppointments));
        sb.append(String.format("  Confirmed: %d\n", confirmedAppointments));
        sb.append(String.format("  Completed: %d\n", completedAppointments));
        sb.append(String.format("  Cancelled: %d\n\n", cancelledAppointments));
        
        List<Appointment> upcoming = getUpcomingAppointments();
        sb.append("Upcoming Appointments:\n");
        if (upcoming.isEmpty()) {
            sb.append("  No upcoming appointments scheduled.\n");
        } else {
            for (Appointment apt : upcoming.stream().limit(10).toList()) {
                Patient patient = patientManagement.findPatientByID(apt.getPatientID());
                String patientName = patient != null ? patient.getPatientName() : "Unknown";
                sb.append(String.format("  %s - %s (%s) with Dr. %s\n",
                    apt.getAppointmentDateTime().toLocalDate(),
                    patientName, apt.getPatientID(), apt.getDoctorID()));
            }
            if (upcoming.size() > 10) {
                sb.append(String.format("  ... and %d more appointments\n", upcoming.size() - 10));
            }
        }
        
        return sb.toString();
    }
}