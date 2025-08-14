package control;

import entity.Doctor;
import javax.swing.JOptionPane;

public class testing {
    public static void main(String[] args) {
        DoctorManagement dm = new DoctorManagement();

        Doctor d1 = new Doctor("S0001", "Nelson", "01112846092", "nelsonchengmingjian@gmail.com", "Neurology");

        dm.registerDoctor(d1);

        String doctorID = JOptionPane.showInputDialog("Enter doctor's ID:");
        dm.updateDoctorInfo(doctorID);
        
        dm.viewDoctorInfo("", "", "", "");
    }
}
