package control;

import entity.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class PatientReportGenerator {
    private final PatientMaintenance pm;
    
    public PatientReportGenerator(PatientMaintenance pm) {
        this.pm = pm;
    }
    
    public String generateVisitQueueAnalysisReport() {
        StringBuilder sb = new StringBuilder();
        LocalDateTime now = LocalDateTime.now();
        String dateTime = now.format(DateTimeFormatter.ofPattern("EEEE, MMM dd yyyy, hh:mm a"));
        
        int total = pm.getQueueSize();
        int walkIn = pm.countByVisitType(VisitType.WALK_IN);
        int appointment = pm.countByVisitType(VisitType.APPOINTMENT);
        double avgWait = pm.avgWaitMinutes(now);
        long maxWait = pm.maxWaitMinutes(now);
        PatientVisit next = pm.getNextVisit();
        
        // Header
        sb.append("=".repeat(100)).append("\n");
        sb.append(center("TUNKU ABDUL RAHMAN UNIVERSITY OF MANAGEMENT AND TECHNOLOGY", 100)).append("\n");
        sb.append(center("PATIENT MANAGEMENT MODULE", 100)).append("\n\n");
        sb.append(center("VISIT QUEUE ANALYSIS REPORT", 100)).append("\n");
        sb.append(center("---------------------------------", 100)).append("\n\n");
        
        sb.append("Generated at: ").append(dateTime).append("\n");
        sb.append("*".repeat(100)).append("\n\n");
        
        // Queue Snapshot
        sb.append(center("Queue Snapshot", 100)).append("\n");
        sb.append("-".repeat(100)).append("\n");
        sb.append(String.format("%-30s: %d\n", "Total Patients", total));
        sb.append(String.format("%-30s: %d\n", "Total Walk-in Patients", walkIn));
        sb.append(String.format("%-30s: %d\n", "Total Appointment Patients", appointment));
        sb.append(String.format("%-30s: %.1f minutes\n", "Average Wait Time", avgWait));
        sb.append(String.format("%-30s: %d minutes\n\n", "Max Wait Time", maxWait));
        
        if (next != null) {
            sb.append(String.format("%-30s: %s (%s)\n", "Next to be served",
                    next.getPatient().getPatientName(), next.getPatient().getPatientID()));
        } else {
            sb.append(String.format("%-30s: (none)\n", "Next to be served"));
        }
        
        sb.append("-".repeat(100)).append("\n\n");
        
        // Wait Time Distribution Chart (5 ranges)
        sb.append(center("Wait Time Distribution (in minutes)", 100)).append("\n");
        sb.append("-".repeat(100)).append("\n");
        
        int[] ranges = new int[5];
        String[] labels = {"0-5 min", "5–9 min", "10–19 min", "20–29 min", "30+ min"};
        
        PatientVisit[] snap = pm.peekNextN(100);
        for (PatientVisit visit : snap) {
            long w = ChronoUnit.MINUTES.between(visit.getArrivalDateTime(), now);
            if (w < 5) ranges[0]++;
            else if (w < 10) ranges[1]++;
            else if (w < 20) ranges[2]++;
            else if (w < 30) ranges[3]++;
            else ranges[4]++;
        }
        
        for (int i = 0; i < 5; i++) {
            sb.append(String.format("%-9s: %-60s (%d)\n", labels[i], "*".repeat(ranges[i]), ranges[i]));
        }
        
        sb.append("-".repeat(100)).append("\n\n");
        
        // Top 5 Longest Waiting Patients
        sb.append(center("Top 5 Longest-Waiting Patients", 100)).append("\n");
        sb.append("-".repeat(100)).append("\n");
        sb.append(String.format("%-5s %-20s %-24s %-10s\n", "No.", "ID", "Patient Name", "Wait (min)"));
        sb.append("-".repeat(100)).append("\n");
        
        
        sortByWaitTimeDesc(snap, now);
        
        for (int i = 0; i < Math.min(5, snap.length); i++) {
            PatientVisit v = snap[i];
            long wait = ChronoUnit.MINUTES.between(v.getArrivalDateTime(), now);
            sb.append(String.format("%-5d %-20s %-24s %10d\n",
                    i + 1,
                    v.getPatient().getPatientID(),
                    v.getPatient().getPatientName(),
                    wait
            ));
        }
        sb.append("-".repeat(100)).append("\n\n");
        
        sb.append("*".repeat(100)).append("\n");
        sb.append(center("END OF REPORT", 100)).append("\n");
        sb.append("=".repeat(100));
        
        return sb.toString();
    }
    
    public String generatePatientSummaryReport() {
        StringBuilder sb = new StringBuilder();
        LocalDateTime now = LocalDateTime.now();
        String dateTime = now.format(DateTimeFormatter.ofPattern("EEEE, MMM dd yyyy, hh:mm a"));
        
        Patient[] patients = pm.getAllPatientsSortedByID(false);
        PatientVisit[] visits = pm.getAllVisits();
        
        int totalPatients = patients.length;
        int maleCount = 0;
        int femaleCount = 0;
        int totalAge = 0;
        int minAge = Integer.MAX_VALUE;
        int maxAge = Integer.MIN_VALUE;
        int[] ageGroup = new int[4];
        
        for (Patient p : patients) {
            int age = p.getAge();
            totalAge += age;
            
            if (age < minAge) minAge = age;
            if (age > maxAge) maxAge = age;
            
            if (age <= 18) ageGroup[0]++;
            else if (age <= 30) ageGroup[1]++;
            else if (age <= 50) ageGroup[2]++;
            else ageGroup[3]++;
            
            if (p.getGender() == Gender.MALE) maleCount++;
            else femaleCount++;
        }
        
        int totalVisits = visits.length;
        String mostFrequent = "";
        String leastFrequent = "";
        int maxCount = 0;
        int minCount = Integer.MAX_VALUE;

        StringBuilder most = new StringBuilder();
        StringBuilder least = new StringBuilder();
        
        for (Patient p : patients) {
            int count = pm.countVisitsByID(p.getPatientID());
            if (count > maxCount) {
                maxCount = count;
                most.setLength(0);
                most.append(String.format(">> %s (%s) - %d visits\n", p.getPatientName(), p.getPatientID(), count));
            } else if (count == maxCount && maxCount > 0) {
                most.append(String.format(">> %s (%s) - %d visits\n", p.getPatientName(), p.getPatientID(), count));
            }
            if (count == 1) {
                least.append(String.format(">> %s (%s)\n", p.getPatientName(), p.getPatientID()));
            }
        }
        
        LocalDateTime earliest = null;
        LocalDateTime latest = null;

        for (PatientVisit v : visits) {
            LocalDateTime dt = v.getArrivalDateTime();
            
            if (earliest == null || dt.isBefore(earliest)) earliest = dt;
            if (latest == null || dt.isAfter(latest)) latest = dt;
        }
        
        // Header
        sb.append("=".repeat(100)).append("\n");
        sb.append(center("TUNKU ABDUL RAHMAN UNIVERSITY OF MANAGEMENT AND TECHNOLOGY", 100)).append("\n");
        sb.append(center("PATIENT MANAGEMENT MODULE", 100)).append("\n\n");
        sb.append(center("PATIENT SUMMARY REPORT", 100)).append("\n");
        sb.append(center("----------------------------", 100)).append("\n\n");
        
        sb.append("Generated at: ").append(dateTime).append("\n");
        sb.append("*".repeat(100)).append("\n\n");
        
        // Patient Demographics
        sb.append(center("Patient Demographics", 100)).append("\n");
        sb.append("-".repeat(100)).append("\n");
        sb.append(String.format("%-30s: %d\n", "Total Registered Patients", totalPatients));
        sb.append(String.format("%-30s: Male - %d, Female - %d\n", "Gender Breakdown", maleCount, femaleCount));
        
        sb.append("\nAge Statistics:\n");
        sb.append(String.format(" %-29s: %.1f\n", "Average Age", totalPatients > 0 ? (double) totalAge / totalPatients : 0));
        sb.append(String.format(" %-29s: %d\n", "Youngest Age", totalPatients > 0 ? minAge : 0));
        sb.append(String.format(" %-29s: %d\n", "Oldest Age", totalPatients > 0 ? maxAge : 0));
        
        sb.append("\nAge Group Distribution:\n");
        String[] ageLabels = {"0–18", "19–30", "31–50", "51+ "};
        for (int i = 0; i < ageGroup.length; i++) {
            sb.append(String.format(" %-6s : %-40s (%d)\n", ageLabels[i], "*".repeat(ageGroup[i]), ageGroup[i]));
        }
        sb.append("-".repeat(100)).append("\n\n");
        
        // Visit Frequency
        sb.append(center("Visit Frequency", 100)).append("\n");
        sb.append("-".repeat(100)).append("\n");
        sb.append(String.format("%-30s: %d\n", "Total Number of Visits", totalVisits));
        sb.append("\nMost Frequent Visitors:\n").append(most);
        sb.append("\nLeast Frequent Visitors (1-time):\n").append(least);
        sb.append("-".repeat(100)).append("\n\n");
        
        // First vs Most Recent Visit
        sb.append(center("First vs Most Recent Visit", 100)).append("\n");
        sb.append("-".repeat(100)).append("\n");
        sb.append(String.format("%-30s: %s\n", "Earliest Visit Date", earliest != null ? earliest.format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a")) : "(none)"));
        sb.append(String.format("%-30s: %s\n", "Most Recent Visit Date", latest != null ? latest.format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a")) : "(none)"));
        sb.append("-".repeat(100)).append("\n\n");
        
        sb.append("*".repeat(100)).append("\n");
        sb.append(center("END OF REPORT", 100)).append("\n");
        sb.append("=".repeat(100));
        
        return sb.toString();
    }
    
    private void sortByWaitTimeDesc(PatientVisit[] arr, LocalDateTime now) {
        for (int i = 0; i < arr.length - 1; i++) {
            int max = i;
            long wMax = ChronoUnit.MINUTES.between(arr[i].getArrivalDateTime(), now);
            
            for (int j = i + 1; j < arr.length; j++) {
                long w = ChronoUnit.MINUTES.between(arr[j].getArrivalDateTime(), now);
                if (w > wMax) {
                    max = j;
                    wMax = w;
                }
            }
            
            PatientVisit temp = arr[i];
            arr[i] = arr[max];
            arr[max] = temp;
        }
    }
    
    private String center(String text, int width) {
        int pad = Math.max(0, (width - text.length()) / 2);
        return " ".repeat(pad) + text;
    }
}