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
import java.time.format.DateTimeFormatter;

public class DoctorUI {
    DoctorManagement dm = new DoctorManagement();
    Doctor doctor = new Doctor();
    Validation validate = new Validation();
    DoctorDutyManagement DocDuty = new DoctorDutyManagement();
    DoctorReportGenerator ReportGen = new DoctorReportGenerator(DocDuty, dm);
    Doctor[] doctors;
    String[] specializationOp = {"CARDIOLOGY", "NEUROLOGY", "ORTHOPEDICS", "PEDIATRICS", "DERMATOLOGY", "PSYCHIATRY", "ONCOLOGY", "GENERAL_SURGERY", "INTERNAL_MEDICINE", "OBSTETRICS_GYNECOLOGY", "OPHTHALMOLOGY", "OTOLARYNGOLOGY", "RADIOLOGY", "PATHOLOGY", "FAMILY_MEDICINE", "EMERGENCY_MEDICINE"};
    String[] confirmationMessage = {"Confirm", "Cancel"};
    YearMonth current = YearMonth.now();
    int year = current.getYear();
    int month = current.getMonthValue();
    private static final int WIDTH = 100;
    
    public void taskSelection(){
        boolean newTask = true;
        String[] menu = {"Register", "Search", "Doctor List", "Duty By Date", "Report", "Back"};
        String[] updateOption = {"Update", "Remove", "Duty", "Update Last Edit", "Back"};
        
        // Hardcoded doctor information
        Doctor A = new Doctor("D202408110001", "Nelson", "0182284609", "nelson@gmail.com", Specialization.PSYCHIATRY, "050715070395");
        Doctor B = new Doctor(IDGenerator.next(IDType.DOCTOR), "Khor Kai Yang", "0121234567", "ky@gmail.com", Specialization.NEUROLOGY, "050704070498");
        Doctor C = new Doctor(IDGenerator.next(IDType.DOCTOR), "Ng Wei Jian", "0121234567", "wj@gmail.com", Specialization.CARDIOLOGY, "050123071233");
        dm.registerDoctor(A);
        dm.registerDoctor(B); 
        dm.registerDoctor(C);
        doctors = dm.getAllDoctor();
            
        do {
            int choice = JOptionPaneConsoleIO.readOption("Which task would you like to perform?", "Doctor Management Module", menu);
            
            if (choice == -1)
                break;
            
            switch (choice) {
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

                            if (modifyChoice == 0 || modifyChoice == 5) {
                                update = 3;
                                continue;
                            }

                            switch (modifyChoice){
                                case 1 -> {
                                    // Modify name
                                    newName = JOptionPaneConsoleIO.readNonEmpty("Enter new name: ");
                                    if (newName == null) {
                                        update = 3;
                                        continue;
                                    }
                                    newName = validate.standardizedName(newName);
                                }
                                case 2 -> {
                                    // Modify phone number
                                    newPhone = JOptionPaneConsoleIO.readNonEmpty("Enter new phone number: ");
                                    if (newPhone == null) {
                                        update = 3;
                                        continue;
                                    }
                                    newPhone = validate.standardizedPhone(newPhone);
                                }
                                case 3 -> {
                                    // Modify email address
                                    newEmail = JOptionPaneConsoleIO.readNonEmpty("Enter new email address: ");
                                    if (newEmail == null) {
                                        update = 3;
                                        continue;
                                    }
                                }
                                case 4 -> {
                                    // Modify specialization
                                    newSpecialization = JOptionPaneConsoleIO.readEnum("Select new specialization: ", Specialization.class, specializationOp);
                                    if (newSpecialization == null) {
                                        update = 3;
                                        continue;
                                    }
                                }
                                default -> JOptionPaneConsoleIO.showError("Please enter a valid option.");
                            }

                            boolean updateResult = dm.updateDoctor(result, modifyChoice, newName, newPhone, newEmail, newSpecialization);

                            if (updateResult)
                                JOptionPaneConsoleIO.showInfo("Information updated.");
                            else
                                JOptionPaneConsoleIO.showError("Update unsuccessfully. Please try again.");
                            
                            update = 3;
                        } else if (updateChoice == 1){
                            // Remove doctor
                            int confirm = JOptionPaneConsoleIO.readOption("Do you CONFIRM to remove doctor?", "Confirmation Message", confirmationMessage);
                            
                            if (confirm != 0) {
                                update = 3;
                                continue;
                            }
                            
                            boolean removeResult = dm.removeDoctor(result);

                            if (removeResult)
                                JOptionPaneConsoleIO.showInfo("Doctor removed.");
                            else
                                JOptionPaneConsoleIO.showError("Remove unsuccessfully. Please try again.");
                            
                            break;
                        } else if (updateChoice == 2){
                            // Duty checker (Display duty in table form of the doctor)
                            do {
                                String table = buildMonthlyRosterByWeeks(result.getDoctorID() ,year, month);
                                update = dutyOpPrompt(table, year, month, result.getDoctorID());

                                switch (update) {
                                    case -1:
                                        update = 2;
                                        break;
                                    case 0:
                                        JOptionPaneConsoleIO.showInfo("Roster updated successful.");
                                        break;
                                    case 1:
                                        JOptionPaneConsoleIO.showError("Past dates are not allowed. Please retry again.");
                                        break;
                                    case 2:
                                        int monthToShow = JOptionPaneConsoleIO.readIntInRange("Enter month: ", 1, 12);
                                        
                                        if (monthToShow == -1) {
                                            update = 2;
                                            break;
                                        }
                                        
                                        month = monthToShow;
                                        break;
                                    case 4:
                                        JOptionPaneConsoleIO.showError("Update is not allowed to past schedule.");
                                        break;
                                    default:
                                        break;
                                }
                            } while (update == 2 || update == 1 || update == 0 || update == 4);
                        } else if (updateChoice == 3) {
                            if (dm.undoLastEdit()) {
                                JOptionPaneConsoleIO.showInfo("Last edit undone.");
                                result = dm.findDoctor(new Doctor(result.getDoctorID(), "", "", "", null, ""));
                            } else {
                                JOptionPaneConsoleIO.showError("No edits to undo.");
                            }
                            update = 3;
                            continue;
                        } else {
                            break;
                        }
                    } while (update == 3);
                    break;
                case 2:
                    // List all doctor
                    if (doctors == null) {
                        JOptionPaneConsoleIO.showError("No doctor found.");
                    } else {
                        JOptionPaneConsoleIO.showMonospaced("Doctor List", dm.listDoctor().toString());
                    }
                    break;
                case 3:
                    int year = JOptionPaneConsoleIO.readIntInRange("Enter year: ", LocalDate.now().getYear(), LocalDate.now().getYear() + 5);
                    
                    if (year == -1){
                        update = 3;
                        continue;
                    }
                    
                    int month = JOptionPaneConsoleIO.readIntInRange("Enter month: ", 1, 12);
                    
                    if (month == -1){
                        update = 3;
                        continue;
                    }
                    
                    int day = JOptionPaneConsoleIO.readIntInRange("Enter day: ", 1, YearMonth.of(year, month).lengthOfMonth());
                    
                    if (day == -1){
                        update = 3;
                        continue;
                    }
                    
                    LocalDate date = LocalDate.of(year, month, day);

                    Shift shift = JOptionPaneConsoleIO.readEnum("Enter shift: ", Shift.class, new String[]{"MORNING", "AFTERNOON", "NIGHT"});
                    
                    if (shift == null){
                        update = 3;
                        continue;
                    }
                    
                    DoctorDuty[] arr = DocDuty.searchDutiesByDateShift(date, shift);
                    
                    if (doctors != null){
                        for (int i = 0; i < dm.doctorAmount(); i++){
                            DocDuty.WeekdayDuty(doctors[i].getDoctorID(), date, shift);
                        }
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
                        JOptionPaneConsoleIO.showError("No doctor duties on " + date + " (" + shift + ").");
                        break;
                    } else {
                        for (int i = 0; i < arr.length; i++) {
                            DoctorDuty d = arr[i];

                            Doctor searchKey = new Doctor(d.getDoctorID(), "", "", "", null, "");
                            Doctor doctor = dm.findDoctor(searchKey);

                            String name = (doctor != null ? doctor.getDoctorName() : d.getDoctorID());
                            String spec = (doctor != null && doctor.getSpecialization() != null)
                                    ? doctor.getSpecialization().toString()
                                    : "-";
                            
                            if (d.getAvailability().equals(Availability.AVAILABLE)) {
                                sb.append(String.format(i + 1 + ".  %s (%s)", name, spec) + "\n");
                            }
                        }
                    }
                    JOptionPaneConsoleIO.showInfo("<html><pre style='font-family:monospace'>" + sb + "</pre></html>");
                    break;
                case 4:
                    boolean repeatReport = false;
                    do {    
                        String[] reportOp = {"Annual Doctor Attendance Report", "Specialization Inventory Report", "Back"};
                        int reportChoice = JOptionPaneConsoleIO.readOption("Which report would you like to generate?", "Generate Report", reportOp);

                        switch (reportChoice) {
                            case 0:
                                // Annual Doctor Attendance Report
                                StringBuilder attReport = new StringBuilder(4096);
                                boolean avaiDoc = false;
                                int yearToGen = JOptionPaneConsoleIO.readIntInRange("Enter year: ", LocalDate.now().getYear() - 5, LocalDate.now().getYear());

                                if (yearToGen == -1){
                                    repeatReport = true;
                                    continue;
                                }

                                attReport.append(JOptionPaneConsoleIO.reportHeader(
                                        "Doctor Management Module", 
                                        "Doctor Annual Attendance Report", 
                                        WIDTH
                                ));
                                
                                for (int i = 0; i < dm.doctorAmount(); i++) {
                                    String regDateStr = doctors[i].getDoctorID().substring(1, doctors[i].getDoctorID().length() - 4);
                                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
                                    LocalDate regDate = LocalDate.parse(regDateStr, formatter);

                                    if (regDate.getYear() <= yearToGen) {
                                        ReportGen.yearlyAttendanceRate(doctors[i].getDoctorID().trim(), yearToGen, true, attReport);
                                        avaiDoc = true;
                                    }
                                }
                                
                                attReport.append(JOptionPaneConsoleIO.reportFooter(WIDTH));
                                
                                if (avaiDoc){
                                    JOptionPaneConsoleIO.showMonospaced("Annual Attendance Report", attReport.toString());
                                } else {
                                    JOptionPaneConsoleIO.showError("No doctor available in this year.");
                                }
                                repeatReport = true;
                                break;
                            case 1:
                                // Clinic Specialization Inventory
                                StringBuilder inv = new StringBuilder(2048);
                                
                                inv.append(JOptionPaneConsoleIO.reportHeader(
                                        "Doctor Management Module", 
                                        "Clinic Specialization Inventory Report", 
                                        WIDTH
                                ));
                                
                                ReportGen.specializationInventory(inv);
                                
                                inv.append(JOptionPaneConsoleIO.reportFooter(WIDTH));
                                
                                JOptionPaneConsoleIO.showMonospaced("Clinic Specilization Inventory Report", inv.toString());
                                repeatReport = true;
                                break;
                            case 2:
                                repeatReport = false;
                                break;
                            default:
                                break;
                        }
                    } while (repeatReport);
                    break;
                case 5:
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
        String icNo;
        String id = IDGenerator.next(IDType.DOCTOR);
                
        do {
            valid = false;
            name = JOptionPaneConsoleIO.readNonEmpty("Enter Doctor Name: ");
            
            if (name == null)
                return null;
            
            if (validate.validName(name)) {
                name = validate.standardizedName(name);
                doctor = new Doctor("", name, "","", null, "");
                if (dm.findDoctor(doctor) == null)
                    valid = true;
                else
                    JOptionPaneConsoleIO.showError("Doctor name existed in the doctor record.");
            } else 
                JOptionPaneConsoleIO.showError("Please enter a valid name.");
        } while (!valid);
        
        do {
            valid = false;
            icNo = JOptionPaneConsoleIO.readNonEmpty("Enter IC Number: ");
            
            if (icNo == null)
                return null;
            
            icNo = validate.standardizedIC(icNo);
            
            if (validate.validIC(icNo)) {
                doctor = new Doctor("", "", "","", null, icNo);
                if (dm.findDoctor(doctor) == null)
                    valid = true;
                else
                    JOptionPaneConsoleIO.showError("Doctor name existed in the doctor record.");
            } else 
                JOptionPaneConsoleIO.showError("Please enter a valid IC number.");
        } while (!valid);
        
        do {
            valid = false;
            phone = JOptionPaneConsoleIO.readNonEmpty("Enter phone number: ");
            
            if (phone == null)
                return null;
            
            phone = validate.standardizedPhone(phone);
            
            if (validate.validPhone(phone)) {
                doctor = new Doctor("", "", phone, "", null, "");
                if (dm.findDoctor(doctor) == null)
                    valid = true;
                else
                    JOptionPaneConsoleIO.showError("Phone number existed in the doctor record.");
            } else 
                JOptionPaneConsoleIO.showError("Please enter a valid phone number.");
        } while (!valid);
        
        do {
            valid = false;
            email = JOptionPaneConsoleIO.readNonEmpty("Enter Email Address: ");
            
            if (email == null)
                return null;
            
            if (validate.validEmail(email)) {
                doctor = new Doctor("", "", "", email, null, "");
                if (dm.findDoctor(doctor) == null)
                    valid = true;
                else
                    JOptionPaneConsoleIO.showError("Email Address existed in the doctor record.");
            } else 
                JOptionPaneConsoleIO.showError("Please enter a valid email address.");
        } while (!valid);
        
        specialization = JOptionPaneConsoleIO.readEnum("Select specialization: ", Specialization.class, specializationOp);

        if (specialization == null)
            return null;
        
        doctor = new Doctor (id, name, phone, email, specialization, icNo);
        return doctor;
    }
    
    private Doctor searchDoctor(){
        String id = "";
        String name = "";
        String phone = "";
        String email = "";
        String icNo = "";
        String detail = JOptionPaneConsoleIO.readNonEmpty("Enter Doctor ID / Name / Phone / Email / IC: ");
        
        if (detail == null)
            return null;
        
        if (validate.validName(detail)) {
            name = detail.trim();
            name = validate.standardizedName(name);
        } else if (validate.validPhone(detail)) {
            phone = detail.trim();
            phone = validate.standardizedPhone(phone);
        } else if (validate.validEmail(detail))
            email = detail.trim();
        else if (validate.validIC(detail)) {
            icNo = detail.trim();
            icNo = validate.standardizedIC(icNo);
        } else 
            id = detail.trim();
        
        doctor = new Doctor(id, name, phone, email, null, icNo);
        return doctor;
    }
    
    private int infoModification(){
        String[] updateOp = {"Name", "Phone Number", "Email Address", "Specialization", "Back"};
        String infoSelection = "Which information would you like to update?";
        return JOptionPaneConsoleIO.readOption(infoSelection, "Information Modification Options", updateOp) + 1;
    }
    
    public String buildMonthlyRosterByWeeks(String doctorID, int year, int month) {
        YearMonth ym = YearMonth.of(year, month);
        int days = ym.lengthOfMonth();
        Shift[] shifts = Shift.values();

        // Auto-create weekday defaults = true (you can flip this to false if you don't want defaults)
        DoctorDuty[][] roster = DocDuty.MonthlyRosterTableMatrix(doctorID, year, month, true);

        StringBuilder sb = new StringBuilder(8192);
        Doctor doctorDuty = new Doctor(doctorID.trim(), "", "", "", null, "");
        sb.append("Duty Roster for Dr. ").append(dm.findDoctor(doctorDuty).getDoctorName())
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
                String hdr = two(d) + " " + dow3(date);
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
        String[] DutyOp = {"Update", "Other Month", "Back"};
        
        int dutyChoice = JOptionPaneConsoleIO.readOption("<html><pre style='font-family:monospace'>" + table + "</pre></html>", "Duty Roster", DutyOp);
        
        switch (dutyChoice) {
            case 0:
                YearMonth ym = YearMonth.of(year, month);
                
                if (ym.isBefore(current))
                    return 4;
                
                int days = ym.lengthOfMonth();
                
                int date = JOptionPaneConsoleIO.readIntInRange("Enter date: ", 1, days);
                
                if (date == -1)
                    return -1;
                
                LocalDate ymd = LocalDate.of(year, month, date);
                
                if (ymd.isBefore(LocalDate.now()))
                    return 4;
                
                Shift selectedShift = JOptionPaneConsoleIO.readEnum("Enter shift to be updated: ", Shift.class, new String[]{"MORNING", "AFTERNOON", "NIGHT"});
                
                if (selectedShift == null)
                    return -1;
                
                Availability selectedAvai = JOptionPaneConsoleIO.readEnum("Enter availability to be updated: ", Availability.class, new String[]{"AVAILABLE", "UNAVAILABLE", "ON_LEAVE"});
                
                if (selectedAvai == null)
                    return -1;
                
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