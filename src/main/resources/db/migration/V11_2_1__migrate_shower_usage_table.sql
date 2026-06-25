DROP TABLE IF EXISTS shower_usage;
CREATE TABLE shower_usage (
    time             TIMESTAMP(0) WITH TIME ZONE NOT NULL PRIMARY KEY,
    duration_minutes INTEGER          NOT NULL,
    liters           DOUBLE PRECISION NOT NULL,
    heat_gj          DOUBLE PRECISION NOT NULL
);
