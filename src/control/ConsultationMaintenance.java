package control;

import adt.AVLTree;
import entity.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

/**
 * The ConsultationManagementModule class handles the core logic for managing
 * patient consultations. It interacts with other data structures to record
 * treatment details, prescriptions, and patient history.
 *
 * This module deliberately avoids functions that belong to other modules:
 * - It does not register new patients or manage their core information (Module 1).
 * - It does not manage doctor schedules or general doctor info (Module 2).
 */
public class ConsultationManagementModule {

    // Using an AVLTree to store PatientHistory for efficient search and retrieval
    private final AVLTree<PatientHistory> patientHistoryTree;
    private final AVLTree<Doctor> doctorTree;
    private final AVLTree<Medicine> medicineTree;
    private int treatmentRecordCounter = 0;

    public ConsultationManagementModule(
        AVLTree<PatientHistory> patientHistoryTree,
        AVLTree<Doctor> doctorTree,
        AVLTree<Medicine> medicineTree
    ) {
        this.patientHistoryTree = patientHistoryTree;
        this.doctorTree = doctorTree;
        this.medicineTree = medicineTree;
    }

    /**
     * Simulates the start of a consultation.
     * In a real system, this would mark the patient as "in consultation".
     *
     * @param patientVisit The patient visit object for the current consultation.
     * @param doctor The doctor assigned to the consultation.
     */
    public void startConsultation(PatientVisit patientVisit, Doctor doctor) {
        System.out.println("--- Starting Consultation ---");
        System.out.println("Patient: " + patientVisit.getPatient().getPatientName() +
                           " (ID: " + patientVisit.getPatient().getPatientID() + ")");
        System.out.println("Doctor: Dr. " + doctor.getDoctorName() +
                           " (ID: " + doctor.getDoctorID() + ")");
        patientVisit.setStatus(VisitStatus.SERVED);
        System.out.println("Status updated to: " + patientVisit.getStatus());
    }

    /**
     * Simulates the end of a consultation, capturing diagnosis, treatment, and prescription.
     * A new TreatmentRecord is created and added to the patient's history.
     *
     * @param patientVisit The patient visit object.
     * @param doctor The doctor who performed the consultation.
     * @param diagnosis The diagnosis provided by the doctor.
     * @param treatment The treatment notes.
     * @param prescriptionItems An array of prescribed items.
     * @return The newly created TreatmentRecord object.
     */
    public TreatmentRecord endConsultation(PatientVisit patientVisit, Doctor doctor,
                                            String diagnosis, String treatment,
                                            PrescriptionItem[] prescriptionItems) {
        System.out.println("--- Ending Consultation ---");

        // 1. Create a prescription
        Prescription prescription = new Prescription(
            patientVisit.getPatient().getPatientID(),
            doctor.getDoctorID()
        );
        for (PrescriptionItem item : prescriptionItems) {
            prescription.addItem(item);
        }

        // 2. Create a new treatment record
        treatmentRecordCounter++;
        String treatmentID = "T" + String.format("%04d", treatmentRecordCounter);
        TreatmentRecord newRecord = new TreatmentRecord(
            treatmentID,
            patientVisit.getPatient().getPatientID(),
            doctor.getDoctorID(),
            diagnosis,
            treatment,
            prescription
        );

        // 3. Add the record to the patient's history
        PatientHistory patientHistory = patientHistoryTree.find(new PatientHistory(patientVisit.getPatient()));
        if (patientHistory == null) {
            patientHistory = new PatientHistory(patientVisit.getPatient());
            patientHistoryTree.insert(patientHistory);
        }
        patientHistory.addRecord(newRecord);

        System.out.println("Consultation completed for patient " + patientVisit.getPatient().getPatientName());
        System.out.println("Treatment record saved. ID: " + newRecord.getTreatmentID());

        // Update visit status
        patientVisit.setStatus(VisitStatus.SERVED);

        return newRecord;
    }
    
    /**
     * Arranges a subsequent appointment for the patient.
     * This function only schedules the appointment and does not check for doctor availability,
     * which would be a function of the Doctor Management Module.
     * @param patient The patient to schedule for.
     * @param doctor The doctor for the appointment.
     * @param appointmentTime The scheduled date and time.
     */
    public void scheduleFollowUpAppointment(Patient patient, Doctor doctor, LocalDateTime appointmentTime) {
        System.out.println("--- Scheduling Follow-up Appointment ---");
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        System.out.println("Appointment for patient " + patient.getPatientName() +
                           " with Dr. " + doctor.getDoctorName() +
                           " scheduled for " + dtf.format(appointmentTime) + ".");
        System.out.println("A new visit record would be created here for Module 1's queue management.");
    }

    /**
     * Main method to demonstrate the module's functionality.
     */
    public static void main(String[] args) {
        // --- 1. SETUP: Simulate data from other modules ---
        // Patient Management data (from Module 1)
        AVLTree<PatientHistory> patientHistoryTree = new AVLTree<>();
        Patient patient1 = new Patient("P001", "Alice Smith", "0123456789", "alice@mail.com", Gender.FEMALE, 25);
        Patient patient2 = new Patient("P002", "Bob Johnson", "0198765432", "bob@mail.com", Gender.MALE, 30);
        patientHistoryTree.insert(new PatientHistory(patient1));
        patientHistoryTree.insert(new PatientHistory(patient2));
        
        // Doctor Management data (from Module 2)
        AVLTree<Doctor> doctorTree = new AVLTree<>();
        Doctor doctor1 = new Doctor("D001", "Jane Doe", "0111223344", "jane@clinic.com", Specialization.PEDIATRICS);
        Doctor doctor2 = new Doctor("D002", "John Lee", "0178899000", "john@clinic.com", Specialization.INTERNAL_MEDICINE);
        doctorTree.insert(doctor1);
        doctorTree.insert(doctor2);
        
        // Pharmacy data
        AVLTree<Medicine> medicineTree = new AVLTree<>();
        Medicine med1 = new Medicine("M101", "Paracetamol", "Pain Reliever", "500mg tablet", 5.50);
        Medicine med2 = new Medicine("M102", "Amoxicillin", "Antibiotic", "250mg capsule", 12.00);
        medicineTree.insert(med1);
        medicineTree.insert(med2);

        // A patient from the queue in Module 1 arrives
        PatientVisit currentVisit = new PatientVisit(patient1, VisitType.WALK_IN, LocalDateTime.now());
        
        // A doctor is assigned by Module 2
        Doctor assignedDoctor = doctor1;

        // --- 2. USAGE: Demonstrate Consultation Management Module ---
        System.out.println("--- Demonstration of Consultation Management Module ---");
        
        ConsultationManagementModule consultationModule = new ConsultationManagementModule(
            patientHistoryTree,
            doctorTree,
            medicineTree
        );
        
        // Scenario 1: Start and end a consultation with a prescription
        System.out.println("\n--- Scenario 1: New Consultation ---");
        consultationModule.startConsultation(currentVisit, assignedDoctor);

        String diagnosis = "Common cold and flu symptoms.";
        String treatmentNotes = "Advised patient to get plenty of rest and stay hydrated.";
        PrescriptionItem[] prescriptionItems = {
            new PrescriptionItem("M101", 10, "Take 2 tablets every 6 hours."),
            new PrescriptionItem("M102", 7, "Take 1 capsule twice daily.")
        };
        
        TreatmentRecord record1 = consultationModule.endConsultation(
            currentVisit,
            assignedDoctor,
            diagnosis,
            treatmentNotes,
            prescriptionItems
        );
        System.out.println("\nNew treatment record created:\n" + record1);

        // Scenario 2: Schedule a follow-up appointment
        System.out.println("\n--- Scenario 2: Scheduling an Appointment ---");
        LocalDateTime followUpTime = LocalDateTime.now().plusWeeks(2);
        consultationModule.scheduleFollowUpAppointment(patient1, doctor2, followUpTime);
    }
}
