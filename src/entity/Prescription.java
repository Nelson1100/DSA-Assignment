package entity;

import adt.LinkedQueue;
import utility.IDGenerator;
import utility.IDType;

public class Prescription {
    private String prescriptionID;
    private String patientID;
    private String doctorID;
    private LinkedQueue<PrescriptionItem> items;

    public Prescription(String patientID, String doctorID) {
        this.patientID = patientID;
        this.doctorID = doctorID;
        this.items = new LinkedQueue<>();
    }
    
    public String getPrescriptionID() {
        return prescriptionID;
    }
    
    public String getPatientID() {
        return patientID;
    }

    public String getDoctorID() {
        return doctorID;
    }

    public LinkedQueue<PrescriptionItem> getItems() {
        return items;
    }

    public void addItem(PrescriptionItem item) {
        items.enqueue(item);
    }

    @Override
    public String toString() {
        return String.format("Prescription ID : %s\nPatient ID     : %s\nDoctor ID      : %s",
                prescriptionID, patientID, doctorID);
    }
}

