BEGIN;

ALTER TABLE tbl_admission_schedule
    ADD COLUMN IF NOT EXISTS maintenance_start TIMESTAMP;

ALTER TABLE tbl_admission_schedule
    ADD COLUMN IF NOT EXISTS maintenance_end TIMESTAMP;

COMMIT;
