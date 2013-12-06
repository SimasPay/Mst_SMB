use mfino;

INSERT IGNORE INTO `enum_text` (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',NOW(),'system',NOW(),'system','0','PartnerStatus','6017','5','Suspend','Suspend');

INSERT IGNORE INTO `enum_text` (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',NOW(),'system',NOW(),'system','0','PartnerStatus','6017','6','InActive','InActive');

update enum_text set DisplayText='Retired' where TagID='6017' and EnumCode='2';
update enum_text set DisplayText='To Grave' where TagID='6017' and EnumCode='3';

update enum_text set DisplayText='Retired' where TagID='5051' and EnumCode='2';
update enum_text set DisplayText='To Grave' where TagID='5051' and EnumCode='3';