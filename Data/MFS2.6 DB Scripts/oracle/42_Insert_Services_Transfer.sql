
DELETE FROM service_transaction where serviceid=(select id from service where servicename='AgentServices') and transactiontypeid=(select id from transaction_type where transactionname='Transfer');

INSERT INTO service_transaction (version,lastupdatetime,updatedby,createtime,createdby,mspid,serviceid, transactiontypeid) VALUES
(1,sysdate,'system',sysdate,'system',1,(select id from service where servicename='AgentServices'),(select id from transaction_type where transactionname='Transfer'));

COMMIT;