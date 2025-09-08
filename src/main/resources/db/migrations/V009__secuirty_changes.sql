CREATE TABLE IF NOT EXISTS clinic_roles
(
    role_id SERIAL PRIMARY KEY,
    name    VARCHAR(64) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS clinic_user_roles
(
    user_id INT NOT NULL REFERENCES clinic_users (user_id) ON DELETE CASCADE,
    role_id INT NOT NULL REFERENCES clinic_roles (role_id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

CREATE INDEX IF NOT EXISTS ix_clinic_users_username ON clinic_users (username);

INSERT INTO clinic_roles(name) VALUES ('admin'), ('doctor') ON CONFLICT DO NOTHING;

INSERT INTO clinic_users(username, password, email)
VALUES ('alice', 'alice123', 'alice@example.com'),
       ('bob',   'bob123',   'bob@example.com')
ON CONFLICT DO NOTHING;

-- Assign roles
INSERT INTO clinic_user_roles(user_id, role_id)
SELECT u.user_id, r.role_id
FROM clinic_users u, clinic_roles r
WHERE u.username = 'alice' AND r.name = 'admin';

INSERT INTO clinic_user_roles(user_id, role_id)
SELECT u.user_id, r.role_id
FROM clinic_users u, clinic_roles r
WHERE u.username = 'bob' AND r.name = 'doctor';