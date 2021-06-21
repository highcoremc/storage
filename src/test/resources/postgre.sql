CREATE TABLE IF NOT EXISTS "users"
(
    "id" SERIAL PRIMARY KEY NOT NULL
);

CREATE TABLE IF NOT EXISTS "user_password"
(
    "id" SERIAL PRIMARY KEY NOT NULL
);

DROP TRIGGER IF EXISTS some_table_update_date ON "user_password";

CREATE OR REPLACE FUNCTION update_modified_column() RETURNS TRIGGER AS
$$
BEGIN
    IF row (NEW.*) IS DISTINCT FROM row (OLD.*) THEN
        NEW.updated_at = now();
        RETURN NEW;
    ELSE
        RETURN OLD;
    END IF;
END;
$$ language 'plpgsql';

CREATE TRIGGER some_table_update_date
    BEFORE UPDATE
    ON "user_password"
    FOR ROW
EXECUTE PROCEDURE update_modified_column();