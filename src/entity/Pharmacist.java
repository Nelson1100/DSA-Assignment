package entity;

public class Pharmacist implements Comparable<Pharmacist> {
    private String pharmacistID;
    private String pharmacistName;
    private String pharmacistRole;
    private String pharmacistShiftCode;
    private boolean active;

    public Pharmacist(String pharmacistID, String pharmacistName, String pharmacistrole, String pharmacistShiftCode, boolean active) {
        this.pharmacistID = pharmacistID;
        this.pharmacistName = pharmacistName;
        this.pharmacistRole = pharmacistRole;
        this.pharmacistShiftCode= pharmacistShiftCode;
        this.active= active;
    }

    public Pharmacist(String pharmacistID){
        this(pharmacistID, "", "", "", true);
    }
    
    public String getPharmacistID() { 
        return pharmacistID; 
    }
    
    public String getPharmacistName() { 
        return pharmacistName; 
    }
    
    public String getPharmacistRole() { 
        return pharmacistRole; 
    }
    
    public String getPharmacistShiftCode() { 
        return pharmacistShiftCode; 
    }
    
    public boolean isActive() { 
        return active; 
    }

    public void setPharmacistName(String pharmacistName) { 
        this.pharmacistName = pharmacistName; 
    }
    
    public void setPharmacistRole(String pharmacistRole) { 
        this.pharmacistRole = pharmacistRole; 
    }
    
    public void setPharmacistShiftCode(String pharmacistShiftCode) { 
        this.pharmacistShiftCode = pharmacistShiftCode; 
    }
    
    public void setActive(boolean active) { 
        this.active= active; 
    }

    @Override
    public int compareTo(Pharmacist other) {
        return this.pharmacistID.compareTo(other.pharmacistID);
    }
    
    @Override
    public String toString(){
        return pharmacistID + " | " + pharmacistName + " | " + pharmacistRole + " | " + pharmacistShiftCode + " | " + (active?"ACTIVE":"INACTIVE");
    }
}
