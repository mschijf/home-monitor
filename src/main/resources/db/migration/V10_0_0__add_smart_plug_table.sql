CREATE TABLE IF NOT EXISTS smart_plug
(
    name               VARCHAR(32),
    time               TIMESTAMP(0) WITH TIME ZONE,
    delta_kwh          DECIMAL(28,18),
    power_kwh          DECIMAL(28,18),

    PRIMARY KEY (name, time)
);
