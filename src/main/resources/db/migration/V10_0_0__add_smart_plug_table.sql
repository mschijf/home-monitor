CREATE TABLE IF NOT EXISTS smart_plug
(
    device_id          VARCHAR(32),
    time               TIMESTAMP(0) WITH TIME ZONE,
    delta_kwh          DECIMAL(28,18),
    power_kwh          DECIMAL(28,18),

    PRIMARY KEY (device_id, time)
);
