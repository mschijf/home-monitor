CREATE TABLE IF NOT EXISTS power
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

    PRIMARY KEY (time)
);


CREATE TABLE IF NOT EXISTS admin
(
    key         VARCHAR(64),
    value       VARCHAR(1024),

    PRIMARY KEY (key)
);


------------------------------------------------------------------------------------------

-- create or replace view power_hour_usage as
-- select power.time,
--        power.power_offpeak_kwh - prevpower.power_offpeak_kwh as power_offpeak_kwh,
--        power.power_normal_kwh - prevpower.power_normal_kwh as power_normal_kwh
-- from power inner join power as prevpower
--                       on power.time - interval '1 hour' = prevpower.time
-- where extract(minute from power.time) = 0 and extract(second from power.time) = 0;
--
--
-- create or replace view water_hour_usage as
-- SELECT min(time) as "time_begin",
--        max(water_m3) - min(water_m3) as water_m3
-- FROM water
-- group by extract(year from time), extract(month from time),extract(day from time), extract(hour from time);
--
--
--
--
-- create or replace view heath_hour_usage as
-- select heath.time,
--        heath.heath_gj - prevheath.heath_gj as delta_heath_gj
-- from heath inner join heath as prevheath
--                       on heath.time - interval '1 hour'  = prevheath.time
-- where extract(minute from heath.time) = 0 and extract(second from heath.time) = 0;
