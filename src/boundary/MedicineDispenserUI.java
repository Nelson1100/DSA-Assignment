package boundary;

import adt.LinkedQueue;
import adt.QueueInterface;
import control.MedicineDispenser;
import control.PharmacistManagement;
import entity.*;
import utility.JOptionPaneConsoleIO;

import java.util.Iterator;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MedicineDispenserUI {

    private final MedicineDispenser dispenser;
    private final PharmacistManagement pharmacistMgmt;
    private final LinkedQueue<Prescription> prescriptionQueue;
    private final LinkedQueue<String> dispensingLabels = new LinkedQueue<>();

        
    public MedicineDispenserUI(MedicineDispenser dispenser, PharmacistManagement pharmacistMgmt, QueueInterface<Prescription> prescriptionQueue) {
        this.dispenser = dispenser;
        this.pharmacistMgmt = pharmacistMgmt;
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
            if (p.getPrescriptionID().equals(selectedValue)) {
                current = p;
                break;
            }
        }

        if (current == null) {
            JOptionPaneConsoleIO.showError("Selected prescription not found.");
            return;
        }

        if (current.getStatus().isDispensed()) {
            JOptionPaneConsoleIO.showError("⚠ This prescription has already been dispensed.");
            return;
        }

        // Confirm
        StringBuilder sb = new StringBuilder();
        sb.append(current.toString()).append("\n\nConfirm Dispensing?");
        boolean confirmed = JOptionPaneConsoleIO.confirmDialog(sb.toString(), "Dispense Confirmation");
        if (!confirmed) {
            return;
        }

        if (!dispenser.clinicalCheck(current)) {
            dispenser.dispense(current);  // simulate failure record insertion
            String reason = current.getRejectionReason();
            JOptionPaneConsoleIO.showError("⚠ Clinical check failed: " + (reason != null ? reason : "Unknown reason") + ". Dispensing aborted.");
            return;
        }

        if (!dispenser.isDispensable(current)) {
            StringBuilder error = new StringBuilder("❌ Not enough stock to dispense:\n\n");
            for (PrescriptionItem item : current) {
                if (!dispenser.hasStockFor(item)) {
                    int shortage = dispenser.calculateShortage(item.getMedicineName(), item.getPrescribedQty());
                    error.append("- ").append(item.getMedicineName()).append(": short by ").append(shortage).append(" units\n");
                }
            }
            JOptionPaneConsoleIO.showError(error.toString());
            return;
        }

        boolean success = dispenser.dispense(current);
        if (success) {
            DispensedRecord target = null;
            for (DispensedRecord r : dispenser.getRecordLog()) {
                if (r.getPrescriptionID().equals(current.getPrescriptionID())
                        && r.getPharmacistName() == null
                        && r.isDispensed()) {
                    target = r;
                    break;
                }
            }

            if (target != null) {
                Pharmacist[] pharmacists = (pharmacistMgmt != null) ? pharmacistMgmt.toArray() : null;
                if (pharmacists == null || pharmacists.length == 0) {
                    JOptionPaneConsoleIO.showError("⚠ No pharmacists available in the system.");
                    return;
                }

                String[] pharmacistOptions = new String[pharmacists.length];
                for (int i = 0; i < pharmacists.length; i++) {
                    pharmacistOptions[i] = pharmacists[i].getPharmacistID() + " - " + pharmacists[i].getPharmacistName();
                }

                String picked = JOptionPaneConsoleIO.readDropdown("Select dispensing pharmacist:", pharmacistOptions);
                if (picked == null) {
                    JOptionPaneConsoleIO.showError("⚠ Dispensing cancelled. No pharmacist selected.");
                    return;
                }

                String pharmacistName = picked.substring(picked.indexOf(" - ") + 3);
                target.setPharmacistName(pharmacistName); // ✅ FIXED: Set name

                String label = dispenser.generateDispensingLabel(current, pharmacistName);
                dispensingLabels.enqueue(label);

                // Remove from queue
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
                JOptionPaneConsoleIO.showError("⚠ Dispensing failed. No valid record found.");
            }
        } else {
            JOptionPaneConsoleIO.showError("⚠ Dispensing failed. Already dispensed or insufficient stock.");
        }
    }


    public void viewDispensingLabels() {
        StringBuilder sb = new StringBuilder();
        final int WIDTH = 60;

        sb.append("=".repeat(WIDTH)).append("\n");
        sb.append(String.format("%" + ((WIDTH + 20) / 2) + "s\n", "ALL DISPENSING LABELS"));
        sb.append("=".repeat(WIDTH)).append("\n\n");

        boolean any = false;
        LocalDate today = LocalDate.now();

        for (DispensedRecord record : dispenser.getRecordLog()) {
            if (record.getTimestamp().toLocalDate().equals(today) && record.isDispensed()) {
                Prescription p = record.getPrescription(); // ✅ Use stored prescription

                if (p != null) {
                    sb.append(dispenser.generateDispensingLabel(p, record.getPharmacistName()));
                    sb.append("\n\n");
                    any = true;
                } 
            }
        }

        if (!any) {
            sb.append("No dispensed prescriptions found.\n");
            sb.append("=".repeat(WIDTH)).append("\n");
        }

        JOptionPaneConsoleIO.showMonospaced("Dispensing Labels", sb.toString());
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
