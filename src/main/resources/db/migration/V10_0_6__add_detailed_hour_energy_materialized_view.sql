CREATE MATERIALIZED VIEW electricity_detail AS

with all_totals as
    (
        select date_trunc('hour', time) as hour,
               (max(power_normal_kwh) - min(power_normal_kwh) +
                max(power_offpeak_kwh) - min(power_offpeak_kwh)) * 1000 as power
        from electricity
        where (time <= (select max(time) from smart_plug))
        group by hour
    )
   , known_per_hour as
    (
        select date_trunc('hour', time) as hour,
               name,
               sum(delta_wh)           as delta_wh
        from smart_plug
        where (name != 'Marantz Versterker')
        group by hour, name
    )
   , smart_totals as
    (
        select date_trunc('hour', time) as hour,
               sum(delta_wh) as power
        from smart_plug
        where (name != 'Marantz Versterker')
        group by hour
    )
   , unknown_per_hour as
    (
        select all_totals.hour,
               'unknown' as name,
               (all_totals.power - coalesce(smart_totals.power,0)) as delta_wh
        from all_totals left join smart_totals on all_totals.hour = smart_totals.hour
    )
    (select hour, name, delta_wh from known_per_hour
     union
     select hour, name, delta_wh from unknown_per_hour)
