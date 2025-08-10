package control;

import entity.Patient;
import entity.VisitType;
import entity.Gender;

import java.time.LocalTime;

public class PatientReportGenerator {
    private final PatientMaintenance pm;
    
    public PatientReportGenerator(PatientMaintenance pm) {
        this.pm = pm;
    }
    
    /* ---------- Report 1: Visit Type Summary (WALK_IN vs APPOINTMENT) ---------- */
    
    public String summaryByVisitType() {
        int walkIn = pm.countByVisitType(VisitType.WALK_IN);
        int appt = pm.countByVisitType(VisitType.APPOINTMENT);
        int total = walkIn + appt;
        
        return String.format(
            "Visit Type Summary%n" +
            "- Walk-in     : %d%n" +
            "- Appointment : %d%n" +
            "- Total       : %d",
            walkIn, appt, total
        );
    }
    
    /* ---------- Report 2: Average Waiting Time (minutes) for all patients currently in queue ---------- */
    
    public String averageWait(LocalTime now) {
        double avg = pm.avgWaitMinutes(now);
        
        return String.format(
            "Average Waiting Time: %.1f minutes", 
            avg
        );
    }
    
    /* ---------- Report 3: Gender Breakdown (MALE vs FEMALE) ---------- */
    
    public String genderBreakdown() {
        int male = pm.countByGender(Gender.MALE);
        int female = pm.countByGender(Gender.FEMALE);
        int total = male + female;
        
        return String.format(
            "Gender Breakdown%n" +
            "- Male   : %d%n" +
            "- Female : %d%n" +
            "- Total  : %d",
            male, female, total
        );
    }
    
    /* ---------- Helpers for UI ---------- */
    
    public String queueHeadline() {
        int n = pm.getQueueSize();
        return String.format(
            "Queue: %d patient%s", 
            n, (n == 1 ? "" : "s")
        );
    }
    
    public entity.Patient[] previewNext(int n) {
        return pm.peekNextN(n);
    }
}
