CREATE TABLE IF NOT EXISTS tado_device_info
(
    time            TIMESTAMP(0) WITH TIME ZONE,
    battery_state   VARCHAR(16),
    PRIMARY KEY (time)
);



