package boundary;

import boundary.StockMaintenanceUI;
import boundary.PharmacistUI;
import boundary.MedicineDispenserUI;

import control.StockMaintenance;
import control.MedicineDispenser;

import utility.JOptionPaneConsoleIO;

public class PharmacyModuleUI {
    private final StockMaintenance stock = new StockMaintenance();
    
    public void run() {
        int choice;
        do {
            choice = JOptionPaneConsoleIO.readOption(
                    "=== Pharmacy Module ===",
                    "Admin Control Panel",
                    new String[]{
                        "Pharmacist Management",
                        "Stock Maintenance",
                        "Medicine Dispensing",
                        "Back to Main Menu"
                    }
            );

            switch (choice) {
                case 0 ->
                    new PharmacistUI().run();
                case 1 ->
                    new StockMaintenanceUI(stock).run();
                case 2 ->
                    MedicineDispenserUI.run(new MedicineDispenser(stock));
                case 3 ->
                    JOptionPaneConsoleIO.showInfo("Exiting Stock Maintenance.");
                default ->
                    JOptionPaneConsoleIO.showError("Invalid option selected.");
            }
        } while (choice != 3);
    }
}
    

