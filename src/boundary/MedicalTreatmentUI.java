package boundary;

import adt.LinkedQueue;
import control.MedicalTreatmentManagement;
import entity.Patient;
import entity.TreatmentRecord;
import utility.ConsoleIO;
import utility.IDGenerator;
import utility.IDType;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Scanner;

public class MedicalTreatmentUI {

    private final MedicalTreatmentManagement control;

    public MedicalTreatmentUI(MedicalTreatmentManagement control) {
        this.control = control;
    }

    public void show(Scanner sc) {
        control.initializePatients(); 
        boolean back = false;
        while (!back) {
            System.out.println("\n=== Medical Treatment Management ===");
            System.out.println("1. Add new patient to waiting queue");
            System.out.println("2. Serve next patient (record diagnosis & treatment)");
            System.out.println("3. Add treatment by patient ID");
            System.out.println("4. View patient history");
            System.out.println("0. Back / Exit");
            
            // validation
            int choice = ConsoleIO.readIntInRange(sc, "Choice: ", 0, 4);
            switch (choice) {
                case 1: addPatientFlow(sc); break;
                case 2: serveNextFlow(sc); break;
                case 3: addTreatmentByIdFlow(sc); break;
                case 4: viewHistoryFlow(sc); break;
                case 0: back = true; break;
            }
        }
    }

    private void addPatientFlow(Scanner sc) {
        String id = IDGenerator.next(IDType.PATIENT);
        System.out.println("Generated Patient ID: " + id);

        String name = ConsoleIO.readNonEmpty(sc, "Name: ");
        String contact = ConsoleIO.readNonEmpty(sc, "Contact No: ");
        String email = ConsoleIO.readNonEmpty(sc, "Email: ");
        // Use ConsoleIO to read enums (it expects allowed names array)
        entity.Gender gender = ConsoleIO.readEnum(sc, "Gender", entity.Gender.class, new String[]{"MALE","FEMALE","OTHER"});
        int age = ConsoleIO.readIntInRange(sc, "Age: ", 0, 150);
        entity.VisitType vt = ConsoleIO.readEnum(sc, "Visit Type", entity.VisitType.class, new String[]{"WALK_IN","APPOINTMENT"});
        LocalTime arrival = LocalTime.now();

        Patient p = new Patient(id, name, contact, email, gender, age, vt, arrival);
        control.addPatient(p);
        System.out.println("Added: " + p.getPatientName() + " (" + p.getPatientID() + ")");
    }

    private void serveNextFlow(Scanner sc) {
        if (control.isWaitingEmpty()) {
            System.out.println("No patients waiting.");
            return;
        }
        String diag = ConsoleIO.readNonEmpty(sc, "Diagnosis: ");
        String treat = ConsoleIO.readNonEmpty(sc, "Treatment: ");
        
        entity.Patient served = control.serveNextPatient(diag, treat);
        if (served != null) {
            
            TreatmentRecord rec = new TreatmentRecord(IDGenerator.next(IDType.TREATMENT),
                    served.getPatientID(), diag, treat, LocalDateTime.now());
            control.addTreatmentById(served.getPatientID(), rec);
            System.out.println("Served and recorded: " + served.getPatientName() + " (" + served.getPatientID() + ")");
        } else {
            System.out.println("No patients waiting.");
        }
    }

    private void addTreatmentByIdFlow(Scanner sc) {
        String pid = ConsoleIO.readNonEmpty(sc, "Patient ID: ");
        String diag = ConsoleIO.readNonEmpty(sc, "Diagnosis: ");
        String treat = ConsoleIO.readNonEmpty(sc, "Treatment: ");
        TreatmentRecord rec = new TreatmentRecord(IDGenerator.next(IDType.TREATMENT), pid, diag, treat, LocalDateTime.now());
        boolean ok = control.addTreatmentById(pid, rec);
        System.out.println(ok ? "Treatment recorded." : "Patient not found.");
    }

    private void viewHistoryFlow(Scanner sc) {
        String pid = ConsoleIO.readNonEmpty(sc, "Patient ID: ");
        var ph = control.findHistory(pid);
        if (ph == null) {
            System.out.println("Patient not found.");
            return;
        }
        System.out.println(ph.getPatient());
        System.out.println("Treatment History:");
        var q = ph.getRecords();
        if (q.isEmpty()) {
            System.out.println("  (No treatment history)");
            return;
        }
        var it = ((LinkedQueue<TreatmentRecord>) q).getIterator();
        while (it.hasNext()) {
            System.out.println("  - " + it.getNext());
        }
    }
}
