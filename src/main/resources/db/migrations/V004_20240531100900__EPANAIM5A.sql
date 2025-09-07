CREATE TABLE IF NOT EXISTS clinic_procedures (
    procedure_id SERIAL PRIMARY KEY,
    name VARCHAR(255) UNIQUE NOT NULL,
    description VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS clinic_appointments (
    appointment_id SERIAL PRIMARY KEY,
    appointment_doctor_id SERIAL,
    appointment_procedure_id SERIAL
);
