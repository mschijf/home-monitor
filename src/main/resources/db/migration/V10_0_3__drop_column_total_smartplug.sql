CREATE TABLE IF NOT EXISTS smart_plug_status
(
    time                TIMESTAMP(0) WITH TIME ZONE,
    number_known        INTEGER,
    number_on_line      INTEGER,

    PRIMARY KEY (time)
);

