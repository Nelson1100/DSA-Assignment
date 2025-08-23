package entity;


public class Medicine implements Comparable<Medicine>{
    private String medicineID;
    private String medicineName;
    private String medicineCategory;
    private String medicineDosage;
    private double unitPrice;
    
    public Medicine (String medicineID, String medicineName, String medicineCategory, String medicineDosage, double unitPrice){
        this.medicineID = medicineID;
        this.medicineName = medicineName;
        this.medicineCategory = medicineCategory;
        this.medicineDosage = medicineDosage;
        this.unitPrice= unitPrice;
    }
    
    public String getMedicineID(){
        return medicineID;
    }
    
    public String getMedicineName(){
        return medicineName;
    }
    
    public String getMedicineCategory(){
        return medicineCategory;
    }
    
    public String getMedicineDosage(){
        return medicineDosage;
    }
    
    public double getUnitPrice(){
        return unitPrice;
    }
    
    public void setMedicineName(String medicineName){
        this.medicineName= medicineName;
    }
    
    public void setMedicineCategory (String medicineCategory){
        this.medicineCategory = medicineCategory;
    }
    
    public void setMedicineDosage (String medicineDosage){
        this.medicineDosage = medicineDosage;
    }
    
    public void setUnitPrice (double unitPrice) {
        this.unitPrice = unitPrice;
    }
    
    @Override
    public int compareTo(Medicine other) {
        return this.medicineID.compareTo(other.medicineID);
    }
    
    @Override
    public String toString() {
        return medicineID + ", " + medicineName + ", " + medicineDosage + " (" + medicineCategory + ") " + ", min = " + unitPrice;
    }
}
