package entity.keys;

import entity.DoctorDuty;
import entity.Shift;

import java.time.LocalDate;

public class DutyByDateShift implements Comparable<DutyByDateShift> {
    private final LocalDate date;
    private final Shift shift;

    // --- Simple singly linked list for bucket ---
    private static class Node {
        DutyByDoctorDateShift key; // holds reference to DoctorDuty inside
        Node next;
        Node(DutyByDoctorDateShift k, Node n) { this.key = k; this.next = n; }
    }
    private Node head;
    private int size;

    public DutyByDateShift(LocalDate date, Shift shift) {
        this.date = date;
        this.shift = shift;
        this.head = null;
        this.size = 0;
    }

    public LocalDate getDate()  { return date; }
    public Shift getShift()     { return shift; }
    public int size()           { return size; }
    public boolean isEmpty()    { return size == 0; }

    /**
     * Add a unique DutyByDoctorDateShift into the bucket.
     * Returns false if a duplicate (same doctorID, date, shift) already exists.
     */
    public boolean add(DutyByDoctorDateShift k) {
        if (k == null) return false;
        // defensive: ensure the (date, shift) matches this group
        if (!sameSlot(k)) return false;

        // check duplicate
        Node curr = head;
        while (curr != null) {
            if (curr.key.compareTo(k) == 0) {
                return false; // duplicate
            }
            curr = curr.next;
        }
        // prepend (order not important)
        head = new Node(k, head);
        size++;
        return true;
    }

    /**
     * Remove a key from bucket. Returns true if removed.
     */
    public boolean remove(DutyByDoctorDateShift k) {
        if (k == null || head == null) return false;

        if (head.key.compareTo(k) == 0) {
            head = head.next;
            size--;
            return true;
        }
        Node prev = head;
        Node curr = head.next;
        while (curr != null) {
            if (curr.key.compareTo(k) == 0) {
                prev.next = curr.next;
                size--;
                return true;
            }
            prev = curr;
            curr = curr.next;
        }
        return false;
    }

    /**
     * Convert bucket to an array of DoctorDuty.
     * Order is unspecified (insertion-prepend order).
     */
    public DoctorDuty[] toDutyArray() {
        DoctorDuty[] arr = new DoctorDuty[size];
        int i = 0;
        Node curr = head;
        while (curr != null) {
            // key.getDuty() may be null only for "probe" instances; we never store probes via add()
            arr[i++] = curr.key.getDuty();
            curr = curr.next;
        }
        return arr;
    }

    /** Helper: ensure a key belongs to this (date, shift) group. */
    private boolean sameSlot(DutyByDoctorDateShift k) {
        if (k.getDate() == null || k.getShift() == null) return false;
        if (!k.getDate().equals(this.date)) return false;
        return k.getShift() == this.shift;
    }

    // ---- Comparable: order groups by (date, shift) ----
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