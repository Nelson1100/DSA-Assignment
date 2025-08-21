package entity.keys;

import entity.DoctorDuty;
import entity.Shift;

import java.time.LocalDate;

public class DutyByDoctorDateShift implements Comparable<DutyByDoctorDateShift> {
    private final String doctorID;
    private final LocalDate date;
    private final Shift shift;
    private final DoctorDuty duty; // may be null for probe keys

    public DutyByDoctorDateShift(String doctorID, LocalDate date, Shift shift, DoctorDuty duty) {
        this.doctorID = doctorID;
        this.date = date;
        this.shift = shift;
        this.duty = duty;
    }

    public String getDoctorID() { return doctorID; }
    public LocalDate getDate() { return date; }
    public Shift getShift() { return shift; }
    public DoctorDuty getDuty() { return duty; }

    @Override
    public int compareTo(DutyByDoctorDateShift o) {
        if (o == null) return 1;
        int c;

        // doctorID (nulls last, but you should not store null doctorIDs in real data)
        if (this.doctorID == null && o.doctorID != null) return 1;
        if (this.doctorID != null && o.doctorID == null) return -1;
        if (this.doctorID != null && o.doctorID != null) {
            c = this.doctorID.compareTo(o.doctorID);
            if (c != 0) return c;
        }

        // date
        if (this.date == null && o.date != null) return 1;
        if (this.date != null && o.date == null) return -1;
        if (this.date != null && o.date != null) {
            c = this.date.compareTo(o.date);
            if (c != 0) return c;
        }

        // shift
        if (this.shift == null && o.shift != null) return 1;
        if (this.shift != null && o.shift == null) return -1;
        if (this.shift != null && o.shift != null) {
            c = Integer.compare(this.shift.ordinal(), o.shift.ordinal());
            if (c != 0) return c;
        }

        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof DutyByDoctorDateShift)) return false;
        return compareTo((DutyByDoctorDateShift) obj) == 0;
    }

    @Override
    public int hashCode() {
        // Not used by your AVL, but keep consistent with equals
        int h = 17;
        h = 31 * h + (doctorID == null ? 0 : doctorID.hashCode());
        h = 31 * h + (date == null ? 0 : date.hashCode());
        h = 31 * h + (shift == null ? 0 : shift.hashCode());
        return h;
    }

    @Override
    public String toString() {
        return "DutyKey{doctorID='" + doctorID + "', date=" + date + ", shift=" + shift + "}";
    }
}