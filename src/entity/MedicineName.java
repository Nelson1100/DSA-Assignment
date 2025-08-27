package entity;

public enum MedicineName {
    PARACETAMOL(MedicineType.TABLET),
    AMOXICILLIN(MedicineType.CAPSULE),
    COUGH_SYRUP(MedicineType.SYRUP),
    INSULIN(MedicineType.INJECTION),
    HYDROCORTISONE(MedicineType.OINTMENT),
    IBUPROFEN(MedicineType.TABLET),
    ASPIRIN(MedicineType.TABLET),
    CETIRIZINE(MedicineType.TABLET),
    VITAMIN_C(MedicineType.CAPSULE);

    private final MedicineType type;

    MedicineName(MedicineType type) {
        this.type = type;
    }

    public MedicineType getType() {
        return type;
    }
}