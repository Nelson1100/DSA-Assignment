Clinic Management System

========================

Problem Statement

Tunku Abdul Rahman University of Management and Technology (TAR UMT) requires a Clinic Management System to manage patient care and clinic operations efficiently. The system handles patient registration, doctor duty schedules, consultations, treatments, pharmacy stock, and pharmacist management. Reports are included in every module to support analysis and decision-making.

This project demonstrates the application of custom-built collection ADTs (AVLTree, LinkedQueue, LinkedStack) to model one-to-many relationships and manage dynamic data efficiently.

Modules Overview

1. Patient Management Module
	- Register new patients with validation (Name, Gender, Email, Contact, IC Number).
	- Patient records stored in multiple AVL indexes (by ID, Name, Contact, Email).
	- Manage patients profiles: search, update, delete, view.
	- Patients are enqueued for visits in a LinkedQueue<PatientVisit>.
 	- Manage patients visits: serve, find, view, delete.

   Reports:
	- Visit Queue Analysis Report: Queue snapshot, wait-time distribution, longest waiting patients.
	- Patient Summary Report: Demographics (gender/age), visit frequency.

2. Doctor Management Module
	- Manage doctor profiles: add, update, remove, search.
	- Duty schedules managed with DoctorDutyManagement.
	- Undo support for doctor updates via LinkedStack<Doctor>.
 	- Doctor listing features.
  	- Duty Checking by date and shift.

   Reports:
	- Annual Attendance Report & Attendance Ranking: Per-doctor yearly attendance summary & Doctors ranked by annual attendance.
	- Specialization Inventory Report: Count and percentage of doctors by specialization.

3. Consultation Management Module
	- Add, list, update, delete consultation records.
	- Booking requires Patient ID, Doctor ID, and Date/Shift.
	- System checks doctor availability before booking.
	- Each consultation assigned a unique Consultation ID.

   Reports:
	- Consultation by Timeslot Report: Popularity of timeslots (morning/afternoon/night).
	- Consultation by Doctor Report: Percentage of consultations per doctor.

4. Medical Treatment Management Module
	- Needs valid / registered patient to proceed with treatments (exists)
	- Doctors prescribe treatments after consultations.
	- Treatment requires Patient ID, Date, Diagnosis, and prescribed treatments.

   Reports:
	- Patient Visit Summary: Total amount of unique patients with their total visits and treatments number.
	- Most Common Symptoms Report: Symptoms frequency and percentage distribution.

6. Pharmacy Management Module
	- This module combines Stock Maintenance, Pharmacist Management, and Medicine Dispensing into one integrated workflow.

a) Stock Maintenance
	- Maintain and manage the medicine stock batches using AVLTree<StockBatch>.
	- Support FEFO (First-Expired, First-Out) logic for batch dispensing.
	- Add new stock batches and update quantities.
	- Identify batches that are expired, low in stock, or matching specific criteria.

   Reports:
	- Inventory & Demand Forecast Report: Predicts demand and identifies risk levels.

b) Pharmacist Management
	- Manage pharmacist records using custom AVLTree<Pharmacist>.
	- Support pharmacist registration, update, removal, and searching by ID.

c) Medicine Dispensing
	- Dispense prescribed medicines from stock using FEFO.
	- Perform clinical checks before dispensing.
	- Manage audit logs for failed/successful dispensing.
	- Generate batch usage summaries and dispensing labels.

   Reports:
	- Dispensing Activity Summary: Shows number of prescriptions dispensed each day and displays daily totals and weekly summaries.


Project Flow
1. Pharmacy stock is initialized with medicine batches.
2. Patients register and receive a unique Patient ID.
3. Patients enqueue for consultations, recording symptoms.
4. Doctors are managed in the system and assigned duty schedules.
5. Patients book consultations based on doctor availability.
6. Doctors record treatments and prescribe medicines.
7. Pharmacy dispenses medicines according to prescriptions, updating stock levels.
8. Pharmacists and duty schedules are managed.
9. Each module generates reports for operational insights.
