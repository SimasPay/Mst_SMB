INSERT INTO offline_report (ID, Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Name, ReportClass, TriggerEnable, IsDaily, IsMonthly, IsOnlineReport) VALUES ('42', '1', sysdate, 'system', sysdate, 'system', 'Transaction Excel Report', 'TransactionExcelReport', '0', '1', '0', '1');

commit;