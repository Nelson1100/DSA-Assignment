package entity;

import adt.LinkedQueue;
import adt.QueueInterface;
import java.util.Iterator;
import java.time.LocalDateTime;

public class Prescription implements Iterable<PrescriptionItem>{
    private String prescriptionID;
    private String patientID;
    private String doctorID;
    private LocalDateTime createdTime;
    private PrescriptionStatus status;
    private String rejectionReason; 
    private final QueueInterface<PrescriptionItem> items = new LinkedQueue<>();

    public Prescription(String prescriptionID, String patientID, String doctorID) {
        this.prescriptionID = prescriptionID;
        this.patientID = patientID;
        this.doctorID = doctorID;
        this.status = PrescriptionStatus.PENDING;
        this.createdTime = LocalDateTime.now();
    }

    // Add a prescription item
    public void addItem(PrescriptionItem item) {
        items.enqueue(item);
    }

    // Getters
    public String getPrescriptionID() {
        return prescriptionID;
    }

    public String getPatientID() {
        return patientID;
    }

    public String getDoctorID() {
        return doctorID;
    }

    public PrescriptionStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }
    
    public String getRejectionReason() {
        return rejectionReason;
    }

    public QueueInterface<PrescriptionItem> getItems() {
        return items;
    }

    // Setters
    public void setStatusDispensed() {
        this.status = PrescriptionStatus.DISPENSED;
    }
    
    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public boolean isDispensed() {
        return this.status == PrescriptionStatus.DISPENSED;
    }
    
    @Override
    public Iterator<PrescriptionItem> iterator() {
        return items.iterator(); 
    }
    
    public PrescriptionItem[] getItemsArray() {
        int size = 0;
        for (PrescriptionItem ignored : items) {
            size++;
        }

        PrescriptionItem[] array = new PrescriptionItem[size];
        int i = 0;
        for (PrescriptionItem item : items) {
            array[i++] = item;
        }
        return array;
    }

    // Display format
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Prescription ID: ").append(prescriptionID).append("\n");
        sb.append("Patient ID     : ").append(patientID).append("\n");
        sb.append("Doctor ID      : ").append(doctorID).append("\n");
        sb.append("Status         : ").append(status).append("\n");
        sb.append("Created Time   : ").append(createdTime.toString().replace('T', ' ').substring(0, 16)).append("\n");
        sb.append("Prescription Items:\n");
        for (PrescriptionItem item : items) {
            sb.append(" - ").append(item.toString()).append("\n");
        }
        return sb.toString();
    }
}
