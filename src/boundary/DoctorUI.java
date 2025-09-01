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

/**
 *
 * @author Nelson Cheng Ming Jian
 */

public class DoctorUI {

    DoctorManagement dm;
    Doctor doctor = new Doctor();
    Validation validate = new Validation();
    DoctorDutyManagement DocDuty = new DoctorDutyManagement();
    DoctorReportGenerator ReportGen;
    Doctor[] doctors;
    String[] specializationOp = {"CARDIOLOGY", "NEUROLOGY", "ORTHOPEDICS", "PEDIATRICS", "DERMATOLOGY", "PSYCHIATRY", "ONCOLOGY", "GENERAL_SURGERY", "INTERNAL_MEDICINE", "OBSTETRICS_GYNECOLOGY", "OPHTHALMOLOGY", "OTOLARYNGOLOGY", "RADIOLOGY", "PATHOLOGY", "FAMILY_MEDICINE", "EMERGENCY_MEDICINE"};
    String[] confirmationMessage = {"Confirm", "Cancel"};
    YearMonth current = YearMonth.now();
    int year = current.getYear();
    int month = current.getMonthValue();
    private static final int WIDTH = 100;
    private boolean seeded = false;

    public DoctorUI(DoctorManagement dm) {
        this.dm = dm;
        this.ReportGen = new DoctorReportGenerator(DocDuty, dm);
    }

    public void initializeDoctors() {
        // Hardcoded doctor information
        if (!seeded) {
            Doctor A = new Doctor("D202408110001", "Nelson Cheng Ming Jian", "0182284609", "nelson@gmail.com", Specialization.PSYCHIATRY, "050715070395");
            Doctor B = new Doctor(IDGenerator.next(IDType.DOCTOR), "Khor Kai Yang", "0121234567", "ky@gmail.com", Specialization.NEUROLOGY, "050704070498");
            Doctor C = new Doctor(IDGenerator.next(IDType.DOCTOR), "Ng Wei Jian", "0121234467", "wj@gmail.com", Specialization.CARDIOLOGY, "050123071233");
            Doctor D = new Doctor(IDGenerator.next(IDType.DOCTOR), "Sim Jia Quan", "0121334567", "jq@gmail.com", Specialization.GENERAL_SURGERY, "050704070433");
            Doctor E = new Doctor(IDGenerator.next(IDType.DOCTOR), "Giggs Teh Ting Wei", "0121234547", "tw@gmail.com", Specialization.CARDIOLOGY, "050123071223");

            if (dm.findDoctor(A) == null) {
                dm.registerDoctor(A);
            }
            if (dm.findDoctor(B) == null) {
                dm.registerDoctor(B);
            }
            if (dm.findDoctor(C) == null) {
                dm.registerDoctor(C);
            }
            if (dm.findDoctor(D) == null) {
                dm.registerDoctor(D);
            }
            if (dm.findDoctor(E) == null) {
                dm.registerDoctor(E);
            }

            doctors = dm.getAllDoctor();
            seeded = true;
        }
    }

    public void taskSelection() {
        boolean newTask = true;
        String[] menu = {"Register", "Search", "Doctor List", "Duty By Date", "Report", "Back"};
        String[] updateOption = {"Update", "Remove", "Duty", "Update Last Edit", "Back"};

        initializeDoctors();

        do {
            int choice = JOptionPaneConsoleIO.readOption("Which task would you like to perform?", "Doctor Management Module", menu);

            if (choice == -1) {
                break;
            }

            switch (choice) {
                case 0 -> {
                    // Register new doctor
                    doctor = newDetailsPrompt();
                    if (doctor == null) {
                        break;
                    }

                    boolean success = dm.registerDoctor(doctor);

                    if (success) {
                        JOptionPaneConsoleIO.showInfo("Doctor is successfully registered.");
                        doctors = dm.getAllDoctor(); // refresh
                    } else {
                        JOptionPaneConsoleIO.showError("Unsuccessful action. Please try again.");
                    }
                }
                case 1 -> {
                    // Search doctor
                    int updateChoice = -1;

                    if (dm.isEmptyTree()) {
                        JOptionPaneConsoleIO.showError("Empty doctor record.");
                        break;
                    }

                    doctor = searchDoctor();
                    if (doctor == null) {
                        break;
                    }

                    Doctor result = dm.findDoctor(doctor);
                    int update = -1;
                    OUTER:
                    do {
                        if (result != null) {
                            updateChoice = JOptionPaneConsoleIO.readOption(result.toString(), "Doctor Information", updateOption);
                        } else {
                            JOptionPaneConsoleIO.showError("No doctor is found.");
                            break;
                        }
                        int modifyChoice = 0;
                        String newName = null;
                        String newPhone = null;
                        String newEmail = null;
                        Specialization newSpecialization = null;

                        switch (updateChoice) {
                            case 0 -> {
                                // Update doctor profile
                                modifyChoice = infoModification();
                                if (modifyChoice == 5) { // Back
                                    update = 3;
                                    continue;
                                }
                                switch (modifyChoice) {
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
                                    default ->
                                        JOptionPaneConsoleIO.showError("Please enter a valid option.");
                                }
                                boolean updateResult = dm.updateDoctor(result, modifyChoice, newName, newPhone, newEmail, newSpecialization);
                                if (updateResult) {
                                    JOptionPaneConsoleIO.showInfo("Information updated.");
                                    doctors = dm.getAllDoctor(); // refresh
                                    // reload result by ID
                                    result = dm.findDoctor(new Doctor(result.getDoctorID(), "", "", "", null, ""));
                                } else {
                                    JOptionPaneConsoleIO.showError("Update unsuccessfully. Please try again.");
                                }
                                update = 3;
                            }
                            case 1 -> {
                                // Remove doctor
                                int confirm = JOptionPaneConsoleIO.readOption("Do you CONFIRM to remove doctor?", "Confirmation Message", confirmationMessage);
                                if (confirm != 0) {
                                    update = 3;
                                    continue;
                                }
                                boolean removeResult = dm.removeDoctor(result);
                                if (removeResult) {
                                    JOptionPaneConsoleIO.showInfo("Doctor removed.");
                                    doctors = dm.getAllDoctor(); // refresh
                                } else {
                                    JOptionPaneConsoleIO.showError("Remove unsuccessfully. Please try again.");
                                }
                                break OUTER;
                            }
                            case 2 -> {
                                // Duty checker (Display duty in table form of the doctor)
                                do {
                                    String table = DocDuty.buildMonthlyRosterByWeeks(result.getDoctorID(), year, month);
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
                            }
                            case 3 -> {
                                if (dm.undoLastEdit()) {
                                    JOptionPaneConsoleIO.showInfo("Last edit undone.");
                                    result = dm.findDoctor(new Doctor(result.getDoctorID(), "", "", "", null, ""));
                                    doctors = dm.getAllDoctor(); // refresh
                                } else {
                                    JOptionPaneConsoleIO.showError("No edits to undo.");
                                }
                                update = 3;
                                continue;
                            }
                            default -> {
                                break OUTER;
                            }
                        }
                    } while (update == 3);
                }
                case 2 -> {
                    String[] searchOp = {"List All Doctor", "List Doctor By Specialization", "Back"};
                    int searchChoice = JOptionPaneConsoleIO.readOption("Select a listing option: ", "Listing Options", searchOp);

                    if (dm.isEmptyTree()) {
                        JOptionPaneConsoleIO.showError("Empty doctor record.");
                        break;
                    }

                    // List all doctor
                    if (searchChoice == 0) {
                        JOptionPaneConsoleIO.showMonospaced("Doctor List", dm.listDoctor().toString());
                    } else if (searchChoice == 1) {
                        Specialization spec = JOptionPaneConsoleIO.readEnum("Select a specialization: ", Specialization.class, specializationOp);
                        JOptionPaneConsoleIO.showMonospaced("Doctor List (" + spec + ")", dm.listDoctorBySpecialization(spec).toString());
                    }
                }
                case 3 -> {
                    int year = JOptionPaneConsoleIO.readIntInRange("Enter year: ", LocalDate.now().getYear(), LocalDate.now().getYear() + 5);
                    if (year == -1) {
                        break;
                    }

                    int month = JOptionPaneConsoleIO.readIntInRange("Enter month: ", 1, 12);
                    if (month == -1) {
                        break;
                    }

                    int day = JOptionPaneConsoleIO.readIntInRange("Enter day: ", 1, YearMonth.of(year, month).lengthOfMonth());
                    if (day == -1) {
                        break;
                    }

                    LocalDate date = LocalDate.of(year, month, day);

                    Shift shift = JOptionPaneConsoleIO.readEnum("Enter shift: ", Shift.class, new String[]{"MORNING", "AFTERNOON", "NIGHT"});
                    if (shift == null) {
                        break;
                    }

                    // always use fresh list
                    Doctor[] doctorsNow = dm.getAllDoctor();

                    DoctorDuty[] arr = DocDuty.searchDutiesByDateShift(date, shift);

                    if (doctorsNow != null) {
                        for (Doctor doctorsNow1 : doctorsNow) {
                            DocDuty.WeekdayDuty(doctorsNow1.getDoctorID(), date, shift);
                        }
                    }

                    if (validate.isWeekday(date)) {
                        for (DoctorDuty arr1 : arr) {
                            if (arr1.getAvailability() == null) {
                                DocDuty.WeekdayDuty(arr1.getDoctorID(), date, shift);
                            }
                        }
                        arr = DocDuty.searchDutiesByDateShift(date, shift);
                    }

                    StringBuilder sb = new StringBuilder(256);
                    sb.append("Duties on ").append(date).append(" (").append(shift).append(")\n");

                    if (arr.length == 0) {
                        JOptionPaneConsoleIO.showError("No doctor duties on " + date + " (" + shift + ").");
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
                                sb.append(String.format("%d.  %s (%s)%n", i + 1, name, spec));
                            }
                        }
                    }
                    JOptionPaneConsoleIO.showInfo("<html><pre style='font-family:monospace'>" + sb + "</pre></html>");
                }
                case 4 -> {
                    boolean repeatReport = false;
                    do {
                        String[] reportOp = {"Annual Doctor Attendance Report", "Specialization Inventory Report", "Back"};
                        int reportChoice = JOptionPaneConsoleIO.readOption("Which report would you like to generate?", "Generate Report", reportOp);

                        switch (reportChoice) {
                            case 0 -> {
                                // Annual Doctor Attendance Report
                                StringBuilder attReport = new StringBuilder(4096);
                                boolean avaiDoc = false;
                                int yearToGen = JOptionPaneConsoleIO.readIntInRange("Enter year: ", LocalDate.now().getYear() - 5, LocalDate.now().getYear());

                                if (yearToGen == -1) {
                                    repeatReport = true;
                                    continue;
                                }

                                String[] scopeOp = {"Specific Doctor", "All Eligible Doctors", "Back"};
                                int scopeChoice = JOptionPaneConsoleIO.readOption("Generate attendance for:", "Report Scope", scopeOp);

                                if (scopeChoice == -1 || scopeChoice == 2) {
                                    repeatReport = true;
                                    continue;
                                }

                                attReport.append(JOptionPaneConsoleIO.reportHeader(
                                        "Doctor Management Module",
                                        "Doctor Annual Attendance Report",
                                        WIDTH
                                ));

                                if (scopeChoice == 0) {
                                    // Specific doctor (avoid repeating many pages)
                                    Doctor key = searchDoctor();
                                    if (key != null) {
                                        Doctor foundDoc = dm.findDoctor(key);
                                        if (foundDoc != null) {
                                            String regDateStr = foundDoc.getDoctorID().substring(1, foundDoc.getDoctorID().length() - 4);
                                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
                                            LocalDate regDate = LocalDate.parse(regDateStr, formatter);

                                            if (regDate.getYear() <= yearToGen) {
                                                ReportGen.yearlyAttendanceRate(foundDoc.getDoctorID().trim(), yearToGen, true, attReport);
                                                avaiDoc = true;
                                            }
                                        }
                                    } else {
                                        continue;
                                    }
                                } else {
                                    // All eligible doctors (original behavior)
                                    for (int i = 0; i < dm.doctorAmount(); i++) {
                                        String regDateStr = doctors[i].getDoctorID().substring(1, doctors[i].getDoctorID().length() - 4);
                                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
                                        LocalDate regDate = LocalDate.parse(regDateStr, formatter);

                                        if (regDate.getYear() <= yearToGen) {
                                            ReportGen.yearlyAttendanceRate(doctors[i].getDoctorID().trim(), yearToGen, true, attReport);
                                            avaiDoc = true;
                                        }
                                    }
                                }

                                ReportGen.attendanceRanking(yearToGen, true, attReport);
                                attReport.append(JOptionPaneConsoleIO.reportFooter(WIDTH));

                                if (avaiDoc) {
                                    JOptionPaneConsoleIO.showMonospaced("Annual Attendance Report", attReport.toString());
                                } else {
                                    JOptionPaneConsoleIO.showError("No doctor available in this year.");
                                }
                                repeatReport = true;
                            }
                            case 1 -> {
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
                            }
                            case 2 ->
                                repeatReport = false;
                            default -> {
                            }
                        }
                    } while (repeatReport);
                }
                case 5 -> // End performing task
                    newTask = false;
                default ->
                    JOptionPaneConsoleIO.showError("Please enter a valid option.");
            }
        } while (newTask);
    }

    private Doctor newDetailsPrompt() {
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

            if (name == null) {
                return null;
            }

            if (validate.validName(name)) {
                name = validate.standardizedName(name);
                doctor = new Doctor("", name, "", "", null, "");
                if (dm.findDoctor(doctor) == null) {
                    valid = true;
                } else {
                    JOptionPaneConsoleIO.showError("Doctor name existed in the doctor record.");
                }
            } else {
                JOptionPaneConsoleIO.showError("Please enter a valid name.");
            }
        } while (!valid);

        do {
            valid = false;
            icNo = JOptionPaneConsoleIO.readNonEmpty("Enter IC Number: ");

            if (icNo == null) {
                return null;
            }

            icNo = validate.standardizedIC(icNo);

            if (validate.validIC(icNo)) {
                doctor = new Doctor("", "", "", "", null, icNo);
                if (dm.findDoctor(doctor) == null) {
                    valid = true;
                } else {
                    JOptionPaneConsoleIO.showError("Doctor name existed in the doctor record.");
                }
            } else {
                JOptionPaneConsoleIO.showError("Please enter a valid IC number.");
            }
        } while (!valid);

        do {
            valid = false;
            phone = JOptionPaneConsoleIO.readNonEmpty("Enter phone number: ");

            if (phone == null) {
                return null;
            }

            phone = validate.standardizedPhone(phone);

            if (validate.validPhone(phone)) {
                doctor = new Doctor("", "", phone, "", null, "");
                if (dm.findDoctor(doctor) == null) {
                    valid = true;
                } else {
                    JOptionPaneConsoleIO.showError("Phone number existed in the doctor record.");
                }
            } else {
                JOptionPaneConsoleIO.showError("Please enter a valid phone number.");
            }
        } while (!valid);

        do {
            valid = false;
            email = JOptionPaneConsoleIO.readNonEmpty("Enter Email Address: ");

            if (email == null) {
                return null;
            }

            if (validate.validEmail(email)) {
                doctor = new Doctor("", "", "", email, null, "");
                if (dm.findDoctor(doctor) == null) {
                    valid = true;
                } else {
                    JOptionPaneConsoleIO.showError("Email Address existed in the doctor record.");
                }
            } else {
                JOptionPaneConsoleIO.showError("Please enter a valid email address.");
            }
        } while (!valid);

        specialization = JOptionPaneConsoleIO.readEnum("Select specialization: ", Specialization.class, specializationOp);

        if (specialization == null) {
            return null;
        }

        doctor = new Doctor(id, name, phone, email, specialization, icNo);
        return doctor;
    }

    private Doctor searchDoctor() {
        String id = "";
        String name = "";
        String phone = "";
        String email = "";
        String icNo = "";
        String detail = JOptionPaneConsoleIO.readNonEmpty("Enter Doctor ID / Name / Phone / Email / IC: ");

        if (detail == null) {
            return null;
        }

        if (validate.validName(detail)) {
            name = detail.trim();
            name = validate.standardizedName(name);
        } else if (validate.validPhone(detail)) {
            phone = detail.trim();
            phone = validate.standardizedPhone(phone);
        } else if (validate.validEmail(detail)) {
            email = detail.trim();
        } else if (validate.validIC(detail)) {
            icNo = detail.trim();
            icNo = validate.standardizedIC(icNo);
        } else {
            id = detail.trim();
        }

        doctor = new Doctor(id, name, phone, email, null, icNo);
        return doctor;
    }

    private int infoModification() {
        String[] updateOp = {"Name", "Phone Number", "Email Address", "Specialization", "Back"};
        String infoSelection = "Which information would you like to update?";
        return JOptionPaneConsoleIO.readOption(infoSelection, "Information Modification Options", updateOp) + 1;
    }

    private int dutyOpPrompt(String table, int year, int month, String doctorID) {
        String[] DutyOp = {"Update", "Other Month", "Back"};

        int dutyChoice = JOptionPaneConsoleIO.readOption("<html><pre style='font-family:monospace'>" + table + "</pre></html>", "Duty Roster", DutyOp);

        switch (dutyChoice) {
            case 0:
                YearMonth ym = YearMonth.of(year, month);

                if (ym.isBefore(current)) {
                    return 4;
                }

                int days = ym.lengthOfMonth();

                int date = JOptionPaneConsoleIO.readIntInRange("Enter date: ", 1, days);

                if (date == -1) {
                    return -1;
                }

                LocalDate ymd = LocalDate.of(year, month, date);

                if (ymd.isBefore(LocalDate.now())) {
                    return 4;
                }

                Shift selectedShift = JOptionPaneConsoleIO.readEnum("Enter shift to be updated: ", Shift.class, new String[]{"MORNING", "AFTERNOON", "NIGHT"});

                if (selectedShift == null) {
                    return -1;
                }

                Availability selectedAvai = JOptionPaneConsoleIO.readEnum("Enter availability to be updated: ", Availability.class, new String[]{"AVAILABLE", "UNAVAILABLE", "ON_LEAVE"});

                if (selectedAvai == null) {
                    return -1;
                }

                boolean update = DocDuty.updateAvailability(doctorID, ymd, selectedShift, selectedAvai);

                if (update) {
                    return 0;
                } else {
                    return 1;
                }
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
