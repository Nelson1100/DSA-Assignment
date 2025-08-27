package dao;

import adt.LinkedQueue;
import entity.MedicineName;
import entity.Prescription;
import entity.PrescriptionItem;

public class PrescriptionInitializer {

    public static LinkedQueue<Prescription> initialize() {
        LinkedQueue<Prescription> prescriptions = new LinkedQueue<>();

        // Sample Prescription 1
        Prescription p1 = new Prescription("P202508210001", "D202508210001");
        p1.addItem(new PrescriptionItem(MedicineName.PARACETAMOL, 10, "Take after meals"));
        p1.addItem(new PrescriptionItem(MedicineName.COUGH_SYRUP, 1, "Before bedtime"));
        prescriptions.enqueue(p1);

        // Sample Prescription 2
        Prescription p2 = new Prescription("P202508210002", "D202508210002");
        p2.addItem(new PrescriptionItem(MedicineName.IBUPROFEN, 15, "After breakfast"));
        prescriptions.enqueue(p2);

        // Sample Prescription 3
        Prescription p3 = new Prescription("P202508210003", "D202508210001");
        p3.addItem(new PrescriptionItem(MedicineName.AMOXICILLIN, 20, "Twice a day"));
        p3.addItem(new PrescriptionItem(MedicineName.CETIRIZINE, 5, "Once before sleep"));
        prescriptions.enqueue(p3);

        return prescriptions;
    }
}
