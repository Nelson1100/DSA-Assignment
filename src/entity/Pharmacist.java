package entity;

public class Pharmacist implements Comparable<Pharmacist> {
    private String pharmacistID;
    private String pharmacistName;
    private String pharmacistPhoneNo;
    private String pharmacistEmail;

    public Pharmacist(String pharmacistID, String pharmacistName, String pharmacistPhoneNo, String pharmacistemail) {
        this.pharmacistID = pharmacistID;
        this.pharmacistName = pharmacistName;
        this.pharmacistPhoneNo = pharmacistPhoneNo;
        this.pharmacistEmail = pharmacistemail;
    }

    public String getPharmacistID() {
        return pharmacistID;
    }

    public void setPharmacistID(String pharmacistID) {
        this.pharmacistID = pharmacistID;
    }

    public String getPharmacistName() {
        return pharmacistName;
    }

    public void setPharmacistName(String pharmacistName) {
        this.pharmacistName = pharmacistName;
    }

    public String getPharmacistPhoneNo() {
        return pharmacistPhoneNo;
    }

    public void setPharmacistPhoneNo(String pharmacistPhoneNo) {
        this.pharmacistPhoneNo = pharmacistPhoneNo;
    }

    public String getPharmacistEmail() {
        return pharmacistEmail;
    }

    public void setpharmacistEmail(String pharmacistEmail) {
        this.pharmacistEmail = pharmacistEmail;
    }
    
    @Override
    public int compareTo(Pharmacist other) {
        return this.pharmacistID.compareTo(other.pharmacistID);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Pharmacist)) return false;
        Pharmacist other = (Pharmacist) obj;
        return pharmacistID != null && pharmacistID.equals(other.pharmacistID);
    }

    @Override
    public String toString() {
        return String.format(
                "Pharmacist ID: %s\npharmacistName: %s\npharmacistPhoneNo: %s\npharmacistEmail:",
                pharmacistID, pharmacistName, pharmacistPhoneNo, pharmacistEmail
        );
    }
}
