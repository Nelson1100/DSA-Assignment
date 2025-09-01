package dao;

import control.ConsultationManagement;
import control.PatientManagement;
import control.DoctorManagement;
import entity.*;
import utility.IDGenerator;
import utility.IDType;

import java.time.LocalDateTime;

/**
 *
 * @author Sim Jia Quan
 */
public class ConsultationInitializer {
    public static void initialize(ConsultationManagement cm, PatientManagement pm, DoctorManagement dm) {
        LocalDateTime now = LocalDateTime.now().withSecond(0).withNano(0);
        
        Patient[] patients = pm.getAllPatientsSortedByID(false);
        if (patients.length == 0) {
            System.out.println("Warning: No patients found in system");
            return;
        }
        
        String[] patientIDs = new String[patients.length];
        for (int i = 0; i < patients.length; i++) {
            patientIDs[i] = patients[i].getPatientID();
        }
        
        Doctor[] doctors = dm.getAllDoctor();
        if (doctors.length == 0) {
            System.out.println("Warning: No doctors found in system");
            return;
        }
        
        String[] doctorIDs = new String[doctors.length];
        for (int i = 0; i < doctors.length; i++) {
            doctorIDs[i] = doctors[i].getDoctorID();
        }
        
        /* Sample Consultation */
        Consultation c1 = new Consultation(IDGenerator.next(IDType.CONSULTATION), 
                                         patientIDs[0], 
                                         doctorIDs[0]);
        c1.setSymptoms("Feeling anxious and stressed about work");
        c1.setDiagnosis("Generalized Anxiety Disorder");
        c1.setNotes("Recommended stress management techniques and follow-up in 2 weeks");
        c1.setStatus(ConsultationStatus.COMPLETED);
        cm.indexConsultation(c1);

        Consultation c2 = new Consultation(IDGenerator.next(IDType.CONSULTATION), 
                                         patientIDs[1], 
                                         doctorIDs[1]);
        c2.setSymptoms("Chest pain and shortness of breath");
        c2.setDiagnosis(""); // Still being diagnosed
        c2.setNotes("ECG normal, recommended gradual increase in physical activity");
        c2.setStatus(ConsultationStatus.IN_PROGRESS);
        cm.indexConsultation(c2);

        Consultation c3 = new Consultation(IDGenerator.next(IDType.CONSULTATION), 
                                         patientIDs[2], 
                                         doctorIDs[2]);
        c3.setSymptoms("Abdominal pain in lower right quadrant");
        c3.setDiagnosis("Appendicitis - surgical intervention required");
        c3.setNotes("Emergency appendectomy performed successfully. Patient recovering well.");
        c3.setStatus(ConsultationStatus.COMPLETED);
        cm.indexConsultation(c3);

        Consultation c4 = new Consultation(IDGenerator.next(IDType.CONSULTATION), 
                                         patientIDs[3], 
                                         doctorIDs[3]);
        c4.setSymptoms("Work-related stress and insomnia");
        c4.setDiagnosis("");
        c4.setNotes("Patient had to cancel due to family emergency. Rescheduled for next week.");
        c4.setStatus(ConsultationStatus.CANCELLED);
        cm.indexConsultation(c4);

        Consultation c5 = new Consultation(IDGenerator.next(IDType.CONSULTATION), 
                                         patientIDs[4], 
                                         doctorIDs[4]);
        c5.setSymptoms("Skin lesion requiring examination");
        c5.setDiagnosis("Benign skin lesion - surgical removal recommended");
        c5.setNotes("Minor surgical procedure scheduled. No malignancy detected.");
        c5.setStatus(ConsultationStatus.COMPLETED);
        cm.indexConsultation(c5);

        if (patientIDs.length > 5) {
            Consultation c6 = new Consultation(IDGenerator.next(IDType.CONSULTATION), 
                                             patientIDs[5], 
                                             doctorIDs[0]); 
            c6.setSymptoms("Recurring headaches and dizziness");
            c6.setDiagnosis(""); // Still being diagnosed
            c6.setNotes("Patient reports headaches for past 2 weeks. Ordering MRI scan.");
            c6.setStatus(ConsultationStatus.IN_PROGRESS);
            cm.indexConsultation(c6);
        }

        if (patientIDs.length > 6) {
            Consultation c7 = new Consultation(IDGenerator.next(IDType.CONSULTATION), 
                                             patientIDs[6], 
                                             doctorIDs[1]); 
            c7.setSymptoms("Heart palpitations during physical activity");
            c7.setDiagnosis("Benign heart palpitations - no treatment required");
            c7.setNotes("Holter monitor results normal. Advised to stay hydrated and avoid caffeine.");
            c7.setStatus(ConsultationStatus.COMPLETED);
            cm.indexConsultation(c7);
        }

        if (patientIDs.length > 7) {
            Consultation c8 = new Consultation(IDGenerator.next(IDType.CONSULTATION), 
                                             patientIDs[7], 
                                             doctorIDs[2]); 
            c8.setSymptoms("Memory issues and concentration problems");
            c8.setDiagnosis("Stress-related cognitive symptoms");
            c8.setNotes("Recommended cognitive behavioral therapy and stress reduction techniques.");
            c8.setStatus(ConsultationStatus.COMPLETED);
            cm.indexConsultation(c8);
        }

        if (patientIDs.length > 8) {
            Consultation c9 = new Consultation(IDGenerator.next(IDType.CONSULTATION), 
                                             patientIDs[8], 
                                             doctorIDs[3]); 
            c9.setSymptoms("Chronic back pain");
            c9.setDiagnosis("Lumbar strain - physical therapy recommended");
            c9.setNotes("Referred to physiotherapist. Prescribed pain management.");
            c9.setStatus(ConsultationStatus.COMPLETED);
            cm.indexConsultation(c9);
        }

        if (patientIDs.length > 9) {
            Consultation c10 = new Consultation(IDGenerator.next(IDType.CONSULTATION), 
                                              patientIDs[9], 
                                              doctorIDs[4]);
            c10.setSymptoms("Fever and sore throat");
            c10.setDiagnosis("Viral pharyngitis");
            c10.setNotes("Symptomatic treatment. Rest and fluids recommended.");
            c10.setStatus(ConsultationStatus.COMPLETED);
            cm.indexConsultation(c10);
        }

        if (patientIDs.length > 10) {
            Consultation c11 = new Consultation(IDGenerator.next(IDType.CONSULTATION), 
                                              patientIDs[10], 
                                              doctorIDs[0]); 
            c11.setSymptoms("Difficulty breathing");
            c11.setDiagnosis(""); // Still being diagnosed
            c11.setNotes("Ordering chest X-ray and blood tests.");
            c11.setStatus(ConsultationStatus.IN_PROGRESS);
            cm.indexConsultation(c11);
        }
    }
}
