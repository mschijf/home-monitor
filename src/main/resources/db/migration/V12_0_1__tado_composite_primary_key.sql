ALTER TABLE tado DROP COLUMN zone_id;
ALTER TABLE tado ADD column IF NOT EXISTS zone_id integer default 1;

ALTER TABLE tado ADD PRIMARY KEY (time, zone_id);
