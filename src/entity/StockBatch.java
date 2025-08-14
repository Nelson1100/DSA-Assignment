package entity;

import java.time.LocalDate;

public class StockBatch {
    private String batchID;
    private String medicineID;
    private int qty;
    private LocalDate expiryDate;
    private LocalDate receivedDate;
    
    public StockBatch(String batchID, String medicineID, int qty, LocalDate expiryDate, LocalDate receivedDate){
        this.batchID = batchID;
        this.medicineID = medicineID;
        this.qty = qty;
        this.expiryDate = expiryDate;
        this.receivedDate = receivedDate;
    }
    
    public String getBatchID(){
        return batchID;
    }
    
    public String getMedicineID(){
        return medicineID;
    }
    
    public int getQty(){
        return qty;
    }
    
    public LocalDate getExpiryDate(){
        return expiryDate;
    }
    
    public LocalDate getReceivedDate(){
        return receivedDate;
    }
    
    public void setBatchID(String batchID){
        this.batchID = batchID;
    }
    
    public void setMedicineID(String medicineID){
        this.medicineID = medicineID;
    }
    
    public void setQty(int qty){
        this.qty = qty;
    }
    
    public void setExpiryDate(LocalDate expiryDate){
        this.expiryDate = expiryDate;
    }
    
    public void setReceivedDate(LocalDate receivedDate){
        this.receivedDate = receivedDate;
    }
    
    @Override
    public String toString() {
        return String.format("StockBatch{batch=%s, med=%s, qty=%d, expiry=%s, received=%s}",
                batchID, medicineID, qty, expiryDate, receivedDate);
    }
}
