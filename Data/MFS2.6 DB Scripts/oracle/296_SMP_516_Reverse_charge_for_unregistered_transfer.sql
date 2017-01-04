update system_parameters set parametervalue = 'false' where parametername = 'reverse.charge.for.expired.transfer.to.unregistered';

Delete from system_parameters where ParameterName = 'transfer.to.unregistered.expiry.time';
INSERT INTO system_parameters (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, ParameterName, ParameterValue, Description) VALUES (1,sysdate,'System',sysdate,'system','transfer.to.unregistered.expiry.time','2','Transfer to UnRegistered Expiry Time (days)');

commit;
