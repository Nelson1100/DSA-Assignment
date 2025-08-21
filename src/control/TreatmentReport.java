package control;

import adt.QueueIterator;
import entity.Gender;
import entity.Patient;
import entity.PatientHistory;
import entity.TreatmentRecord;
import entity.VisitType;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class TreatmentReport {

    private final MedicalTreatmentManagement mtm;

    public TreatmentReport(MedicalTreatmentManagement mtm) {
        this.mtm = mtm;
    }

    /** Report #1 — Waiting summary (count + earliest arrival) */
    public String reportWaitingSummary() {
        int count = 0;
        java.time.LocalTime earliest = null;

        QueueIterator<Patient> it = mtm.waitingIterator();
        while (it.hasNext()) {
            Patient p = it.getNext();
            count++;
            if (earliest == null || p.getArrivalTime().isBefore(earliest)) {
                earliest = p.getArrivalTime();
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("=== Report: Waiting Summary ===\n");
        sb.append("Patients currently waiting : ").append(count).append("\n");
        sb.append("Earliest arrival time      : ").append(earliest == null ? "-" : earliest).append("\n");
        sb.append("--------------------------------");
        return sb.toString();
    }

    public String reportDailyTreatmentSummary(LocalDate date) {
        if (date == null) date = LocalDate.now();

        int patientsTreatedToday = 0;
        int totalTreatmentsToday = 0;

        int walkInCount = 0;
        int appointmentCount = 0;

        int maleCount = 0;
        int femaleCount = 0;
        int otherGenderCount = 0;

        int ageSum = 0;
        int ageCount = 0;

        QueueIterator<PatientHistory> histIt = mtm.historiesIterator();
        while (histIt.hasNext()) {
            PatientHistory ph = histIt.getNext();
            int n = countRecordsOnDate(ph, date);
            if (n > 0) {
                patientsTreatedToday++;
                totalTreatmentsToday += n;

                VisitType vt = ph.getPatient().getVisitType();
                if (vt == VisitType.WALK_IN) walkInCount++;
                else appointmentCount++;

                Gender g = ph.getPatient().getGender();
                if (g == Gender.MALE) maleCount++;
                else if (g == Gender.FEMALE) femaleCount++;
                else otherGenderCount++;

                ageSum += ph.getPatient().getAge();
                ageCount++;
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("=== Report: Daily Treatment Summary (").append(date).append(") ===\n");
        sb.append("Patients treated today       : ").append(patientsTreatedToday).append("\n");
        sb.append("Total treatment events today : ").append(totalTreatmentsToday).append("\n");
        sb.append("By visit type -> WALK_IN     : ").append(walkInCount).append("\n");
        sb.append("               APPOINTMENT   : ").append(appointmentCount).append("\n");
        sb.append("By gender     -> MALE        : ").append(maleCount).append("\n");
        sb.append("               FEMALE        : ").append(femaleCount).append("\n");
        sb.append("               OTHER         : ").append(otherGenderCount).append("\n");
        sb.append("Average age of treated       : ").append(ageCount == 0 ? "-" : (ageSum / ageCount)).append("\n");
        sb.append("--------------------------------------------------------");
        return sb.toString();
    }

    public String patientHistoryReport(String patientID) {
        PatientHistory ph = mtm.findHistory(patientID);
        if (ph == null) {
            return "Patient with ID '" + patientID + "' not found.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append(ph.getPatient()).append("\n");
        sb.append("Treatment History:\n");

        var q = ph.getRecords();
        if (q.isEmpty()) {
            sb.append("  (No treatment history)");
            return sb.toString();
        }

        QueueIterator<TreatmentRecord> rit = ((adt.LinkedQueue<TreatmentRecord>) q).getIterator();
        while (rit.hasNext()) {
            sb.append("  - ").append(rit.getNext()).append("\n");
        }
        return sb.toString();
    }

    private int countRecordsOnDate(PatientHistory ph, LocalDate date) {
        int count = 0;
        var q = ph.getRecords();
        var it = ((adt.LinkedQueue<TreatmentRecord>) q).getIterator();
        while (it.hasNext()) {
            TreatmentRecord tr = it.getNext();
            java.time.LocalDate trDate = tr.getDateTime().toLocalDate();
            if (trDate.equals(date)) count++;
        }
        return count;
    }
}
