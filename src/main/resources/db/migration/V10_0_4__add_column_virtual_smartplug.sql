ALTER TABLE smart_plug ADD is_virtual BOOLEAN DEFAULT FALSE;
update smart_plug set is_virtual = false;

