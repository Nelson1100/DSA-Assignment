package boundary;

import control.PatientReportGenerator;
import entity.Patient;
import utility.ScannerConsoleIO;
import utility.ScannerDisplayEffect;

import java.time.LocalTime;
import java.util.Scanner;

public class PatientReportUI {
    private final PatientReportGenerator report;
    private final Scanner sc;
    
    public PatientReportUI(PatientReportGenerator report) {
        this(report, new Scanner(System.in));
    }
    
    public PatientReportUI(PatientReportGenerator report, Scanner sharedScanner) {
        this.report = report;
        this.sc = sharedScanner;
    }
    
    public void start() {
        int choice;
        
        do {
            ScannerDisplayEffect.clearScreen();
            ScannerDisplayEffect.printHeader("Patient Reports");
            printMenu();
            choice = ScannerConsoleIO.readInt(sc, "Enter your choice: ");
            
            switch (choice) {
                case 1:
                    ScannerDisplayEffect.clearScreen();
                    ScannerDisplayEffect.printHeader("Visit Type Summary");
                    showVisitTypeSummary();
                    ScannerConsoleIO.pause(sc);
                    break;
                case 2:
                    ScannerDisplayEffect.clearScreen();
                    ScannerDisplayEffect.printHeader("Average Waiting Time");
                    showAverageWaitNow();
                    ScannerConsoleIO.pause(sc);
                    break;
                case 3:
                    ScannerDisplayEffect.clearScreen();
                    ScannerDisplayEffect.printHeader("Gender Breakdown");
                    showGenderBreakdown();
                    ScannerConsoleIO.pause(sc);
                    break;
                case 4:
                    ScannerDisplayEffect.clearScreen();
                    ScannerDisplayEffect.printHeader("Preview Next Patients");
                    previewNextN();
                    ScannerConsoleIO.pause(sc);
                    break;
                case 5:
                    ScannerDisplayEffect.clearScreen();
                    ScannerDisplayEffect.printHeader("All Reports");
                    showAllReports();
                    ScannerConsoleIO.pause(sc);
                    break;
                case 0:
                    System.out.println("Back to main menu...");
                    break;
                default:
                    System.out.println("Invalid choice");
                    ScannerConsoleIO.pause(sc);
            }
        } while (choice != 0);
    }
    
    /* ---------- Screens ---------- */
    
    private void showVisitTypeSummary() {
        System.out.println(report.queueHeadline());
        System.out.println();
        ScannerDisplayEffect.printSubheader("Visit Type Summary");
        System.out.println(report.summaryByVisitType());
        ScannerDisplayEffect.printDivider();
    }

    private void showAverageWaitNow() {
        System.out.println(report.queueHeadline());
        System.out.println();
        ScannerDisplayEffect.printSubheader("Average Waiting Time");
        System.out.println(report.averageWait(LocalTime.now()));
        ScannerDisplayEffect.printDivider();
    }

    private void showGenderBreakdown() {
        System.out.println(report.queueHeadline());
        System.out.println();
        ScannerDisplayEffect.printSubheader("Gender Breakdown");
        System.out.println(report.genderBreakdown());
        ScannerDisplayEffect.printDivider();
    }

    private void previewNextN() {
        int n = ScannerConsoleIO.readInt(sc, "Preview how many patients? ");
        System.out.println();
        ScannerDisplayEffect.printSubheader("Next Patients");
        Patient[] next = report.previewNext(n);
        if (next.length == 0) {
            System.out.println("(none)");
        } else {
            for (int i = 0; i < next.length; i++) {
                System.out.printf("%d) %s%n", i + 1, next[i]);
            }
        }
        ScannerDisplayEffect.printDivider();
    }

    private void showAllReports() {
        System.out.println(report.queueHeadline());
        System.out.println();

        ScannerDisplayEffect.printSubheader("Visit Type Summary");
        System.out.println(report.summaryByVisitType());
        ScannerDisplayEffect.printDivider();
        System.out.println();

        ScannerDisplayEffect.printSubheader("Average Waiting Time");
        System.out.println(report.averageWait(LocalTime.now()));
        ScannerDisplayEffect.printDivider();
        System.out.println();

        ScannerDisplayEffect.printSubheader("Gender Breakdown");
        System.out.println(report.genderBreakdown());
        ScannerDisplayEffect.printDivider();
    }
    
    /* ---------- Helpers ---------- */
    
    private void printMenu() {
        System.out.println("1. Visit Type Summary");
        System.out.println("2. Average Waiting Time (now)");
        System.out.println("3. Gender Breakdown");
        System.out.println("4. Preview Next N Patients");
        System.out.println("5. Print All Reports");
        System.out.println("0. Back");
        System.out.println();
    }
}
