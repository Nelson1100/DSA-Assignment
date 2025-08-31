package entity;

import java.time.LocalDateTime;

public class DispensedRecord {

    private final String recordID;
    private final String prescriptionID;
    private final String patientID;
    private final String doctorID;
    private final LocalDateTime timestamp;

    private final MedicineName[] medicines; // snapshot of items at dispense time
    private final int[] quantities;

    private final boolean dispensed;        // true = success, false = failed
    private final String rejectionReason;   // null if success

    public DispensedRecord(
            String recordID,
            String prescriptionID,
            String patientID,
            String doctorID,
            LocalDateTime timestamp,
            MedicineName[] medicines,
            int[] quantities,
            boolean dispensed,
            String rejectionReason
    ) {
        this.recordID = recordID;
        this.prescriptionID = prescriptionID;
        this.patientID = patientID;
        this.doctorID = doctorID;
        this.timestamp = timestamp;
        this.medicines = medicines;
        this.quantities = quantities;
        this.dispensed = dispensed;
        this.rejectionReason = rejectionReason;
    }

    public String getRecordID() {
        return recordID;
    }

    public String getPrescriptionID() {
        return prescriptionID;
    }

    public String getPatientID() {
        return patientID;
    }

    public String getDoctorID() {
        return doctorID;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public MedicineName[] getMedicines() {
        return medicines;
    }

    public int[] getQuantities() {
        return quantities;
    }

    public boolean isDispensed() {
        return dispensed;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }
}
