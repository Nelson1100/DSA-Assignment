package control;

import adt.AVLTree;
import adt.AVLInterface;
import entity.StockBatch;
import entity.MedicineName;

import java.time.LocalDate;
import java.util.Iterator;

public class StockMaintenance {
    private AVLInterface<StockBatch> idxByStockKey = new AVLTree<>();
      
    // add a stock batch
    public boolean addBatch(MedicineName medicineName, String batchID, int qty,
             LocalDate received, LocalDate expiry) {
        if (medicineName == null || batchID == null || batchID.isEmpty()
                || qty <= 0 || received == null || expiry == null ) {
            return false;
        }

        StockBatch existing = findBatch(medicineName, batchID);
        if (existing == null) {
            return idxByStockKey.insert(new StockBatch(batchID, medicineName, qty, received, expiry));
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
            if (b.getMedicineName() == name && !b.getExpiryDate().isBefore(LocalDate.now())) {
                tmp[n][0] = b.getMedicineName().name();
                tmp[n][1] = b.getBatchID();
                tmp[n][2] = Integer.toString(b.getStockQty());
                tmp[n][3] = b.getReceivedDate().toString();
                tmp[n][4] = b.getExpiryDate().toString();
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
        sb.append(String.format("%-15s %-14s %-8s %-12s %-12s%n", "Medicine", "BatchID", "Qty", "Received", "Expiry"));
        sb.append("-----------------------------------------------------------\n");

        for (StockBatch b : idxByStockKey) {
            if (!b.getExpiryDate().isBefore(LocalDate.now())) {
            sb.append(String.format("%-15s %-14s %-8d %-12s %-12s%n",
                    b.getMedicineName().name(),
                    b.getBatchID(),
                    b.getStockQty(),
                    b.getReceivedDate(),
                    b.getExpiryDate()));
            }
        }
        return sb.toString();
    }
        
    
    // list all for reports
    public String[][] listAll() {
        int size = idxByStockKey.size();
        StockBatch[] batches = new StockBatch[size];
        idxByStockKey.toArrayInorder(batches);

        // Use temporary list to collect valid rows
        String[][] temp = new String[size][5];
        int count = 0;

        for (int i = 0; i < size; i++) {
            StockBatch b = batches[i];
            if (b.getExpiryDate().isBefore(LocalDate.now())) {
                continue; // Skip expired batches
            }

            temp[count][0] = b.getMedicineName().name();
            temp[count][1] = b.getBatchID();
            temp[count][2] = String.valueOf(b.getStockQty());
            temp[count][3] = b.getReceivedDate().toString();
            temp[count][4] = b.getExpiryDate().toString();
            count++;
        }

        // Copy only the non-expired rows to the final result array
        String[][] rows = new String[count][5];
        System.arraycopy(temp, 0, rows, 0, count);

        return rows;
    }
    
    public int totalBalance(MedicineName name) {
        int sum = 0;
        Iterator<StockBatch> it = idxByStockKey.iterator();
        while (it.hasNext()) {
            StockBatch b = it.next();
            if (b.getMedicineName() == name && !b.getExpiryDate().isBefore(LocalDate.now())) {
                sum += b.getStockQty();
            }
        }
        return sum;
    }
    
    public StockBatch earliestBatchNonExpired(MedicineName name) {
        Iterator<StockBatch> it = idxByStockKey.iterator();
        StockBatch best = null;
        while (it.hasNext()) {
            StockBatch b = it.next();
            if (b.getMedicineName() != name
                    || b.getStockQty() <= 0
                    || b.getExpiryDate().isBefore(LocalDate.now())) {
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
    
    public StockBatch[] getSortedNonExpiredBatches(MedicineName name) {
        StockBatch[] temp = new StockBatch[100]; // assume max 100 batches
        int count = 0;

        for (StockBatch batch : idxByStockKey) {
            if (batch.getMedicineName() == name && !batch.getExpiryDate().isBefore(LocalDate.now())) {
                temp[count++] = batch;
            }
        }

        // simple selection sort by expiry
        for (int i = 0; i < count - 1; i++) {
            for (int j = i + 1; j < count; j++) {
                if (temp[i].getExpiryDate().isAfter(temp[j].getExpiryDate())) {
                    StockBatch tmp = temp[i];
                    temp[i] = temp[j];
                    temp[j] = tmp;
                }
            }
        }

        StockBatch[] result = new StockBatch[count];
        System.arraycopy(temp, 0, result, 0, count);
        return result;
    }
    
    public Iterable<StockBatch> getAllBatches() {
        return idxByStockKey;
    }
    
    public StockBatch[] getAllValidBatches() {
        int size = idxByStockKey.size();
        StockBatch[] all = new StockBatch[size];
        idxByStockKey.toArrayInorder(all);

        // Count valid first
        int count = 0;
        for (int i = 0; i < size; i++) {
            if (!all[i].getExpiryDate().isBefore(LocalDate.now())) {
                count++;
            }
        }

        // Copy only valid
        StockBatch[] valid = new StockBatch[count];
        int j = 0;
        for (int i = 0; i < size; i++) {
            if (!all[i].getExpiryDate().isBefore(LocalDate.now())) {
                valid[j++] = all[i];
            }
        }

        return valid;
    }
    
    public boolean deduct(MedicineName name, int qty) {
        while (qty > 0) {
            StockBatch batch = earliestBatchNonExpired(name);
            if (batch == null) {
                return false; // No stock left
            }
            int available = batch.getStockQty();
            int deductQty = Math.min(available, qty);

            if (!batch.deduct(deductQty)) {
                return false; 
            }

            qty -= deductQty;
        }
        return true;
    }
}
