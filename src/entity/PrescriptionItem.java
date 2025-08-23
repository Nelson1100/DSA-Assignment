package entity;

public class PrescriptionItem {
    private String medicineID;
    private int prescribedQty;
    private String dosageInstruction;  // for example this kind "2 tablets twice daily"

    public PrescriptionItem(String medicineID, int prescribedQty, String dosageInstruction) {
        this.medicineID = medicineID;
        this.prescribedQty = prescribedQty;
        this.dosageInstruction = dosageInstruction;
    }

    public String getMedicineID() {
        return medicineID;
    }

    public int getprescribedQty() {
        return prescribedQty;
    }

    public String getDosageInstruction() {
        return dosageInstruction;
    }

    @Override
    public String toString() {
        return medicineID + " x" + prescribedQty + " (" + dosageInstruction + ")";
    }
}


