CREATE TABLE IF NOT EXISTS shelly
(
    time                           TIMESTAMP(0) WITH TIME ZONE,
    inside_temperature             DECIMAL(5,2),
    humidity_percentage            DECIMAL(5,2),
    updated                        VARCHAR(32)
);
