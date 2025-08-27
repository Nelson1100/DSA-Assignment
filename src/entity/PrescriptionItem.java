package entity;

public class PrescriptionItem {
    private MedicineName medicineName;
    private int prescribedQty;
    private String dosageInstruction;  // for example this kind "2 tablets twice daily"

    public PrescriptionItem(MedicineName medicineName, int prescribedQty, String dosageInstruction) {
        this.medicineName = medicineName;
        this.prescribedQty = prescribedQty;
        this.dosageInstruction = dosageInstruction;
    }

    public MedicineName getMedicineName() {
        return medicineName;
    }

    public int getPrescribedQty() {
        return prescribedQty;
    }

    public String getDosageInstruction() {
        return dosageInstruction;
    }

    @Override
    public String toString() {
        return medicineName + " x" + prescribedQty + " (" + dosageInstruction + ")";
    }
}


