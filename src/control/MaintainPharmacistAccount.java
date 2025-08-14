package control;

import adt.AVL_Tree;
import entity.Pharmacist;
import utility.IDGenerator;
import utility.IDType;

public class MaintainPharmacistAccount {
    private AVL_Tree<Pharmacist> pharmacistTree;
    
    public MaintainPharmacistAccount(){
        pharmacistTree = new AVL_Tree<>();
    }
    
    // register a new pharmacist if not already in the tree
    public boolean registerPharmacist(String name, String phone) {
        String newID = IDGenerator.next(IDType.PHARMACIST);
        Pharmacist p = new Pharmacist(newID, name, phone);
        if(!pharmacistTree.contains(p)){
            pharmacistTree.insert(p);
            return true;
        }
        return false; // alr exist
    }
    
    // search for pharmacist by ID
    public Pharmacist searchPharmacist(String ID){
        for(Pharmacist p : pharmacistTree.toListInorder()){
            if(p.getID().equals(ID)){
                return p;
            }
        }
        return null;
    }
    
    // remove pharmacist by ID
    public boolean removePharmacist(String ID){
        Pharmacist p = searchPharmacist(ID);
        if(p != null){
            pharmacistTree.delete(p);
            return true;
        }
        return false;
    }
    
    // update pharmacist name or phone
    public boolean updatePharmacist(String id, String newName, String newPhone){
        Pharmacist p = searchPharmacist(id);
        if (p != null) {
            if (newName != null && !newName.isEmpty()) p.setName(newName);
            if (newPhone != null && !newPhone.isEmpty()) p.setPhone(newPhone);
            return true;
        }
        return false;
    }
    
    // Display all pharmacist
    public void viewPharmacist(){
        for(Pharmacist p : pharmacistTree.toListInorder()){
            System.out.println(p);
        }
    }
    
    // check if any pharmacist registered
    public boolean isEmpty(){
        return pharmacistTree.isEmpty();
    }
    
    // total number of pharmacist
    public int countPharmacist(){
        return pharmacistTree.size();
    }
}
