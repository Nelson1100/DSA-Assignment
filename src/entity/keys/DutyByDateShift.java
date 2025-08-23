package entity.keys;

import entity.DoctorDuty;
import entity.Shift;
import java.time.LocalDate;

public class DutyByDateShift implements Comparable<DutyByDateShift> {
    private final LocalDate date;
    private final Shift shift;
    private Node head;
    private int size;

    private static class Node {
        DutyByDoctorDateShift key;
        Node next;
        Node(DutyByDoctorDateShift k, Node n) { this.key = k; this.next = n; }
    }

    public DutyByDateShift(LocalDate date, Shift shift) {
        this.date = date;
        this.shift = shift;
        this.head = null;
        this.size = 0;
    }

    // Getters
    public LocalDate getDate() { return date; }
    public Shift getShift() { return shift; }
    public int size() { return size; }
    public boolean isEmpty() { return size == 0; }

    /** Add unique (by doctorID+date+shift). Reject null duty payloads. */
    public boolean add(DutyByDoctorDateShift k) {
        if (k == null || k.getDuty() == null) return false;
        if (!sameSlot(k)) return false;

        Node curr = head;
        while (curr != null) {
            if (curr.key.compareTo(k) == 0) return false; // duplicate
            curr = curr.next;
        }
        head = new Node(k, head); // push-front (O(1)). If you prefer stable order, add at tail.
        size++;
        return true;
    }

    public boolean remove(DutyByDoctorDateShift k) {
        if (k == null || head == null) return false;

        if (head.key.compareTo(k) == 0) {
            head = head.next; size--; return true;
        }
        Node prev = head, curr = head.next;
        while (curr != null) {
            if (curr.key.compareTo(k) == 0) {
                prev.next = curr.next; size--; return true;
            }
            prev = curr; curr = curr.next;
        }
        return false;
    }

    /** Check membership by composite key. */
    public boolean contains(DutyByDoctorDateShift k) {
        if (k == null || !sameSlot(k)) return false;
        Node curr = head;
        while (curr != null) {
            if (curr.key.compareTo(k) == 0) return true;
            curr = curr.next;
        }
        return false;
    }

    /** Find a duty by doctorID within this (date, shift) bucket. */
    public DoctorDuty getDutyByDoctor(String doctorID) {
        Node curr = head;
        while (curr != null) {
            DutyByDoctorDateShift x = curr.key;
            if (x.getDoctorID() != null && x.getDoctorID().equals(doctorID)) {
                return x.getDuty();
            }
            curr = curr.next;
        }
        return null;
    }

    /** Optional: iterate keys (useful for printing). */
    public void forEach(java.util.function.Consumer<DutyByDoctorDateShift> action) {
        Node curr = head;
        while (curr != null) { action.accept(curr.key); curr = curr.next; }
    }

    /** Convert to array (current order = reverse insertion). */
    public DoctorDuty[] toDutyArray() {
        DoctorDuty[] arr = new DoctorDuty[size];
        int i = 0;
        Node curr = head;
        while (curr != null) {
            arr[i++] = curr.key.getDuty();
            curr = curr.next;
        }
        return arr;
    }

    public void clear() { head = null; size = 0; }

    private boolean sameSlot(DutyByDoctorDateShift k) {
        if (k.getDate() == null || k.getShift() == null) return false;
        if (!k.getDate().equals(this.date)) return false;
        return k.getShift() == this.shift;
    }

    @Override
    public int compareTo(DutyByDateShift o) {
        if (o == null) return 1;
        int c;
        if (this.date == null && o.date != null) return 1;
        if (this.date != null && o.date == null) return -1;
        if (this.date != null && o.date != null) {
            c = this.date.compareTo(o.date);
            if (c != 0) return c;
        }
        if (this.shift == null && o.shift != null) return 1;
        if (this.shift != null && o.shift == null) return -1;
        if (this.shift != null && o.shift != null) {
            c = Integer.compare(this.shift.ordinal(), o.shift.ordinal());
            if (c != 0) return c;
        }
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof DutyByDateShift)) return false;
        return compareTo((DutyByDateShift) obj) == 0;
    }

    @Override
    public int hashCode() {
        int h = 17;
        h = 31 * h + (date == null ? 0 : date.hashCode());
        h = 31 * h + (shift == null ? 0 : shift.hashCode());
        return h;
    }

    @Override
    public String toString() {
        return "DutyGroup{date=" + date + ", shift=" + shift + ", size=" + size + "}";
    }
}
