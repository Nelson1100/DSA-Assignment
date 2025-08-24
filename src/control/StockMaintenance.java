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
    public boolean addStockBatch(StockBatch batch) {
        StockBatch existing = batchIndex.find(batch);
        if (existing != null) {
            existing.add(batch.getStockQty()); // combine the stock quantities if same batch
        } else {
            batchIndex.insert(batch);
        }
        return true;
    }
    // find stock batch by Stockkey
    public StockBatch findBatch(String medicineID, String batchID) {
        return batchIndex.find(new StockBatch(medicineID, batchID, 0, LocalDate.MIN, LocalDate.MIN));
    }
    
    // remove expired batches
     public void removeExpiredStock(LocalDate today) {
        for (StockBatch b : batchIndex) {
            if (b.getExpiryDate().isBefore(today)) {
                batchIndex.delete(b);
            }
        }
    }
     
    // Get next batch to dispense (FEFO)
    public StockBatch getNextBatchToDispense(String medicineID) {
        StockBatch nextBatch = null;
        for (StockBatch batch : batchIndex) {
            if (batch.getMedicineID().equals(medicineID) && batch.getStockQty() > 0) {
                if (nextBatch == null || batch.getExpiryDate().isBefore(nextBatch.getExpiryDate())) {
                    nextBatch = batch;
                }
            }
        }
        return nextBatch;
    }
}
