INSERT INTO offline_report (Version,LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Name,ReportSql,ReportClass,TriggerEnable) VALUES
 (1,sysdate,'system',sysdate,'system','EODSummaryReport',NULL,'com.mfino.report.generalreports.EODSummaryReport',0);


-- replace emailid
 INSERT INTO offline_report_receiver (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy,ReportID, Email)
 (select '1',sysdate,'system',sysdate,'system', id, 'emailid' from offline_report where name='EODSummaryReport');

 commit;