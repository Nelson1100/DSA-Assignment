package boundary;

import control.ConsultationMaintenance;
import entity.Doctor;
import entity.Gender;
import entity.Patient;
import entity.PatientVisit;
import entity.VisitType;
import java.util.InputMismatchException;
import java.util.Scanner;

public class ConsultationReportUI {

    private final Scanner scanner;
    private final ConsultationMaintenance controller;

    public ConsultationReportUI() {
        this.scanner = new Scanner(System.in);
        this.controller = new ConsultationMaintenance();
    }

    public void run() {
        int choice = -1;
        while (choice != 0) {
            try {
                displayMenu();
                System.out.print("Enter your choice: ");
                choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1:
                        addNewPatient();
                        break;
                    case 2:
                        addPatientToQueue();
                        break;
                    case 3:
                        startConsultation();
                        break;
                    case 4:
                        viewPatientHistory();
                        break;
                    case 5:
                        viewWaitingQueue();
                        break;
                    case 6:
                        viewAllDoctors();
                        break;
                    case 7:
                        viewAllPatients();
                        break;
                    case 0:
                        System.out.println("Exiting the system. Goodbye!");
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine(); // Clear the invalid input from scanner
                choice = -1; // Reset choice to loop again
            }
        }
    }

    private void displayMenu() {
        System.out.println("\n--- Clinic Management System Menu ---");
        System.out.println("1. Add New Patient");
        System.out.println("2. Add Patient to Queue");
        System.out.println("3. Start Consultation for Next Patient");
        System.out.println("4. View Patient History");
        System.out.println("5. View Waiting Queue");
        System.out.println("6. View All Doctors");
        System.out.println("7. View All Patients");
        System.out.println("0. Exit");
    }

    private void addNewPatient() {
        System.out.println("\n--- Add New Patient ---");
        System.out.print("Enter Patient ID: ");
        String id = scanner.nextLine();
        System.out.print("Enter Patient Name: ");
        String name = scanner.nextLine();
        System.out.print("Enter Contact No: ");
        String contact = scanner.nextLine();
        System.out.print("Enter Email: ");
        String email = scanner.nextLine();
        System.out.print("Enter Gender (MALE/FEMALE): ");
        Gender gender = Gender.valueOf(scanner.nextLine().toUpperCase());
        System.out.print("Enter Age: ");
        int age = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        Patient newPatient = new Patient(id, name, contact, email, gender, age);
        if (controller.addNewPatient(newPatient)) {
            System.out.println("Patient added successfully.");
        }
    }

    private void addPatientToQueue() {
        System.out.println("\n--- Add Patient to Queue ---");
        System.out.print("Enter Patient ID: ");
        String patientId = scanner.nextLine();
        System.out.print("Enter Visit Type (WALK_IN/APPOINTMENT): ");
        VisitType visitType = VisitType.valueOf(scanner.nextLine().toUpperCase());
        controller.addPatientToQueue(patientId, visitType);
    }
    
    private void startConsultation() {
        PatientVisit visit = controller.peekNextPatient();
        if (visit == null) {
            System.out.println("No patients in the waiting queue.");
            return;
        }

        System.out.println("\n--- Starting Consultation for Next Patient ---");
        System.out.println("Serving: " + visit.getPatient().getPatientName() + " (ID: " + visit.getPatient().getPatientID() + ")");

        // Get doctor ID
        System.out.print("Enter Doctor ID: ");
        String doctorID = scanner.nextLine();
        Doctor doctor = controller.getDoctor(doctorID);
        if (doctor == null) {
            System.out.println("Error: Doctor not found with ID " + doctorID + ".");
            return;
        }

        // Get diagnosis and treatment
        System.out.print("Enter Diagnosis: ");
        String diagnosis = scanner.nextLine();
        System.out.print("Enter Treatment Notes: ");
        String treatment = scanner.nextLine();

        // Check if a prescription is needed
        System.out.print("Add a prescription? (yes/no): ");
        String addPrescription = scanner.nextLine();
        String[] medicineIDs = null;
        if (addPrescription.equalsIgnoreCase("yes")) {
            System.out.print("Enter medicine IDs (comma-separated): ");
            medicineIDs = scanner.nextLine().split(",");
        }
        
        // Remove patient from queue and record consultation
        controller.serveNextPatient();
        controller.recordConsultation(visit, diagnosis, treatment, doctorID, medicineIDs);
    }

    private void viewPatientHistory() {
        System.out.println("\n--- View Patient History ---");
        System.out.print("Enter Patient ID: ");
        String patientId = scanner.nextLine();
        controller.displayPatientHistory(patientId);
    }
    
    private void viewWaitingQueue() {
        controller.displayWaitingQueue();
    }
    
    private void viewAllDoctors() {
        controller.displayAllDoctors();
    }
    
    private void viewAllPatients() {
        controller.displayAllPatients();
    }
}