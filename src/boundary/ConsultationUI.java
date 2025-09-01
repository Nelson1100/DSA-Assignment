package boundary;

import control.ConsultationManagement;
import control.PatientManagement;
import control.DoctorManagement;
import control.ConsultationReportGenerator;
import entity.*;
import utility.JOptionPaneConsoleIO;
import adt.*;

import javax.swing.JOptionPane;

/**
 *
 * @author Sim Jia Quan
 */

public class ConsultationUI {
    private final ConsultationManagement consultationManagement;
    private final PatientManagement patientManagement;
    private final DoctorManagement doctorManagement;
    private final ConsultationReportGenerator reportGenerator;
    
    public ConsultationUI(ConsultationManagement consultationManagement, 
                         PatientManagement patientManagement,
                         DoctorManagement doctorManagement) {
        this.consultationManagement = consultationManagement;
        this.patientManagement = patientManagement;
        this.doctorManagement = doctorManagement;
        this.reportGenerator = new ConsultationReportGenerator(consultationManagement, patientManagement, doctorManagement);
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
            String patientID = JOptionPaneConsoleIO.readNonEmpty("Enter Patient ID:");
            if (patientID == null) {
                return; // User cancelled, just return silently
            }
            if (patientID.trim().isEmpty()) {
                JOptionPaneConsoleIO.showError("Patient ID cannot be empty.");
                return;
            }
            
            Patient patient = patientManagement.findPatientByID(patientID.trim());
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
            
            if (doctorChoice == -1) return; 
            
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
        AVLTree<Consultation> activeConsultations = consultationManagement.getActiveConsultations();
        
        if (activeConsultations.isEmpty()) {
            JOptionPaneConsoleIO.showPlain("No active consultations found.", "Active Consultations");
            return;
        }
        
        StringBuilder sb = new StringBuilder("=== Active Consultations ===\n\n");
        
        for (Consultation consultation : activeConsultations) {
            Patient patient = patientManagement.findPatientByID(consultation.getPatientID());
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
        if (consultationID == null) {
            return; // User cancelled
        }
        if (consultationID.trim().isEmpty()) {
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
        if (consultationID == null) {
            return; // User cancelled
        }
        if (consultationID.trim().isEmpty()) {
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
        
        JOptionPaneConsoleIO.showPlain(consultation.toString(), "Current Consultation");
        
        String finalDiagnosis = JOptionPaneConsoleIO.readNonEmpty("Enter Final Diagnosis:");
        if (finalDiagnosis == null) {
            return; // User cancelled
        }
        String treatmentNotes = JOptionPane.showInputDialog("Enter Treatment Notes:");
        
        if (finalDiagnosis.trim().isEmpty()) {
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
        if (consultationID == null) {
            return; // User cancelled
        }
        if (consultationID.trim().isEmpty()) {
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
        if (consultationID == null) {
            return; // User cancelled
        }
        if (consultationID.trim().isEmpty()) {
            JOptionPaneConsoleIO.showError("Consultation ID cannot be empty.");
            return;
        }
        
        Consultation consultation = consultationManagement.getConsultationByID(consultationID.trim());
        if (consultation == null) {
            JOptionPaneConsoleIO.showError("Consultation not found with ID: " + consultationID);
            return;
        }
        
        Patient patient = patientManagement.findPatientByID(consultation.getPatientID());
        String patientName = patient != null ? patient.getPatientName() : "Unknown";
        
        String displayInfo = consultation.toString() + "\nPatient Name: " + patientName;
        JOptionPaneConsoleIO.showPlain(displayInfo, "Consultation Details");
    }
    
    private void searchByPatientID() {
        String patientID = JOptionPaneConsoleIO.readNonEmpty("Enter Patient ID:");
        if (patientID == null) {
            return; // User cancelled
        }
        if (patientID.trim().isEmpty()) {
            JOptionPaneConsoleIO.showError("Patient ID cannot be empty.");
            return;
        }
        
        AVLTree<Consultation> consultations = consultationManagement.getConsultationsByPatientID(patientID.trim());
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
        if (doctorID == null) {
            return; // User cancelled
        }
        if (doctorID.trim().isEmpty()) {
            JOptionPaneConsoleIO.showError("Doctor ID cannot be empty.");
            return;
        }
        
        AVLTree<Consultation> consultations = consultationManagement.getConsultationsByDoctorID(doctorID.trim());
        if (consultations.isEmpty()) {
            JOptionPaneConsoleIO.showPlain("No consultations found for doctor ID: " + doctorID, "Search Results");
            return;
        }
        
        StringBuilder sb = new StringBuilder("=== Consultations for Doctor " + doctorID + " ===\n\n");
        
        for (Consultation consultation : consultations) {
            Patient patient = patientManagement.findPatientByID(consultation.getPatientID());
            String patientName = patient != null ? patient.getPatientName() : "Unknown";
            
            sb.append(consultation.getConsultationID()).append(" | ")
              .append(patientName).append(" | ")
              .append(consultation.getDoctorID()).append(" | ")
              .append(consultation.getStatus()).append("\n");
        }
        
        JOptionPaneConsoleIO.showPlain(sb.toString(), "Search Results");
    }
    
    private void viewAllConsultations() {
        AVLTree<Consultation> consultations = consultationManagement.getAllConsultations();
        if (consultations.isEmpty()) {
            JOptionPaneConsoleIO.showPlain("No consultations found in the system.", "All Consultations");
            return;
        }
        
        StringBuilder sb = new StringBuilder("=== All Consultations ===\n\n");
        
        for (Consultation consultation : consultations) {
            Patient patient = patientManagement.findPatientByID(consultation.getPatientID());
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
        boolean repeat = true;
        
        while (repeat) {
            String[] reportOptions = {
                "Consultation Summary",
                "Doctor Consultation Report",
                "Patient Consultation Report",
                "Back"
            };
            
            int choice = JOptionPaneConsoleIO.readOption(
                "Select report type:", "Consultation Reports", reportOptions
            );
            
            switch (choice) {
                case 0:
                    showConsultationSummary();
                    break;
                case 1:
                    showDoctorReport();
                    break;
                case 2:
                    showPatientReport();
                    break;
                case 3:
                case -1:
                    repeat = false;
                    break;
                default:
                    JOptionPaneConsoleIO.showError("Please choose a valid option.");
                    break;
            }
        }
    }
    
    private void showConsultationSummary() {
        String summary = reportGenerator.generateDetailedSummary();
        JOptionPaneConsoleIO.showPlain(summary, "Consultation Summary Report");
    }
    
    private void showDoctorReport() {
        String doctorID = JOptionPaneConsoleIO.readNonEmpty("Enter Doctor ID:");
        if (doctorID == null) {
            return; // User cancelled
        }
        if (doctorID.trim().isEmpty()) {
            JOptionPaneConsoleIO.showError("Doctor ID cannot be empty.");
            return;
        }
        
        String doctorReport = reportGenerator.generateDoctorReport(doctorID.trim());
        JOptionPaneConsoleIO.showPlain(doctorReport, "Doctor Consultation Report");
    }
    
    private void showPatientReport() {
        String patientID = JOptionPaneConsoleIO.readNonEmpty("Enter Patient ID:");
        if (patientID == null) {
            return; // User cancelled
        }
        if (patientID.trim().isEmpty()) {
            JOptionPaneConsoleIO.showError("Patient ID cannot be empty.");
            return;
        }
        
        String patientReport = reportGenerator.generatePatientReport(patientID.trim());
        JOptionPaneConsoleIO.showPlain(patientReport, "Patient Consultation Report");
    }
    

}