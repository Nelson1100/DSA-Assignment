package control;

import adt.LinkedQueue;
import entity.*;
import entity.Instruction;
import utility.IDGenerator;
import utility.IDType;
import utility.JOptionPaneConsoleIO;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;

public class MedicineDispenser {

    private final StockMaintenance stock;
    private final LinkedQueue<DispensedRecord> recordLog;
    private final LinkedQueue<String> auditLog;
    private final LinkedQueue<Prescription> prescriptionQueue;

    public MedicineDispenser(StockMaintenance stock, LinkedQueue<Prescription> prescriptionQueue) {
        this.stock = stock;
        this.recordLog = new LinkedQueue<>();
        this.auditLog = new LinkedQueue<>();
        this.prescriptionQueue = prescriptionQueue;
    }

    public boolean dispense(Prescription p) {
        if (p == null || p.getItems() == null || !p.getItems().iterator().hasNext() || p.getStatus().isDispensed()) {
            JOptionPaneConsoleIO.showError("Invalid or already dispensed prescription.");
            return false;
        }

        if (!clinicalCheck(p)) {
            String recordID = IDGenerator.next(IDType.DISPENSEDRECORD);
            DispensedRecord record = new DispensedRecord(
                    recordID, p.getPrescriptionID(), p.getPatientID(), p.getDoctorID(),
                    LocalDateTime.now(), new MedicineName[0], new int[0], false, p.getRejectionReason(), null
            );
            recordLog.enqueue(record);

            String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a"));
            auditLog.enqueue("[" + now + "] [FAILED] " + p.getPrescriptionID() + " — " + p.getRejectionReason());

            return false;
        }


        int size = 0;
        for (PrescriptionItem i : p.getItems()) {
            size++;
        }

        MedicineName[] meds = new MedicineName[size];
        int[] qtys = new int[size];

        int i = 0;
        for (PrescriptionItem item : p.getItems()) {
            meds[i] = item.getMedicineName();
            qtys[i] = item.getPrescribedQty();
            stock.deduct(item.getMedicineName(), item.getPrescribedQty());
            i++;
        }

        p.setStatusDispensed();
        String recordID = IDGenerator.next(IDType.DISPENSEDRECORD);
        DispensedRecord record = new DispensedRecord(
                recordID, p.getPrescriptionID(), p.getPatientID(), p.getDoctorID(),
                LocalDateTime.now(), meds, qtys, true, null, p
        );
        recordLog.enqueue(record);
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a"));
        auditLog.enqueue("[" + now + "] [DISPENSED] " + p.getPrescriptionID());

        return true;

    }

    public boolean clinicalCheck(Prescription p) {
        if (p == null || p.getItems() == null || p.getItems().isEmpty()) {
            p.setRejectionReason("Invalid prescription: No items found.");
            return false;
        }

        for (PrescriptionItem item : p.getItems()) {
            MedicineName name = item.getMedicineName();
            int qty = item.getPrescribedQty();

            if (!hasStockFor(item)) {
                p.setRejectionReason("expired-only/insufficient stock");
                return false;
            }

            if (!validateDoseRange(name, qty)) {
                p.setRejectionReason("invalid dosage");
                return false;
            }
        }
        return true;
    }

    public boolean isDispensable(Prescription p) {
        for (PrescriptionItem item : p.getItems()) {
            if (!hasStockFor(item)) {
                return false;
            }
        }
        return true;
    }

    public boolean hasStockFor(PrescriptionItem item) {
        int required = item.getPrescribedQty();
        int available = stock.totalBalance(item.getMedicineName());
        return available >= required;
    }

    public int calculateShortage(MedicineName name, int qty) {
        int available = stock.totalBalance(name);
        return Math.max(0, qty - available);
    }

    public boolean validateDoseRange(MedicineName name, int qty) {
        return qty >= name.getMinDose() && qty <= name.getMaxDose();
    }

    public String generateDispensingLabel(Prescription p, String pharmacistName) {
        StringBuilder sb = new StringBuilder();
        final int WIDTH = 60;

        sb.append("=".repeat(WIDTH)).append("\n");
        sb.append(String.format("%" + ((WIDTH + 22) / 2) + "s\n", "MEDICATION DISPENSING LABEL"));
        sb.append("=".repeat(WIDTH)).append("\n\n");

        sb.append(String.format("%-18s: %s\n", "Prescription ID", p.getPrescriptionID()));
        sb.append(String.format("%-18s: %s\n", "Patient ID", p.getPatientID()));
        sb.append(String.format("%-18s: %s\n", "Doctor ID", p.getDoctorID()));
        sb.append(String.format("%-18s: %s\n", "Pharmacist", pharmacistName));
        sb.append("\n" + "-".repeat(WIDTH) + "\n");
        sb.append(String.format("%-20s%-10s%s\n", "Medicine", "Qty", "Instructions"));
        sb.append("-".repeat(WIDTH)).append("\n");

        for (PrescriptionItem item : p.getItems()) {
            String med = item.getMedicineName().toString();
            int qty = item.getPrescribedQty();
            String instr = item.getInstructions().getLabel();
            sb.append(String.format("%-20s%-10d%s\n", med, qty, instr));
        }

        sb.append("-".repeat(WIDTH)).append("\n");
        sb.append("Dispensed At: ")
                .append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a")))
                .append("\n");
        sb.append("=".repeat(WIDTH)).append("\n");

        return sb.toString();
    }

    public String[] getAuditTrail() {
        int size = auditLog.size();
        String[] trail = new String[size];
        int i = 0;
        for (String entry : auditLog) {
            trail[i++] = entry;
        }
        return trail;
    }

    public LinkedQueue<DispensedRecord> getRecordLog() {
        return recordLog;
    }

    public LinkedQueue<String> getAuditLog() {
        return auditLog;
    }
    
    public Prescription findPrescriptionById(String id) {
        if (id == null) {
            return null;
        }
        for (Prescription p : prescriptionQueue) {
            if (p != null && id.equals(p.getPrescriptionID())) {
                return p;
            }
        }
        return null;
    }
}
