package entity;

import entity.MedicineType;

public enum MedicineName {
    PARACETAMOL(MedicineType.TABLET, 1, 20),
    AMOXICILLIN(MedicineType.CAPSULE, 1, 30),
    COUGH_SYRUP(MedicineType.SYRUP, 1, 2),
    INSULIN(MedicineType.INJECTION, 1, 5),
    HYDROCORTISONE(MedicineType.OINTMENT, 1, 10),
    IBUPROFEN(MedicineType.TABLET, 1, 15),
    ASPIRIN(MedicineType.TABLET, 1, 10),
    CETIRIZINE(MedicineType.TABLET, 1, 14),
    VITAMIN_C(MedicineType.CAPSULE, 1, 30);

    private final MedicineType type;
    private final int minDose;
    private final int maxDose;

    MedicineName(MedicineType type, int minDose, int maxDose) {
        this.type = type;
        this.minDose = minDose;
        this.maxDose = maxDose;
    }

    public MedicineType getType() {
        return type;
    }

    public int getMinDose() {
        return minDose;
    }

    public int getMaxDose() {
        return maxDose;
    }
    
}
