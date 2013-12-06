DELETE FROM system_parameters WHERE parametername='days.to.suspend.of.active.subscriber.when.no.activity';
DELETE FROM system_parameters WHERE parametername='days.to.inactivate.of.active.subscriber.when.no.activity';
INSERT INTO system_parameters (version, lastupdatetime, updatedby, createtime, createdby, parametername, parametervalue, description) VALUES (1,sysdate,'System',sysdate,'system','days.to.inactivate.of.active.subscriber.when.no.activity','90','number of days to inactivate active subscriber when no activity is done');

commit;