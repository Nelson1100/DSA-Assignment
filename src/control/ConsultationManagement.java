package control;

import adt.*;
import entity.*;
import entity.keys.*;
import utility.IDGenerator;
import utility.IDType;

import java.util.ArrayList;
import java.util.List;

public class ConsultationManagement {    
    private final AVLTree<ConsultationByID> idxByID = new AVLTree<>();
    private final AVLTree<ConsultationByPatientID> idxByPatientID = new AVLTree<>();
    private final AVLTree<ConsultationByDoctorID> idxByDoctorID = new AVLTree<>();
    
    private final PatientMaintenance patientMaintenance;
    private final DoctorManagement doctorManagement;
    private final MedicalTreatmentManagement treatmentManagement;
    
    public ConsultationManagement(PatientMaintenance patientMaintenance, 
                                 DoctorManagement doctorManagement,
                                 MedicalTreatmentManagement treatmentManagement) {
        this.patientMaintenance = patientMaintenance;
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
        
    public String startConsultation(String patientID, String doctorID) {
        // Validate patient exists
        Patient patient = patientMaintenance.findPatientByID(patientID);
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
    
    private Doctor getDoctorByID(String doctorID) {
        Doctor searchKey = new Doctor();
        searchKey.setDoctorID(doctorID);
        return doctorManagement.findDoctor(searchKey);
    }
        
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
}