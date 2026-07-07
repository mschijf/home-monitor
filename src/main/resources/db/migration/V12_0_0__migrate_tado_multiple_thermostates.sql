CREATE TABLE IF NOT EXISTS tado_outside
(
    time                           TIMESTAMP(0) WITH TIME ZONE NOT NULL PRIMARY KEY,
    outside_temperature            DECIMAL(5,2),
    solar_intensity_percentage     DECIMAL(5,2)
);


INSERT INTO tado_outside (
    time,
    outside_temperature,
    solar_intensity_percentage
)
SELECT
    time,
    outside_temperature,
    solar_intensity_percentage
FROM tado
ON CONFLICT (time) DO NOTHING;

ALTER TABLE tado ADD column IF NOT EXISTS zone_id VARCHAR(3) default '1';
ALTER TABLE tado DROP column IF EXISTS outside_temperature;
ALTER TABLE tado DROP column IF EXISTS solar_intensity_percentage;
ALTER TABLE tado DROP column IF EXISTS weather_state;

