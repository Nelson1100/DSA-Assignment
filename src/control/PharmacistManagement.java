package control;

import adt.AVLInterface;
import adt.AVLTree;
import entity.Pharmacist;
import java.util.Iterator;

public class PharmacistManagement {
    private AVLInterface<Pharmacist> pharmacistTree = new AVLTree<>();

    // CREATE (Register new pharmacist)
    public boolean registerPharmacist(String id, String name, String phoneNo, String email) {
        if (findPharmacist(id) != null) {
            return false; // duplicate ID
        }
        pharmacistTree.insert(new Pharmacist(id, name, phoneNo, email));
        return true;
    }
    
    public boolean addPharmacist(Pharmacist pharmacist) {
        return registerPharmacist(
            pharmacist.getPharmacistID(),
            pharmacist.getPharmacistName(),
            pharmacist.getPharmacistPhoneNo(),
            pharmacist.getPharmacistEmail()
        );
    }

    // READ (search pharmacist by ID)
    public Pharmacist findPharmacist(String id) {
        return pharmacistTree.find(new Pharmacist(id, "", "", ""));
    }

    // READ ALL (Display all pharmacists)
    public String ViewAllPharmacistReport() {
        StringBuilder sb = new StringBuilder();
        Iterator<Pharmacist> it = pharmacistTree.iterator();
        while (it.hasNext()) {
            sb.append(it.next()).append("\n\n");
        }
        return sb.toString().trim();
    }

    // UPDATE (Modify name, phone number, or email)
    public boolean updatePharmacist(String id, String newName, String newPhoneNo, String newEmail) {
        Pharmacist pharmacist = findPharmacist(id);
        if (pharmacist != null) {
            pharmacist.setPharmacistName(newName);
            pharmacist.setPharmacistPhoneNo(newPhoneNo);
            pharmacist.setpharmacistEmail(newEmail);
            return true;
        }
        return false;
    }

    // DELETE (Remove pharmacist by ID)
    public boolean removePharmacist(String id) {
        return pharmacistTree.delete(new Pharmacist(id, "", "", ""));
    }

    // UTILITY: Check if pharmacist exists
    public boolean hasPharmacist(String id) {
        return findPharmacist(id) != null;
    }
    
    public Pharmacist getPharmacist(String id) {
        return pharmacistTree.find(new Pharmacist(id, "", "", ""));
    }
}
