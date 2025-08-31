package boundary;

import adt.LinkedQueue;
import adt.QueueInterface;
import control.MedicineDispenser;
import entity.Prescription;
import entity.PrescriptionItem;
import utility.JOptionPaneConsoleIO;

import java.util.Iterator;

public class MedicineDispenserUI {

    private final MedicineDispenser dispenser;
    private final LinkedQueue<Prescription> prescriptionQueue;
    private final LinkedQueue<String> dispensingLabels = new LinkedQueue<>();

    public MedicineDispenserUI(MedicineDispenser dispenser, QueueInterface<Prescription> prescriptionQueue) {
        this.dispenser = dispenser;
        this.prescriptionQueue = (LinkedQueue<Prescription>) prescriptionQueue;
    }

    public void run() {
        int choice;

        do {
            choice = JOptionPaneConsoleIO.readOption(
                    "=== MEDICINE DISPENSING MENU ===",
                    "SELECT AN OPTION",
                    new String[]{
                        "Dispense Prescription",
                        "View Daily Dispensing Labels",
                        "View Audit Trail",
                        "Exit"
                    }
            );
            switch (choice) {
                case 0 ->
                    dispensePrescription();
                case 1 ->
                    viewDispensingLabels();
                case 2 ->
                    viewAuditTrail();
                case 3 ->
                    JOptionPaneConsoleIO.showInfo("Exiting Medicine Dispensing.");
                default ->
                    JOptionPaneConsoleIO.showError("Invalid option selected.");
            }
        } while (choice != 3);
    }

    private void dispensePrescription() {
        if (prescriptionQueue.isEmpty()) {
            JOptionPaneConsoleIO.showError("No prescriptions available.");
            return;
        }

        // Prepare dropdown options
        Prescription[] prescriptions = prescriptionQueue.toArray(new Prescription[0]);
        String[] options = new String[prescriptions.length];
        for (int i = 0; i < prescriptions.length; i++) {
            options[i] = prescriptions[i].getPrescriptionID();
        }

        // Dropdown selection
        String selectedValue = JOptionPaneConsoleIO.readDropdown("Select a prescription to dispense:", options);
        if (selectedValue == null) {
            return;
        }

        // Find selected prescription
        Prescription current = null;
        for (Prescription p : prescriptions) {
            String value = p.getPrescriptionID();
            if (value.equals(selectedValue)) {
                current = p;
                break;
            }
        }

        if (current == null) {
            JOptionPaneConsoleIO.showError("Selected prescription not found.");
            return;
        }

        // Already dispensed check
        if (current.getStatus().isDispensed()) {
            JOptionPaneConsoleIO.showError("⚠ This prescription has already been dispensed.");
            return;
        }

        // Confirm
        StringBuilder sb = new StringBuilder();
        sb.append(current.toString())
                .append("\n\nConfirm Dispensing?");
        boolean confirmed = JOptionPaneConsoleIO.confirmDialog(sb.toString(), "Dispense Confirmation");
        if (!confirmed) {
            return;
        }

        // Clinical validation
        if (!dispenser.clinicalCheck(current)) {
            JOptionPaneConsoleIO.showError("⚠ Clinical check failed. Dispensing aborted.");
            return;
        }

        // Stock check
        if (!dispenser.isDispensable(current)) {
            StringBuilder error = new StringBuilder("❌ Not enough stock to dispense:\n\n");
            Iterator<PrescriptionItem> iterator = current.iterator();
            while (iterator.hasNext()) {
                PrescriptionItem item = iterator.next();
                if (!dispenser.hasStockFor(item)) {
                    int shortage = dispenser.calculateShortage(item.getMedicineName(), item.getPrescribedQty());
                    error.append("- ").append(item.getMedicineName())
                            .append(": short by ").append(shortage).append(" units\n");
                }
            }
            JOptionPaneConsoleIO.showError(error.toString());
            return;
        }

        // Dispense
        boolean success = dispenser.dispense(current);
        if (success) {
            // Ask for pharmacist name
            String pharmacistName = JOptionPaneConsoleIO.readNonEmpty("Enter pharmacist name:");

            // Generate label with pharmacist name
            String label = dispenser.generateDispensingLabel(current, pharmacistName);
            dispensingLabels.enqueue(label);

            // Remove prescription from queue
            LinkedQueue<Prescription> tempQueue = new LinkedQueue<>();
            for (Prescription p : prescriptionQueue) {
                if (!p.getPrescriptionID().equals(current.getPrescriptionID())) {
                    tempQueue.enqueue(p);
                }
            }
            prescriptionQueue.clear();
            for (Prescription p : tempQueue) {
                prescriptionQueue.enqueue(p);
            }

            JOptionPaneConsoleIO.showInfo("✓ Dispensing successful!");
        } else {
            JOptionPaneConsoleIO.showError("⚠ Dispensing failed. Already dispensed or insufficient stock.");
        }
    }

    private void viewDispensingLabels() {
        if (dispensingLabels.isEmpty()) {
            JOptionPaneConsoleIO.showError("No dispensing labels available.");
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (String label : dispensingLabels) {
            sb.append(label).append("\n").append("-".repeat(40)).append("\n");
        }

        JOptionPaneConsoleIO.showMonospaced("Daily Dispensing Labels", sb.toString());
    }

    private void viewAuditTrail() {
        String[] audit = dispenser.getAuditTrail();
        if (audit == null || audit.length == 0) {
            JOptionPaneConsoleIO.showError("No audit trail available.");
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (String entry : audit) {
            sb.append(entry).append("\n");
        }
        JOptionPaneConsoleIO.showMonospaced("Dispense Audit Log", sb.toString());
    }
}
