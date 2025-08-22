package dao;

import adt.*;
import entity.*;

import java.time.LocalDateTime;

/**
 *                           ONLY NEEDED WHEN USING FILES
 */
public class PatientInitializer {
    public static void initialize(QueueInterface<PatientVisit> visitQueue) {
        // Sample patients
        Patient bryant = new Patient("P202508190001", "Bryant", "012-3456789", "bryant@example.com",  Gender.MALE,   20);
        Patient clairo = new Patient("P202508190002", "Clairo", "012-8888888", "clairo@mail.com",     Gender.FEMALE, 26);
        Patient travis = new Patient("P202508200001", "Travis", "018-4567890", "travis123@gmail.com", Gender.MALE,   34);
        Patient drake  = new Patient("P202508210001", "Drake",  "017-2223344", "drake@email.com",     Gender.MALE,   38);
        
        // Simulated visits (same different patients)
        visitQueue.enqueue(new PatientVisit(bryant, VisitType.WALK_IN,     LocalDateTime.of(2025, 8, 19, 9, 0)));
        visitQueue.enqueue(new PatientVisit(clairo, VisitType.APPOINTMENT, LocalDateTime.of(2025, 8, 19, 9, 15)));
        visitQueue.enqueue(new PatientVisit(bryant, VisitType.APPOINTMENT, LocalDateTime.of(2025, 8, 20, 10, 30))); // revisit
        visitQueue.enqueue(new PatientVisit(travis, VisitType.WALK_IN,     LocalDateTime.of(2025, 8, 20, 10, 45)));
        visitQueue.enqueue(new PatientVisit(clairo, VisitType.WALK_IN,     LocalDateTime.of(2025, 8, 20, 11, 10))); // revisit
        visitQueue.enqueue(new PatientVisit(drake,  VisitType.WALK_IN,     LocalDateTime.of(2025, 8, 21, 11, 25)));
    }
}
