use mfino;

INSERT IGNORE INTO `enum_text` (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',NOW(),'system',NOW(),'system','0','SubscriberStatus','5024','5','Suspend','Suspend');

INSERT IGNORE INTO `enum_text` (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',NOW(),'system',NOW(),'system','0','SubscriberStatus','5024','6','InActive','InActive');

INSERT IGNORE INTO `enum_text` (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',NOW(),'system',NOW(),'system','0','SubscriberStatus','5155','5','Suspend','Suspend');

INSERT IGNORE INTO `enum_text` (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',NOW(),'system',NOW(),'system','0','SubscriberStatus','5155','6','InActive','InActive');

update enum_text set DisplayText='Retired' where TagID='5024' and EnumCode='2';
update enum_text set DisplayText='To Grave' where TagID='5024' and EnumCode='3';
update enum_text set DisplayText='Retired' where TagID='5155' and EnumCode='2';
update enum_text set DisplayText='To Grave' where TagID='5155' and EnumCode='3';

delete from  system_parameters where ParameterName = 'days.to.suspend.of.no.activation';
INSERT IGNORE INTO `system_parameters` (`Version`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`ParameterName`,`ParameterValue`,`Description`) VALUES 
 (1,now(),'System',now(),'system','days.to.suspend.of.no.activation','2','Number of Days to Suspend of No Activation Account');

delete from  system_parameters where ParameterName = 'days.to.suspend.of.inactive';
INSERT IGNORE INTO `system_parameters` (`Version`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`ParameterName`,`ParameterValue`,`Description`) VALUES 
 (1,now(),'System',now(),'system','days.to.suspend.of.inactive','90','Number of Days to Suspend of InActive Account');
 
INSERT IGNORE INTO `system_parameters` (`Version`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`ParameterName`,`ParameterValue`,`Description`) VALUES 
 (1,now(),'System',now(),'system','days.to.retire.of.suspended','270','Number of Days to Retire of Suspended Account');
 
INSERT IGNORE INTO `system_parameters` (`Version`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`ParameterName`,`ParameterValue`,`Description`) VALUES 
 (1,now(),'System',now(),'system','days.to.grave.of.retired','365','Number of Days to Grave of Retired Account');
 
ALTER TABLE `subscriber_mdn` ADD COLUMN `IsForceCloseRequested` TINYINT DEFAULT 0 ;