package control;

import adt.AVLTree;
import adt.AVLInterface;
import entity.StockBatch;
import entity.MedicineName;

import java.time.LocalDate;
import java.util.Iterator;

public class StockMaintenance {
    private AVLInterface<StockBatch> idxByStockKey = new AVLTree<>();
    
    
    // search medicine by ID
    
    // update medicine price

    
    // add a stock batch
    public boolean addBatch(MedicineName medicineName, String batchID, int qty,
            LocalDate expiry, LocalDate received) {
        if (medicineName == null || batchID == null || batchID.isEmpty()
                || qty <= 0 || expiry == null || received == null) {
            return false;
        }

        StockBatch existing = findBatch(medicineName, batchID);
        if (existing == null) {
            return idxByStockKey.insert(new StockBatch(batchID, medicineName, qty, expiry, received));
        } else {
            existing.add(qty);
            // Optional policy: keep earliest expiry if different
            if (expiry.isBefore(existing.getExpiryDate())) {
                existing.setExpiryDate(expiry);
            }
            return true;
        }
    }
    
    public boolean batchIDExists(String batchID) {
        for (StockBatch b : idxByStockKey) {
            if (b.getBatchID().equalsIgnoreCase(batchID)) {
                return true;
            }
        }
        return false;
    }
    
    // find stock batch by Stockkey
    public StockBatch findBatch(MedicineName name, String batchId) {
        if (name == null || batchId == null) {
            return null;
        }
    
        StockBatch probe = new StockBatch(batchId, name, 0, LocalDate.MIN, LocalDate.MIN);
        return idxByStockKey.find(probe);
    }
    
    // find stock batch by id
    public StockBatch findBatchByID(String batchID) {
        if (batchID == null) {
            return null;
        }

        for (StockBatch b : idxByStockKey) {
            if (b.getBatchID().equalsIgnoreCase(batchID)) {
                return b;
            }
        }
        return null;
    }
    
    // list all stock batch for a selected medicine
    public String[][] listByMedicine(MedicineName name) {
        String[][] tmp = new String[800][5];
        int n = 0;
        Iterator<StockBatch> it = idxByStockKey.iterator();
        while (it.hasNext()) {
            StockBatch b = it.next();
            if (b.getMedicineName() == name) {
                tmp[n][0] = b.getMedicineName().name();
                tmp[n][1] = b.getBatchID();
                tmp[n][2] = Integer.toString(b.getStockQty());
                tmp[n][3] = b.getExpiryDate().toString();
                tmp[n][4] = b.getReceivedDate().toString();
                n++;
            }
        }
        String[][] out = new String[n][5];
        for (int i = 0; i < n; i++) System.arraycopy(tmp[i], 0, out[i], 0, 5);
        return out;
    }
    
    // list all for UI display
    public String viewAllBatches() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-15s %-10s %-8s %-12s %-12s%n", "Medicine", "BatchID", "Qty", "Expiry", "Received"));
        sb.append("-----------------------------------------------------------\n");

        Iterator<StockBatch> it = idxByStockKey.iterator();
        while (it.hasNext()) {
            StockBatch b = it.next();
            sb.append(String.format("%-15s %-10s %-8d %-12s %-12s%n",
                    b.getMedicineName().name(),
                    b.getBatchID(),
                    b.getStockQty(),
                    b.getExpiryDate(),
                    b.getReceivedDate()));
        }

        return sb.toString();
    }
    
    // list all for reports
    public String[][] listAll() {
        int size = idxByStockKey.size(); // Or track this manually
        StockBatch[] batches = new StockBatch[size];
        idxByStockKey.toArrayInorder(batches);

        String[][] rows = new String[size][5]; // 5 columns: Medicine, BatchID, Qty, Expiry, Received

        for (int i = 0; i < size; i++) {
            StockBatch b = batches[i];
            rows[i][0] = b.getMedicineName().name();
            rows[i][1] = b.getBatchID();
            rows[i][2] = String.valueOf(b.getStockQty());
            rows[i][3] = b.getExpiryDate().toString();
            rows[i][4] = b.getReceivedDate().toString();
        }

        return rows;
    }
    
    public int totalBalance(MedicineName name) {
        int sum = 0;
        Iterator<StockBatch> it = idxByStockKey.iterator();
        while (it.hasNext()) {
            StockBatch b = it.next();
            if (b.getMedicineName() == name) sum += b.getStockQty();
        }
        return sum;
    }
    
    public StockBatch earliestBatch(MedicineName name) {
        Iterator<StockBatch> it = idxByStockKey.iterator();
        StockBatch best = null;
        while (it.hasNext()) {
            StockBatch b = it.next();
            if (b.getMedicineName() != name || b.getStockQty() <= 0) {
                continue;
            }
            if (best == null || b.getExpiryDate().isBefore(best.getExpiryDate())) {
                best = b;
            }
        }
        return best;
    }
    
    public String[][] expiringWithin(int days) {
        String[][] tmp = new String[600][4];
        int n = 0;
        LocalDate limit = LocalDate.now().plusDays(days);
        Iterator<StockBatch> it = idxByStockKey.iterator();
        while (it.hasNext()) {
            StockBatch b = it.next();
            if (b.getStockQty() > 0 && !b.getExpiryDate().isAfter(limit)) {
                tmp[n][0] = b.getMedicineName().name();
                tmp[n][1] = b.getBatchID();
                tmp[n][2] = Integer.toString(b.getStockQty());
                tmp[n][3] = b.getExpiryDate().toString();
                n++;
            }
        }
        String[][] out = new String[n][4];
        for (int i = 0; i < n; i++) System.arraycopy(tmp[i], 0, out[i], 0, 4);
        return out;
    }
    
    public boolean exists(MedicineName name, String batchId) {
        return findBatch(name, batchId) != null;
    }
    
    
    
}
