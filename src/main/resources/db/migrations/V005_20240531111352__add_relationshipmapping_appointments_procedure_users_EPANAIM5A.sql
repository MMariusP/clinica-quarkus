ALTER TABLE clinic_appointments
ADD FOREIGN KEY (appointment_procedure_id) REFERENCES clinic_procedures(procedure_id);

ALTER TABLE clinic_appointments
ADD FOREIGN KEY (appointment_doctor_id) REFERENCES clinic_users(user_id);