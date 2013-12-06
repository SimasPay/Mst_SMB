use mfino;

 
 INSERT INTO `offline_report` (`Version`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Name`,`ReportSql`,`ReportClass`) VALUES
 (1,now(),'system',now(),'system','EndofDayProcessReport',NULL,'com.mfino.report.zenithreport.EndofDayProcessReport');
 
  INSERT IGNORE INTO `offline_report_receiver` (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy,ReportID, Email)
 (select '1',NOW(),'system',NOW(),'system', id, 'emailid' from offline_report where name='EndofDayProcessReport');
 
 
 ALTER TABLE `user` ADD COLUMN `LastPasswordChangeTime` datetime DEFAULT NULL;
 
 insert into system_parameters(Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, ParameterName, ParameterValue, Description)
     values(1, now(), 'System', now(), 'system', 'days.to.expirepassword', '30', 'force user change password after this days');
 