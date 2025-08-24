package control;

import entity.Doctor;
import entity.DoctorDuty;
import entity.Shift;
import entity.Specialization;
import java.time.*;
import utility.Validation;

public class DoctorReportGenerator {
    private final DoctorDutyManagement DocDuty;
    Validation validate = new Validation();
    DoctorManagement dm = new DoctorManagement();

    public DoctorReportGenerator(DoctorDutyManagement docDuty, DoctorManagement dm) {
        this.DocDuty = docDuty;
        this.dm = dm;
    }

    public void yearlyAttendanceRate(String doctorID, int year, boolean autoCreateWeekdays, StringBuilder sb) {
        Shift[] shifts = Shift.values();

        int scheduled   = 0;
        int present     = 0;
        int unavailable = 0;
        int onLeave     = 0;

        for (int month = 1; month <= 12; month++) {
            YearMonth ym = YearMonth.of(year, month);
            int days = ym.lengthOfMonth();

            for (int day = 1; day <= days; day++) {
                LocalDate date = ym.atDay(day);

                for (int i = 0; i < shifts.length; i++) {
                    Shift shift = shifts[i];

                    DoctorDuty duty = DocDuty.searchDutyByDoctorDateShift(doctorID, date, shift);

                    if (duty == null && autoCreateWeekdays && validate.isWeekday(date))
                        duty = DocDuty.WeekdayDuty(doctorID, date, shift);

                    if (duty == null) 
                        continue;

                    scheduled++;
                    switch (duty.getAvailability()) {
                        case AVAILABLE:
                            present++;
                            break;
                        case UNAVAILABLE:
                            unavailable++;
                            break;
                        case ON_LEAVE:
                            onLeave++;
                            break;
                        default:
                            break;
                    }
                }
            }
        }

        int denom = present + unavailable + onLeave;
        double rate = (denom == 0) ? 0.0 : ((double) present / (double) denom) * 100.00;

        Doctor found = dm.findDoctor(new Doctor(doctorID.trim(), "", "", "", null));

        String name = (found != null && found.getDoctorName() != null && !found.getDoctorName().isBlank())
                        ? found.getDoctorName()
                        : doctorID;
        
        if (sb != null) {
            sb.append("Attendance Report — Dr. ").append(name)
              .append(" (").append(year).append(")\n")
              .append("Total Scheduled Shifts: ").append(scheduled).append('\n')
              .append("Present Shifts      : ").append(present).append('\n')
              .append("Unavailable Shifts  : ").append(unavailable).append('\n')
              .append("On Leave Shifts     : ").append(onLeave).append('\n')
              .append(String.format("Attendance: %.2f%%\n\n", rate));
        }
    }
    
    public void specializationInventory(StringBuilder sb) {
        if (sb == null)
            return;

        Specialization[] specs = Specialization.values();
        int P = specs.length;
        int[] counts = new int[P];

        Doctor[] docs = dm.getAllDoctor();
        int totalDoctors = (docs == null ? 0 : docs.length);

        if (docs != null) {
            for (int i = 0; i < docs.length; i++) {
                if (docs[i] == null) continue;
                int idx = docs[i].getSpecialization().ordinal();
                counts[idx]++;
            }
        }

        sb.append("Clinic Specialization Inventory — as of ")
          .append(java.time.LocalDate.now()).append('\n');
        sb.append(String.format("%-28s%-10s%-10s%n", "Specialization", "Doctors", "Percent"));
        sb.append(String.format("%-28s%-10s%-10s%n", "----------------------------", "--------", "--------"));

        for (int p = 0; p < P; p++) {
            double pct = (totalDoctors == 0) ? 0.0 : (counts[p] * 100.0 / totalDoctors);
            sb.append(String.format("%-28s%-10d%-7.2f%%%n", specs[p].name(), counts[p], pct));
        }

        sb.append('\n');
        sb.append(String.format("%-28s%d%n%n", "TOTAL DOCTORS", totalDoctors));
    }
}
