package control;

import adt.LinkedQueue;
import entity.Prescription;
import entity.PrescriptionItem;
import entity.StockBatch;

public class MedicineDispenser {
    private StockMaintenance stockMaintenance;

    public MedicineDispenser(StockMaintenance stockMaintenance) {
        this.stockMaintenance = stockMaintenance;
    }

    public boolean dispense(Prescription prescription) {
        boolean allSuccess = true;

        LinkedQueue<PrescriptionItem> items = prescription.getItems();
        for (PrescriptionItem item : items) {
            String medID = item.getMedicineID();
            int qtyToDispense = item.getPrescribedQty();
            int qtyRemaining = qtyToDispense;


            while (qtyRemaining > 0) {
                StockBatch batch = stockMaintenance.getNextBatchToDispense(medID);
                if (batch == null) {
                    System.out.println("\n[Error] Not enough stock for medicine: " + medID);
                    allSuccess = false;
                    break;
                }

                int available = batch.getStockQty();
                if (available >= qtyRemaining) {
                    batch.deduct(qtyRemaining);
                    System.out.printf("Dispensed %d of %s from batch %s\n", qtyRemaining, medID, batch.getBatchID());
                    qtyRemaining = 0;
                } else {
                    batch.deduct(available);
                    System.out.printf("Dispensed %d of %s from batch %s (partial)\n", available, medID, batch.getBatchID());
                    qtyRemaining -= available;
                }
            }
        }

        return allSuccess;
    }
}
