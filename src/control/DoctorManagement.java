package control;

import adt.*;
import entity.Doctor;
import entity.Specialization;
import utility.*;

/**
 *
 * @author Nelson Cheng Ming Jian
 */
public class DoctorManagement {

    AVLInterface<Doctor> doctorTree = new AVLTree<>();
    Validation validate = new Validation();
    private final LinkedStack<Doctor> undoStack = new LinkedStack<>();
    final int width = 130;

    public boolean isEmptyTree() {
        return doctorTree.isEmpty();
    }

    public int doctorAmount() {
        return doctorTree.size();
    }

    // Abstract Classes
    public boolean registerDoctor(Doctor doctor) {
        return doctorRegistration(doctor);
    }

    public Doctor findDoctor(Doctor doctor) {
        Doctor found = searchByKey(doctor);

        if (found == null) {
            return null;
        } else {
            return found;
        }
    }

    public boolean updateDoctor(Doctor key, int infoSelected, String newName, String newPhone, String newEmail, Specialization newSpecialization) {
        Doctor selectedDoc = searchByKey(key);
        if (selectedDoc == null) {
            return false;
        }

        Doctor snapshot = selectedDoc.clone();

        boolean ok;
        switch (infoSelected) {
            case 1 ->
                ok = modifyName(selectedDoc, newName);
            case 2 ->
                ok = modifyPhone(selectedDoc, newPhone);
            case 3 ->
                ok = modifyEmail(selectedDoc, newEmail);
            case 4 ->
                ok = modifySpecialization(selectedDoc, newSpecialization);
            default -> {
                return false;
            }
        }

        if (ok) {
            undoStack.push(snapshot);
        }
        return ok;
    }

    public boolean undoLastEdit() {
        if (undoStack.isEmpty()) {
            return false;
        }

        Doctor prev = undoStack.pop();

        // Replace current record with the previous snapshot
        Doctor current = searchByKey(new Doctor(prev.getDoctorID(), "", "", "", prev.getSpecialization(), prev.getIcNo()));
        if (current != null) {
            doctorTree.delete(current);
        }
        doctorTree.insert(prev);
        return true;
    }

    public boolean removeDoctor(Doctor doctor) {
        Doctor selectedDoc = searchByKey(doctor);
        return eraseDoctor(selectedDoc);
    }

    public Doctor[] getAllDoctor() {
        Doctor[] doctors = new Doctor[doctorTree.size()];
        return doctorTree.toArrayInorder(doctors);
    }

    public StringBuilder listDoctor() {
        StringBuilder sb = new StringBuilder();
        sb.append(headerBuilder());

        for (Doctor d : doctorTree) {
            sb.append(String.format("%-15s %-25s %-15s %-10s %-15s %-25s %-20s\n",
                    d.getDoctorID(),
                    d.getDoctorName(),
                    d.getIcNo(),
                    validate.getGenderFromIC(d.getIcNo()),
                    d.getContactNo(),
                    d.getEmail(),
                    d.getSpecialization()
            ));
        }

        sb.append(JOptionPaneConsoleIO.line('-', width)).append("\n");
        return sb;
    }

    public StringBuilder listDoctorBySpecialization(Specialization s) {
        StringBuilder sb = new StringBuilder();
        sb.append(headerBuilder());
        Doctor[] doctors = findBySpecialization(s);

        for (Doctor d : doctors) {
            sb.append(String.format("%-15s %-25s %-15s %-10s %-15s %-25s %-20s\n",
                    d.getDoctorID(),
                    d.getDoctorName(),
                    d.getIcNo(),
                    validate.getGenderFromIC(d.getIcNo()),
                    d.getContactNo(),
                    d.getEmail(),
                    d.getSpecialization()
            ));
        }

        sb.append(JOptionPaneConsoleIO.line('-', width)).append("\n");
        return sb;
    }

    // Implementation Classes
    private boolean doctorRegistration(Doctor doctor) {
        String phone = validate.standardizedPhone(doctor.getContactNo());
        doctor.setContactNo(phone);
        String icNo = validate.standardizedIC(doctor.getIcNo());
        doctor.setIcNo(icNo);
        return doctorTree.insert(doctor);
    }

    private Doctor searchByKey(Doctor doctor) {
        for (Doctor doc : getAllDoctor()) {
            if (!doctor.getDoctorID().isEmpty() && doctor.getDoctorID().equals(doc.getDoctorID())) {
                return doc;
            }
            if (!doctor.getDoctorName().isEmpty() && doctor.getDoctorName().equals(doc.getDoctorName())) {
                return doc;
            }
            if (!doctor.getContactNo().isEmpty() && validate.standardizedPhone(doctor.getContactNo()).equals(doc.getContactNo())) {
                return doc;
            }
            if (!doctor.getEmail().isEmpty() && doctor.getEmail().equals(doc.getEmail())) {
                return doc;
            }
            if (!doctor.getIcNo().isEmpty()) {
                String a = doctor.getIcNo().replaceAll("\\D", "");
                String b = doc.getIcNo().replaceAll("\\D", "");
                if (a.length() == 12 && b.length() == 12 && a.equals(b)) {
                    return doc;
                }
            }
        }
        return null;
    }

    private boolean modifyName(Doctor doctor, String newName) {
        if (validate.validName(newName) && !newName.equals(doctor.getDoctorName())) {
            doctor.setDoctorName(newName);
            return true;
        }
        return false;
    }

    private boolean modifyPhone(Doctor doctor, String newPhone) {
        if (validate.validPhone(newPhone) && !newPhone.equals(doctor.getContactNo())) {
            doctor.setContactNo(newPhone);
            return true;
        }
        return false;
    }

    private boolean modifyEmail(Doctor doctor, String newEmail) {
        if (validate.validEmail(newEmail) && !newEmail.equals(doctor.getEmail())) {
            doctor.setEmail(newEmail);
            return true;
        }
        return false;
    }

    private boolean modifySpecialization(Doctor doctor, Specialization newSpecialization) {
        if (!newSpecialization.equals(doctor.getSpecialization())) {
            doctor.setSpecialization(newSpecialization);
            return true;
        }
        return false;
    }

    private boolean eraseDoctor(Doctor doctor) {
        return doctorTree.delete(doctor);
    }

    private Doctor[] findBySpecialization(Specialization s) {
        int n = doctorTree.size();
        Doctor[] buf = new Doctor[n];
        int k = 0;

        for (Doctor d : doctorTree) {
            if (d.getSpecialization() == s) {
                buf[k++] = d;
            }
        }

        Doctor[] out = new Doctor[k];

        for (int i = 0; i < k; i++) {
            out[i] = buf[i];
        }

        return out;
    }

    private String headerBuilder() {
        String header = JOptionPaneConsoleIO.line('-', width) + "\n"
                + JOptionPaneConsoleIO.sectionTitle("Doctor List", width)
                + String.format("%-15s %-25s %-15s %-10s %-15s %-25s %-20s\n",
                        "Doctor ID", "Name", "IC Number", "Gender", "Contact", "Email", "Specialization")
                + JOptionPaneConsoleIO.line('-', width) + "\n";

        return header;
    }
}
