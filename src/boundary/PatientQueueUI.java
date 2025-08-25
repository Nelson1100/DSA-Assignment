package boundary;

import control.PatientManagement;
import entity.*;
import java.time.format.DateTimeFormatter;
import utility.*;


public class PatientQueueUI {
    private final PatientManagement pm;
    private final Validation validate = new Validation();
    
    public PatientQueueUI(PatientManagement pm) {
        this.pm = pm;
    }
    
    public void run() {
        boolean repeat = true;
        
        String[] menu = {
            "Register",
            "Serve Next",
            "Find Position",
            "View Queue",
            "Remove",
            "Back"
        };
        
        while (repeat) {
            int choice = JOptionPaneConsoleIO.readOption(
                    "Please select a Visit Queue option:", 
                    "Patient Queue Management", 
                    menu
            );
            
            switch (choice) {
                case 0 -> registerVisit();
                case 1 -> serveNext();
                case 2 -> findPosition();
                case 3 -> viewQueue();
                case 4 -> removeVisit();
                case 5, -1 -> repeat = false;
                default -> JOptionPaneConsoleIO.showError("Please choose a valid option.");
            }
        }
    }

    private void registerVisit() {
        Patient patient = promptForPatient("Register Visit");
        
        if (patient == null) return;
        
        String[] types = {
            "Walk-in",
            "Appointment"
        };
        int type = JOptionPaneConsoleIO.readOption(
                "Select Visit Type:",
                "Visit Type",
                types
        );
        
        if (type == -1) return;
        
        VisitType visitType = (type == 0) ? VisitType.WALK_IN : VisitType.APPOINTMENT;
        
        boolean success = pm.registerVisit(patient, visitType);
        
        if (!success) {
            JOptionPaneConsoleIO.showInfo("Patient has already registered recently. Try again later.");
        } else {
            JOptionPaneConsoleIO.showInfo("Visit registered successfully.");
        }
    }

    private void serveNext() {
        PatientVisit next = pm.serveNextVisit();
        
        if (next == null) {
            JOptionPaneConsoleIO.showInfo("No patients in the queue.");
        } else {
            Patient p = next.getPatient();
            JOptionPaneConsoleIO.showInfo("Now serving: " + p.getPatientName() + " (" + p.getPatientID() + ")");
        }
    }
    
    private void findPosition() {
        Patient patient = promptForPatient("Find Position");
        
        if (patient == null) return;
        
        int pos = pm.findPosition(patient.getPatientID());
        
        if (pos == -1) {
            JOptionPaneConsoleIO.showError("Patient is not in the queue.");
        } else {
            JOptionPaneConsoleIO.showInfo("Patient position in queue: " + pos);
        }
    }
    
    private void viewQueue() {
        PatientVisit[] visits = pm.peekNextN(100);
        
        if (visits.length == 0) {
            JOptionPaneConsoleIO.showInfo("No patients in the queue.");
            return;
        }
        
        final int width = 100;
        StringBuilder sb = new StringBuilder();
        sb.append(JOptionPaneConsoleIO.line('-', width)).append("\n");
        sb.append(JOptionPaneConsoleIO.sectionTitle("Current Queue", width));
        sb.append(String.format(
            "%-4s %-15s %-25s %-8s %-5s %-13s %-10s\n",
            "No.", "Patient ID", "Name", "Gender", "Age", "Visit Type", "Arrived"
        ));
        sb.append(JOptionPaneConsoleIO.line('-', width)).append("\n");
        
        for (int i = 0; i < visits.length; i++) {
            Patient p = visits[i].getPatient();
            String arrival = visits[i].getArrivalDateTime().format(DateTimeFormatter.ofPattern("hh:mm a"));
            
            sb.append(String.format(
                "%-4d %-15s %-25s %-8s %-5d %-13s %-10s\n",
                i + 1,
                p.getPatientID(),
                p.getPatientName(),
                p.getGender(),
                p.getAge(),
                visits[i].getVisitType(),
                arrival
            ));
        }
        
        sb.append(JOptionPaneConsoleIO.line('-', width));
        JOptionPaneConsoleIO.showMonospaced("Patient Queue", sb.toString());
    }
    
    private void removeVisit() {
        Patient patient = promptForPatient("Remove Visit");
        
        if (patient == null) return;
        
        int pos = pm.findPosition(patient.getPatientID());
        
        if (pos == -1) {
            JOptionPaneConsoleIO.showError("Patient is not in the queue.");
            return;
        }
        
        boolean confirm = JOptionPaneConsoleIO.confirmDialog(
                "Are you sure you want to remove " + patient.getPatientName() + " (" + patient.getPatientID() + ") "+ "from the queue?", 
                "Confirm Removal");
        
        if (confirm) {
            boolean result = pm.removeVisitByID(patient.getPatientID());
            JOptionPaneConsoleIO.showInfo(result ? "Visit removed." : "Failed to remove visit.");
        }
    }
    
    private Patient promptForPatient(String title) {
        Patient match = null;
        boolean cancel = false;
        
        do {
            String input = JOptionPaneConsoleIO.readNonEmpty("Enter ID / Name / Contact / Email:");
            if (input == null) {
                cancel = true; // pressed cancel or cross
                break;
            } 
            
            input = input.trim();
            Patient[] matches = null;
            
            // decide the prompt type
            if (validate.validName(input)) {
                matches = pm.findPatientsByName(input);
            } else if (validate.validPhone(validate.standardizedPhone(input))) {
                match = pm.findPatientByPhone(validate.standardizedPhone(input));
            } else if (validate.validEmail(input)) {
                match = pm.findPatientByEmail(input.toLowerCase());
            } else {
                match = pm.findPatientByID(input);
            }
            
            // handle multiple matches (for name prompt only)
            if (matches != null && matches.length > 1) {
                String[] options = new String[matches.length];

                for (int i = 0; i < matches.length; i++) {
                    options[i] = String.format("%s (%s, %s, %s)", 
                            matches[i].getPatientName(), 
                            matches[i].getPatientID(), 
                            matches[i].getContactNo(),
                            matches[i].getEmail()
                    );
                }

                int sel = JOptionPaneConsoleIO.readOption("Multiple matches found:", title, options);

                if (sel != -1)
                    match = matches[sel];
                else
                    cancel = true;
            } else if (matches != null && matches.length == 1) {
                match = matches[0];
            }

            if (match == null && !cancel) {
                JOptionPaneConsoleIO.showError("Patient not found.");
            }
        } while (match == null && !cancel);
        
        return match;
    }
    
}
