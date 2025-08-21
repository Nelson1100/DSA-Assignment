package entity;

import java.time.LocalDate;

public class DoctorDuty {
    private String doctorID;
    private LocalDate date;
    private Shift shift;
    private Availability availability;

    public DoctorDuty(String doctorID, LocalDate date, Shift shift, Availability availability) {
        this.doctorID = doctorID;
        this.date = date;
        this.shift = shift;
        this.availability = availability;
    }

    // Getters
    public String getDoctorID() {
        return doctorID;
    }

    public LocalDate getDate() {
        return date;
    }

    public Shift getShift() {
        return shift;
    }

    public Availability getAvailability() {
        return availability;
    }

    // Setters
    public void setAvailability(Availability availability) {
        this.availability = availability;
    }

    // Convenience: display duty info
    @Override
    public String toString() {
        return "DoctorDuty{" +
                "doctorID='" + doctorID + '\'' +
                ", date=" + date +
                ", shift=" + shift +
                ", availability=" + availability +
                '}';
    }
}
