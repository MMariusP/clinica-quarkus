CREATE TABLE IF NOT EXISTS clinic_users (
    user_id SERIAL PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email varchar(50) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    last_login TIMESTAMP
)