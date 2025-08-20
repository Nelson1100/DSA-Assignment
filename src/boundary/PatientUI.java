package boundary;

import control.PatientMaintenance;
import control.PatientReportGenerator;
import entity.Patient;
import entity.Gender;
import entity.VisitType;
import utility.*;

import java.time.LocalTime;
import java.util.Scanner;

public class PatientUI {
    private final Scanner sc = new Scanner(System.in);
    private final PatientMaintenance pm = new PatientMaintenance();
    private final PatientReportGenerator reportGen = new PatientReportGenerator(pm);
    private final PatientReportUI reportUI = new PatientReportUI(reportGen, sc); // share scanner

    public void start() {
        int choice;
        
        do {
            ScannerDisplayEffect.clearScreen();
            ScannerDisplayEffect.printHeader("Patient Management");
            printMainMenu();
            
            choice = ScannerConsoleIO.readInt(sc, "Enter your choice: ");
            switch (choice) {
                case 1:
                    ScannerDisplayEffect.clearScreen();
                    ScannerDisplayEffect.printHeader("Register New Patient");
                    registerFlow();
                    ScannerConsoleIO.pause(sc);
                    break;
                case 2:
                    ScannerDisplayEffect.clearScreen();
                    ScannerDisplayEffect.printHeader("Serve Next Patient");
                    serveFlow();
                    ScannerConsoleIO.pause(sc);
                    break;
                case 3:
                    ScannerDisplayEffect.clearScreen();
                    ScannerDisplayEffect.printHeader("Current Queue");
                    pm.viewAllPatients();
                    ScannerDisplayEffect.printDivider();
                    ScannerConsoleIO.pause(sc);
                    break;
                case 4:
                    ScannerDisplayEffect.clearScreen();
                    ScannerDisplayEffect.printHeader("Find Position");
                    findPositionFlow();
                    ScannerConsoleIO.pause(sc);
                    break;
                case 5:
                    ScannerDisplayEffect.clearScreen();
                    ScannerDisplayEffect.printHeader("Remove Patient");
                    removeByIDFlow();
                    ScannerConsoleIO.pause(sc);
                    break;
                case 6:
                    reportUI.start();
                    break;
                case 0:
                    System.out.println("Bye bye ;)");
                    break;
                default:
                    System.out.println("Invalid choice.");
                    ScannerConsoleIO.pause(sc);
            }
            
        } while (choice != 0);
    }
    
    /* ---------- FLOWS ---------- */
    
    private void registerFlow() {
        ScannerDisplayEffect.printSubheader("Enter Patient Details");
        
        String id = IDGenerator.next(IDType.PATIENT);
        System.out.println("Generated Patient ID: " + id);
        
        String name    = ScannerConsoleIO.readNonEmpty(sc, "Name       : ");
        String contact = ScannerConsoleIO.readNonEmpty(sc, "Contact No : ");
        String email   = ScannerConsoleIO.readNonEmpty(sc, "Email      : ");
    
        Gender gender = ScannerConsoleIO.readEnum(sc, "Gender", Gender.class, new String[]{"MALE","FEMALE"});
        
        int age = ScannerConsoleIO.readIntInRange(sc, "Age         : ", 0, 120);
        
        VisitType visitType = ScannerConsoleIO.readEnum(sc, "Visit Type", VisitType.class, new String []{"WALK_IN","APPOINTMENT"});
        LocalTime arrival = LocalTime.now();
        
        Patient p = new Patient(id, name, contact, email, gender, age, visitType, arrival);
        
        pm.registerPatient(p);
        ScannerDisplayEffect.printDivider();
        ScannerDisplayEffect.printSubheader("Registered Patient");
        System.out.println(p);
        System.out.println("Queue size is now: " + pm.getQueueSize());
        ScannerDisplayEffect.printDivider();
    }
    
    private void serveFlow() {
        if (pm.isEmpty()) {
            System.out.println("No patients in queue.");
            ScannerDisplayEffect.printDivider();
            return;
        }
        
        Patient served = pm.serveNextPatient();
        
        System.out.println(served == null ? "No patients in queue." :("Serving: " + served));
        System.out.println("Queue size is now: " + pm.getQueueSize());
        ScannerDisplayEffect.printDivider();
    }

    private void findPositionFlow() {
        String id = ScannerConsoleIO.readNonEmpty(sc, "Patient ID: ");
        int pos = pm.findPosition(id);
        
        System.out.println(pos < 0 ? "Patient not found." : ("Patient " + id + " is #" + pos + " in the queue."));
        ScannerDisplayEffect.printDivider();
    }

    private void removeByIDFlow() {
        String id = ScannerConsoleIO.readNonEmpty(sc, "Patient ID to remove: ");
        boolean removed = pm.removeByID(id);
        
        System.out.print(removed ? ("Removed patient " + id + ".") : "Patient not found.");
        System.out.println("Queue size is now: " + pm.getQueueSize());
        ScannerDisplayEffect.printDivider();
    }

    /* ---------- MAIN MENU ---------- */
    
    private void printMainMenu() {
        System.out.println("1. Register new patient");
        System.out.println("2. Serve new patient");
        System.out.println("3. View current queue");
        System.out.println("4. Find positon by ID");
        System.out.println("5. Remove patient by ID");
        System.out.println("6. Generate reports");
        System.out.println("0. Exit");
        System.out.println();
    }
}
