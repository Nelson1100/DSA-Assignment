package dao;

import adt.*;
import entity.Patient;

import entity.Gender;
import entity.VisitType;

import java.time.LocalTime;

/**
 *                           ONLY NEEDED WHEN USING FILES
 */
public class PatientInitializer {
    public static void initialize(QueueInterface<Patient> queue) {
        queue.enqueue(new Patient("P001", "Bryant", "0123456789", "bryant@example.com",
                Gender.MALE, 20, VisitType.WALK_IN, LocalTime.of(9, 0)));

        queue.enqueue(new Patient("P002", "Clairo", "0128888888", "clairo@mail.com",
                Gender.FEMALE, 26, VisitType.APPOINTMENT, LocalTime.of(9, 15)));

        queue.enqueue(new Patient("P003", "Travis", "0184567890", "travis123@gmail.com",
                Gender.MALE, 34, VisitType.WALK_IN, LocalTime.of(9, 20)));

        queue.enqueue(new Patient("P004", "Drake", "0172223344", "drake@email.com",
                Gender.MALE, 38, VisitType.APPOINTMENT, LocalTime.of(9, 30)));
    }
}
