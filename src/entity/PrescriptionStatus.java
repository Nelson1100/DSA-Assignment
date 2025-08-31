package entity;


public enum PrescriptionStatus {
    PENDING,
    DISPENSED,
    CANCELLED;
    
    public boolean isDispensed() {
        return this == DISPENSED;
    }
}
