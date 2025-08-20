package entity;

import adt.LinkedQueue;

public class Medicine implements Comparable<Medicine>{
    private String medicineID;
    private String medicineName;
    private String medicineDescription;
    private int totalStock;
    private LinkedQueue<StockBatch> stockBatches;
    
    public Medicine (String medicineID, String medicineName, String medicineDescription, int totalStock){
        this.medicineID = medicineID;
        this.medicineName = medicineName;
        this.medicineDescription = medicineDescription;
        this.totalStock = 0;
        this.stockBatches = new LinkedQueue<>();
    }
    
    public String getMedicineID(){
        return medicineID;
    }
    
    public String getMedicineName(){
        return medicineName;
    }
    
    public String getMedicineDescription(){
        return medicineDescription;
    }
    
    public int getTotalStock(){
        return totalStock;
    }
    
    public void setMedicineID(String medicineID){
        this.medicineID = medicineID;
    }
    
    public void setMedicineName(String medicineName){
        this.medicineName= medicineName;
    }
    
    public void setTotalStock(int totalStock) {
        this.totalStock = totalStock;
    }
     
    public LinkedQueue<StockBatch> getStockBatches() {
        return stockBatches;
    }
    
    @Override
    public int compareTo(Medicine other) {
        return this.medicineID.compareTo(other.medicineID);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Medicine)) return false;
        Medicine other = (Medicine) o;
        return medicineID.equals(other.medicineID);
    }
    @Override
    public String toString() {
        return String.format("Medicine[ID=%s, Name=%s, Desc=%s, Stock=%d]", 
                medicineID, medicineName, medicineDescription, totalStock);
    }
    
}
