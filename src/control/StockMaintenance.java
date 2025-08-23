package control;

import adt.AVLTree;
import adt.AVLInterface;
import entity.Medicine;
import entity.StockBatch;

import java.time.LocalDate;

public class StockMaintenance {
    private AVLInterface<Medicine> medicineCatalog = new AVLTree<>();
    private AVLInterface<StockBatch> batchIndex = new AVLTree<>();
    
    // add new medicine
    public boolean addMedicine(Medicine medicine){
        if(medicineCatalog.find(medicine) != null)
            return false;
        medicineCatalog.insert(medicine);
        return true;
    }
    
    // search medicine by ID
    public Medicine findMedicineByID(String medicineID) {
        return medicineCatalog.find(new Medicine(medicineID, "", "", "", 0.0));
    }
    
    // update medicine price
    public boolean updateMedicinePrice(String medicineID, double newPrice){
        Medicine m = findMedicineByID(medicineID);
        if (m == null)
            return false;
        m.setUnitPrice(newPrice);
        return true;
    }
    
    // add a stock batch
    
    // find stock batch by Stockkey
    
    // remove expired batches
}
