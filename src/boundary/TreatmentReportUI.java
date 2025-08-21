package boundary;

import control.TreatmentReport;
import java.time.LocalDate;
import java.util.Scanner;

public class TreatmentReportUI {

    private final TreatmentReport control;

    public TreatmentReportUI(TreatmentReport control) {
        this.control = control;
    }

    public void show(Scanner sc) {
        boolean back = false;
        while (!back) {
            System.out.println("\n=== Reports ===");
            System.out.println("1. Waiting Summary");
            System.out.println("2. Daily Treatment Summary");
            System.out.println("0. Back");

            int choice = readIntInRange(sc, "Choice: ", 0, 2);
            switch (choice) {
                case 1:
                    System.out.println(control.reportWaitingSummary());
                    pause(sc);
                    break;
                case 2:
                    LocalDate date = readDate(sc, "Enter date (YYYY-MM-DD): ");
                    System.out.println(control.reportDailyTreatmentSummary(date));
                    pause(sc);
                    break;
                case 0:
                    back = true;
                    break;
            }
        }
    }

    // Local helper methods (no need for ConsoleIO changes)
    private int readIntInRange(Scanner sc, String prompt, int min, int max) {
        int num;
        while (true) {
            System.out.print(prompt);
            if (sc.hasNextInt()) {
                num = sc.nextInt();
                sc.nextLine(); // consume newline
                if (num >= min && num <= max) {
                    return num;
                }
            } else {
                sc.nextLine(); // consume invalid
            }
            System.out.println("Invalid choice. Please enter between " + min + " and " + max + ".");
        }
    }

    private LocalDate readDate(Scanner sc, String prompt) {
        LocalDate date = null;
        while (date == null) {
            System.out.print(prompt);
            String raw = sc.nextLine().trim();
            try {
                date = LocalDate.parse(raw);
            } catch (Exception e) {
                System.out.println("Invalid date format. Please use YYYY-MM-DD.");
            }
        }
        return date;
    }

    private void pause(Scanner sc) {
        System.out.println("Press Enter to continue...");
        sc.nextLine();
    }
}
