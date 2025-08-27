package entity;

import java.time.LocalDate;

public class StockBatch implements Comparable<StockBatch> {

    private final String batchID;
    private final MedicineName medicineName;
    private int stockQty;
    private LocalDate expiryDate;
    private LocalDate receivedDate;

    public StockBatch(String batchID, MedicineName medicineName, int stockQty,
            LocalDate expiryDate, LocalDate receivedDate) {
        this.batchID = batchID;
        this.medicineName = medicineName;
        this.stockQty = stockQty;
        this.expiryDate = expiryDate;
        this.receivedDate = receivedDate;
    }

    // Getters & setters (no validation)
    public String getBatchID() {
        return batchID;
    }

    public MedicineName getMedicineName() {
        return medicineName;
    }

    public int getStockQty() {
        return stockQty;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public LocalDate getReceivedDate() {
        return receivedDate;
    }

    public void setStockQty(int stockQty) {
        this.stockQty = stockQty;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public void setReceivedDate(LocalDate receivedDate) {
        this.receivedDate = receivedDate;
    }

    // Operations
    public void add(int qty) {
        this.stockQty += qty;
    }

    public boolean deduct(int qty) {
        if (qty > stockQty) {
            return false;
        }
        this.stockQty -= qty;
        return true;
    }

    public String stockKey() {
        return medicineName.name() + "#" + batchID;
    }

    @Override
    public int compareTo(StockBatch other) {
        int c = this.medicineName.name().compareTo(other.medicineName.name());
        if (c != 0) {
            return c;
        }
        return this.batchID.compareTo(other.batchID);
    }

    @Override
    public String toString() {
        return String.format("StockBatch{batch=%s, med=%s, stockQty=%d, expiry=%s, received=%s}",
                batchID, medicineName, stockQty, expiryDate, receivedDate);
    }
}
