package entity;

import adt.LinkedQueue;

public class Prescription {
    private String patientID;
    private String doctorID;
    private LinkedQueue<PrescriptionItem> items;

    public Prescription(String patientID, String doctorID) {
        this.patientID = patientID;
        this.doctorID = doctorID;
        this.items = new LinkedQueue<>();
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
        return "Prescription for patient " + patientID + " by Dr. " + doctorID;
    }
}

