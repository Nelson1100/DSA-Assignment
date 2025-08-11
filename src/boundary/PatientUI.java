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
        
        DisplayEffect.clearScreen();
        DisplayEffect.printHeader("Patient Management");
        printMainMenu();
    }
    
    /* ---------- FLOWS ---------- */
    
    private void registerFlow() {
        DisplayEffect.printSubheader("Enter Patient Details");
    }

    /* ---------- MAIN MENU ---------- */
    
    private void printMainMenu() {
        System.out.println("1. Register new patient");
        System.out.println("2. Serve new patient");
        System.out.println("3. View current queue");
        System.out.println("4. Find positon by ID");
        System.out.println("5. Remove patient by ID");
        System.out.println("6. Reports...");
        System.out.println();
        System.out.println("0. Exit");
    }
}
