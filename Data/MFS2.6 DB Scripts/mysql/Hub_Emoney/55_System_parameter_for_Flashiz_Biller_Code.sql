
DELETE FROM `system_parameters` WHERE ParameterName='flashiz.biller.code';

INSERT INTO `system_parameters` (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, ParameterName, ParameterValue, Description) VALUES (1,now(),'System',now(),'system','flashiz.biller.code','QRFLASHIZ','Flashiz Biller Code in MFS System');
