package control;

import entity.Availability;
import entity.DoctorDuty;
import entity.Shift;
import java.time.LocalDate;

public class TestDoctorDutyManagement {

    public static void main(String[] args) {
        DoctorDutyManagement dm = new DoctorDutyManagement();

        LocalDate d1 = LocalDate.of(2025, 8, 22);
        LocalDate d2 = LocalDate.of(2025, 8, 23);

        // --- 1) ADD ---
        println("=== ADD ===");
        DoctorDuty A = new DoctorDuty("D001", d1, Shift.MORNING, Availability.AVAILABLE);
        DoctorDuty B = new DoctorDuty("D002", d1, Shift.MORNING, Availability.AVAILABLE);
        DoctorDuty C = new DoctorDuty("D001", d1, Shift.AFTERNOON, Availability.UNAVAILABLE);

        assertTrue(dm.addDuty(A), "Add A (D001, 2025-08-22, MORNING)");
        assertTrue(dm.addDuty(B), "Add B (D002, 2025-08-22, MORNING)");
        assertTrue(dm.addDuty(C), "Add C (D001, 2025-08-22, AFTERNOON)");

        // duplicate (same doctorID/date/shift) should fail
        DoctorDuty A_dup = new DoctorDuty("D001", d1, Shift.MORNING, Availability.AVAILABLE);
        assertFalse(dm.addDuty(A_dup), "Add A duplicate should fail");

        // sanity: indexes valid?

        // --- 2) SEARCH (exact) ---
        println("\n=== SEARCH exact (doctorID+date+shift) ===");
        DoctorDuty foundA = dm.searchDutyByDoctorDateShift("D001", d1, Shift.MORNING);
        assertNotNull(foundA, "Found A");
        assertEquals("D001", foundA.getDoctorID(), "Found A doctorID");

        DoctorDuty notFound = dm.searchDutyByDoctorDateShift("D999", d1, Shift.MORNING);
        assertNull(notFound, "Search non-existing should be null");

        // --- 3) SEARCH (group) ---
        println("\n=== SEARCH group (date+shift) ===");
        DoctorDuty[] morningDuties = dm.searchDutiesByDateShift(d1, Shift.MORNING);
        assertEquals(2, morningDuties.length, "Two duties in MORNING 2025-08-22");
        dumpArray("Morning duties", morningDuties);

        DoctorDuty[] nightDuties = dm.searchDutiesByDateShift(d1, Shift.NIGHT);
        assertEquals(0, nightDuties.length, "Zero duties in NIGHT 2025-08-22");

        // --- 4) UPDATE availability ---
        println("\n=== UPDATE availability ===");
        boolean updated = dm.updateAvailability("D001", d1, Shift.MORNING, Availability.ON_LEAVE);
        assertTrue(updated, "Update A availability to ON_LEAVE");
        DoctorDuty afterUpdate = dm.searchDutyByDoctorDateShift("D001", d1, Shift.MORNING);
        assertEquals(Availability.ON_LEAVE, afterUpdate.getAvailability(), "Availability updated");


        // --- 5) REMOVE ---
        println("\n=== REMOVE ===");
        assertTrue(dm.removeDuty("D002", d1, Shift.MORNING), "Remove B");
        DoctorDuty[] morningAfterRemove = dm.searchDutiesByDateShift(d1, Shift.MORNING);
        assertEquals(1, morningAfterRemove.length, "One duty left in MORNING 2025-08-22");

        // remove the last one in the group; group node should be dropped internally
        assertTrue(dm.removeDuty("D001", d1, Shift.MORNING), "Remove A");
        morningAfterRemove = dm.searchDutiesByDateShift(d1, Shift.MORNING);
        assertEquals(0, morningAfterRemove.length, "Zero duties left in MORNING 2025-08-22");

        // removing non-existing should return false
        assertFalse(dm.removeDuty("D001", d2, Shift.MORNING), "Remove non-existing duty should fail");


        println("\nAll tests completed.");
    }

    // ----- tiny assert helpers (no JUnit needed) -----
    private static void assertTrue(boolean cond, String msg) {
        if (cond) ok(msg); else fail(msg);
    }
    private static void assertFalse(boolean cond, String msg) {
        assertTrue(!cond, msg);
    }
    private static void assertNull(Object obj, String msg) {
        assertTrue(obj == null, msg + " (expected null)");
    }
    private static void assertNotNull(Object obj, String msg) {
        assertTrue(obj != null, msg + " (expected not null)");
    }
    private static void assertEquals(Object expected, Object actual, String msg) {
        boolean eq = (expected == null) ? (actual == null) : expected.equals(actual);
        assertTrue(eq, msg + " [expected=" + expected + ", actual=" + actual + "]");
    }
    private static void ok(String msg)   { System.out.println("[PASS] " + msg); }
    private static void fail(String msg) { System.out.println("[FAIL] " + msg); }
    private static void println(String s){ System.out.println(s); }

    private static void dumpArray(String title, DoctorDuty[] arr) {
        System.out.println(title + " (size=" + arr.length + "):");
        for (DoctorDuty d : arr) {
            System.out.println("  " + d);
        }
    }
}
