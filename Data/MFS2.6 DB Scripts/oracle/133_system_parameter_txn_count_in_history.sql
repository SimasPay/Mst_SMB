Delete from system_parameters where ParameterName = 'max.txn.count.in.history';
INSERT INTO system_parameters (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, ParameterName, ParameterValue, Description) VALUES (1,sysdate,'System',sysdate,'system','max.txn.count.in.history','3','Maximum number of transactions to show in History');

commit;