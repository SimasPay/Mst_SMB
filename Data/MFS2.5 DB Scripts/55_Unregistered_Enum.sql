USE mfino;

DELETE from enum_text where TagID='5051' and EnumCode='4';

INSERT IGNORE INTO `mfino`.`enum_text` (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES 
('1',NOW(),'system',NOW(),'system','0','PocketStatus','5051','4','OneTimeActive','OneTimeActive');