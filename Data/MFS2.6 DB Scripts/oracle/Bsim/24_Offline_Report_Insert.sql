Delete from offline_report;

INSERT INTO offline_report (ID, Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Name, ReportClass, TriggerEnable, IsDaily, IsMonthly, IsOnlineReport) VALUES ('1', '1', sysdate, 'system', sysdate, 'system', 'Registration Report', 'RegistrationReport', '0', '1', '0', '1');

INSERT INTO offline_report (ID, Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Name, ReportClass, TriggerEnable, IsDaily, IsMonthly, IsOnlineReport) VALUES ('2', '1', sysdate, 'system', sysdate, 'system', 'Transaction Monthly Report', 'TransactionMonthlyReport', '0', '0', '1', '0');

INSERT INTO offline_report (ID, Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Name, ReportClass, TriggerEnable, IsDaily, IsMonthly, IsOnlineReport) VALUES ('3', '1', sysdate, 'system', sysdate, 'system', 'Activation Report', 'ActivationReport', '0', '1', '0', '1');

INSERT INTO offline_report (ID, Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Name, ReportClass, TriggerEnable, IsDaily, IsMonthly, IsOnlineReport) VALUES ('4', '1', sysdate, 'system', sysdate, 'system', 'Transaction Report', 'TransactionReport', '0', '1', '0', '1');

commit;

