package boundary;

import control.ConsultationManagement;
import control.PatientManagement;
import control.DoctorManagement;
import entity.*;
import utility.JOptionPaneConsoleIO;

import javax.swing.JOptionPane;
import java.util.List;

public class ConsultationUI {
    private final ConsultationManagement consultationManagement;
    private final PatientManagement patientMaintenance;
    private final DoctorManagement doctorManagement;
    
    public ConsultationUI(ConsultationManagement consultationManagement, 
                         PatientManagement patientMaintenance,
                         DoctorManagement doctorManagement) {
        this.consultationManagement = consultationManagement;
        this.patientMaintenance = patientMaintenance;
        this.doctorManagement = doctorManagement;
    }
    
    public void run() {
        boolean repeat = true;
        
        String[] menu = {
            "Start New Consultation",
            "View Active Consultations", 
            "Update Consultation Details",
            "Complete Consultation",
            "Cancel Consultation",
            "Search Consultations",
            "View Reports",
            "Back"
        };
        
        while (repeat) {
            int choice = JOptionPaneConsoleIO.readOption(
                    "Select an option:", 
                    "Consultation Module", 
                    menu
            );
            
            switch (choice) {
                case 0 -> startNewConsultation();
                case 1 -> viewActiveConsultations();
                case 2 -> updateConsultationDetails();
                case 3 -> completeConsultation();
                case 4 -> cancelConsultation();
                case 5 -> searchConsultations();
                case 6 -> viewReports();
                case 7, -1 -> repeat = false;
                default -> JOptionPaneConsoleIO.showError("Please choose a valid option.");
            }
        }
    }
    
    private void startNewConsultation() {
        try {
            // Get patient ID
            String patientID = JOptionPaneConsoleIO.readNonEmpty("Enter Patient ID:");
            if (patientID == null || patientID.trim().isEmpty()) {
                JOptionPaneConsoleIO.showError("Patient ID cannot be empty.");
                return;
            }
            
            // Validate patient exists
            Patient patient = patientMaintenance.findPatientByID(patientID.trim());
            if (patient == null) {
                JOptionPaneConsoleIO.showError("Patient not found with ID: " + patientID);
                return;
            }
            
            Doctor[] doctors = doctorManagement.getAllDoctor();
            if (doctors.length == 0) {
                JOptionPaneConsoleIO.showError("No doctors available in the system.");
                return;
            }
            
            String[] doctorOptions = new String[doctors.length];
            for (int i = 0; i < doctors.length; i++) {
                doctorOptions[i] = doctors[i].getDoctorID() + " - " + doctors[i].getDoctorName() + 
                                 " (" + doctors[i].getSpecialization() + ")";
            }
            
            int doctorChoice = JOptionPaneConsoleIO.readOption(
                "Select a doctor:", "Available Doctors", doctorOptions
            );
            
            if (doctorChoice == -1) return; // User cancelled
            
            String doctorID = doctors[doctorChoice].getDoctorID();
            
            String result = consultationManagement.startConsultation(patientID.trim(), doctorID);
            
            if (result.startsWith("Error:")) {
                JOptionPaneConsoleIO.showError(result);
            } else {
                JOptionPaneConsoleIO.showPlain(result, "Success");
            }
            
        } catch (Exception e) {
            JOptionPaneConsoleIO.showError("An error occurred: " + e.getMessage());
        }
    }
    
    private void viewActiveConsultations() {
        List<Consultation> activeConsultations = consultationManagement.getActiveConsultations();
        
        if (activeConsultations.isEmpty()) {
            JOptionPaneConsoleIO.showPlain("No active consultations found.", "Active Consultations");
            return;
        }
        
        StringBuilder sb = new StringBuilder("=== Active Consultations ===\n\n");
        for (Consultation consultation : activeConsultations) {
            Patient patient = patientMaintenance.findPatientByID(consultation.getPatientID());
            String patientName = patient != null ? patient.getPatientName() : "Unknown";
            
            sb.append(String.format("ID: %s | Patient: %s (%s) | Doctor: %s | Status: %s\n",
                consultation.getConsultationID(),
                patientName,
                consultation.getPatientID(),
                consultation.getDoctorID(),
                consultation.getStatus()
            ));
        }
        
        JOptionPaneConsoleIO.showPlain(sb.toString(), "Active Consultations");
    }
    
    private void updateConsultationDetails() {
        String consultationID = JOptionPaneConsoleIO.readNonEmpty("Enter Consultation ID:");
        if (consultationID == null || consultationID.trim().isEmpty()) {
            JOptionPaneConsoleIO.showError("Consultation ID cannot be empty.");
            return;
        }
        
        Consultation consultation = consultationManagement.getConsultationByID(consultationID.trim());
        if (consultation == null) {
            JOptionPaneConsoleIO.showError("Consultation not found with ID: " + consultationID);
            return;
        }
        
        if (consultation.isCompleted() || consultation.isCancelled()) {
            JOptionPaneConsoleIO.showError("Cannot update a " + consultation.getStatus().toString().toLowerCase() + " consultation.");
            return;
        }
        
        JOptionPaneConsoleIO.showPlain(consultation.toString(), "Current Details");
        
        String symptoms = JOptionPane.showInputDialog("Enter Symptoms (current: " + 
                         (consultation.getSymptoms().isEmpty() ? "None" : consultation.getSymptoms()) + "):");
        String diagnosis = JOptionPane.showInputDialog("Enter Diagnosis (current: " + 
                          (consultation.getDiagnosis().isEmpty() ? "None" : consultation.getDiagnosis()) + "):");
        String notes = JOptionPane.showInputDialog("Enter Notes (current: " + 
                      (consultation.getNotes().isEmpty() ? "None" : consultation.getNotes()) + "):");
        
        // Use existing values if user provides empty input
        symptoms = (symptoms == null || symptoms.trim().isEmpty()) ? consultation.getSymptoms() : symptoms.trim();
        diagnosis = (diagnosis == null || diagnosis.trim().isEmpty()) ? consultation.getDiagnosis() : diagnosis.trim();
        notes = (notes == null || notes.trim().isEmpty()) ? consultation.getNotes() : notes.trim();
        
        String result = consultationManagement.updateConsultationDetails(consultationID.trim(), symptoms, diagnosis, notes);
        
        if (result.startsWith("Error:")) {
            JOptionPaneConsoleIO.showError(result);
        } else {
            JOptionPaneConsoleIO.showPlain(result, "Success");
        }
    }
    
    private void completeConsultation() {
        String consultationID = JOptionPaneConsoleIO.readNonEmpty("Enter Consultation ID:");
        if (consultationID == null || consultationID.trim().isEmpty()) {
            JOptionPaneConsoleIO.showError("Consultation ID cannot be empty.");
            return;
        }
        
        Consultation consultation = consultationManagement.getConsultationByID(consultationID.trim());
        if (consultation == null) {
            JOptionPaneConsoleIO.showError("Consultation not found with ID: " + consultationID);
            return;
        }
        
        if (consultation.isCompleted()) {
            JOptionPaneConsoleIO.showError("Consultation is already completed.");
            return;
        }
        
        if (consultation.isCancelled()) {
            JOptionPaneConsoleIO.showError("Cannot complete a cancelled consultation.");
            return;
        }
        
        // Show current details
        JOptionPaneConsoleIO.showPlain(consultation.toString(), "Current Consultation");
        
        String finalDiagnosis = JOptionPaneConsoleIO.readNonEmpty("Enter Final Diagnosis:");
        String treatmentNotes = JOptionPane.showInputDialog("Enter Treatment Notes:");
        
        if (finalDiagnosis == null || finalDiagnosis.trim().isEmpty()) {
            JOptionPaneConsoleIO.showError("Final diagnosis is required to complete consultation.");
            return;
        }
        
        String result = consultationManagement.completeConsultation(consultationID.trim(), 
                                                                   finalDiagnosis.trim(), 
                                                                   treatmentNotes != null ? treatmentNotes.trim() : "");
        
        if (result.startsWith("Error:")) {
            JOptionPaneConsoleIO.showError(result);
        } else {
            JOptionPaneConsoleIO.showPlain(result, "Success");
        }
    }
    
    private void cancelConsultation() {
        String consultationID = JOptionPaneConsoleIO.readNonEmpty("Enter Consultation ID:");
        if (consultationID == null || consultationID.trim().isEmpty()) {
            JOptionPaneConsoleIO.showError("Consultation ID cannot be empty.");
            return;
        }
        
        Consultation consultation = consultationManagement.getConsultationByID(consultationID.trim());
        if (consultation == null) {
            JOptionPaneConsoleIO.showError("Consultation not found with ID: " + consultationID);
            return;
        }
        
        if (consultation.isCompleted()) {
            JOptionPaneConsoleIO.showError("Cannot cancel a completed consultation.");
            return;
        }
        
        JOptionPaneConsoleIO.showPlain(consultation.toString(), "Current Consultation");
        
        int confirm = JOptionPane.showConfirmDialog(null, 
            "Are you sure you want to cancel this consultation?", 
            "Confirm Cancellation", 
            JOptionPane.YES_NO_OPTION);
            
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        
        String reason = JOptionPane.showInputDialog("Enter Cancellation Reason:");
        reason = reason != null ? reason.trim() : "No reason provided";
        
        String result = consultationManagement.cancelConsultation(consultationID.trim(), reason);
        
        if (result.startsWith("Error:")) {
            JOptionPaneConsoleIO.showError(result);
        } else {
            JOptionPaneConsoleIO.showPlain(result, "Success");
        }
    }
    
    private void searchConsultations() {
        String[] searchOptions = {
            "Search by Consultation ID",
            "Search by Patient ID", 
            "Search by Doctor ID",
            "View All Consultations",
            "Back"
        };
        
        int choice = JOptionPaneConsoleIO.readOption(
            "Select search type:", "Search Consultations", searchOptions
        );
        
        switch (choice) {
            case 0 -> searchByConsultationID();
            case 1 -> searchByPatientID();
            case 2 -> searchByDoctorID();
            case 3 -> viewAllConsultations();
            case 4, -1 -> { /* Back */ }
            default -> JOptionPaneConsoleIO.showError("Please choose a valid option.");
        }
    }
    
    private void searchByConsultationID() {
        String consultationID = JOptionPaneConsoleIO.readNonEmpty("Enter Consultation ID:");
        if (consultationID == null || consultationID.trim().isEmpty()) {
            JOptionPaneConsoleIO.showError("Consultation ID cannot be empty.");
            return;
        }
        
        Consultation consultation = consultationManagement.getConsultationByID(consultationID.trim());
        if (consultation == null) {
            JOptionPaneConsoleIO.showError("Consultation not found with ID: " + consultationID);
            return;
        }
        
        Patient patient = patientMaintenance.findPatientByID(consultation.getPatientID());
        String patientName = patient != null ? patient.getPatientName() : "Unknown";
        
        String displayInfo = consultation.toString() + "\nPatient Name: " + patientName;
        JOptionPaneConsoleIO.showPlain(displayInfo, "Consultation Details");
    }
    
    private void searchByPatientID() {
        String patientID = JOptionPaneConsoleIO.readNonEmpty("Enter Patient ID:");
        if (patientID == null || patientID.trim().isEmpty()) {
            JOptionPaneConsoleIO.showError("Patient ID cannot be empty.");
            return;
        }
        
        List<Consultation> consultations = consultationManagement.getConsultationsByPatientID(patientID.trim());
        if (consultations.isEmpty()) {
            JOptionPaneConsoleIO.showPlain("No consultations found for patient ID: " + patientID, "Search Results");
            return;
        }
        
        StringBuilder sb = new StringBuilder("=== Consultations for Patient " + patientID + " ===\n\n");
        for (Consultation consultation : consultations) {
            sb.append(consultation.getConsultationID()).append(" | ")
              .append(consultation.getConsultationDateTime().toLocalDate()).append(" | ")
              .append(consultation.getStatus()).append("\n");
        }
        
        JOptionPaneConsoleIO.showPlain(sb.toString(), "Search Results");
    }
    
    private void searchByDoctorID() {
        String doctorID = JOptionPaneConsoleIO.readNonEmpty("Enter Doctor ID:");
        if (doctorID == null || doctorID.trim().isEmpty()) {
            JOptionPaneConsoleIO.showError("Doctor ID cannot be empty.");
            return;
        }
        
        List<Consultation> consultations = consultationManagement.getConsultationsByDoctorID(doctorID.trim());
        if (consultations.isEmpty()) {
            JOptionPaneConsoleIO.showPlain("No consultations found for doctor ID: " + doctorID, "Search Results");
            return;
        }
        
        StringBuilder sb = new StringBuilder("=== Consultations for Doctor " + doctorID + " ===\n\n");
        for (Consultation consultation : consultations) {
            Patient patient = patientMaintenance.findPatientByID(consultation.getPatientID());
            String patientName = patient != null ? patient.getPatientName() : "Unknown";
            
            sb.append(consultation.getConsultationID()).append(" | ")
              .append(patientName).append(" (").append(consultation.getPatientID()).append(") | ")
              .append(consultation.getConsultationDateTime().toLocalDate()).append(" | ")
              .append(consultation.getStatus()).append("\n");
        }
        
        JOptionPaneConsoleIO.showPlain(sb.toString(), "Search Results");
    }
    
    private void viewAllConsultations() {
        List<Consultation> consultations = consultationManagement.getAllConsultations();
        if (consultations.isEmpty()) {
            JOptionPaneConsoleIO.showPlain("No consultations found in the system.", "All Consultations");
            return;
        }
        
        StringBuilder sb = new StringBuilder("=== All Consultations ===\n\n");
        for (Consultation consultation : consultations) {
            Patient patient = patientMaintenance.findPatientByID(consultation.getPatientID());
            String patientName = patient != null ? patient.getPatientName() : "Unknown";
            
            sb.append(consultation.getConsultationID()).append(" | ")
              .append(patientName).append(" | ")
              .append(consultation.getDoctorID()).append(" | ")
              .append(consultation.getConsultationDateTime().toLocalDate()).append(" | ")
              .append(consultation.getStatus()).append("\n");
        }
        
        JOptionPaneConsoleIO.showPlain(sb.toString(), "All Consultations");
    }
    
    private void viewReports() {
        String[] reportOptions = {
            "Consultation Summary",
            "Active Consultations Report",
            "Completed Consultations Report", 
            "Doctor Performance Report",
            "Patient History Report",
            "Back"
        };
        
        int choice = JOptionPaneConsoleIO.readOption(
            "Select report type:", "Consultation Reports", reportOptions
        );
        
        switch (choice) {
            case 0 -> showConsultationSummary();
            case 1 -> showActiveConsultationsReport();
            case 2 -> showCompletedConsultationsReport();
            case 3 -> showDoctorPerformanceReport();
            case 4 -> showPatientHistoryReport();
            case 5, -1 -> { /* Back */ }
            default -> JOptionPaneConsoleIO.showError("Please choose a valid option.");
        }
    }
    
    private void showConsultationSummary() {
        String summary = consultationManagement.getConsultationSummary();
        JOptionPaneConsoleIO.showPlain(summary, "Consultation Summary");
    }
    
    private void showActiveConsultationsReport() {
        List<Consultation> activeConsultations = consultationManagement.getActiveConsultations();
        
        if (activeConsultations.isEmpty()) {
            JOptionPaneConsoleIO.showPlain("No active consultations found.", "Active Consultations Report");
            return;
        }
        
        StringBuilder sb = new StringBuilder("=== Active Consultations Report ===\n\n");
        sb.append("Total Active: ").append(activeConsultations.size()).append("\n\n");
        
        for (Consultation consultation : activeConsultations) {
            Patient patient = patientMaintenance.findPatientByID(consultation.getPatientID());
            String patientName = patient != null ? patient.getPatientName() : "Unknown";
            
            sb.append("ID: ").append(consultation.getConsultationID()).append("\n");
            sb.append("Patient: ").append(patientName).append(" (").append(consultation.getPatientID()).append(")\n");
            sb.append("Doctor: ").append(consultation.getDoctorID()).append("\n");
            sb.append("Started: ").append(consultation.getConsultationDateTime().toLocalDate()).append("\n");
            sb.append("---\n");
        }
        
        JOptionPaneConsoleIO.showPlain(sb.toString(), "Active Consultations Report");
    }
    
    private void showCompletedConsultationsReport() {
        List<Consultation> completedConsultations = consultationManagement.getCompletedConsultations();
        
        if (completedConsultations.isEmpty()) {
            JOptionPaneConsoleIO.showPlain("No completed consultations found.", "Completed Consultations Report");
            return;
        }
        
        StringBuilder sb = new StringBuilder("=== Completed Consultations Report ===\n\n");
        sb.append("Total Completed: ").append(completedConsultations.size()).append("\n\n");
        
        for (Consultation consultation : completedConsultations) {
            Patient patient = patientMaintenance.findPatientByID(consultation.getPatientID());
            String patientName = patient != null ? patient.getPatientName() : "Unknown";
            
            sb.append("ID: ").append(consultation.getConsultationID()).append("\n");
            sb.append("Patient: ").append(patientName).append(" (").append(consultation.getPatientID()).append(")\n");
            sb.append("Doctor: ").append(consultation.getDoctorID()).append("\n");
            sb.append("Date: ").append(consultation.getConsultationDateTime().toLocalDate()).append("\n");
            sb.append("Diagnosis: ").append(consultation.getDiagnosis()).append("\n");
            sb.append("---\n");
        }
        
        JOptionPaneConsoleIO.showPlain(sb.toString(), "Completed Consultations Report");
    }
    
    private void showDoctorPerformanceReport() {
        Doctor[] doctors = doctorManagement.getAllDoctor();
        if (doctors.length == 0) {
            JOptionPaneConsoleIO.showPlain("No doctors found in the system.", "Doctor Performance Report");
            return;
        }
        
        StringBuilder sb = new StringBuilder("=== Doctor Performance Report ===\n\n");
        
        for (Doctor doctor : doctors) {
            int totalConsultations = consultationManagement.getConsultationsCountByDoctor(doctor.getDoctorID());
            List<Consultation> doctorConsultations = consultationManagement.getConsultationsByDoctorID(doctor.getDoctorID());
            
            long activeCount = doctorConsultations.stream().filter(Consultation::isInProgress).count();
            long completedCount = doctorConsultations.stream().filter(Consultation::isCompleted).count();
            
            sb.append("Doctor: ").append(doctor.getDoctorName()).append(" (").append(doctor.getDoctorID()).append(")\n");
            sb.append("Specialization: ").append(doctor.getSpecialization()).append("\n");
            sb.append("Total Consultations: ").append(totalConsultations).append("\n");
            sb.append("Active: ").append(activeCount).append(" | Completed: ").append(completedCount).append("\n");
            sb.append("---\n");
        }
        
        JOptionPaneConsoleIO.showPlain(sb.toString(), "Doctor Performance Report");
    }
    
    private void showPatientHistoryReport() {
        String patientID = JOptionPaneConsoleIO.readNonEmpty("Enter Patient ID:");
        if (patientID == null || patientID.trim().isEmpty()) {
            JOptionPaneConsoleIO.showError("Patient ID cannot be empty.");
            return;
        }
        
        Patient patient = patientMaintenance.findPatientByID(patientID.trim());
        if (patient == null) {
            JOptionPaneConsoleIO.showError("Patient not found with ID: " + patientID);
            return;
        }
        
        List<Consultation> consultations = consultationManagement.getConsultationsByPatientID(patientID.trim());
        
        StringBuilder sb = new StringBuilder("=== Patient History Report ===\n\n");
        sb.append("Patient: ").append(patient.getPatientName()).append(" (").append(patient.getPatientID()).append(")\n");
        sb.append("Total Consultations: ").append(consultations.size()).append("\n\n");
        
        if (consultations.isEmpty()) {
            sb.append("No consultation history found.");
        } else {
            for (Consultation consultation : consultations) {
                sb.append("Date: ").append(consultation.getConsultationDateTime().toLocalDate()).append("\n");
                sb.append("Doctor: ").append(consultation.getDoctorID()).append("\n");
                sb.append("Status: ").append(consultation.getStatus()).append("\n");
                sb.append("Diagnosis: ").append(consultation.getDiagnosis().isEmpty() ? "Not recorded" : consultation.getDiagnosis()).append("\n");
                sb.append("---\n");
            }
        }
        
        JOptionPaneConsoleIO.showPlain(sb.toString(), "Patient History Report");
    }
}