package dao;

import adt.*;
import entity.*;

import java.time.LocalTime;

/**
 *                           ONLY NEEDED WHEN USING FILES
 */
public class PatientInitializer {
    public static void initialize(QueueInterface<PatientVisit> visitQueue) {
        // Sample patients
        Patient bryant = new Patient("P001", "Bryant", "0123456789", "bryant@example.com",  Gender.MALE,   20);
        Patient clairo = new Patient("P002", "Clairo", "0128888888", "clairo@mail.com",     Gender.FEMALE, 26);
        Patient travis = new Patient("P003", "Travis", "0184567890", "travis123@gmail.com", Gender.MALE,   34);
        Patient drake  = new Patient("P004", "Drake",  "0172223344", "drake@email.com",     Gender.MALE,   38);
        
        // Simulated visits (same different patients)
        visitQueue.enqueue(new PatientVisit(bryant, VisitType.WALK_IN,     LocalTime.of(9, 0)));
        visitQueue.enqueue(new PatientVisit(clairo, VisitType.APPOINTMENT, LocalTime.of(9, 15)));
        visitQueue.enqueue(new PatientVisit(bryant, VisitType.APPOINTMENT, LocalTime.of(10, 30))); // revisit
        visitQueue.enqueue(new PatientVisit(travis, VisitType.WALK_IN,     LocalTime.of(10, 45)));
        visitQueue.enqueue(new PatientVisit(clairo, VisitType.WALK_IN,     LocalTime.of(11, 10))); // revisit
        visitQueue.enqueue(new PatientVisit(drake,  VisitType.WALK_IN,     LocalTime.of(11, 25)));
    }
}
