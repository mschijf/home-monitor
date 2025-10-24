ALTER TABLE smart_plug RENAME COLUMN delta_kwh TO delta_wh;
ALTER TABLE smart_plug ADD device_id VARCHAR(32) DEFAULT 'unknown';

update smart_plug set device_id = null where is_virtual = true;
update smart_plug set device_id = 'unknown' where is_virtual = false;

update smart_plug set device_id = 'bf60ff004a1abac9896jej' where name = 'Marantz Versterker' and is_virtual = false;
update smart_plug set device_id = 'bf60ff004a1abac9896jej' where name = 'Koffiezetapparaat' and is_virtual = false;
update smart_plug set device_id = 'bf7c8e021095eb1bbf1lzp' where name = 'Liebherr Vriezer' and is_virtual = false;
update smart_plug set device_id = 'bf1452e9dbf6164045ikse' where name = 'Airfryer' and is_virtual = false;
update smart_plug set device_id = 'bf16eb692b0b46ca3fn4ia' where name = 'Waterkoker' and is_virtual = false;
update smart_plug set device_id = 'bf16eb692b0b46ca3fn4ia' where name = 'Meterkast (IoT)' and is_virtual = false;
update smart_plug set device_id = 'bf1cad733d3d0bb0c8zn4w' where name = 'TV Meubel' and is_virtual = false;
update smart_plug set device_id = 'bf6beb1e8505214fcd3vk4' where name = 'Wasmachine' and is_virtual = false;
update smart_plug set device_id = 'bf3ac579c46e54ee44cfgv' where name = 'Vaatwasser' and is_virtual = false;
update smart_plug set device_id = 'bf0ba3ec8d433fd8f2sp9y' where name = 'Koelkast' and is_virtual = false;
update smart_plug set device_id = 'bf058375a7231071e71wur' where name = 'Dreame Stofzuiger' and is_virtual = false;
update smart_plug set device_id = 'bf058375a7231071e71wur' where name = 'TV, Kickr, Harry' and is_virtual = false;

ALTER TABLE smart_plug drop column is_virtual;