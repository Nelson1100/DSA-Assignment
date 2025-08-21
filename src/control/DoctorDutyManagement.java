package control;

import adt.AVLTree;
import entity.DoctorDuty;
import entity.Availability;
import entity.Shift;
import entity.keys.DutyByDoctorDateShift;
import entity.keys.DutyByDateShift;

import java.time.LocalDate;

public class DoctorDutyManagement {
    // Unique index: (doctorID, date, shift) -> DoctorDuty
    private final AVLTree<DutyByDoctorDateShift> idxByDoctorDateShift = new AVLTree<>();
    // Grouped index: (date, shift) -> bucket of duties
    private final AVLTree<DutyByDateShift> idxByDateShift = new AVLTree<>();

    /**
     * Add a new duty. Enforces uniqueness on (doctorID, date, shift).
     * Inserts into both indexes; rolls back if the group insert fails.
     */
    public boolean addDuty(DoctorDuty duty) {
        if (duty == null) return false;

        DutyByDoctorDateShift uniqueKey =
                new DutyByDoctorDateShift(duty.getDoctorID(), duty.getDate(), duty.getShift(), duty);

        // Uniqueness check via AVL.find(...)
        if (idxByDoctorDateShift.find(uniqueKey) != null) return false;

        // Insert into unique index
        boolean ok1 = idxByDoctorDateShift.insert(uniqueKey);
        if (!ok1) return false;

        // Upsert group node for (date, shift)
        DutyByDateShift groupProbe = new DutyByDateShift(duty.getDate(), duty.getShift());
        DutyByDateShift groupNode = idxByDateShift.find(groupProbe);
        boolean insertedGroupNode = false;

        if (groupNode == null) {
            groupNode = new DutyByDateShift(duty.getDate(), duty.getShift());
            insertedGroupNode = idxByDateShift.insert(groupNode);
            if (!insertedGroupNode) {
                // rollback unique index
                idxByDoctorDateShift.delete(uniqueKey);
                return false;
            }
        }

        // Add to the group's internal bucket
        boolean okBucket = groupNode.add(uniqueKey);
        if (!okBucket) {
            // rollback both indexes
            idxByDoctorDateShift.delete(uniqueKey);
            if (insertedGroupNode && groupNode.isEmpty()) {
                idxByDateShift.delete(groupNode);
            }
            return false;
        }

        return true;
    }

    /**
     * Remove duty by (doctorID, date, shift).
     */
    public boolean removeDuty(String doctorID, LocalDate date, Shift shift) {
        DutyByDoctorDateShift probe = new DutyByDoctorDateShift(doctorID, date, shift, null);
        DutyByDoctorDateShift found = idxByDoctorDateShift.find(probe);
        if (found == null) return false;

        // Remove from unique index
        boolean ok1 = idxByDoctorDateShift.delete(found);
        if (!ok1) return false;

        // Remove from group bucket; drop group node if empty
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

    /**
     * SEARCH #1: exact search by (doctorID, date, shift).
     */
    public DoctorDuty searchDutyByDoctorDateShift(String doctorID, LocalDate date, Shift shift) {
        DutyByDoctorDateShift probe = new DutyByDoctorDateShift(doctorID, date, shift, null);
        DutyByDoctorDateShift found = idxByDoctorDateShift.find(probe);
        return (found == null) ? null : found.getDuty();
    }

    /**
     * SEARCH #2: list all duties for a (date, shift).
     */
    public DoctorDuty[] searchDutiesByDateShift(LocalDate date, Shift shift) {
        DutyByDateShift probe = new DutyByDateShift(date, shift);
        DutyByDateShift node = idxByDateShift.find(probe);
        if (node == null) return new DoctorDuty[0];
        return node.toDutyArray();
    }

    /**
     * Update availability for an existing duty.
     */
    public boolean updateAvailability(String doctorID, LocalDate date, Shift shift, Availability newAvailability) {
        DutyByDoctorDateShift probe = new DutyByDoctorDateShift(doctorID, date, shift, null);
        DutyByDoctorDateShift found = idxByDoctorDateShift.find(probe);
        if (found == null) return false;

        found.getDuty().setAvailability(newAvailability);
        return true;
    }
}
