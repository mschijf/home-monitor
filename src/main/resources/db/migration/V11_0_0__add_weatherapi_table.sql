CREATE TABLE IF NOT EXISTS weather
(
    time                TIMESTAMP(0) WITH TIME ZONE,
    outside_temperature DECIMAL(5, 2),
    humidity_percentage DECIMAL(5, 2),
    condition           VARCHAR(32),
    wind_kph            DECIMAL(5, 2),
    wind_dir            VARCHAR(4),
    pressure_mb         DECIMAL(6, 2),
    precip_mm           DECIMAL(5, 2),
    cloud               DECIMAL(5, 2),
    uv                  DECIMAL(5, 2)
);