package entity;


public class Medicine{
    private MedicineName medicineName;
    private String medicineDescription;
    private String medicineManufacturer;
    private double unitPrice;
    
    public Medicine (MedicineName medicineName, String medicineDescription, String medicineManufacturer, double unitPrice){
        this.medicineName = medicineName;
        this.medicineDescription = medicineDescription;
        this.medicineManufacturer = medicineManufacturer;
        this.unitPrice= unitPrice;
    }
    
    public MedicineName getMedicineName(){
        return medicineName;
    }
    
    public MedicineType getType(){
        return medicineName.getType();
    }
    
    public String getMedicineDescription(){
        return medicineDescription;
    }
    
    public String getMedicineManufacturer(){
        return medicineManufacturer;
    }
    
    public double getUnitPrice(){
        return unitPrice;
    }
    
    public void setMedicineDescription (String medicineDescription){
        this.medicineDescription = medicineDescription;
    }
    
    public void setMedicineManufacturer(String medicineManufacturer){
        this.medicineManufacturer = medicineManufacturer;
    }
    
    public void setUnitPrice (double unitPrice) {
        this.unitPrice = unitPrice;
    }
    
    public String toString() {
        return String.format("%s (%s) - %s | Manufacturer: %s | Price: RM %.2f",
                medicineName.name(),
                medicineName.getType(),
                medicineDescription != null ? medicineDescription : "No description",
                medicineManufacturer != null ? medicineManufacturer : "Unknown",
                unitPrice
        );
    }
}
