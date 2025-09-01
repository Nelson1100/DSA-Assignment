package control;

import adt.LinkedQueue;
import adt.QueueInterface;
import adt.QueueIterator;
import entity.PatientHistory;
import entity.TreatmentRecord;

import java.time.LocalDate;
import java.util.Locale;

public class TreatmentReport {
    private final MedicalTreatmentManagement mtm;

    public TreatmentReport(MedicalTreatmentManagement mtm) {
        this.mtm = mtm;
    }

    public String reportDailyTreatmentSummary(LocalDate date) {
        PatientHistory[] all = mtm.listAllHistories();
        int total = 0;
        int uniquePatients = 0;

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("=== Daily Treatment Summary (%s) ===%n", date));

        for (PatientHistory ph : all) {
            int c = ph.countRecordsOn(date);
            if (c > 0) uniquePatients++;
            total += c;
        }

        sb.append(String.format("Date            : %s%n", date));
        sb.append(String.format("Unique patients : %d%n", uniquePatients));
        sb.append(String.format("Total treatments: %d%n", total));
        sb.append("\nDetailed records:\n");

        // list treatments for that date
        for (PatientHistory ph : all) {
            QueueIterator<TreatmentRecord> it = ((LinkedQueue<TreatmentRecord>) ph.getRecords()).getIterator();
            while (it.hasNext()) {
                TreatmentRecord tr = it.getNext();
                if (tr.getDateTime().toLocalDate().equals(date)) {
                    sb.append(String.format("%-10s | %-8s | %s | %s%n",
                            tr.getTreatmentID(), tr.getPatientID(),
                            tr.getDateTime().toLocalTime().truncatedTo(java.time.temporal.ChronoUnit.MINUTES),
                            tr.getDiagnosis() + " / " + tr.getTreatment()));
                }
            }
        }
        return sb.toString();
    }

    public String reportDiagnosisFrequency() {
        DiagnosisCountList counts = new DiagnosisCountList();
        PatientHistory[] all = mtm.listAllHistories();
        for (PatientHistory ph : all) {
            QueueIterator<TreatmentRecord> it = ((LinkedQueue<TreatmentRecord>) ph.getRecords()).getIterator();
            while (it.hasNext()) {
                String dx = it.getNext().getDiagnosis();
                if (dx != null && !dx.trim().isEmpty()) counts.increment(dx.trim());
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append("=== Diagnosis Frequency (All Time) ===\n");
        counts.appendLines(sb);
        if (counts.isEmpty()) sb.append("(No data)\n");
        return sb.toString();
    }

    private static class DiagnosisCountList {
        private static class E { String d; int c; E(String d,int c){this.d=d;this.c=c;} }
        private final QueueInterface<E> q = new LinkedQueue<>();

        void increment(String diagnosis) {
            QueueIterator<E> it = ((LinkedQueue<E>) q).getIterator();
            while (it.hasNext()) {
                E e = it.getNext();
                if (e.d.equalsIgnoreCase(diagnosis)) { e.c++; return; }
            }
            q.enqueue(new E(diagnosis,1));
        }

        boolean isEmpty() { return !((LinkedQueue<E>) q).getIterator().hasNext(); }

        void appendLines(StringBuilder sb) {
            QueueIterator<E> it = ((LinkedQueue<E>) q).getIterator();
            int i = 1;
            while (it.hasNext()) {
                E e = it.getNext();
                sb.append(String.format("%2d) %-30s : %d%n", i++, e.d, e.c));
            }
        }
    }
}
