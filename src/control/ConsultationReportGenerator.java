package control;

import adt.AVLTree;
import entity.Consultation;
import entity.ConsultationStatus;
import entity.Doctor;
import entity.Patient;

import java.time.format.DateTimeFormatter;

/**
 *
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
    
     /* Generates a comprehensive consultation summary report */
    public String generateDetailedSummary() {
        StringBuilder sb = new StringBuilder();
        
        /* Header */
        sb.append(line('=', WIDTH)).append('\n');
        sb.append(center("Consultation Management Module", WIDTH)).append('\n');
        sb.append(center("Comprehensive Consultation Summary", WIDTH)).append('\n');
        sb.append(line('=', WIDTH)).append('\n');
        
        /* Statistics Section */
        int total = consultationManagement.getTotalConsultationsCount();
        int active = consultationManagement.getActiveConsultationsCount();
        int completed = consultationManagement.getCompletedConsultationsCount();
        int cancelled = total - active - completed;
        
        sb.append(line('-', WIDTH)).append('\n');
        sb.append(center("Consultation Statistics", WIDTH)).append('\n');
        sb.append(line('-', WIDTH)).append('\n');
        
        sb.append(kv("Total Consultations", Integer.toString(total)));
        sb.append(kv("Active Consultations", Integer.toString(active)));
        sb.append(kv("Completed Consultations", Integer.toString(completed)));
        sb.append(kv("Cancelled Consultations", Integer.toString(cancelled)));
        
        if (total > 0) {
            double completionRate = (double) completed / total * 100;
            double activeRate = (double) active / total * 100;
            double cancellationRate = (double) cancelled / total * 100;
            
            sb.append(kv("Completion Rate", String.format("%.1f%%", completionRate)));
            sb.append(kv("Active Rate", String.format("%.1f%%", activeRate)));
            sb.append(kv("Cancellation Rate", String.format("%.1f%%", cancellationRate)));
        }
        
        sb.append(line('-', WIDTH)).append('\n').append('\n').append('\n');
        
        /* Status Distribution Visual */
        sb.append(center("Consultation Status Distribution", WIDTH)).append('\n');
        sb.append(line('-', WIDTH)).append('\n');
        
        final int barW = 40; // 100% => 40 asterisks for better visibility
        double completionPct = total > 0 ? (double) completed / total * 100 : 0.0;
        double activePct = total > 0 ? (double) active / total * 100 : 0.0;
        double cancellationPct = total > 0 ? (double) cancelled / total * 100 : 0.0;
        
        int completionStars = (int)Math.round((completionPct / 100.0) * barW);
        int activeStars = (int)Math.round((activePct / 100.0) * barW);
        int cancellationStars = (int)Math.round((cancellationPct / 100.0) * barW);
        
        if (completionPct > 0.0 && completionStars == 0) completionStars = 1;
        if (activePct > 0.0 && activeStars == 0) activeStars = 1;
        if (cancellationPct > 0.0 && cancellationStars == 0) cancellationStars = 1;
        
        sb.append(String.format("%-20s: %-45s (%.1f%%)%n", 
            "COMPLETED", repeat('*', completionStars), completionPct));
        sb.append(String.format("%-20s: %-45s (%.1f%%)%n", 
            "IN PROGRESS", repeat('*', activeStars), activePct));
        sb.append(String.format("%-20s: %-45s (%.1f%%)%n", 
            "CANCELLED", repeat('*', cancellationStars), cancellationPct));
        
        sb.append(line('-', WIDTH)).append('\n').append('\n').append('\n');
        
        /* Recent Consultations Section */
        sb.append(line('-', WIDTH)).append('\n');
        sb.append(center("Recent Consultations (Last 10)", WIDTH)).append('\n');
        sb.append(line('-', WIDTH)).append('\n');
        
        sb.append(String.format("%-20s %-30s %-18s %-15s %-15s%n", 
            "Consultation ID", "Patient Name", "Doctor ID", "Date", "Status"));
        sb.append(String.format("%-20s %-30s %-18s %-15s %-15s%n", 
            "--------------------", "------------------------------", "------------------", "---------------", "---------------"));
        
        AVLTree<Consultation> allConsultations = consultationManagement.getAllConsultations();
        int count = 0;
        for (Consultation consultation : allConsultations) {
            if (count >= 10) break;
            
            Patient patient = patientManagement.findPatientByID(consultation.getPatientID());
            String patientName = patient != null ? patient.getPatientName() : "Unknown";
            
            sb.append(String.format("%-20s %-30s %-18s %-15s %-15s%n",
                consultation.getConsultationID(),
                truncate(patientName, 28),
                consultation.getDoctorID(),
                consultation.getConsultationDateTime().format(DATE_FORMATTER),
                formatStatus(consultation.getStatus())));
            count++;
        }
        
        sb.append('\n');
        sb.append(String.format("%-18s %d%n", "TOTAL CONSULTATIONS", total));
        
        sb.append(line('-', WIDTH)).append('\n');
        sb.append(center("End of Report", WIDTH)).append('\n');
        sb.append(line('-', WIDTH)).append('\n');
        
        return sb.toString();
    }
    
    /* Generates a doctor-specific consultation report */
    public String generateDoctorReport(String doctorID) {
        StringBuilder sb = new StringBuilder();
        
        Doctor doctor = doctorManagement.findDoctor(new Doctor(doctorID, "", "", "", null, ""));
        String doctorName = doctor != null ? doctor.getDoctorName() : "Unknown Doctor";
        
        /* Header */
        sb.append(line('=', WIDTH)).append('\n');
        sb.append(center("Consultation Management Module", WIDTH)).append('\n');
        sb.append(center("Doctor Consultation Report", WIDTH)).append('\n');
        sb.append(line('=', WIDTH)).append('\n');
        
        AVLTree<Consultation> doctorConsultations = consultationManagement.getConsultationsByDoctorID(doctorID);
        
        if (doctorConsultations.isEmpty()) {
            sb.append("No consultations found for this doctor.\n");
            sb.append(line('-', WIDTH)).append('\n');
            sb.append(center("End of Report", WIDTH)).append('\n');
            sb.append(line('-', WIDTH)).append('\n');
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
        
        sb.append(line('-', WIDTH)).append('\n');
        sb.append(center("Doctor Statistics", WIDTH)).append('\n');
        sb.append(line('-', WIDTH)).append('\n');
        
        sb.append(kv("Doctor ID", doctorID));
        sb.append(kv("Doctor Name", doctorName));
        sb.append(kv("Total Consultations", Integer.toString(total)));
        sb.append(kv("Completed", Integer.toString(completed)));
        sb.append(kv("Active", Integer.toString(active)));
        sb.append(kv("Cancelled", Integer.toString(cancelled)));
        
        if (total > 0) {
            double completionRate = (double) completed / total * 100;
            sb.append(kv("Completion Rate", String.format("%.1f%%", completionRate)));
        }
        
        sb.append(line('-', WIDTH)).append('\n').append('\n');
        
        /* Recent Consultations Section */
        sb.append(line('-', WIDTH)).append('\n');
        sb.append(center("Recent Consultations", WIDTH)).append('\n');
        sb.append(line('-', WIDTH)).append('\n');
        
        sb.append(String.format("%-20s %-30s %-15s %-10s %-15s%n", 
            "Consultation ID", "Patient Name", "Date", "Time", "Status"));
        sb.append(String.format("%-20s %-30s %-15s %-10s %-15s%n", 
            "--------------------", "------------------------------", "---------------", "----------", "---------------"));
        
        int count = 0;
        for (Consultation consultation : doctorConsultations) {
            if (count >= 10) break;
            
            Patient patient = patientManagement.findPatientByID(consultation.getPatientID());
            String patientName = patient != null ? patient.getPatientName() : "Unknown";
            
            sb.append(String.format("%-20s %-30s %-15s %-10s %-15s%n",
                consultation.getConsultationID(),
                truncate(patientName, 28),
                consultation.getConsultationDateTime().format(DATE_FORMATTER),
                consultation.getConsultationDateTime().format(DateTimeFormatter.ofPattern("HH:mm")),
                formatStatus(consultation.getStatus())));
            count++;
        }
        
        sb.append('\n');
        sb.append(String.format("%-18s %d%n", "TOTAL CONSULTATIONS", total));
        
        sb.append(line('-', WIDTH)).append('\n');
        sb.append(center("End of Report", WIDTH)).append('\n');
        sb.append(line('-', WIDTH)).append('\n');
        
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
        
        /* Header */
        sb.append(line('=', WIDTH)).append('\n');
        sb.append(center("Consultation Management Module", WIDTH)).append('\n');
        sb.append(center("Patient Consultation Report", WIDTH)).append('\n');
        sb.append(line('=', WIDTH)).append('\n');
        
        AVLTree<Consultation> patientConsultations = consultationManagement.getConsultationsByPatientID(patientID);
        
        if (patientConsultations.isEmpty()) {
            sb.append("No consultations found for this patient.\n");
            sb.append(line('-', WIDTH)).append('\n');
            sb.append(center("End of Report", WIDTH)).append('\n');
            sb.append(line('-', WIDTH)).append('\n');
            return sb.toString();
        }
        
        /* Patient Information */
        sb.append(line('-', WIDTH)).append('\n');
        sb.append(center("Patient Information", WIDTH)).append('\n');
        sb.append(line('-', WIDTH)).append('\n');
        
        sb.append(kv("Patient ID", patient.getPatientID()));
        sb.append(kv("Name", patient.getPatientName()));
        sb.append(kv("Age", Integer.toString(patient.getAge())));
        sb.append(kv("Gender", patient.getGender().toString()));
        sb.append(kv("Contact", patient.getContactNo()));
        sb.append(kv("Email", patient.getEmail()));
        
        sb.append(line('-', WIDTH)).append('\n').append('\n');
        
        /* Patient Statistics */
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
        
        sb.append(line('-', WIDTH)).append('\n');
        sb.append(center("Consultation Statistics", WIDTH)).append('\n');
        sb.append(line('-', WIDTH)).append('\n');
        
        sb.append(kv("Total Consultations", Integer.toString(total)));
        sb.append(kv("Completed", Integer.toString(completed)));
        sb.append(kv("Active", Integer.toString(active)));
        sb.append(kv("Cancelled", Integer.toString(cancelled)));
        
        if (total > 0) {
            double completionRate = (double) completed / total * 100;
            sb.append(kv("Completion Rate", String.format("%.1f%%", completionRate)));
        }
        
        sb.append(line('-', WIDTH)).append('\n').append('\n');
        
        /* Consultation History Section */
        sb.append(line('-', WIDTH)).append('\n');
        sb.append(center("Consultation History", WIDTH)).append('\n');
        sb.append(line('-', WIDTH)).append('\n');
        
        sb.append(String.format("%-20s %-18s %-15s %-10s %-15s%n", 
            "Consultation ID", "Doctor ID", "Date", "Time", "Status"));
        sb.append(String.format("%-20s %-18s %-15s %-10s %-15s%n", 
            "--------------------", "------------------", "---------------", "----------", "---------------"));
        
        for (Consultation consultation : patientConsultations) {
            sb.append(String.format("%-20s %-18s %-15s %-10s %-15s%n",
                consultation.getConsultationID(),
                consultation.getDoctorID(),
                consultation.getConsultationDateTime().format(DATE_FORMATTER),
                consultation.getConsultationDateTime().format(DateTimeFormatter.ofPattern("HH:mm")),
                formatStatus(consultation.getStatus())));
        }
        
        sb.append('\n');
        sb.append(String.format("%-18s %d%n", "TOTAL CONSULTATIONS", total));
        
        sb.append(line('-', WIDTH)).append('\n').append('\n');
        
        /* Latest Consultation Details */
        if (!patientConsultations.isEmpty()) {
            Consultation latest = null;
            for (Consultation consultation : patientConsultations) {
                if (latest == null || consultation.getConsultationDateTime().isAfter(latest.getConsultationDateTime())) {
                    latest = consultation;
                }
            }
            
            if (latest != null) {
                sb.append(line('-', WIDTH)).append('\n');
                sb.append(center("Latest Consultation Details", WIDTH)).append('\n');
                sb.append(line('-', WIDTH)).append('\n');
                
                sb.append(kv("Consultation ID", latest.getConsultationID()));
                sb.append(kv("Date & Time", latest.getConsultationDateTime().format(DATETIME_FORMATTER)));
                sb.append(kv("Status", latest.getStatus().toString()));
                sb.append(kv("Symptoms", latest.getSymptoms()));
                sb.append(kv("Diagnosis", latest.getDiagnosis().isEmpty() ? "Pending" : latest.getDiagnosis()));
                sb.append(kv("Notes", latest.getNotes().isEmpty() ? "None" : latest.getNotes()));
            }
        }
        
        sb.append(line('-', WIDTH)).append('\n');
        sb.append(center("End of Report", WIDTH)).append('\n');
        sb.append(line('-', WIDTH)).append('\n');
        
        return sb.toString();
    }
    
    /* ---------- Helper Methods (following DoctorReportGenerator style) ---------- */
    
    private String kv(String key, String value) {
        return String.format("%-28s: %s%n", key, value == null ? "-" : value);
    }
    
    private String line(char c, int n) {
        if (n <= 0) return "";
        StringBuilder b = new StringBuilder(n);
        for (int i = 0; i < n; i++) b.append(c);
        return b.toString();
    }
    
    private String center(String s, int width) {
        if (s == null) s = "";
        int pad = Math.max(0, (width - s.length()) / 2);
        StringBuilder b = new StringBuilder(width);
        for (int i = 0; i < pad; i++) b.append(' ');
        b.append(s);
        return b.toString();
    }
    
    private String truncate(String str, int maxLength) {
        if (str == null) return "";
        if (str.length() <= maxLength) return str;
        return str.substring(0, maxLength - 3) + "...";
    }
    
    private String formatStatus(ConsultationStatus status) {
        if (status == null) return "UNKNOWN";
        
        switch (status) {
            case IN_PROGRESS:
                return "[ACTIVE]";
            case COMPLETED:
                return "[DONE]";
            case CANCELLED:
                return "[CANCELLED]";
            default:
                return "[UNKNOWN]";
        }
    }
    
    private String repeat(char c, int n) {
        if (n <= 0) return "";
        StringBuilder b = new StringBuilder(n);
        for (int i = 0; i < n; i++) b.append(c);
        return b.toString();
    }
}
