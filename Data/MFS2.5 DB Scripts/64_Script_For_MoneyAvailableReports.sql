use mfino;

INSERT INTO `offline_report` (`Version`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Name`,`ReportSql`,`ReportClass`,`TriggerEnable`) VALUES
 (1,now(),'system',now(),'system','AgentMoneyAvailableReport',NULL,'com.mfino.report.zenithreport.AgentMoneyAvailableReport',0),
 (1,now(),'system',now(),'system','PartnerMoneyAvailableReport',NULL,'com.mfino.report.zenithreport.PartnerMoneyAvailableReport',0),
 (1,now(),'system',now(),'system','SubscriberMoneyAvailableReport',NULL,'com.mfino.report.zenithreport.SubscriberMoneyAvailableReport',0);

-- replace emailid
 INSERT IGNORE INTO `offline_report_receiver` (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy,ReportID, Email)
 (select '1',NOW(),'system',NOW(),'system', id, 'emailid' from offline_report where name='AgentMoneyAvailableReport');
 INSERT IGNORE INTO `offline_report_receiver` (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy,ReportID, Email)
 (select '1',NOW(),'system',NOW(),'system', id, 'emailid' from offline_report where name='PartnerMoneyAvailableReport');
 INSERT IGNORE INTO `offline_report_receiver` (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy,ReportID, Email)
 (select '1',NOW(),'system',NOW(),'system', id, 'emailid' from offline_report where name='SubscriberMoneyAvailableReport');



