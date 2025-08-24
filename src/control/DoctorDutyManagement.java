package control;

import adt.AVLTree;
import entity.DoctorDuty;
import entity.Availability;
import entity.Shift;
import entity.keys.DutyByDoctorDateShift;
import entity.keys.DutyByDateShift;
import utility.Validation;
import java.time.*;
import java.time.format.DateTimeFormatter;

public class DoctorDutyManagement {
    // Unique index: (doctorID, date, shift) -> DoctorDuty
    private final AVLTree<DutyByDoctorDateShift> idxByDoctorDateShift = new AVLTree<>();
    // Grouped index: (date, shift) -> bucket of duties
    private final AVLTree<DutyByDateShift> idxByDateShift = new AVLTree<>();
    Validation validate = new Validation();

    // Adding a new duty
    public boolean addDuty(DoctorDuty duty) {
        if (duty == null)
            return false;

        DutyByDoctorDateShift uniqueKey =  new DutyByDoctorDateShift(duty.getDoctorID(), duty.getDate(), duty.getShift(), duty);

        if (idxByDoctorDateShift.find(uniqueKey) != null)
            return false;
        
        if (!idxByDoctorDateShift.insert(uniqueKey))
            return false;

        DutyByDateShift groupProbe = new DutyByDateShift(duty.getDate(), duty.getShift());
        DutyByDateShift groupNode = idxByDateShift.find(groupProbe);
        boolean insertedGroupNode = false;

        if (groupNode == null) {
            groupNode = groupProbe;
            insertedGroupNode = idxByDateShift.insert(groupNode);
            if (!insertedGroupNode) {
                idxByDoctorDateShift.delete(uniqueKey);
                return false;
            }
        }

        boolean groupInsertion = groupNode.add(uniqueKey);
        if (!groupInsertion) {
            idxByDoctorDateShift.delete(uniqueKey);

            if (insertedGroupNode && groupNode.isEmpty()) {
                idxByDateShift.delete(groupNode);
            }
            return false;
        }
        return true;
    }

    // Remove certain duty
    public boolean removeDuty(String doctorID, LocalDate date, Shift shift) {
        DutyByDoctorDateShift searchKey = new DutyByDoctorDateShift(doctorID, date, shift, null);
        DutyByDoctorDateShift found = idxByDoctorDateShift.find(searchKey);
        
        if (found == null)
            return false;

        boolean deletion = idxByDoctorDateShift.delete(found);
        
        if (!deletion) 
            return false;

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

    // SEARCH #1: exact search by (doctorID, date, shift).
    public DoctorDuty searchDutyByDoctorDateShift(String doctorID, LocalDate date, Shift shift) {
        DutyByDoctorDateShift searchKey = new DutyByDoctorDateShift(doctorID, date, shift, null);
        DutyByDoctorDateShift found = idxByDoctorDateShift.find(searchKey);
        return (found == null) ? null : found.getDuty();
    }

    // SEARCH #2: list all duties for a (date, shift).
    public DoctorDuty[] searchDutiesByDateShift(LocalDate date, Shift shift) {
        DutyByDateShift searchKey = new DutyByDateShift(date, shift);
        DutyByDateShift node = idxByDateShift.find(searchKey);
        
        if (node == null)
            return new DoctorDuty[0];
        
        return node.toDutyArray();
    }
    
    // Update availability for an existing duty.
    public boolean updateAvailability(String doctorID, LocalDate date, Shift shift, Availability newAvailability) {
        if (date.isAfter(LocalDate.now())){
            DutyByDoctorDateShift searchKey = new DutyByDoctorDateShift(doctorID, date, shift, null);
            DutyByDoctorDateShift found = idxByDoctorDateShift.find(searchKey);

            if (found != null) {
                found.getDuty().setAvailability(newAvailability);
                return true;
            }

            DoctorDuty created = new DoctorDuty(doctorID, date, shift, newAvailability);
            return addDuty(created);
        }
        return false;
    }

    // Build a monthly duty roster
    public DoctorDuty[][] MonthlyRosterTableMatrix(String doctorID, int year, int month, boolean autoCreateWeedays){
        YearMonth ym = YearMonth.of(year, month);
        int days = ym.lengthOfMonth();
        Shift[] shifts = Shift.values();
        DoctorDuty[][] roster = new DoctorDuty[days][shifts.length];

        for (int day = 1; day <= days; day++) {
            LocalDate date = ym.atDay(day);
            for (int i = 0; i < shifts.length; i++) {
                Shift shift = shifts[i];
                roster[day - 1][i] = autoCreateWeedays
                        ? WeekdayDuty(doctorID, date, shift)
                        : findDuty(doctorID, date, shift);
            }
        }
        return roster;
    }
    
    // Assuming every weekday has duty
    public DoctorDuty WeekdayDuty(String doctorID, LocalDate date, Shift shift){
        DoctorDuty docDuty = findDuty(doctorID, date, shift);
        
        String regDateStr = doctorID.substring(1, doctorID.length() - 4);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate regDate = LocalDate.parse(regDateStr, formatter);
        
        if (date.isBefore(regDate))
            return null;
        
        if (docDuty != null)
            return docDuty;

        if (!validate.isWeekday(date))
            return null;

        DoctorDuty created = new DoctorDuty(doctorID, date, shift, Availability.AVAILABLE);
        return addDuty(created) ? created : null;
    }
    
    private DoctorDuty findDuty(String doctorID, LocalDate date, Shift shift) {
        DutyByDoctorDateShift searchKey = new DutyByDoctorDateShift(doctorID, date, shift, null);
        DutyByDoctorDateShift leaf = idxByDoctorDateShift.find(searchKey);
        return (leaf == null) ? null : leaf.getDuty();
    }
}
