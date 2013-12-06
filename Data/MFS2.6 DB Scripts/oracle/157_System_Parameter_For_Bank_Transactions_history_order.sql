Delete from system_parameters where ParameterName = 'bank.transactions.history.record.order.isAscending';
INSERT INTO system_parameters (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, ParameterName, ParameterValue, Description) VALUES (1,sysdate,'System',sysdate,'system','bank.transactions.history.record.order.isAscending','1','Show the transactions in same order as given by bank');

commit;