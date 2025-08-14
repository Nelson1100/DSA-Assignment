package control;

import adt.AVL_Tree;
import adt.LinkedQueue;
import entity.Medicine;
import entity.StockBatch;

import java.time.LocalDate;
import java.util.Iterator;
import java.util.UUID;

public class MedicationStockMaintenance {
    private AVL_Tree<Medicine> medicineTree;
    
    public MedicationStockMaintenance(AVL_Tree<Medicine> medicineTree) {
        this.medicineTree = medicineTree;
    }
   
    // add 
    public boolean addStockBatch(String medicineID, int quantity, LocalDate expiryDate){
        
    }
    
    //search
    
    //update
    
    //report
}
