alter table if exists clinic_appointments alter column appointment_id set data type bigint;
alter table if exists clinic_appointments alter column state set data type varchar(255);
alter table if exists clinic_appointments alter column appointment_doctor_id set data type bigint;
alter table if exists clinic_appointments alter column appointment_procedure_id set data type bigint;
alter table if exists clinic_procedures alter column procedure_id set data type bigint;
alter table if exists clinic_users alter column user_id set data type bigint;

