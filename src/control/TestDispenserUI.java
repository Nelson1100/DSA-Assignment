//package control;
//
//import control.MedicineDispenser;
//import control.StockMaintenance;
//import dao.PrescriptionInitializer;
//import dao.StockInitializer;
//import entity.Prescription;
//import adt.LinkedQueue;
//import boundary.MedicineDispenserUI;
//
//public class TestDispenserUI {
//    public static void main(String[] args) {
//        // Step 1: Initialize stock
//        StockMaintenance stock = new StockMaintenance();
//        StockInitializer.initialize(stock);
//
//        // Step 2: Initialize prescriptions
//        LinkedQueue<Prescription> prescriptions = PrescriptionInitializer.initialize();
//
//        // Step 3: Create the dispenser
//        MedicineDispenser dispenser = new MedicineDispenser(stock);
//
//        // Step 4: Launch the UI
//        MedicineDispenserUI ui = new MedicineDispenserUI(dispenser, prescriptions);
//        ui.run();
//    }
//}
