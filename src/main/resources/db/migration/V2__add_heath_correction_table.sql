CREATE TABLE IF NOT EXISTS manual_measured_heath
(
    time        TIMESTAMP(0) WITH TIME ZONE,
    heath_gj    DECIMAL(7,3),

    PRIMARY KEY (time)
);