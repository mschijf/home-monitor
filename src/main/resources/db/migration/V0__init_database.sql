CREATE TABLE IF NOT EXISTS electricity
(
    time                TIMESTAMP(0) WITH TIME ZONE,
    power_normal_kwh    DECIMAL(14,4),
    power_offpeak_kwh   DECIMAL(14,4),

    PRIMARY KEY (time)
);

CREATE TABLE IF NOT EXISTS water
(
    time                TIMESTAMP(0) WITH TIME ZONE,
    water_m3            DECIMAL(14,4),

    PRIMARY KEY (time)
);


CREATE TABLE IF NOT EXISTS heath
(
    time               TIMESTAMP(0) WITH TIME ZONE,
    delta_gj           DECIMAL(28,18),
    heath_gj           DECIMAL(28,18),

    PRIMARY KEY (time)
);

CREATE TABLE IF NOT EXISTS tado
(
    time                           TIMESTAMP(0) WITH TIME ZONE,
    inside_temperature             DECIMAL(5,2),
    outside_temperature            DECIMAL(5,2),
    humidity_percentage            DECIMAL(5,2),
    heating_power_percentage       DECIMAL(5,2),
    setting_power_on               BOOLEAN,
    setting_temperature            DECIMAL(5,2),
    solar_intensity_percentage     DECIMAL(5,2),
    weather_state                  VARCHAR(16),
    call_for_heat                  INTEGER,
    PRIMARY KEY (time)
);


CREATE TABLE IF NOT EXISTS admin_timestamp
(
    key         VARCHAR(64),
    time        TIMESTAMP(0) WITH TIME ZONE,

    PRIMARY KEY (key)
);

CREATE TABLE IF NOT EXISTS backup_stats
(
    id         INTEGER,
    oldest     TIMESTAMP(0) WITH TIME ZONE,
    last       TIMESTAMP(0) WITH TIME ZONE,
    size       DECIMAL(20),
    free_space DECIMAL(20),

    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS eneco_stats
(
    time        TIMESTAMP(0) WITH TIME ZONE,
    success     BOOLEAN,

    PRIMARY KEY (time)
);
