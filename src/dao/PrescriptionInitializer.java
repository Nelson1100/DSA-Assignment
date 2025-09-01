package dao;

import adt.LinkedQueue;
import control.MedicineDispenser;
import entity.*;
import utility.IDGenerator;
import utility.IDType;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class PrescriptionInitializer {

    public static void initialize(LinkedQueue<Prescription> queue, MedicineDispenser dispenser) {
        // Hardcoded prescriptions
        if (queue == null) {
            return;
        }

        // optional: avoid duplicates when re-running
        try {
            queue.clear();
        } catch (UnsupportedOperationException ignore) {
        }

        final String TODAY = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        int[] seq = {1}; // wrap in array so it's mutable

        java.util.function.Supplier<String> nextPR = () -> {
            return "PR" + TODAY + String.format("%04d", seq[0]++);
        };

        Prescription p1 = new Prescription(nextPR.get(), "P0001", "D0001");
        p1.addItem(new PrescriptionItem(MedicineName.PARACETAMOL, 20, Instruction.AFTER_MEAL));
        p1.addItem(new PrescriptionItem(MedicineName.IBUPROFEN, 10, Instruction.EVERY_8_HOURS));
        queue.enqueue(p1);

        Prescription p2 = new Prescription(nextPR.get(), "P0002", "D0002");
        p2.addItem(new PrescriptionItem(MedicineName.VITAMIN_C, 100, Instruction.MORNING_ONLY));
        queue.enqueue(p2);

        Prescription p3 = new Prescription(nextPR.get(), "P0003", "D0003");
        p3.addItem(new PrescriptionItem(MedicineName.COUGH_SYRUP, 5, Instruction.WHEN_NEEDED));
        queue.enqueue(p3);

        Prescription p4 = new Prescription(nextPR.get(), "P0004", "D0001");
        p4.addItem(new PrescriptionItem(MedicineName.IBUPROFEN, 8, Instruction.EVERY_6_HOURS));
        queue.enqueue(p4);

        Prescription p5 = new Prescription(nextPR.get(), "P0005", "D0002");
        p5.addItem(new PrescriptionItem(MedicineName.CETIRIZINE, 5, Instruction.ONCE_DAILY));
        queue.enqueue(p5);

        Prescription p6 = new Prescription(nextPR.get(), "P0006", "D0003");
        p6.addItem(new PrescriptionItem(MedicineName.INSULIN, 2, Instruction.BEFORE_MEAL));
        p6.addItem(new PrescriptionItem(MedicineName.ASPIRIN, 99, Instruction.THREE_TIMES_DAILY));
        queue.enqueue(p6);

        Prescription p7 = new Prescription(nextPR.get(), "P0007", "D0001");
        p7.addItem(new PrescriptionItem(MedicineName.HYDROCORTISONE, 3, Instruction.AFTER_MEAL));
        queue.enqueue(p7);

        Prescription p8 = new Prescription(nextPR.get(), "P0008", "D0002");
        p8.addItem(new PrescriptionItem(MedicineName.AMOXICILLIN, 10, Instruction.BEFORE_MEAL));
        queue.enqueue(p8);

        // Add random dispensed
        addRandomDispensedPrescriptions(dispenser, 20);
    }

    private static void addRandomDispensedPrescriptions(MedicineDispenser dispenser, int count) {
        Random random = new Random();
        MedicineName[] meds = MedicineName.values();
        Instruction[] instructions = Instruction.values();

        LocalDate[] dates = new LocalDate[count];
        for (int i = 0; i < count; i++) {
            dates[i] = LocalDate.now().minusDays(random.nextInt(30));
        }

        // Manual sort (bubble sort)
        for (int i = 0; i < count - 1; i++) {
            for (int j = 0; j < count - i - 1; j++) {
                if (dates[j].isAfter(dates[j + 1])) {
                    LocalDate temp = dates[j];
                    dates[j] = dates[j + 1];
                    dates[j + 1] = temp;
                }
            }
        }

        // Track sequence per unique date (up to 31 days max)
        LocalDate[] trackedDates = new LocalDate[31];
        int[] dateCounts = new int[31];
        int tracked = 0;

        for (int i = 0; i < count; i++) {
            LocalDate date = dates[i];
            int seq = 1;

            boolean found = false;
            for (int j = 0; j < tracked; j++) {
                if (trackedDates[j].equals(date)) {
                    dateCounts[j]++;
                    seq = dateCounts[j];
                    found = true;
                    break;
                }
            }

            if (!found) {
                trackedDates[tracked] = date;
                dateCounts[tracked] = 1;
                seq = 1;
                tracked++;
            }

            String yyyymmdd = date.format(DateTimeFormatter.BASIC_ISO_DATE);
            String prid = "PR" + yyyymmdd + String.format("%04d", seq);
            String pid = String.format("P9%03d", i + 1);
            String did = String.format("D9%03d", i + 1);

            Prescription p = new Prescription(prid, pid, did);
            int numItems = 1 + random.nextInt(2);

            MedicineName[] medUsed = new MedicineName[numItems];
            int[] qtyUsed = new int[numItems];

            for (int j = 0; j < numItems; j++) {
                MedicineName med = meds[random.nextInt(meds.length)];
                int qty = med.getMinDose() + random.nextInt(Math.max(1, med.getMaxDose() - med.getMinDose() + 1));
                Instruction instr = instructions[random.nextInt(instructions.length)];
                p.addItem(new PrescriptionItem(med, qty, instr));

                medUsed[j] = med;
                qtyUsed[j] = qty;
            }

            p.setStatusDispensed();

            DispensedRecord record = new DispensedRecord(
                    IDGenerator.next(IDType.DISPENSEDRECORD),
                    prid, pid, did,
                    date.atTime(random.nextInt(24), random.nextInt(60)),
                    medUsed,
                    qtyUsed,
                    true,
                    null,
                    null
            );

            dispenser.getRecordLog().enqueue(record);
        }
    }
}
