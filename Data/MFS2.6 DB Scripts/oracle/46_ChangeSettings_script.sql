INSERT INTO transaction_type (Version,LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,MSPID,TransactionName,DisplayName) VALUES
(1,sysdate,'system',sysdate,'system',1,'ChangeSettings','ChangeSettings');

INSERT INTO service_transaction (Version,LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,MSPID,ServiceID, TransactionTypeID) VALUES
(1,sysdate,'system',sysdate,'system',1,(select id from service where ServiceName='Account'),(select id from transaction_type where TransactionName='ChangeSettings'));

commit;