package control;

import adt.LinkedQueue;
import adt.QueueInterface;
import adt.QueueIterator;
import entity.PatientHistory;
import entity.TreatmentRecord;

import java.time.LocalDate;

public class TreatmentReport {

    private final MedicalTreatmentManagement control;

    public TreatmentReport(MedicalTreatmentManagement control) {
        this.control = control;
    }

    // Summary of treatment for a SPECIFIC date
    public String reportDailyTreatmentSummary(LocalDate date) {
        int totalTreatments = 0;
        int uniquePatients = 0;

        PatientHistory[] all = control.listAllHistories();
        if (all == null || all.length == 0) {
            return "=== Daily Treatment Summary ===\n"
                 + "Date            : " + date + "\n"
                 + "Unique patients : 0\n"
                 + "Total treatments: 0\n";
        }

        for (PatientHistory ph : all) {
            int c = ph.countRecordsOn(date);   
            if (c > 0) uniquePatients++;
            totalTreatments += c;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("=== Daily Treatment Summary ===\n")
          .append("Date            : ").append(date).append("\n")
          .append("Unique patients : ").append(uniquePatients).append("\n")
          .append("Total treatments: ").append(totalTreatments).append("\n");
        return sb.toString();
    }

    // diagnosis report to show frequency of dianosis across all histories
    public String reportDiagnosisFrequency() {
        DiagnosisCountList counts = new DiagnosisCountList();

        PatientHistory[] all = control.listAllHistories();
        if (all != null) {
            for (PatientHistory ph : all) {
                QueueInterface<TreatmentRecord> recQ = ph.getRecords();
                QueueIterator<TreatmentRecord> itR =
                        ((LinkedQueue<TreatmentRecord>) recQ).getIterator();
                while (itR.hasNext()) {
                    String dx = itR.getNext().getDiagnosis();
                    if (dx != null && !dx.trim().isEmpty()) {
                        counts.increment(dx.trim());
                    }
                }
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("=== Diagnosis Frequency (All Time) ===\n");
        counts.appendLines(sb);
        if (counts.isEmpty()) sb.append("(No data)\n");
        return sb.toString();
    }

    private static class DiagnosisCountList {
        private static class Entry {
            String diagnosis;
            int count;
            Entry(String d, int c){ diagnosis = d; count = c; }
        }
        private final QueueInterface<Entry> q = new LinkedQueue<>();

        void increment(String diagnosis) {
            QueueIterator<Entry> it = ((LinkedQueue<Entry>) q).getIterator();
            while (it.hasNext()) {
                Entry e = it.getNext();
                if (e.diagnosis.equalsIgnoreCase(diagnosis)) {
                    e.count++;
                    return;
                }
            }
            q.enqueue(new Entry(diagnosis, 1));
        }

        boolean isEmpty() {
            return !((LinkedQueue<Entry>) q).getIterator().hasNext();
        }

        void appendLines(StringBuilder sb) {
            QueueIterator<Entry> it = ((LinkedQueue<Entry>) q).getIterator();
            int i = 1;
            while (it.hasNext()) {
                Entry e = it.getNext();
                sb.append(String.format("%d) %-30s : %d%n", i++, e.diagnosis, e.count));
            }
        }
    }
}
