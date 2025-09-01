package boundary;

import adt.LinkedQueue;
import control.MedicineDispenser;
import control.PharmacistReportGenerator;
import control.StockMaintenance;
import control.PharmacistManagement;
import dao.*;
import entity.Prescription;
import utility.JOptionPaneConsoleIO;

public class PharmacyModuleUI {

    StockMaintenance stock = new StockMaintenance();
    private final LinkedQueue<Prescription> prescriptionQueue = new LinkedQueue<>();
    private final PharmacistManagement pharmacistManagement = new PharmacistManagement();
    private final MedicineDispenser dispenser = new MedicineDispenser(stock, prescriptionQueue);

    
    public void run() {
        StockInitializer.initialize(stock);
        PrescriptionInitializer.initialize(prescriptionQueue, dispenser);
        PharmacistInitializer.initialize(pharmacistManagement);
        
        int choice;
        do {
            choice = JOptionPaneConsoleIO.readOption(
                    "=== Pharmacy Module ===",
                    "Phamacist Management Module",
                    new String[]{
                        "Pharmacist Management",
                        "Stock Maintenance",
                        "Medicine Dispensing",
                        "Report",
                        "Back to Main Menu"
                    }
            );

            switch (choice) {
                case 0 ->
                    new PharmacistUI(pharmacistManagement).run();
                case 1 ->
                    new StockMaintenanceUI(stock).run();
                case 2 ->
                    new MedicineDispenserUI(dispenser, pharmacistManagement, prescriptionQueue).run();
                case 3 -> {
                    int reportChoice = JOptionPaneConsoleIO.readOption(
                            "=== Pharmacy Report Menu ===",
                            "Select a report to generate:",
                            new String[]{
                                "Dispensing Activity Summary Report",
                                "Inventory and Demand Forecasting Report",
                                "Back"
                            }
                    );

                    switch (reportChoice) {
                        case 0 ->
                            PharmacistReportGenerator.generateDispensingSummaryReport(dispenser.getRecordLog());
                        case 1 ->
                            PharmacistReportGenerator.generateInventoryForecastReport(stock);
                        case 2 ->
                            JOptionPaneConsoleIO.showInfo("Returning to Pharmacy Menu...");
                        default ->
                            JOptionPaneConsoleIO.showError("Invalid report option.");
                    }
                }
                case 4 ->
                    JOptionPaneConsoleIO.showInfo("Exiting Pharmacy Module...");
                default ->
                    JOptionPaneConsoleIO.showError("Invalid option selected.");
            }

        } while (choice != 4);
    }
}
