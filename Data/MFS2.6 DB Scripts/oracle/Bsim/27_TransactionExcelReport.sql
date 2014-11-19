INSERT INTO offline_report (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Name, ReportClass, TriggerEnable, IsDaily, IsMonthly, IsOnlineReport) VALUES ('1', sysdate, 'system', sysdate, 'system', 'Transaction Excel Report', 'TransactionExcelReport', '0', '0', '0', '1');

commit;