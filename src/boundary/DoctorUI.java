package boundary;

import utility.JOptionPaneConsoleIO;
import control.DoctorManagement;
import entity.Doctor;
import utility.*;
import control.DoctorDutyManagement;
import entity.Availability;
import entity.DoctorDuty;
import entity.Shift;
import java.time.*;
import entity.Specialization;
import control.DoctorReportGenerator;

public class DoctorUI {
    DoctorManagement dm = new DoctorManagement();
    Doctor doctor = new Doctor();
    Validation validate = new Validation();
    DoctorDutyManagement DocDuty = new DoctorDutyManagement();
    DoctorReportGenerator ReportGen = new DoctorReportGenerator(DocDuty, validate);
    Doctor[] doctors;
    String[] specializationOp = {"CARDIOLOGY", "NEUROLOGY", "ORTHOPEDICS", "PEDIATRICS", "DERMATOLOGY", "PSYCHIATRY", "ONCOLOGY", "GENERAL_SURGERY", "INTERNAL_MEDICINE", "OBSTETRICS_GYNECOLOGY", "OPHTHALMOLOGY", "OTOLARYNGOLOGY", "RADIOLOGY", "PATHOLOGY", "FAMILY_MEDICINE", "EMERGENCY_MEDICINE"};

    public void taskSelection(){
        boolean newTask = true;
        String[] menu = {"Register", "Search", "Duty By Date", "Report", "Cancel"};
        String[] updateOption = {"Update", "Remove", "Duty", "Cancel"};
        
        // Hardcoded doctor information
        Doctor A = new Doctor(IDGenerator.next(IDType.DOCTOR), "Nelson", "0182284609", "nelson@gmail.com", Specialization.PSYCHIATRY);
        Doctor B = new Doctor(IDGenerator.next(IDType.DOCTOR), "Choonni", "0121234567", "cn@gmail.com", Specialization.NEUROLOGY);
        Doctor C = new Doctor(IDGenerator.next(IDType.DOCTOR), "WeiJian", "0121234567", "wj@gmail.com", Specialization.CARDIOLOGY);
        dm.registerDoctor(A);
        dm.registerDoctor(B); 
        dm.registerDoctor(C);
        doctors = dm.getAllDoctor();
            
        do {
            int choice = JOptionPaneConsoleIO.readOption("Which task would you like to perform?", "Doctor Management Module", menu);
            
            if (choice == -1)
                break;
            
            switch (choice){
                case 0:
                    // Register new doctor
                    doctor = newDetailsPrompt();
                    if (doctor == null)
                        break;
                    
                    boolean success = dm.registerDoctor(doctor);
                    
                    if (success)
                        JOptionPaneConsoleIO.showInfo("Doctor is successfully registered.");
                    else
                        JOptionPaneConsoleIO.showError("Unsuccessful action. Please try again.");                    
                    break;
                case 1:
                    // Search doctor
                    int updateChoice = -1;
                    
                    if (dm.isEmptyTree()) {
                        JOptionPaneConsoleIO.showError("Empty doctor record.");
                        break;
                    }
                    
                    doctor = searchDoctor();
                    if (doctor == null)
                        break;
                    
                    Doctor result = dm.findDoctor(doctor);
                    int update = -1;
                    do {
                        if (result != null)
                            updateChoice = JOptionPaneConsoleIO.readOption(result.toString(), "Doctor Information", updateOption);
                        else {
                            JOptionPaneConsoleIO.showError("No doctor is found.");
                            break;
                        }

                        int modifyChoice = 0;
                        String newName = null;
                        String newPhone = null;
                        String newEmail = null;
                        Specialization newSpecialization = null;

                        if (updateChoice == 0) {
                            // Update doctor profile
                            modifyChoice = infoModification();

                            if (modifyChoice == -1 || modifyChoice == 5) {
                                update = 3;
                                continue;
                            }

                            switch (modifyChoice){
                                case 1:
                                    // Modify name
                                    newName = JOptionPaneConsoleIO.readNonEmpty("Enter new name: ");
                                    if (newName == null) {
                                        update = 3;
                                        continue;
                                    }
                                    break;
                                case 2:
                                    // Modify phone number
                                    newPhone = JOptionPaneConsoleIO.readNonEmpty("Enter new phone number: ");
                                    newPhone = validate.standardizedPhone(newPhone);
                                    if (newPhone == null) {
                                        update = 3;
                                        continue;
                                    }
                                    break;
                                case 3:
                                    // Modify email address
                                    newEmail = JOptionPaneConsoleIO.readNonEmpty("Enter new email address: ");
                                    if (newEmail == null) {
                                        update = 3;
                                        continue;
                                    }
                                    break;
                                case 4:
                                    // Modify specialization
                                    newSpecialization = JOptionPaneConsoleIO.readEnum("Enter new specialization: ", Specialization.class, specializationOp);
                                    if (newSpecialization == null) {
                                        update = 3;
                                        continue;
                                    }
                                    break;
                                default:
                                    JOptionPaneConsoleIO.showError("Please enter a valid option.");
                            }

                            boolean updateResult = dm.updateDoctor(result, modifyChoice, newName, newPhone, newEmail, newSpecialization);

                            if (updateResult)
                                JOptionPaneConsoleIO.showInfo("Information updated.");
                            else
                                JOptionPaneConsoleIO.showError("Update unsuccessfully. Please try again.");
                            
                            update = 3;
                        } else if (updateChoice == 1){
                            // Remove doctor
                            boolean removeResult = dm.removeDoctor(result);

                            if (removeResult)
                                JOptionPaneConsoleIO.showInfo("Doctor removed.");
                            else
                                JOptionPaneConsoleIO.showError("Remove unsuccessfully. Please try again.");
                            
                            break;
                        } else if (updateChoice == 2){
                            // Duty checker (Display duty in table form of the doctor)
                            YearMonth current = YearMonth.now();
                            int year = current.getYear();
                            int month = current.getMonthValue();

                            do {
                                String table = buildMonthlyRosterByWeeks(result.getDoctorID() ,year, month);
                                update = dutyOpPrompt(table, year, month, result.getDoctorID());

                                switch (update) {
                                    case 0:
                                        JOptionPaneConsoleIO.showInfo("Roster updated successful.");
                                        break;
                                    case 1:
                                        JOptionPaneConsoleIO.showError("Please retry again.");
                                        break;
                                    case 2:
                                        month = JOptionPaneConsoleIO.readIntInRange("Enter month: ", 1, 12);
                                        break;
                                    default:
                                        break;
                                }
                            } while (update == 2 || update == 1 || update == 0);
                        } else {
                            break;
                        }
                    } while (update == 3);
                    break;
                case 2:
                    int year = JOptionPaneConsoleIO.readInt("Enter year: ");
                    int month = JOptionPaneConsoleIO.readIntInRange("Enter month: ", 1, 12);
                    int day = JOptionPaneConsoleIO.readIntInRange("Enter day: ", 1, YearMonth.of(year, month).lengthOfMonth());
                    LocalDate date = LocalDate.of(year, month, day);

                    Shift shift = JOptionPaneConsoleIO.readEnum("Enter shift: ", Shift.class, new String[]{"MORNING", "AFTERNOON", "NIGHT"});
                    DoctorDuty[] arr = DocDuty.searchDutiesByDateShift(date, shift);
                    
                    if (doctors != null){
                        for (int i = 0; i < dm.doctorAmount(); i++){
                            DocDuty.WeekdayDuty(doctors[i].getDoctorID(), date, shift);
                        }
                    }
                    
                    if (arr.length == 0 && !validate.isWeekday(date)) {
                        JOptionPaneConsoleIO.showError("No doctor duties on " + date + " (" + shift + ").");
                        break;
                    }
                    
                    if (validate.isWeekday(date)) {
                        for (int i = 0; i < arr.length; i++) {
                            if (arr[i].getAvailability() == null) {
                                DocDuty.WeekdayDuty(arr[i].getDoctorID(), date, shift);
                            }
                        }
                        arr = DocDuty.searchDutiesByDateShift(date, shift);
                    }
                    
                    StringBuilder sb = new StringBuilder(256);
                    sb.append("Duties on ").append(date).append(" (").append(shift).append(")\n");

                    if (arr.length == 0) {
                        sb.append("  (none)\n");
                    } else {
                        for (int i = 0; i < arr.length; i++) {
                            DoctorDuty d = arr[i];

                            Doctor searchKey = new Doctor(d.getDoctorID(), "", "", "", null);
                            Doctor doctor = dm.findDoctor(searchKey);

                            String name = (doctor != null ? doctor.getDoctorName() : d.getDoctorID()); // or getName()
                            String spec = (doctor != null && doctor.getSpecialization() != null)
                                          ? doctor.getSpecialization().toString()
                                          : "-";

                            if (d.getAvailability().equals(Availability.AVAILABLE)) {
                                sb.append(String.format(i+1 + ".  %s (%s) | Availability: %s%n", name, spec, d.getAvailability()));
                            }
                        }
                    }
                    JOptionPaneConsoleIO.showInfo("<html><pre style='font-family:monospace'>" + sb + "</pre></html>");
                    break;
                case 3:
                    // Generate report
                    String[] reportOp = {"Annual Doctor Attendance Report", "Report", "Cancel"};
                    int reportChoice = JOptionPaneConsoleIO.readOption("Which report would you like to generate?", "Generate Report", reportOp);
                    
                    if (reportChoice == 0){
                        StringBuilder attReport = new StringBuilder(50000);
                        int yearToGen = JOptionPaneConsoleIO.readInt("Enter year: ");
                        
                        for (int i = 0; i < doctors.length; i++){
                            ReportGen.yearlyAttendanceRate(doctors[i].getDoctorID(), yearToGen, true, attReport);
                        }
                        
                        JOptionPaneConsoleIO.showInfo("<html><pre style='font-family:monospace'>" + attReport + "</pre></html>");
                    } else if (reportChoice == 1){
                        
                    } else {
                        break;
                    }
                    break;
                case 4:
                    // End performing task
                    newTask = false;
                    break;
                default:
                    JOptionPaneConsoleIO.showError("Please enter a valid option.");
                
            } 
        } while (newTask);
    }
    
    private Doctor newDetailsPrompt(){
        boolean valid;
        String name;
        String phone;
        String email;
        Specialization specialization;
        String id = IDGenerator.next(IDType.DOCTOR);
                
        do {
            valid = false;
            name = JOptionPaneConsoleIO.readNonEmpty("Enter Doctor Name: ");
            
            if (name == null)
                return null;
            
            if (validate.validName(name))
                valid = true;
            else 
                JOptionPaneConsoleIO.showError("Please enter a valid name.");
        } while (!valid);
        
        do {
            valid = false;
            phone = JOptionPaneConsoleIO.readNonEmpty("Enter phone number: ");
            
            if (phone == null)
                return null;
            
            phone = validate.standardizedPhone(phone);
            
            if (validate.validPhone(phone))
                valid = true;
            else 
                JOptionPaneConsoleIO.showError("Please enter a valid phone number.");
        } while (!valid);
        
        do {
            valid = false;
            email = JOptionPaneConsoleIO.readNonEmpty("Enter Email Address: ");
            
            if (email == null)
                return null;
            
            if (validate.validEmail(email))
                valid = true;
            else 
                JOptionPaneConsoleIO.showError("Please enter a valid email address.");
        } while (!valid);
        
        specialization = JOptionPaneConsoleIO.readEnum("Enter new specialization: ", Specialization.class, specializationOp);

        if (specialization == null)
            return null;
        
        doctor = new Doctor (id, name, phone, email, specialization);
        return doctor;
    }
    
    private Doctor searchDoctor(){
        String id = "";
        String name = "";
        String phone = "";
        String email = "";
        String detail = JOptionPaneConsoleIO.readNonEmpty("Enter Doctor ID / Name / Phone / Email): ");
        
        if (detail == null)
            return null;
        
        if (validate.validName(detail))
            name = detail.trim();
        else if (validate.validPhone(detail)) {
            detail = validate.standardizedPhone(phone);
            phone = detail.trim();
        }
        else if (validate.validEmail(detail))
            email = detail.trim();
        
        doctor = new Doctor(id, name, phone, email, null);
        return doctor;
    }
    
    private int infoModification(){
        String infoSelection = "Which information would you like to update?\n[1] Name\n[2] Phone Number\n[3] Email Address\n[4] Specialization\n";
        return JOptionPaneConsoleIO.readInt(infoSelection);
    }
    
    public String buildMonthlyRosterByWeeks(String doctorID, int year, int month) {
        YearMonth ym = YearMonth.of(year, month);
        int days = ym.lengthOfMonth();
        Shift[] shifts = Shift.values();

        // Auto-create weekday defaults = true (you can flip this to false if you don't want defaults)
        DoctorDuty[][] roster = DocDuty.MonthlyRosterTableMatrix(doctorID, year, month, true);

        StringBuilder sb = new StringBuilder(8192);
        sb.append("Duty Roster for Doctor ").append(doctorID)
          .append(" — ").append(ym).append('\n')
          .append("Legend: ✅ Available   ❌ Unavailable   ⭕ On Leave   - No record\n");

        int weekNo = 1;
        int day = 1;
        while (day <= days) {
            int start = day;
            int end = Math.min(day + 6, days);

            // Week header
            sb.append('\n')
              .append("Week ").append(weekNo++)
              .append(" (").append(ym.atDay(start)).append(" – ").append(ym.atDay(end)).append(")\n");

            // Column headers
            sb.append(fixed("Shift", 10));
            for (int d = start; d <= end; d++) {
                LocalDate date = ym.atDay(d);
                String hdr = two(d) + " " + dow3(date); // e.g., "01 Mon"
                sb.append(fixed(hdr, 10));
            }
            sb.append('\n');

            // Rows per shift
            for (int s = 0; s < shifts.length; s++) {
                sb.append(fixed(shifts[s].name(), 10));
                for (int d = start; d <= end; d++) {
                    DoctorDuty duty = roster[d - 1][s];
                    sb.append(fixed(cell(duty), 10));
                }
                sb.append('\n');
            }

            day = end + 1;
        }

        return sb.toString();
    }

    /* ===== helpers (no collections) ===== */
    private static String cell(DoctorDuty duty) {
        if (duty == null) return "-";
        switch (duty.getAvailability()) {
            case AVAILABLE:
                return "✅";      // or "AV"
            case UNAVAILABLE:
                return "❌";      // or "UN"
            case ON_LEAVE:
                return "⭕";      // or "OL"
            default:
                return "-";
        }
    }

    // left-pad/truncate to fixed width so columns line up decently even in proportional fonts
    private static String fixed(String s, int width) {
        if (s == null) s = "";
        if (s.length() >= width) return s.substring(0, width);
        StringBuilder b = new StringBuilder(width);
        b.append(s);
        while (b.length() < width) b.append(' ');
        return b.toString();
    }

    private static String two(int n) {
        return (n < 10) ? ("0" + n) : Integer.toString(n);
    }

    private static String dow3(LocalDate d) {
        switch (d.getDayOfWeek()) {
            case MONDAY:    return "Mon";
            case TUESDAY:   return "Tue";
            case WEDNESDAY: return "Wed";
            case THURSDAY:  return "Thu";
            case FRIDAY:    return "Fri";
            case SATURDAY:  return "Sat";
            default:        return "Sun";
        }
    }
    
    private int dutyOpPrompt(String table, int year, int month, String doctorID){
        String[] DutyOp = {"Update", "Other Month", "Cancel"};
        
        int dutyChoice = JOptionPaneConsoleIO.readOption("<html><pre style='font-family:monospace'>" + table + "</pre></html>", "Duty Roster", DutyOp);
        
        switch (dutyChoice) {
            case 0:
                YearMonth ym = YearMonth.of(year, month);
                int days = ym.lengthOfMonth();
                
                int date = JOptionPaneConsoleIO.readIntInRange("Enter date: ", 1, days);
                LocalDate ymd = LocalDate.of(year, month, date);
                Shift selectedShift = JOptionPaneConsoleIO.readEnum("Enter shift to be updated: ", Shift.class, new String[]{"MORNING", "AFTERNOON", "NIGHT"});
                Availability selectedAvai = JOptionPaneConsoleIO.readEnum("Enter availability to be updated: ", Availability.class, new String[]{"AVAILABLE", "UNAVAILABLE", "ON_LEAVE"});
                
                boolean update = DocDuty.updateAvailability(doctorID, ymd, selectedShift, selectedAvai);
                
                if (update)
                    return 0;
                else
                    return 1;
            case 1:
                return 2;
            case 2:
                return 3;
            default:
                JOptionPaneConsoleIO.showError("Please enter a valid option.");
        }
        return 3;
    }
}