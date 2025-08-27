package boundary;

import control.MedicineDispenser;
import control.StockMaintenance;
import entity.MedicineName;
import entity.Prescription;
import entity.PrescriptionItem;
import entity.StockBatch;
import utility.JOptionPaneConsoleIO;
import utility.Validation;

public class MedicineDispenserUI {

    private static final Validation validate = new Validation();

    public static void run(MedicineDispenser dispenser) {
        StockMaintenance stock = getStock(dispenser); // access shared stock
        if (stock == null) {
            JOptionPaneConsoleIO.showError("StockMaintenance not accessible.");
            return;
        }

        JOptionPaneConsoleIO.showInfo("=== DISPENSING MEDICINE ===");

        String input = JOptionPaneConsoleIO.readNonEmpty("Enter number of medicines to dispense:");
        if (!validate.validNumber(input, 1, 100)) {
            JOptionPaneConsoleIO.showError("Invalid number. Please enter a positive integer.");
            return;
        }

        int count = Integer.parseInt(input);
        Prescription prescription = new Prescription("PS0001", "DR0001");

        for (int i = 0; i < count; i++) {
            MedicineName selected = null;
            StockBatch testBatch = null;

            // Keep prompting until medicine with available stock is selected
            do {
                selected = JOptionPaneConsoleIO.readEnum(
                        "Select medicine #" + (i + 1),
                        MedicineName.class,
                        getMedicineNameOptions()
                );

                if (selected == null) {
                    JOptionPaneConsoleIO.showError("Selection cancelled.");
                    return;
                }

                testBatch = stock.earliestBatch(selected);
                if (testBatch == null) {
                    JOptionPaneConsoleIO.showError("No available stock for " + selected.name() + ". Please choose another.");
                }

            } while (testBatch == null);

            String qtyStr = JOptionPaneConsoleIO.readNonEmpty("Enter quantity for " + selected.name() + ":");
            if (!validate.validNumber(qtyStr, 1, Integer.MAX_VALUE)) {
                JOptionPaneConsoleIO.showError("Invalid quantity entered.");
                return;
            }

            int qty = Integer.parseInt(qtyStr);
            new PrescriptionItem(selected, qty, prescription.getPrescriptionID());
        }

        // Call dispenser to perform FEFO dispensing
        boolean success = dispenser.dispense(prescription);

        if (success) {
            JOptionPaneConsoleIO.showInfo("✅ All medicines dispensed successfully.");
        } else {
            JOptionPaneConsoleIO.showError("⚠️ Some medicines could not be fully dispensed due to insufficient stock.");
        }
    }

    // Extract StockMaintenance from the dispenser object (if accessible)
    private static StockMaintenance getStock(MedicineDispenser dispenser) {
        try {
            java.lang.reflect.Field stockField = MedicineDispenser.class.getDeclaredField("stockMaintenance");
            stockField.setAccessible(true);
            return (StockMaintenance) stockField.get(dispenser);
        } catch (Exception e) {
            return null;
        }
    }

    // Helper to convert MedicineName enum to string options
    private static String[] getMedicineNameOptions() {
        MedicineName[] names = MedicineName.values();
        String[] options = new String[names.length];
        for (int i = 0; i < names.length; i++) {
            options[i] = names[i].name();
        }
        return options;
    }
}
