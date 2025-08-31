package control;

import entity.Doctor;
import entity.DoctorDuty;
import entity.Shift;
import entity.Specialization;
import entity.Availability;
import java.time.*;
import java.time.format.DateTimeFormatter;
import utility.Validation;
import adt.LinkedQueue;
import adt.AVLTree;

/**
 *
 * @author Nelson Cheng Ming Jian
 */
public class DoctorReportGenerator {
    private final DoctorDutyManagement DocDuty;
    Validation validate = new Validation();
    DoctorManagement dm = new DoctorManagement();
    private static final int WIDTH = 100;

    public DoctorReportGenerator(DoctorDutyManagement docDuty, DoctorManagement dm) {
        this.DocDuty = docDuty;
        this.dm = dm;
    }

    /**
     * Annual attendance summary for a single doctor (clean text).
     */
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
                    String regDateStr = doctorID.substring(1, doctorID.length() - 4);
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
                    LocalDate regDate = LocalDate.parse(regDateStr, formatter);
                    boolean registeredByThatDay = !regDate.isAfter(date);

                    if (duty == null && autoCreateWeekdays && validate.isWeekday(date) && registeredByThatDay)
                        duty = DocDuty.WeekdayDuty(doctorID, date, shift);

                    if (duty == null)
                        continue;

                    scheduled++;
                    switch (duty.getAvailability()) {
                        case AVAILABLE -> present++;
                        case UNAVAILABLE -> unavailable++;
                        case ON_LEAVE -> onLeave++;
                        default -> { }
                    }
                }
            }
        }

        int denom = present + unavailable + onLeave;
        double rate = (denom == 0) ? 0.0 : ((double) present / (double) denom) * 100.0;

        Doctor found = dm.findDoctor(new Doctor(doctorID.trim(), "", "", "", null, ""));
        String name = (found != null && found.getDoctorName() != null && !found.getDoctorName().isBlank())
                        ? found.getDoctorName()
                        : doctorID;

        if (sb != null) {
            sb.append(line('=', WIDTH)).append('\n');
            sb.append(center("Doctor Annual Attendance", WIDTH)).append('\n');
            sb.append(line('-', WIDTH)).append('\n');

            sb.append(kv("Doctor", "Dr. " + name));
            sb.append(kv("Year", Integer.toString(year)));
            sb.append(kv("Total Scheduled Shifts", Integer.toString(scheduled)));
            sb.append(kv("Present Shifts", Integer.toString(present)));
            sb.append(kv("Unavailable Shifts", Integer.toString(unavailable)));
            sb.append(kv("On Leave Shifts", Integer.toString(onLeave)));
            sb.append(kv("Attendance", String.format("%.2f%%", rate)));

            sb.append(line('-', WIDTH)).append('\n').append('\n');
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
                int idx = (docs[i].getSpecialization() == null) ? -1 : docs[i].getSpecialization().ordinal();
                if (idx >= 0) counts[idx]++;
            }
        }

        sb.append(line('=', WIDTH)).append('\n');
        sb.append(center("Clinic Specialization Inventory", WIDTH)).append('\n');
        sb.append(line('-', WIDTH)).append('\n');

        sb.append(String.format("%-32s%-12s%-10s%n", "Specialization", "Doctors", "Percent"));
        sb.append(String.format("%-32s%-12s%-10s%n", "--------------------------------", "--------", "--------"));

        for (int p = 0; p < P; p++) {
            double pct = (totalDoctors == 0) ? 0.0 : (counts[p] * 100.0 / totalDoctors);
            sb.append(String.format("%-32s%-12d%7.2f%%%n", specs[p].name(), counts[p], pct));
        }

        sb.append('\n');
        sb.append(String.format("%-32s%d%n", "TOTAL DOCTORS", totalDoctors));
        sb.append(line('-', WIDTH)).append('\n').append('\n');

        // Visual bars (percentage-scaled, 100% => fixed width)
        specializationDistributionBars(specs, counts, totalDoctors, sb);
    }

    // ---------- specialization visual (LinkedQueue) ----------
    private void specializationDistributionBars(Specialization[] specs, int[] counts, int totalDoctors, StringBuilder sb){
        sb.append(center("Specialization Distribution", WIDTH)).append('\n');
        sb.append(line('-', WIDTH)).append('\n');

        final int barW = 30; // 100% => 30 asterisks
        LinkedQueue<String> lines = new LinkedQueue<>();

        for (int i = 0; i < specs.length; i++){
            double pct = (totalDoctors == 0 ? 0.0 : (counts[i] * 100.0 / totalDoctors));
            int stars = (int)Math.round((pct / 100.0) * barW);
            if (pct > 0.0 && stars == 0) stars = 1;
            String bar = repeat('*', stars);
            String right = String.format("(%.0f%%)", pct);
            lines.enqueue(String.format("%-28s: %-32s %s%n", specs[i].name(), bar, right));
        }

        for (String line : lines) {
            sb.append(line);
        }

        sb.append(line('-', WIDTH)).append('\n');
    }

    public void attendanceRanking(int year, boolean autoCreateWeekdays, StringBuilder sb) {
        if (sb == null) return;

        AVLTree<DoctorAttendance> ranking = new AVLTree<>();

        Doctor[] docs = dm.getAllDoctor();
        if (docs != null) {
            for (Doctor d : docs) {
                if (d == null) continue;
                int[] data = computeAttendance(d.getDoctorID(), year, autoCreateWeekdays);
                double pct = (data[1]==0 ? 0.0 : (data[0]*100.0/data[1]));
                ranking.insert(new DoctorAttendance(d, pct, data[0], data[1]));
            }
        }

        sb.append(line('=', WIDTH)).append('\n');
        sb.append(center("Doctor Attendance Ranking " + year, WIDTH)).append('\n');
        sb.append(line('-', WIDTH)).append('\n');
        sb.append(String.format("%-4s %-25s %-15s %-15s %-10s%n", "No.", "Doctor", "ID", "Present/Total", "Attend%"));
        sb.append(line('-', WIDTH)).append('\n');

        int rank = 1;
        for (DoctorAttendance da : ranking) {
            sb.append(String.format("%-4d %-25s %-15s %5d/%-8d %7.2f%%%n",
                    rank++, da.name, da.id, da.present, da.total, da.pct));
        }

        sb.append(line('-', WIDTH)).append('\n').append('\n');
    }

    // ---------- helpers ----------
    private int[] computeAttendance(String doctorID, int year, boolean autoCreateWeekdays){
        Shift[] shifts = Shift.values();
        int present = 0;
        int total   = 0;

        for (int m = 1; m <= 12; m++){
            YearMonth ym = YearMonth.of(year, m);
            int days = ym.lengthOfMonth();
            for (int d = 1; d <= days; d++){
                LocalDate date = ym.atDay(d);
                for (Shift sh : shifts){
                    DoctorDuty duty = DocDuty.searchDutyByDoctorDateShift(doctorID, date, sh);
                    if (duty == null && autoCreateWeekdays && validate.isWeekday(date))
                        duty = DocDuty.WeekdayDuty(doctorID, date, sh);
                    if (duty == null) continue;
                    total++;
                    if (duty.getAvailability() == Availability.AVAILABLE) present++;
                }
            }
        }
        return new int[]{present,total};
    }

    private static class DoctorAttendance implements Comparable<DoctorAttendance> {
        String name;
        String id;
        int present;
        int total;
        double pct;
        DoctorAttendance(Doctor d, double pct, int present, int total){
            this.name = (d.getDoctorName()==null||d.getDoctorName().isBlank())? d.getDoctorID() : d.getDoctorName();
            this.id   = d.getDoctorID();
            this.pct  = pct;
            this.present = present;
            this.total   = total;
        }
        @Override
        public int compareTo(DoctorAttendance o){
            // higher percentage first; if tie, name ascending
            if (this.pct < o.pct) return 1;
            if (this.pct > o.pct) return -1;
            return this.name.compareTo(o.name);
        }
    }

    private String kv(String key, String value){
        return String.format("%-28s: %s%n", key, value == null ? "-" : value);
    }

    private String line(char c, int n){
        if (n <= 0) return "";
        StringBuilder b = new StringBuilder(n);
        for (int i = 0; i < n; i++) b.append(c);
        return b.toString();
    }

    private String center(String s, int width){
        if (s == null) s = "";
        int pad = Math.max(0, (width - s.length()) / 2);
        StringBuilder b = new StringBuilder(width);
        for (int i = 0; i < pad; i++) b.append(' ');
        b.append(s);
        return b.toString();
    }

    private String repeat(char c, int n){
        if (n <= 0) return "";
        StringBuilder b = new StringBuilder(n);
        for (int i = 0; i < n; i++) b.append(c);
        return b.toString();
    }
}