package control;

import adt.AVLTree;
import entity.DoctorDuty;
import entity.Availability;
import entity.Doctor;
import entity.Shift;
import entity.keys.DutyByDoctorDateShift;
import entity.keys.DutyByDateShift;
import utility.Validation;
import java.time.*;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author Nelson Cheng Ming Jian
 */
public class DoctorDutyManagement {
    // Unique index: (doctorID, date, shift) -> DoctorDuty
    private final AVLTree<DutyByDoctorDateShift> idxByDoctorDateShift = new AVLTree<>();
    // Grouped index: (date, shift) -> bucket of duties
    private final AVLTree<DutyByDateShift> idxByDateShift = new AVLTree<>();
    Validation validate = new Validation();
    DoctorManagement dm = new DoctorManagement();

    // Adding a new duty
    public boolean addDuty(DoctorDuty duty) {
        if (duty == null)
            return false;

        DutyByDoctorDateShift uniqueKey =  new DutyByDoctorDateShift(duty.getDoctorID(), duty.getDate(), duty.getShift(), duty);

        if (idxByDoctorDateShift.find(uniqueKey) != null)
            return false;
        
        if (!idxByDoctorDateShift.insert(uniqueKey))
            return false;

        DutyByDateShift groupProbe = new DutyByDateShift(duty.getDate(), duty.getShift());
        DutyByDateShift groupNode = idxByDateShift.find(groupProbe);
        boolean insertedGroupNode = false;

        if (groupNode == null) {
            groupNode = groupProbe;
            insertedGroupNode = idxByDateShift.insert(groupNode);
            if (!insertedGroupNode) {
                idxByDoctorDateShift.delete(uniqueKey);
                return false;
            }
        }

        boolean groupInsertion = groupNode.add(uniqueKey);
        if (!groupInsertion) {
            idxByDoctorDateShift.delete(uniqueKey);

            if (insertedGroupNode && groupNode.isEmpty()) {
                idxByDateShift.delete(groupNode);
            }
            return false;
        }
        return true;
    }

    // Remove certain duty
    public boolean removeDuty(String doctorID, LocalDate date, Shift shift) {
        DutyByDoctorDateShift searchKey = new DutyByDoctorDateShift(doctorID, date, shift, null);
        DutyByDoctorDateShift found = idxByDoctorDateShift.find(searchKey);
        
        if (found == null)
            return false;

        boolean deletion = idxByDoctorDateShift.delete(found);
        
        if (!deletion) 
            return false;

        DutyByDateShift groupProbe = new DutyByDateShift(date, shift);
        DutyByDateShift groupNode = idxByDateShift.find(groupProbe);
        
        if (groupNode != null) {
            groupNode.remove(found);
            if (groupNode.isEmpty()) {
                idxByDateShift.delete(groupNode);
            }
        }
        return true;
    }

    // SEARCH #1: exact search by (doctorID, date, shift).
    public DoctorDuty searchDutyByDoctorDateShift(String doctorID, LocalDate date, Shift shift) {
        DutyByDoctorDateShift searchKey = new DutyByDoctorDateShift(doctorID, date, shift, null);
        DutyByDoctorDateShift found = idxByDoctorDateShift.find(searchKey);
        return (found == null) ? null : found.getDuty();
    }

    // SEARCH #2: list all duties for a (date, shift).
    public DoctorDuty[] searchDutiesByDateShift(LocalDate date, Shift shift) {
        DutyByDateShift searchKey = new DutyByDateShift(date, shift);
        DutyByDateShift node = idxByDateShift.find(searchKey);
        
        if (node == null)
            return new DoctorDuty[0];
        
        return node.toDutyArray();
    }
    
    // Update availability for an existing duty.
    public boolean updateAvailability(String doctorID, LocalDate date, Shift shift, Availability newAvailability) {
        if (!date.isBefore(LocalDate.now())){
            DutyByDoctorDateShift searchKey = new DutyByDoctorDateShift(doctorID, date, shift, null);
            DutyByDoctorDateShift found = idxByDoctorDateShift.find(searchKey);

            if (found != null) {
                found.getDuty().setAvailability(newAvailability);
                return true;
            }

            DoctorDuty created = new DoctorDuty(doctorID, date, shift, newAvailability);
            return addDuty(created);
        }
        return false;
    }

    // Build a monthly duty roster
    public String buildMonthlyRosterByWeeks(String doctorID, int year, int month) {
        YearMonth ym = YearMonth.of(year, month);
        int days = ym.lengthOfMonth();
        Shift[] shifts = Shift.values();

        // Auto-create weekday defaults = true (you can flip this to false if you don't want defaults)
        DoctorDuty[][] roster = MonthlyRosterTableMatrix(doctorID, year, month, true);

        StringBuilder sb = new StringBuilder(8192);
        Doctor doctorDuty = new Doctor(doctorID.trim(), "", "", "", null, "");
        Doctor found = dm.findDoctor(doctorDuty);
        String doctorName = (found != null ? found.getDoctorName() : ("(" + doctorID + ")"));
        sb.append("Duty Roster for Dr. ").append(doctorName)
          .append(" — ").append(ym).append('\n')
          .append("Legend: ✅ Available   ❌ Unavailable   ⭕ On Leave   - No record\n");

        int weekNo = 1;
        int day = 1;
        while (day <= days) {
            int start = day;
            int end = Math.min(day + 6, days);

            // Week header
            sb.append('\n')
              .append("Week ").append(weekNo++)
              .append(" (").append(ym.atDay(start)).append(" – ").append(ym.atDay(end)).append(")\n");

            // Column headers
            sb.append(fixed("Shift", 10));
            for (int d = start; d <= end; d++) {
                LocalDate date = ym.atDay(d);
                String hdr = two(d) + " " + dow3(date);
                sb.append(fixed(hdr, 10));
            }
            sb.append('\n');

            // Rows per shift
            for (int s = 0; s < shifts.length; s++) {
                sb.append(fixed(shifts[s].name(), 10));
                for (int d = start; d <= end; d++) {
                    DoctorDuty duty = roster[d - 1][s];
                    sb.append(fixed(cell(duty), 10));
                }
                sb.append('\n');
            }

            day = end + 1;
        }

        return sb.toString();
    }

    private static String cell(DoctorDuty duty) {
        if (duty == null)
            return "-";
        
        return switch (duty.getAvailability()) {
            case AVAILABLE -> "✅";
            case UNAVAILABLE -> "❌";
            case ON_LEAVE -> "⭕";
            default -> "-";
        };
    }

    // left-pad/truncate to fixed width so columns line up decently even in proportional fonts
    private static String fixed(String s, int width) {
        if (s == null)
            s = "";
        
        if (s.length() >= width)
            return s.substring(0, width);
        
        StringBuilder b = new StringBuilder(width);
        b.append(s);
        while (b.length() < width)
            b.append(' ');
        
        return b.toString();
    }

    private static String two(int n) {
        return (n < 10) ? ("0" + n) : Integer.toString(n);
    }

    private static String dow3(LocalDate d) {
        return switch (d.getDayOfWeek()) {
            case MONDAY -> "Mon";
            case TUESDAY -> "Tue";
            case WEDNESDAY -> "Wed";
            case THURSDAY -> "Thu";
            case FRIDAY -> "Fri";
            case SATURDAY -> "Sat";
            default -> "Sun";
        };
    }
    
    private DoctorDuty[][] MonthlyRosterTableMatrix(String doctorID, int year, int month, boolean autoCreateWeedays){
        YearMonth ym = YearMonth.of(year, month);
        int days = ym.lengthOfMonth();
        Shift[] shifts = Shift.values();
        DoctorDuty[][] roster = new DoctorDuty[days][shifts.length];

        for (int day = 1; day <= days; day++) {
            LocalDate date = ym.atDay(day);
            for (int i = 0; i < shifts.length; i++) {
                Shift shift = shifts[i];
                roster[day - 1][i] = autoCreateWeedays
                        ? WeekdayDuty(doctorID, date, shift)
                        : findDuty(doctorID, date, shift);
            }
        }
        return roster;
    }
    
    // Assuming every weekday has duty
    public DoctorDuty WeekdayDuty(String doctorID, LocalDate date, Shift shift){
        DoctorDuty docDuty = findDuty(doctorID, date, shift);

        // guard: if already exists, return
        if (docDuty != null)
            return docDuty;

        // only weekdays
        if (!validate.isWeekday(date))
            return null;

        // safe parse of registration date from doctorID (if format matches), else skip
        try {
            String regDateStr = doctorID.substring(1, doctorID.length() - 4);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            LocalDate regDate = LocalDate.parse(regDateStr, formatter);
            if (date.isBefore(regDate))
                return null;
        } catch (Exception ignore) {
            // ignore if format not as expected
        }

        DoctorDuty created = new DoctorDuty(doctorID, date, shift, Availability.AVAILABLE);
        return addDuty(created) ? created : null;
    }
    
    private DoctorDuty findDuty(String doctorID, LocalDate date, Shift shift) {
        DutyByDoctorDateShift searchKey = new DutyByDoctorDateShift(doctorID, date, shift, null);
        DutyByDoctorDateShift leaf = idxByDoctorDateShift.find(searchKey);
        return (leaf == null) ? null : leaf.getDuty();
    }
}