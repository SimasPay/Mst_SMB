
DELETE FROM offline_report WHERE Name='FloatAccountReport';

INSERT INTO offline_report(Version,LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Name,ReportSql,ReportClass,TriggerEnable) VALUES(1,sysdate,'system',sysdate,'system','FloatAccountReport',NULL,'com.mfino.report.generalreports.FloatAccountReport',0);

