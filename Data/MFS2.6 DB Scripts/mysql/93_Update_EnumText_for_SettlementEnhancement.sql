ALTER TABLE schedule_template ADD Month INT(11) NULL;

ALTER TABLE schedule_template ADD Description VARCHAR(255) NULL;

ALTER TABLE settlement_template DROP FOREIGN KEY FK_setlmnt_tmplt_schdl;

Delete from schedule_template;

ALTER TABLE schedule_template MODIFY DayOfWeek VARCHAR(255) NULL;

ALTER TABLE schedule_template DROP COLUMN TimeType;

Update settlement_template SET ScheduleTemplateID=1 where SettlementType=1;

Update settlement_template SET scheduleTemplateID=2 where SettlementType=2;

Update settlement_template SET scheduleTemplateID=3 where SettlementType=3;

Update settlement_template SET scheduleTemplateID=3 where SettlementType=4;

Update settlement_template SET scheduleTemplateID=1 where SettlementType=5;

INSERT INTO schedule_template
 (ID, Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Name, ModeType, DayOfWeek, DayOfMonth, Cron, MSPID) VALUES
  ('1','1', NOW(), 'system', NOW(), 'system', 'Daily','3','?','*','0 0 22 * * ? *', 1);

INSERT INTO schedule_template
 (ID, Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Name, ModeType, DayOfWeek, DayOfMonth, Cron, MSPID) VALUES
  ('2','1', NOW(), 'system', NOW(), 'system', 'Weekly','4','FRI ','?','0 0 22 ? * FRI *', 1);

INSERT INTO schedule_template
 (ID, Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Name, ModeType, DayOfWeek, DayOfMonth, Cron, MSPID) VALUES
  ('3','1', NOW(), 'system', NOW(), 'system', 'Monthly','5','?','31','0 0 22 31 1/1 ? *', 1);

ALTER TABLE settlement_template ADD CONSTRAINT FK_setlmnt_tmplt_schdl FOREIGN KEY (ScheduleTemplateID) REFERENCES schedule_template(`ID`);

DELETE FROM enum_text where TagID=8031;

INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',now(),'system',now(),'system','0','ModeType','8031','1','Minutes','Minutes');

INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',now(),'system',now(),'system','0','ModeType','8031','2','Hourly','Hourly');

INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',now(),'system',now(),'system','0','ModeType','8031','3','Daily','Daily');

INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',now(),'system',now(),'system','0','ModeType','8031','4','Weekly','Weekly');


INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',now(),'system',now(),'system','0','ModeType','8031','5','Monthly','Monthly');

INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',now(),'system',now(),'system','0','ModeType','8031','6','Advanced','Advanced');


ALTER TABLE settlement_template ADD CutoffTime VARCHAR(255) DEFAULT '00:00';

ALTER TABLE schedule_template DROP COLUMN CutoffTime;

DELETE FROM permission_item where Permission in (21101,21102,21103,21104,21105);

INSERT INTO permission_item (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Permission, ItemType, ItemID, FieldID, Action) VALUES ('1',now(),'system',now(),'system','21101','1','schedulerConfig','All','default');

INSERT INTO permission_item (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Permission, ItemType, ItemID, FieldID, Action) VALUES ('1',now(),'system',now(),'system','21102','1','schedulerConfig.view','All','default');

INSERT INTO permission_item (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Permission, ItemType, ItemID, FieldID, Action) VALUES ('1',now(),'system',now(),'system','21103','1','schedulerConfig.add','All','default');

INSERT INTO permission_item (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Permission, ItemType, ItemID, FieldID, Action) VALUES ('1',now(),'system',now(),'system','21104','1','schedulerConfig.edit','All','default');

INSERT INTO permission_item (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Permission, ItemType, ItemID, FieldID, Action) VALUES ('1',now(),'system',now(),'system','21105','1','schedulerConfig.delete','All','default');

DELETE FROM role_permission where Permission in (21101,21102,21103,21104,21105);

INSERT INTO role_permission (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Role, Permission) VALUES ('1',now(),'system',now(),'system','1','21101');

INSERT INTO role_permission (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Role, Permission) VALUES ('1',now(),'system',now(),'system','1','21102');

INSERT INTO role_permission (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Role, Permission) VALUES ('1',now(),'system',now(),'system','1','21103');

INSERT INTO role_permission (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Role, Permission) VALUES ('1',now(),'system',now(),'system','1','21104');

INSERT INTO role_permission (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Role, Permission) VALUES ('1',now(),'system',now(),'system','1','21105');