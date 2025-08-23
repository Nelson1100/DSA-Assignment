package control;

import entity.DoctorDuty;
import entity.Shift;
import java.time.*;
import utility.Validation;

public class DoctorReportGenerator {
    private final DoctorDutyManagement DocDuty;
    private final Validation validate;

    public DoctorReportGenerator(DoctorDutyManagement docDuty, Validation validate) {
        this.DocDuty = docDuty;
        this.validate = validate;
    }

    public double yearlyAttendanceRate(String doctorID, int year,
                                       boolean autoCreateWeekdays,
                                       StringBuilder sb) {
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

        int denom = present + unavailable;
        double rate = (denom == 0) ? 0.0 : (present / (double) denom) * 100;

        if (sb != null) {
            sb.append("Attendance Report — Doctor ").append(doctorID)
              .append(" (").append(year).append(")\n")
              .append("Total Scheduled Shifts: ").append(scheduled).append('\n')
              .append("Present Shifts      : ").append(present).append('\n')
              .append("Unavailable Shifts  : ").append(unavailable).append('\n')
              .append("On Leave Shifts     : ").append(onLeave).append('\n')
              .append(String.format("Attendance: %.2f%%\n\n", rate));
        }
        return rate;
    }
}
