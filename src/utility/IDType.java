package utility;

public enum IDType {
    PATIENT("P"),
    DOCTOR("D"),
    PHARMACIST("PH"),
    APPOINTMENT("APT"),
    CONSULTATION("C"),
    TREATMENT("T"),
    PRESCRIPTION("PR"),
    DISPENSE("DP"),
    MEDICINE("M"),
    STOCKBATCH("SB"),
    INVOICE("INV");
    
    private final String prefix;
    
    private IDType(String p) {
        this.prefix = p;
    }
    
    public String getPrefix() {
        return prefix;
    }
}
