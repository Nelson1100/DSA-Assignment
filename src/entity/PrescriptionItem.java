package entity;

public class PrescriptionItem {
    private MedicineName medicineName;
    private int prescribedQty;
    private Instruction instructions; // for example this kind "2 tablets twice daily"

    public PrescriptionItem(MedicineName medicineName, int prescribedQty, Instruction instructions) {
        this.medicineName = medicineName;
        this.prescribedQty = prescribedQty;
        this.instructions = instructions;
    }

    public MedicineName getMedicineName() {
        return medicineName;
    }

    public int getPrescribedQty() {
        return prescribedQty;
    }

    public Instruction getInstructions() {
        return instructions;
    }
    
    @Override
    public String toString() {
        return medicineName + " x" + prescribedQty + " (" + instructions + ")";
    }
}


