INSERT IGNORE INTO `mfino`.`enum_text` (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',NOW(),'system',NOW(),'system','0','SourceApplicationSearch','5421','16','SMS','SMS');

INSERT IGNORE INTO `mfino`.`offline_report_company` (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, ReportID, CompanyID)  (select '1',NOW(),'system',NOW(),'system', id, 1 from offline_report where name!='OpenAPI');
INSERT IGNORE INTO `mfino`.`offline_report_company` (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, ReportID, CompanyID)  (select '1',NOW(),'system',NOW(),'system', id, 2 from offline_report where name!='OpenAPI');
INSERT IGNORE INTO `mfino`.`offline_report_company` (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, ReportID, CompanyID)  (select '1',NOW(),'system',NOW(),'system', id, null from offline_report where name ='OpenAPI');

