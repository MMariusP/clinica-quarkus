CREATE OR REPLACE FUNCTION update_created_at_column()
RETURNS TRIGGER AS $$
BEGIN
   NEW.created_at = NOW();
   RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_created_at_trigger
BEFORE INSERT ON clinic_users
FOR EACH ROW
EXECUTE PROCEDURE update_created_at_column();


