package boundary;

import control.PatientReportGenerator;
import entity.Patient;
import utility.ConsoleIO;
import utility.DisplayEffect;

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
            DisplayEffect.clearScreen();
            DisplayEffect.printHeader("Patient Reports");
            printMenu();
            choice = ConsoleIO.readInt(sc, "Choice: ");
            
            switch (choice) {
                case 1:
                    DisplayEffect.clearScreen();
                    DisplayEffect.printHeader("Visit Type Summary");
                    showVisitTypeSummary();
                    ConsoleIO.pause(sc);
                case 2:
                    DisplayEffect.clearScreen();
                    DisplayEffect.printHeader("Average Waiting Time");
                    showAverageWaitNow();
                    ConsoleIO.pause(sc);
                case 3:
                    DisplayEffect.clearScreen();
                    DisplayEffect.printHeader("Gender Breakdown");
                    showGenderBreakdown();
                    ConsoleIO.pause(sc);
                case 4:
                    DisplayEffect.clearScreen();
                    DisplayEffect.printHeader("Preview Next Patients");
                    previewNextN();
                    ConsoleIO.pause(sc);
                case 5:
                    DisplayEffect.clearScreen();
                    DisplayEffect.printHeader("All Reports");
                    showAllReports();
                    ConsoleIO.pause(sc);
                case 0:
                    System.out.println("Back to main menu...");
                default:
                    System.out.println("Invalid choice");
                    ConsoleIO.pause(sc);
            }
        } while (choice != 0);
    }
    
    /* ---------- Screens ---------- */
    
    private void showVisitTypeSummary() {
        System.out.println(report.queueHeadline());
        System.out.println();
        DisplayEffect.printSubheader("Visit Type Summary");
        System.out.println(report.summaryByVisitType());
        DisplayEffect.printDivider();
    }

    private void showAverageWaitNow() {
        System.out.println(report.queueHeadline());
        System.out.println();
        DisplayEffect.printSubheader("Average Waiting Time");
        System.out.println(report.averageWait(LocalTime.now()));
        DisplayEffect.printDivider();
    }

    private void showGenderBreakdown() {
        System.out.println(report.queueHeadline());
        System.out.println();
        DisplayEffect.printSubheader("Gender Breakdown");
        System.out.println(report.genderBreakdown());
        DisplayEffect.printDivider();
    }

    private void previewNextN() {
        int n = ConsoleIO.readInt(sc, "Preview how many patients? ");
        System.out.println();
        DisplayEffect.printSubheader("Next Patients");
        Patient[] next = report.previewNext(n);
        if (next.length == 0) {
            System.out.println("(none)");
        } else {
            for (int i = 0; i < next.length; i++) {
                System.out.printf("%d) %s%n", i + 1, next[i]);
            }
        }
        DisplayEffect.printDivider();
    }

    private void showAllReports() {
        System.out.println(report.queueHeadline());
        System.out.println();

        DisplayEffect.printSubheader("Visit Type Summary");
        System.out.println(report.summaryByVisitType());
        DisplayEffect.printDivider();
        System.out.println();

        DisplayEffect.printSubheader("Average Waiting Time");
        System.out.println(report.averageWait(LocalTime.now()));
        DisplayEffect.printDivider();
        System.out.println();

        DisplayEffect.printSubheader("Gender Breakdown");
        System.out.println(report.genderBreakdown());
        DisplayEffect.printDivider();
    }
    
    /* ---------- Helpers ---------- */
    
    private void printMenu() {
        System.out.println("1. Visit Type Summary");
        System.out.println("2. Average Waiting Time (now)");
        System.out.println("3. Gender Breakdown");
        System.out.println("4. Preview Next N Patients");
        System.out.println("5. Print All Reports");
        System.out.println();
        System.out.println("0. Back");
    }
}
