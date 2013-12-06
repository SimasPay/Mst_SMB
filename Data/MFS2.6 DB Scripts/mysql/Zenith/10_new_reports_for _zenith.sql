
delete from offline_report where Name='FloatAccountReport';

INSERT INTO `offline_report` (`Version`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Name`,`ReportSql`,`ReportClass`,`TriggerEnable`) VALUES(1,now(),'system',now(),'system','FloatAccountReport',NULL,'com.mfino.report.generalreports.FloatAccountReport',0);
