alter table clinic_appointments
    add column if not exists patient_name varchar(255),
    add column if not exists start_at timestamptz;

alter table clinic_appointments
    alter column patient_name SET NOT NULL,
    alter column start_at SET NOT NULL;

create INDEX if not exists idx_clinic_appointments_start_at
    ON clinic_appointments(start_at);

