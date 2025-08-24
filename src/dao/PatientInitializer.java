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
        Patient p1  = new Patient("P202508210001", "Bryant Yeoh",       "012-3456789", "bryant@gmail.com",  Gender.MALE,   20); // 19–30
        Patient p2  = new Patient("P202508210002", "Claire Cottrill",   "012-8888888", "clairo@gmail.com",  Gender.FEMALE, 26); // 19–30
        Patient p3  = new Patient("P202508210003", "Travis Scott",      "018-4567890", "travis@gmail.com",  Gender.MALE,   34); // 31–50
        Patient p4  = new Patient("P202508210004", "Drake Graham",      "017-2223344", "drizzy@gmail.com",  Gender.MALE,   38); // 31–50
        Patient p5  = new Patient("P202508210005", "Kendrick Lamar",    "017-2223344", "kdot@gmail.com",    Gender.MALE,   38); // 0–18
        Patient p6  = new Patient("P202508210006", "Jermaine Cole",     "017-2223344", "jcole@gmail.com",   Gender.MALE,   40); // 31–50
        Patient p7  = new Patient("P202508210007", "Rihanna Fenty",     "012-9988776", "rihanna@gmail.com", Gender.FEMALE, 32);  // 31–50
        Patient p8  = new Patient("P202508210008", "Billie Eilish",     "013-1111222", "billie@gmail.com",  Gender.FEMALE, 17);  // 0–18
        Patient p9  = new Patient("P202508210009", "Doja Cat",          "016-2222333", "doja@gmail.com",    Gender.FEMALE, 29);  // 19–30
        Patient p10 = new Patient("P202508210010", "Zack Tabudlo",      "014-3333444", "zack@gmail.com",    Gender.MALE,   51);  // 51+
        Patient p11 = new Patient("P202508210011", "Jack Harlow",       "011-1111222", "jack@gmail.com",    Gender.MALE,   27);  // 19–30

        
        // visits
        LocalDateTime now = LocalDateTime.now();
        visitQueue.enqueue(new PatientVisit(p1,  VisitType.WALK_IN,     now.minusMinutes(10)));
        visitQueue.enqueue(new PatientVisit(p2,  VisitType.APPOINTMENT, now.minusMinutes(10)));
        visitQueue.enqueue(new PatientVisit(p3,  VisitType.WALK_IN,     now.minusMinutes(5)));
        visitQueue.enqueue(new PatientVisit(p4,  VisitType.APPOINTMENT, now.minusMinutes(25)));
        visitQueue.enqueue(new PatientVisit(p5,  VisitType.WALK_IN,     now.minusMinutes(5)));
        visitQueue.enqueue(new PatientVisit(p6,  VisitType.WALK_IN,     now.minusMinutes(7)));
        visitQueue.enqueue(new PatientVisit(p7,  VisitType.APPOINTMENT, now.minusMinutes(20)));
        visitQueue.enqueue(new PatientVisit(p8,  VisitType.WALK_IN,     now.minusMinutes(2)));
        visitQueue.enqueue(new PatientVisit(p9,  VisitType.APPOINTMENT, now.minusMinutes(18)));
        visitQueue.enqueue(new PatientVisit(p10, VisitType.WALK_IN,     now.minusMinutes(35)));
        visitQueue.enqueue(new PatientVisit(p11, VisitType.WALK_IN,     now.minusMinutes(52)));
        
        // repeated visits
        visitQueue.enqueue(new PatientVisit(p1,  VisitType.WALK_IN,     now.minusMinutes(60)));
        visitQueue.enqueue(new PatientVisit(p1,  VisitType.WALK_IN,     now.minusMinutes(15)));
        visitQueue.enqueue(new PatientVisit(p11, VisitType.APPOINTMENT, now.minusMinutes(1)));
        visitQueue.enqueue(new PatientVisit(p11, VisitType.WALK_IN,     now.minusMinutes(3)));
        visitQueue.enqueue(new PatientVisit(p11, VisitType.APPOINTMENT, now.minusMinutes(4)));
        visitQueue.enqueue(new PatientVisit(p5,  VisitType.WALK_IN,     now.minusMinutes(30)));
    }
}
