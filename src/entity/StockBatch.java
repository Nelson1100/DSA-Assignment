package entity;

import java.time.LocalDate;

public class StockBatch implements Comparable<StockBatch>{
    private String batchID;
    private String medicineID;
    private int stockQty;
    private LocalDate expiryDate;
    private LocalDate receivedDate;
    
    public StockBatch(String batchID, String medicineID, int stockQty, LocalDate expiryDate, LocalDate receivedDate){
        this.batchID = batchID;
        this.medicineID = medicineID;
        this.stockQty = stockQty;
        this.expiryDate = expiryDate;
        this.receivedDate = receivedDate;
    }
        
    public String getBatchID(){
        return batchID;
    }
    
    public String getMedicineID(){
        return medicineID;
    }
    
    public int getStockQty(){
        return stockQty;
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
    
    public void setStockQty(int stockQty){
        this.stockQty = stockQty;
    }
    
    public void setExpiryDate(LocalDate expiryDate){
        this.expiryDate = expiryDate;
    }
    
    public void setReceivedDate(LocalDate receivedDate){
        this.receivedDate = receivedDate;
    }
    
    public String stockKey() {
        return medicineID + "#" + batchID;
    }
    
    // add
    public void add(int stockQty) {
        this.stockQty += stockQty;
   }
    
    // remove
    public void deduct(int stockQty) { 
        this.stockQty -= stockQty; 
    }
    
    public int compareTo(StockBatch other){
        return this.stockKey().compareTo(other.stockKey());
    }
    
    @Override
    public String toString() {
        return String.format("StockBatch{batch=%s, med=%s, stockQty=%d, expiry=%s, received=%s}",
                batchID, medicineID, stockQty, expiryDate, receivedDate);
    }
}
