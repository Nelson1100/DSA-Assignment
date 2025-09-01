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
	- Doctors indexed in AVLTree by ID and key fields.
	- Duty schedules managed with DoctorDutyManagement (indexed by doctor/date/shift).
	- Undo support for doctor updates via LinkedStack<Doctor>.

   Reports:
	- Annual Attendance Report: Per-doctor yearly attendance summary.
	- Specialization Inventory Report: Count and percentage of doctors by specialization, with percentage bar visualization.
	- Attendance Ranking: Doctors ranked by annual attendance using AVLTree.

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
	- Manage medicine stock batches: add, update, delete, display.
	- Batches sorted by expiry date (AVLTree), dispensed using FEFO (First Expiry, First Out).

   Reports:
	- Stock Health Report: Displays stock level and flags low/critical items.
	- Inventory & Demand Forecast Report: Predicts demand and identifies risk levels.

b) Pharmacist Management
	- Register pharmacists and manage their contact information.
	- Schedule pharmacist duties by date and shift (AVLTree index).

   Reports:
	- Pharmacist Duty Report: Overview of duty schedules and availability.

c) Medicine Dispensing
	- Dispense medicines based on prescriptions using FEFO (First Expired, First Out) logic.
	- Supports stock simulation, shortage calculation, and instruction summary.
	- Keeps an audit log of all dispensing attempts (successful or failed).

   Reports:
	- Dispensing Activity Summary: Daily labels and dispensed records.
	- Inventory & Usage Report: Summary of prescription demand vs. stock availability.

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
