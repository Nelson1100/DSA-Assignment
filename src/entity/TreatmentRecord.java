package entity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TreatmentRecord {

    private final String treatmentID;
    private String patientID;
    private String doctorID;
    private final String diagnosis;
    private final String treatment;
    private Prescription prescription;
    private LocalDateTime consultationTime;
    private LocalDateTime dateTime;

    public TreatmentRecord(String treatmentID, String patientID, String doctorID, 
                           String diagnosis, String treatment, Prescription prescription, LocalDateTime dateTime) {
        this.treatmentID = treatmentID;
        this.patientID = patientID;
        this.doctorID = doctorID;
        this.diagnosis = diagnosis;
        this.treatment = treatment;
        this.prescription = prescription;
        this.consultationTime = LocalDateTime.now();    // set time automatically
    }

    public String getTreatmentID() { return treatmentID; }
    public String getPatientID()   { return patientID; }
    public String getDoctorID()    { return doctorID; }
    public String getDiagnosis()   { return diagnosis; }
    public String getTreatment()   { return treatment; }
    public Prescription getPrescription() {return prescription; }
    public LocalDateTime getconsultationTime() { return consultationTime; }
    
    public LocalDateTime getDateTime() {
        return dateTime;
    }
    @Override
    public String toString() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        return String.format(
            "=== Treatment Record ===\n" +
            "Treatment ID   : %s\n" +
            "Patient ID     : %s\n" +
            "Doctor ID      : %s\n" +
            "Consulted On   : %s\n" +
            "Diagnosis      : %s\n" +
            "Treatment Notes: %s\n%s",
            treatmentID, patientID, doctorID, consultationTime.format(dtf),
            diagnosis, treatment, prescription.toString()
        );
    }
}
