CREATE TABLE IF NOT EXISTS tado_device_state
(
    serial_number   VARCHAR(32)  NOT NULL PRIMARY KEY,
    zone_id         INT          NOT NULL,
    zone_name       VARCHAR(16),
    battery_state   VARCHAR(16)
);
