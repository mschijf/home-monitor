DROP MATERIALIZED VIEW electricity_hour_detail;

CREATE MATERIALIZED VIEW electricity_hour_detail AS

with hour_standings as
    (
        select date_trunc('hour', time) as hour, 1000*max(power_normal_kwh + power_offpeak_kwh) as power_wh
        from electricity
        group by hour
    ),
    all_totals as
        (
            select hour, power_wh - LAG(power_wh) OVER (order by hour) as delta_wh
            from hour_standings
        )
   , known_per_hour as
    (
        select date_trunc('hour', time) as hour,
               name,
               sum(delta_wh)           as delta_wh,
               bool_or(device_id is null)       as isVirtual
        from smart_plug
        group by hour, name
    )
   , smart_totals as
    (
        select date_trunc('hour', time) as hour,
               sum(delta_wh) as delta_wh
        from smart_plug
        group by hour
    )
   , unknown_per_hour as
    (
        select all_totals.hour,
               'Unknown' as name,
               (all_totals.delta_wh - coalesce(smart_totals.delta_wh,0)) as delta_wh,
               true as isVirtual
        from all_totals left join smart_totals on all_totals.hour = smart_totals.hour
    )

    (select hour as time, name, delta_wh, isVirtual from known_per_hour
     union
     select hour as time, name, delta_wh, isVirtual from unknown_per_hour)
