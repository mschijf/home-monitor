CREATE TABLE shower_usage (
    date           DATE             NOT NULL PRIMARY KEY,
    shower_count   INTEGER          NOT NULL,
    total_liters   DOUBLE PRECISION NOT NULL,
    total_heat_gj  DOUBLE PRECISION NOT NULL
);
