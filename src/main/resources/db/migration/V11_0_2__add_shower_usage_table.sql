CREATE TABLE shower_usage (
    start_time       TIMESTAMP        NOT NULL PRIMARY KEY,
    end_time         TIMESTAMP        NOT NULL,
    duration_minutes INTEGER          NOT NULL,
    liters           DOUBLE PRECISION NOT NULL,
    heat_gj          DOUBLE PRECISION NOT NULL
);
