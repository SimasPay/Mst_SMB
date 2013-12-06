
DELETE FROM enum_text where TagID=8031;

DELETE FROM schedule_template where ID=3;

DELETE FROM schedule_template where ID=2;

DELETE FROM schedule_template where ID=1;

ALTER TABLE schedule_template ADD TimeType VARCHAR(255) NULL;

INSERT INTO schedule_template 
 (ID, Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Name, ModeType, TimeType, DayOfWeek, DayOfMonth, Cron, MSPID) VALUES
  ('1','1', NOW(), 'system', NOW(), 'system', 'Daily','Daily','Daily',1,'3','0 0/5 * * * ?', 1);

ALTER TABLE schedule_template DROP COLUMN Month;