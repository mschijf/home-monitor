ALTER TABLE shelly DROP column updated;
alter table shelly add column IF NOT EXISTS updated TIMESTAMP(0) WITH TIME ZONE;
