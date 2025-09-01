package control;

import adt.*;
import dao.ConsultationInitializer;
import entity.*;
import entity.keys.*;
import utility.IDGenerator;
import utility.IDType;

public class ConsultationManagement {
    private final AVLTree<ConsultationByID> idxByID = new AVLTree<>();
    private final AVLTree<ConsultationByPatientID> idxByPatientID = new AVLTree<>();
    private final AVLTree<ConsultationByDoctorID> idxByDoctorID = new AVLTree<>();
        
    private final PatientManagement patientManagement;
    private final DoctorManagement doctorManagement;
    
public ConsultationManagement(PatientManagement patientManagement, 
                             DoctorManagement doctorManagement) {
    this.patientManagement = patientManagement;
    this.doctorManagement = doctorManagement;
}

    public void initializeData() {
        ConsultationInitializer.initialize(this, patientManagement, doctorManagement); 
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
        
        /* Validate patient ID */
        if (!isValidPatientID(patientID)) {
            result.addError("Invalid patient ID: " + patientID + 
                          ". Patient ID must exist in the Patient Management module and follow format PYYYYMMDD####");
        }
        
        /* Validate doctor ID */
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
        
        return String.format("""
                             Patient ID: %s
                             Name: %s
                             Age: %d
                             Gender: %s
                             Contact: %s
                             Email: %s""",
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
        
        return String.format("""
                             Doctor ID: %s
                             Name: %s
                             Specialization: %s
                             Contact: %s
                             Email: %s""",
            doctor.getDoctorID(),
            doctor.getDoctorName(),
            doctor.getSpecialization(),
            doctor.getContactNo(),
            doctor.getEmail()
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
        
    /* ---------- Indexing Methods ---------- */
    
    public void indexConsultation(Consultation consultation) {
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
        return idxByID.size();
    }
    
    public int getActiveConsultationsCount() {
        AVLTree<Consultation> activeConsultations = getActiveConsultations();
        return activeConsultations.size();
    }
    
    public int getCompletedConsultationsCount() {
        AVLTree<Consultation> completedConsultations = getCompletedConsultations();
        return completedConsultations.size();
    }
    
    public int getConsultationsCountByDoctor(String doctorID) {
        AVLTree<Consultation> doctorConsultations = getConsultationsByDoctorID(doctorID);
        return doctorConsultations.size();
    }
    
    public int getConsultationsCountByPatient(String patientID) {
        AVLTree<Consultation> patientConsultations = getConsultationsByPatientID(patientID);
        return patientConsultations.size();
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