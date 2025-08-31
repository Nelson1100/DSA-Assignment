package boundary;

import adt.QueueInterface;
import adt.LinkedQueue;
import control.MedicineDispenser;
import control.PharmacistReportGenerator;
import control.StockMaintenance;
import dao.PrescriptionInitializer;
import dao.StockInitializer;
import entity.DispensedRecord;
import entity.Prescription;
import utility.JOptionPaneConsoleIO;

public class PharmacyModuleUI {

    StockMaintenance stock = new StockMaintenance();
    private LinkedQueue<Prescription> prescriptionQueue = new LinkedQueue<>();
    MedicineDispenser dispenser = new MedicineDispenser(stock);

    
    public void run() {
        StockInitializer.initialize(stock);
        PrescriptionInitializer.initialize(prescriptionQueue, dispenser);
        
        int choice;
        do {
            choice = JOptionPaneConsoleIO.readOption(
                    "=== Pharmacy Module ===",
                    "Admin Control Panel",
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
                    new PharmacistUI().run();
                case 1 ->
                    new StockMaintenanceUI(stock).run();
                case 2 ->
                    new MedicineDispenserUI(dispenser, prescriptionQueue).run();
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
