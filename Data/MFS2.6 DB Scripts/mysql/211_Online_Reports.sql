
 ALTER TABLE offline_report ADD COLUMN IsOnlineReport tinyint(4) DEFAULT '1'; 
 
 UPDATE offline_report set IsDaily = '0', IsMonthly = '1', IsOnlineReport = '0' where ReportClass = 'AMLReport'
 
 UPDATE offline_report set IsDaily = '0', IsMonthly = '0', IsOnlineReport = '0' where ReportClass = 'EmoneyMovementReport-L2AdditionSummaryPerSubscriber'
 
 UPDATE offline_report set IsDaily = '0', IsMonthly = '0', IsOnlineReport = '0' where ReportClass = 'EmoneyMovementReport-L2DeductionSummaryPerSubscriber'

 