package control;

import adt.AVLTree;
import entity.Consultation;
import entity.ConsultationStatus;
import entity.Doctor;
import entity.Patient;

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
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    public ConsultationReportGenerator(ConsultationManagement consultationManagement,
                                     PatientManagement patientManagement,
                                     DoctorManagement doctorManagement) {
        this.consultationManagement = consultationManagement;
        this.patientManagement = patientManagement;
        this.doctorManagement = doctorManagement;
    }
    
    /* Generates a consultation summary report */
    public String generateDetailedSummary() {
        StringBuilder sb = new StringBuilder();
        
        /* Header */
        appendHeader(sb, "COMPREHENSIVE CONSULTATION SUMMARY");
        
        /* Statistics Section */
        appendSectionTitle(sb, "CONSULTATION STATISTICS");
        int total = consultationManagement.getTotalConsultationsCount();
        int active = consultationManagement.getActiveConsultationsCount();
        int completed = consultationManagement.getCompletedConsultationsCount();
        int cancelled = total - active - completed; 
        
        sb.append(String.format("%-25s: %d\n", "Total Consultations", total));
        sb.append(String.format("%-25s: %d\n", "Active Consultations", active));
        sb.append(String.format("%-25s: %d\n", "Completed Consultations", completed));
        sb.append(String.format("%-25s: %d\n", "Cancelled Consultations", cancelled));
        
        if (total > 0) {
            double completionRate = (double) completed / total * 100;
            double activeRate = (double) active / total * 100;
            double cancellationRate = (double) cancelled / total * 100;
            
            sb.append(String.format("%-25s: %.1f%%\n", "Completion Rate", completionRate));
            sb.append(String.format("%-25s: %.1f%%\n", "Active Rate", activeRate));
            sb.append(String.format("%-25s: %.1f%%\n", "Cancellation Rate", cancellationRate));
        }
        sb.append("\n");
        
        /* Recent Consultations Section */
        appendSectionTitle(sb, "RECENT CONSULTATIONS (Last 10)");
        sb.append(String.format("%-18s %-20s %-15s %-12s\n", 
            "Consultation ID", "Patient Name", "Doctor ID", "Date"));
        appendLine(sb, '-');
        
        AVLTree<Consultation> allConsultations = consultationManagement.getAllConsultations();
        int count = 0;
        for (Consultation consultation : allConsultations) {
            if (count >= 10) break;
            
            Patient patient = patientManagement.findPatientByID(consultation.getPatientID());
            String patientName = patient != null ? patient.getPatientName() : "Unknown";
            
            sb.append(String.format("%-18s %-20s %-15s %-12s\n",
                consultation.getConsultationID(),
                truncate(patientName, 18),
                consultation.getDoctorID(),
                consultation.getConsultationDateTime().format(DATE_FORMATTER)));
            count++;
        }
        sb.append("\n");
        
        appendFooter(sb);
        return sb.toString();
    }
    
    /* Generates a doctor consultation report */
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
        appendSectionTitle(sb, "DOCTOR STATISTICS");
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
        
        sb.append(String.format("%-25s: %s\n", "Doctor ID", doctorID));
        sb.append(String.format("%-25s: %s\n", "Doctor Name", doctorName));
        sb.append(String.format("%-25s: %d\n", "Total Consultations", total));
        sb.append(String.format("%-25s: %d\n", "Completed", completed));
        sb.append(String.format("%-25s: %d\n", "Active", active));
        sb.append(String.format("%-25s: %d\n", "Cancelled", cancelled));
        
        if (total > 0) {
            sb.append(String.format("%-25s: %.2f%%\n", "Completion Rate", (double) completed / total * 100));
        }
        sb.append("\n");
        
        /* Recent consultations */
        appendSectionTitle(sb, "RECENT CONSULTATIONS");
        sb.append(String.format("%-18s %-20s %-12s %-8s\n", 
            "Consultation ID", "Patient Name", "Date", "Time"));
        appendLine(sb, '-');
        
        int count = 0;
        for (Consultation consultation : doctorConsultations) {
            if (count >= 10) break; // Show only last 10
            
            Patient patient = patientManagement.findPatientByID(consultation.getPatientID());
            String patientName = patient != null ? patient.getPatientName() : "Unknown";
            
            sb.append(String.format("%-18s %-20s %-12s %-8s\n",
                consultation.getConsultationID(),
                truncate(patientName, 18),
                consultation.getConsultationDateTime().format(DATE_FORMATTER),
                consultation.getConsultationDateTime().format(DateTimeFormatter.ofPattern("HH:mm"))));
            count++;
        }
        sb.append("\n");
        
        appendFooter(sb);
        return sb.toString();
    }
    
    /* Generates a patient consultation report*/
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
        
        /* Patient Information */
        appendSectionTitle(sb, "PATIENT INFORMATION");
        sb.append(String.format("%-25s: %s\n", "Patient ID", patient.getPatientID()));
        sb.append(String.format("%-25s: %s\n", "Name", patient.getPatientName()));
        sb.append(String.format("%-25s: %d\n", "Age", patient.getAge()));
        sb.append(String.format("%-25s: %s\n", "Gender", patient.getGender()));
        sb.append(String.format("%-25s: %s\n", "Contact", patient.getContactNo()));
        sb.append(String.format("%-25s: %s\n", "Email", patient.getEmail()));
        sb.append("\n");
        
        /* Patient Statistics */
        appendSectionTitle(sb, "CONSULTATION STATISTICS");
        int total = 0, completed = 0, active = 0, cancelled = 0;
        
        for (Consultation consultation : patientConsultations) {
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
        
        sb.append(String.format("%-25s: %d\n", "Total Consultations", total));
        sb.append(String.format("%-25s: %d\n", "Completed", completed));
        sb.append(String.format("%-25s: %d\n", "Active", active));
        sb.append(String.format("%-25s: %d\n", "Cancelled", cancelled));
        
        if (total > 0) {
            sb.append(String.format("%-25s: %.2f%%\n", "Completion Rate", (double) completed / total * 100));
        }
        sb.append("\n");
        
        /* Consultation History */
        appendSectionTitle(sb, "CONSULTATION HISTORY");
        sb.append(String.format("%-18s %-15s %-12s %-8s\n", 
            "Consultation ID", "Doctor ID", "Date", "Time"));
        appendLine(sb, '-');
        
        for (Consultation consultation : patientConsultations) {
            sb.append(String.format("%-18s %-15s %-12s %-8s\n",
                consultation.getConsultationID(),
                consultation.getDoctorID(),
                consultation.getConsultationDateTime().format(DATE_FORMATTER),
                consultation.getConsultationDateTime().format(DateTimeFormatter.ofPattern("HH:mm"))));
        }
        sb.append("\n");
        
        /* Latest Consultation Details */
        if (!patientConsultations.isEmpty()) {
            appendSectionTitle(sb, "LATEST CONSULTATION DETAILS");
            Consultation latest = null;
            for (Consultation consultation : patientConsultations) {
                if (latest == null || consultation.getConsultationDateTime().isAfter(latest.getConsultationDateTime())) {
                    latest = consultation;
                }
            }
            
            if (latest != null) {
                sb.append(String.format("%-25s: %s\n", "Consultation ID", latest.getConsultationID()));
                sb.append(String.format("%-25s: %s\n", "Date & Time", latest.getConsultationDateTime().format(DATETIME_FORMATTER)));
                sb.append(String.format("%-25s: %s\n", "Status", latest.getStatus()));
                sb.append(String.format("%-25s: %s\n", "Symptoms", latest.getSymptoms()));
                sb.append(String.format("%-25s: %s\n", "Diagnosis", latest.getDiagnosis().isEmpty() ? "Pending" : latest.getDiagnosis()));
                sb.append(String.format("%-25s: %s\n", "Notes", latest.getNotes().isEmpty() ? "None" : latest.getNotes()));
            }
        }
        
        appendFooter(sb);
        return sb.toString();
    }
    
    /* ---------- Helper Methods for Professional Formatting ---------- */
    
    private void appendHeader(StringBuilder sb, String title) {
        appendLine(sb, '=', WIDTH);
        sb.append(center(title, WIDTH)).append('\n');
        appendLine(sb, '=', WIDTH);
        sb.append('\n');
    }
    
    private void appendSectionTitle(StringBuilder sb, String title) {
        sb.append(center(title, WIDTH)).append('\n');
        appendLine(sb, '-', WIDTH);
        sb.append('\n');
    }
    
    private void appendFooter(StringBuilder sb) {
        appendLine(sb, '=', WIDTH);
        sb.append(center("End of Report", WIDTH)).append('\n');
        appendLine(sb, '=', WIDTH);
    }
    
    private void appendLine(StringBuilder sb, char c) {
        appendLine(sb, c, WIDTH);
    }
    
    private void appendLine(StringBuilder sb, char c, int width) {
        sb.append(repeat(c, width)).append('\n');
    }
    
    private String center(String s, int width) {
        if (s == null) s = "";
        int pad = Math.max(0, (width - s.length()) / 2);
        StringBuilder b = new StringBuilder(width);
        for (int i = 0; i < pad; i++) b.append(' ');
        b.append(s);
        return b.toString();
    }
    
    private String repeat(char c, int n) {
        if (n <= 0) return "";
        StringBuilder b = new StringBuilder(n);
        for (int i = 0; i < n; i++) b.append(c);
        return b.toString();
    }
    
    private String truncate(String str, int maxLength) {
        if (str == null) return "";
        if (str.length() <= maxLength) return str;
        return str.substring(0, maxLength - 3) + "...";
    }
}
