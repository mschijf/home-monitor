CREATE TABLE IF NOT EXISTS tado_token
(
    time          TIMESTAMP(0) WITH TIME ZONE,
    refresh_token VARCHAR(4096),
    PRIMARY KEY (time)
);

