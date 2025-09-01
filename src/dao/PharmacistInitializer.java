package dao;

import control.PharmacistManagement;
import entity.Pharmacist;
import utility.IDGenerator;
import utility.IDType;

public class PharmacistInitializer {

    public static void initialize(PharmacistManagement pharmacistMgmt) {
        if (pharmacistMgmt == null) return;

        // Hardcoded seed data
        Pharmacist A = new Pharmacist("PH202409260001", "JayTan", "0125566789", "jay@gmail.com");
        Pharmacist B = new Pharmacist(IDGenerator.next(IDType.PHARMACIST), "DericTan", "0121234567", "deric@gmail.com");
        Pharmacist C = new Pharmacist(IDGenerator.next(IDType.PHARMACIST), "KelsonTan", "0121234567", "kelson@gmail.com");

        pharmacistMgmt.addPharmacist(A);
        pharmacistMgmt.addPharmacist(B);
        pharmacistMgmt.addPharmacist(C);
    }
}
