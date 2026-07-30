CREATE TABLE speedtest_stats
(
    time              TIMESTAMP(0) WITH TIME ZONE NOT NULL PRIMARY KEY,
    download_speed    FLOAT        NOT NULL,
    upload_speed      FLOAT        NOT NULL,
    download_jitter   FLOAT        NOT NULL,
    upload_jitter     FLOAT        NOT NULL
);
