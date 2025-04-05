CREATE TABLE IF NOT EXISTS tado_hour_aggregate
(
    time                           TIMESTAMP(0) WITH TIME ZONE,
    inside_temperature             DECIMAL(5,2),
    outside_temperature            DECIMAL(5,2),
    humidity_percentage            DECIMAL(5,2),
    power_on_minutes               INTEGER,
    setting_temperature            DECIMAL(5,2),
    sunny_minutes                  INTEGER,
    weather_state                  VARCHAR(16),
    call_for_heat                  INTEGER,
    PRIMARY KEY (time)
);


