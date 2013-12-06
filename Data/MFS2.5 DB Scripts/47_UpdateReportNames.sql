use mfino;

 INSERT INTO `offline_report` (`Version`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Name`,`ReportSql`,`ReportClass`,`TriggerEnable`) VALUES
 (1,now(),'system',now(),'system','AgentsSettlementReport',NULL,'com.mfino.report.zenithreport.AgentsSettlementReport',0);
 INSERT IGNORE INTO `offline_report_receiver` (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy,ReportID, Email)
 (select '1',NOW(),'system',NOW(),'system', id, 'emailid' from offline_report where name='AgentsSettlementReport');
 INSERT INTO `offline_report` (`Version`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Name`,`ReportSql`,`ReportClass`,`TriggerEnable`) VALUES
 (1,now(),'system',now(),'system','PartnersSettlementReport',NULL,'com.mfino.report.zenithreport.PartnersSettlementReport',0);
 INSERT IGNORE INTO `offline_report_receiver` (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy,ReportID, Email)
 (select '1',NOW(),'system',NOW(),'system', id, 'emailid' from offline_report where name='PartnersSettlementReport');
 INSERT INTO `offline_report` (`Version`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Name`,`ReportSql`,`ReportClass`,`TriggerEnable`) VALUES
 (1,now(),'system',now(),'system','AgentClassificationReport',NULL,'com.mfino.report.zenithreport.AgentClassificationReport',0);
 INSERT IGNORE INTO `offline_report_receiver` (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy,ReportID, Email)
 (select '1',NOW(),'system',NOW(),'system', id, 'emailid' from offline_report where name='AgentClassificationReport');
 INSERT INTO `offline_report` (`Version`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Name`,`ReportSql`,`ReportClass`,`TriggerEnable`) VALUES
 (1,now(),'system',now(),'system','PartnerClassificationReport',NULL,'com.mfino.report.zenithreport.PartnerClassificationReport',0);
 INSERT IGNORE INTO `offline_report_receiver` (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy,ReportID, Email)
 (select '1',NOW(),'system',NOW(),'system', id, 'emailid' from offline_report where name='PartnerClassificationReport');



UPDATE offline_report SET Name = 'eaZymoneysummaryReport' WHERE Name = 'SummaryReport';

