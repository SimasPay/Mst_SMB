Delete from system_parameters where ParameterName = 'flashiz.biller.code';
INSERT INTO system_parameters (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, ParameterName, ParameterValue, Description) VALUES (1,sysdate,'System',sysdate,'system','flashiz.biller.code','QRFLASHIZ','Flashiz Biller Code in MFS System');

commit;


