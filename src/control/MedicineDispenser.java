package control;

import adt.LinkedQueue;
import entity.MedicineName;
import entity.Prescription;
import entity.PrescriptionItem;
import entity.StockBatch;

public class MedicineDispenser {
    private StockMaintenance stockMaintenance;

    public MedicineDispenser(StockMaintenance stockMaintenance) {
        this.stockMaintenance = stockMaintenance;
    }

    public boolean dispense(Prescription pp) {
        boolean allSuccess = true;

        LinkedQueue<PrescriptionItem> items = pp.getItems();
        
        for (PrescriptionItem item : pp.getItems()) {
            MedicineName name = item.getMedicineName();
            int qtyRemaining = item.getPrescribedQty();


            while (qtyRemaining > 0) {
                StockBatch batch = stockMaintenance.earliestBatch(name);
                if (batch == null) {
                    System.out.println("\n[Error] Not enough stock for medicine: " + name);
                    allSuccess = false;
                    break;
                }

                int take = Math.min(batch.getStockQty(), qtyRemaining);
                batch.deduct(take);
                qtyRemaining -= take;
                // Do NOT delete batch at 0 — keep as history
            }

            if (qtyRemaining > 0) {
                allSuccess = false;
            }
        }
        return allSuccess;
    }
}
