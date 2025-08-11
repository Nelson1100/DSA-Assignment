/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

public class Medicine {
    private String medicineID;
    private String medicineName;
    private String category;
    private double unitPrice;
    private int reorderLevel;
    
    public Medicine (String medicineID, String medicineName, String category, double unitPrice, int reorderLevel){
        this.medicineID = medicineID;
        this.medicineName = medicineName;
        this.category = category;
        this.unitPrice = unitPrice;
        this.reorderLevel = reorderLevel;
    }
    
    public String getMedicineID(){
        return medicineID;
    }
    
    public String getMedicineName(){
        return medicineName;
    }
    
    public String getCategory(){
        return category;
    }
    
    public double getUnitPrice(){
        return unitPrice;
    }
    
    public int getReorderLevel(){
        return reorderLevel;
    }
    
    public void setMedicineID(String medicineID){
        this.medicineID = medicineID;
    }
    
    public void setMedicineName(String medicineName){
        this.medicineName= medicineName;
    }
    
    public void setCategory(String category){
        this.category= category;
    }
    
    public void setUnitPrice(double unitPrice){
        this.unitPrice = unitPrice;
    }
    
    public void setReorderLevel(int reorderLevel){
        this.reorderLevel = reorderLevel;
    }
    
    @Override
    public String toString() {
        return String.format("Medicine{id=%s, name=%s, category=%s, price=%.2f, reorder=%d}",
                medicineID, medicineName, category, unitPrice, reorderLevel);
    }
    
}
