package dao;

import adt.LinkedQueue;
import control.MedicineDispenser;
import entity.*;
import utility.IDGenerator;
import utility.IDType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Random;

public class PrescriptionInitializer {

    public static void initialize(LinkedQueue<Prescription> queue, MedicineDispenser dispenser) {
        // ========== HARDCODED ==========
        // Valid prescription (normal stock)
        Prescription p1 = new Prescription("PR202508210001", "P0001", "D0001");
        p1.addItem(new PrescriptionItem(MedicineName.PARACETAMOL, 20, Instruction.AFTER_MEAL));
        p1.addItem(new PrescriptionItem(MedicineName.IBUPROFEN, 10, Instruction.EVERY_8_HOURS));
        queue.enqueue(p1);

// Overdose prescription (FAIL: VITAMIN_C > max allowed)
        Prescription p2 = new Prescription("PR202508210002", "P0002", "D0002");
        p2.addItem(new PrescriptionItem(MedicineName.VITAMIN_C, 100, Instruction.MORNING_ONLY)); // overdose!
        queue.enqueue(p2);

// Invalid stock prescription (FAIL: COUGH_SYRUP not in stock or expired-only)
        Prescription p3 = new Prescription("PR202508210003", "P0003", "D0003");
        p3.addItem(new PrescriptionItem(MedicineName.COUGH_SYRUP, 5, Instruction.WHEN_NEEDED));
        queue.enqueue(p3);

// Mixed valid
        Prescription p4 = new Prescription("PR202508210004", "P0004", "D0001");
        p4.addItem(new PrescriptionItem(MedicineName.IBUPROFEN, 8, Instruction.EVERY_6_HOURS));
        queue.enqueue(p4);

// Low stock but valid
        Prescription p5 = new Prescription("PR202508210005", "P0005", "D0002");
        p5.addItem(new PrescriptionItem(MedicineName.CETIRIZINE, 5, Instruction.ONCE_DAILY));
        queue.enqueue(p5);

// Mixed: 1 valid, 1 overdose
        Prescription p6 = new Prescription("PR202508210006", "P0006", "D0003");
        p6.addItem(new PrescriptionItem(MedicineName.INSULIN, 2, Instruction.BEFORE_MEAL));
        p6.addItem(new PrescriptionItem(MedicineName.ASPIRIN, 99, Instruction.THREE_TIMES_DAILY)); // overdose
        queue.enqueue(p6);

// Borderline valid
        Prescription p7 = new Prescription("PR202508210007", "P0007", "D0001");
        p7.addItem(new PrescriptionItem(MedicineName.HYDROCORTISONE, 3, Instruction.AFTER_MEAL));
        queue.enqueue(p7);

// Good valid case
        Prescription p8 = new Prescription("PR202508210008", "P0008", "D0002");
        p8.addItem(new PrescriptionItem(MedicineName.AMOXICILLIN, 10, Instruction.BEFORE_MEAL));
        queue.enqueue(p8);

        // ========== RANDOM 20 DISPENSED ==========
        addRandomDispensedPrescriptions(dispenser, 20);
    }

    private static void addRandomDispensedPrescriptions(MedicineDispenser dispenser, int count) {
        Random random = new Random();
        MedicineName[] meds = MedicineName.values();
        Instruction[] instructions = Instruction.values();

        // Step 1: Generate random LocalDates within past 30 days
        LocalDate[] dates = new LocalDate[count];
        for (int i = 0; i < count; i++) {
            dates[i] = LocalDate.now().minusDays(random.nextInt(30));
        }

        // Step 2: Sort dates (ascending)
        for (int i = 0; i < count - 1; i++) {
            for (int j = 0; j < count - i - 1; j++) {
                if (dates[j].isAfter(dates[j + 1])) {
                    LocalDate temp = dates[j];
                    dates[j] = dates[j + 1];
                    dates[j + 1] = temp;
                }
            }
        }

        // Step 3: Use HashMap for date -> sequence tracking
        HashMap<String, Integer> dailyCounter = new HashMap<>();

        // Step 4: Generate prescriptions and add to dispenser recordLog
        for (int i = 0; i < count; i++) {
            LocalDate date = dates[i];
            String yyyymmdd = date.format(DateTimeFormatter.BASIC_ISO_DATE); // YYYYMMDD

            // Update sequence number
            int seq = dailyCounter.getOrDefault(yyyymmdd, 0) + 1;
            dailyCounter.put(yyyymmdd, seq);

            // Generate ID
            String prid = "PR" + yyyymmdd + String.format("%04d", seq);
            String pid = String.format("P9%03d", i + 1);
            String did = String.format("D9%03d", i + 1);

            Prescription p = new Prescription(prid, pid, did);

            int numItems = 1 + random.nextInt(2); // 1–2 items
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

            // Add to log
            DispensedRecord record = new DispensedRecord(
                    IDGenerator.next(IDType.DISPENSEDRECORD),
                    prid, pid, did,
                    date.atTime(random.nextInt(24), random.nextInt(60)),
                    medUsed,
                    qtyUsed,
                    true,
                    null
            );

            dispenser.getRecordLog().enqueue(record);
        }
    }
}
