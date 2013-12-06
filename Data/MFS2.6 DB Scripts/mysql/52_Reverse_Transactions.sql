
DELETE FROM system_parameters WHERE parametername='max.no.of.days.to.reverse.transaction';

INSERT INTO system_parameters (version, lastupdatetime, updatedby, createtime, createdby, parametername, parametervalue, description) VALUES (1,now(),'System',now(),'system','max.no.of.days.to.reverse.transaction','30','Max Number of Days to Reverse Transaction');
