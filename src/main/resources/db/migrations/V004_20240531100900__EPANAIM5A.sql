CREATE TABLE IF NOT EXISTS clinic_procedures (
    procedure_id SERIAL PRIMARY KEY,
    name VARCHAR(255) UNIQUE NOT NULL,
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS clinic_appointments (
    appointment_id SERIAL PRIMARY KEY,
    appointment_doctor_id SERIAL,
    appointment_procedure_id SERIAL,
    created_at TIMESTAMP NOT NULL
);

CREATE TRIGGER update_created_at_trigger_procedures
BEFORE INSERT ON clinic_procedures
FOR EACH ROW
EXECUTE PROCEDURE update_created_at_column();

CREATE TRIGGER update_created_at_trigger_appointments
BEFORE INSERT ON clinic_appointments
FOR EACH ROW
EXECUTE PROCEDURE update_created_at_column();