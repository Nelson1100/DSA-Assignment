package control;

import adt.AVLInterface;
import adt.AVLTree;
import entity.Pharmacist;

public class MaintainPharmacistAccount{
    private final AVLInterface<Pharmacist> pharmacistTree;
    private int count;

    public MaintainPharmacistAccount() {
        this.pharmacistTree = new AVLTree<>();
        this.count = 0;
    }

    // Register with manual ID (no IDGenerator)
    public boolean registerPharmacist(String pharmacistID, String pharmacistName, String pharmacistRole, String pharmacistShiftCode){
        if (pharmacistID == null || pharmacistID.isEmpty()) return false;
        pharmacistID = pharmacistID.trim();
        if (searchPharmacist(pharmacistID) != null) return false;
        //validation
        if(!isValidRole(pharmacistRole)) return false;
        if(!isValidShift(pharmacistShiftCode)) return false;
        Pharmacist p = new Pharmacist(pharmacistID, pharmacistName, pharmacistRole, pharmacistShiftCode, true);
        pharmacistTree.insert(p);
        count++;
        return true;
    }

    // Search by ID using a probe (works because Pharmacist.compareTo uses pharmacistID)
    public Pharmacist searchPharmacist(String pharmacistID) {
        if (pharmacistID == null) return null;
        pharmacistID = pharmacistID.trim();
        Pharmacist probe = new Pharmacist(pharmacistID);
        return pharmacistTree.find(probe);
    }

    // deactive
    public boolean removePharmacist(String pharmacistID) {
        Pharmacist p = searchPharmacist(pharmacistID);
        if (p == null) return false;
        p.setActive(false);
        return true;
    }
    
    // activate 
    public boolean activatePharmacist(String pharmacistID) {
        Pharmacist p = searchPharmacist(pharmacistID);
        if (p == null) return false;
        p.setActive(true);
        return true;
    }

    // Update name/phone
    public boolean updatePharmacist(Pharmacist updated) {
        if (updated == null) return false;
        Pharmacist existing = searchPharmacist(updated.getPharmacistID());
        if (existing == null) return false;
        existing.setPharmacistName(updated.getPharmacistName());
        existing.setPharmacistRole(updated.getPharmacistRole());
        existing.setPharmacistShiftCode(updated.getPharmacistShiftCode());
        existing.setActive(updated.isActive());
            return true;
    }
    
    public int size(){
        return count;
    }
    
    public boolean isEmpty() {
        return pharmacistTree.isEmpty();
    }
    
    public Pharmacist[] toArraySorted(){
    Comparable[] tmp = pharmacistTree.toArrayInorder(); // actual runtime type
    Pharmacist[] out = new Pharmacist[tmp.length];
    for (int i = 0; i < tmp.length; i++) {
        out[i] = (Pharmacist) tmp[i];
        }
        return out;
    }
    
    // delete
    public boolean remove(String pharmacistID){
        Pharmacist existing = searchPharmacist(pharmacistID);
        if (existing == null) return false;
        pharmacistTree.delete(existing);
        count--;
        return true;
    }
    
    //validation
    private boolean isValidRole(String pharmacistRole){
        if (pharmacistRole == null) return false;
        String r = pharmacistRole.trim().toUpperCase();
        return r.equals("DISPENSER") || r.equals("PIC") || r.equals("PHARMACIST");
    }
    
    private boolean isValidShift(String pharmacistShiftCode){
        if (pharmacistShiftCode == null) return false;
        String s = pharmacistShiftCode.trim().toUpperCase();
        return s.equals("M") || s.equals("E") || s.equals("N");
    }
}
