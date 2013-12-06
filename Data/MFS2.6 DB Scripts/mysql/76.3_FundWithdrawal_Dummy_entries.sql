
DELETE FROM expiration_type where ID=1;
INSERT INTO expiration_type (ID,Version,LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,MSPID,ExpiryType,ExpiryMode,ExpiryValue,ExpiryDescription) VALUES (1,0,now(),'System',now(),'System',1,1,1,129600000,'expireFund');

DELETE FROM purpose where ID=1;
INSERT INTO purpose (ID,Version,LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,MSPID,Category,Code) VALUES (1,0,now(),'System',now(),'System',1,1,'111');

DELETE FROM fund_events where ID=1;
INSERT INTO fund_events (ID,Version,LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,MSPID,FundEventType,FundEventDescription) VALUES (1,0,now(),'System',now(),'System',1,1,'Fund reversal');

DELETE FROM fund_events where ID=2;
INSERT INTO fund_events (ID,Version,LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,MSPID,FundEventType,FundEventDescription) VALUES (2,0,now(),'System',now(),'System',1,2,'Regenerate FAC automatically on failure or for multiple withdrawal');

DELETE FROM fund_events where ID=3;
INSERT INTO fund_events (ID,Version,LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,MSPID,FundEventType,FundEventDescription) VALUES (3,0,now(),'System',now(),'System',1,3,'Regenerate FAC manually on failure or for multiple withdrawal');

DELETE FROM fund_definition where ID=1;
INSERT INTO fund_definition (ID,Version,LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,MSPID,PurposeID,FACLength,FACPrefix,ExpiryID,MaxFailAttemptsAllowed,OnFundAllocationTimeExpiry,OnFailedAttemptsExceeded,GenerationOfOTPOnFailure,IsMultipleWithdrawalAllowed) VALUES (1,0,now(),'System',now(),'System',1,1,10,'12345',1,10,1,1,2,1);




