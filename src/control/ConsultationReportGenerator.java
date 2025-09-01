package control;

import entity.*;
import adt.*;
import java.time.*;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author Sim Jia Quan
 */

public class ConsultationReportGenerator {
    private final ConsultationManagement consultationManagement;
    private final PatientManagement patientManagement;
    private final DoctorManagement doctorManagement;
    
    private static final int WIDTH = 100;
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    
    public ConsultationReportGenerator(ConsultationManagement consultationManagement,
                                     PatientManagement patientManagement,
                                     DoctorManagement doctorManagement) {
        this.consultationManagement = consultationManagement;
        this.patientManagement = patientManagement;
        this.doctorManagement = doctorManagement;
    }
    

    
    /* Generates a detailed consultation summary report */
    public String generateDetailedSummary() {
        StringBuilder sb = new StringBuilder();
        
        appendHeader(sb, "DETAILED CONSULTATION SUMMARY");
        
        int total = consultationManagement.getTotalConsultationsCount();
        int active = consultationManagement.getActiveConsultationsCount();
        int completed = consultationManagement.getCompletedConsultationsCount();
        int cancelled = total - active - completed;
        
        sb.append(String.format("%-30s: %d\n", "Total Consultations", total));
        sb.append(String.format("%-30s: %d\n", "Active Consultations", active));
        sb.append(String.format("%-30s: %d\n", "Completed Consultations", completed));
        sb.append(String.format("%-30s: %d\n", "Cancelled Consultations", cancelled));
        
        if (total > 0) {
            sb.append(String.format("%-30s: %.2f%%\n", "Completion Rate", (double) completed / total * 100));
            sb.append(String.format("%-30s: %.2f%%\n", "Cancellation Rate", (double) cancelled / total * 100));
        }
        
        appendFooter(sb);
        return sb.toString();
    }
    
    /* Generates a doctor-specific consultation report */
    public String generateDoctorReport(String doctorID) {
        StringBuilder sb = new StringBuilder();
        
        Doctor doctor = doctorManagement.findDoctor(new Doctor(doctorID, "", "", "", null, ""));
        String doctorName = doctor != null ? doctor.getDoctorName() : "Unknown Doctor";
        
        appendHeader(sb, "DOCTOR CONSULTATION REPORT - " + doctorName);
        
        AVLTree<Consultation> doctorConsultations = consultationManagement.getConsultationsByDoctorID(doctorID);
        
        if (doctorConsultations.isEmpty()) {
            sb.append("No consultations found for this doctor.\n");
            appendFooter(sb);
            return sb.toString();
        }
        
        /* Doctor Statistics */
        int total = 0;
        int completed = 0;
        int active = 0;
        int cancelled = 0;
        
        for (Consultation consultation : doctorConsultations) {
            total++;
            switch (consultation.getStatus()) {
                case COMPLETED:
                    completed++;
                    break;
                case IN_PROGRESS:
                    active++;
                    break;
                case CANCELLED:
                    cancelled++;
                    break;
                default:
                    break;
            }
        }
        
        sb.append(String.format("%-30s: %s\n", "Doctor ID", doctorID));
        sb.append(String.format("%-30s: %s\n", "Doctor Name", doctorName));
        sb.append(String.format("%-30s: %d\n", "Total Consultations", total));
        sb.append(String.format("%-30s: %d\n", "Completed", completed));
        sb.append(String.format("%-30s: %d\n", "Active", active));
        sb.append(String.format("%-30s: %d\n", "Cancelled", cancelled));
        
        if (total > 0) {
            sb.append(String.format("%-30s: %.2f%%\n", "Completion Rate", (double) completed / total * 100));
        }
        
        /* Recent consultations */
        sb.append("\n=== Recent Consultations ===\n");
        int count = 0;
        for (Consultation consultation : doctorConsultations) {
            if (count >= 10) break; // Show only last 10
            
            Patient patient = patientManagement.findPatientByID(consultation.getPatientID());
            String patientName = patient != null ? patient.getPatientName() : "Unknown";
            
            sb.append(String.format("%s | %s | %s | %s\n",
                consultation.getConsultationID(),
                patientName,
                consultation.getConsultationDateTime().format(DATETIME_FORMATTER),
                consultation.getStatus()));
            count++;
        }
        
        appendFooter(sb);
        return sb.toString();
    }
    
    /* Generates a patient-specific consultation report */
    public String generatePatientReport(String patientID) {
        StringBuilder sb = new StringBuilder();
        
        Patient patient = patientManagement.findPatientByID(patientID);
        if (patient == null) {
            sb.append("Patient not found with ID: " + patientID);
            return sb.toString();
        }
        
        appendHeader(sb, "PATIENT CONSULTATION REPORT - " + patient.getPatientName());
        
        AVLTree<Consultation> patientConsultations = consultationManagement.getConsultationsByPatientID(patientID);
        
        if (patientConsultations.isEmpty()) {
            sb.append("No consultations found for this patient.\n");
            appendFooter(sb);
            return sb.toString();
        }
        
        /* Patient Statistics */
        int total = 0, completed = 0, active = 0, cancelled = 0;
        
        for (Consultation consultation : patientConsultations) {
            total++;
            switch (consultation.getStatus()) {
                case COMPLETED -> completed++;
                case IN_PROGRESS -> active++;
                case CANCELLED -> cancelled++;
            }
        }
        
        sb.append(String.format("%-30s: %s\n", "Patient ID", patientID));
        sb.append(String.format("%-30s: %s\n", "Patient Name", patient.getPatientName()));
        sb.append(String.format("%-30s: %d\n", "Total Consultations", total));
        sb.append(String.format("%-30s: %d\n", "Completed", completed));
        sb.append(String.format("%-30s: %d\n", "Active", active));
        sb.append(String.format("%-30s: %d\n", "Cancelled", cancelled));
        
        /* Consultation History */
        sb.append("\n=== Consultation History ===\n");
        for (Consultation consultation : patientConsultations) {
            Doctor doctor = doctorManagement.findDoctor(new Doctor(consultation.getDoctorID(), "", "", "", null, ""));
            String doctorName = doctor != null ? doctor.getDoctorName() : "Unknown";
            
            sb.append(String.format("%s | %s | %s | %s\n",
                consultation.getConsultationID(),
                doctorName,
                consultation.getConsultationDateTime().format(DATETIME_FORMATTER),
                consultation.getStatus()));
        }
        
        appendFooter(sb);
        return sb.toString();
    }
    
    /* Private helper methods */
    private void appendHeader(StringBuilder sb, String title) {
        sb.append(line('=', WIDTH)).append('\n');
        sb.append(center(title, WIDTH)).append('\n');
        sb.append(line('-', WIDTH)).append('\n');
        sb.append("Generated on: ").append(LocalDateTime.now().format(DATETIME_FORMATTER)).append('\n');
        sb.append(line('-', WIDTH)).append('\n').append('\n');
    }
    
    private void appendFooter(StringBuilder sb) {
        sb.append('\n').append(line('-', WIDTH)).append('\n');
        sb.append(center("End of Report", WIDTH)).append('\n');
        sb.append(line('=', WIDTH)).append('\n');
    }

    private String center(String text, int width) {
        int padding = (width - text.length()) / 2;
        return " ".repeat(Math.max(0, padding)) + text;
    }
    
    private String line(char c, int width) {
        return String.valueOf(c).repeat(width);
    }
}
