CREATE TABLE IF NOT EXISTS weather_condition
(
    id   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    text VARCHAR(64) NOT NULL UNIQUE
);

INSERT INTO weather_condition (text)
SELECT DISTINCT condition FROM weather WHERE condition IS NOT NULL;

ALTER TABLE weather ADD COLUMN condition_id BIGINT REFERENCES weather_condition(id);

UPDATE weather w
SET condition_id = wc.id
FROM weather_condition wc
WHERE w.condition = wc.text;

ALTER TABLE weather DROP COLUMN condition;
