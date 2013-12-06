-- Create Table close_acct_setl_mdn
drop table close_acct_setl_mdn;
CREATE TABLE close_acct_setl_mdn (
  ID NUMBER(19,0) NOT NULL PRIMARY KEY,
  Version NUMBER(10,0) NOT NULL,
  LastUpdateTime TIMESTAMP NOT NULL,
  UpdatedBy VARCHAR2(255) NOT NULL,
  CreateTime TIMESTAMP NOT NULL,
  CreatedBy VARCHAR2(255) NOT NULL,
  MDNID NUMBER(19,0) NOT NULL,  
  ToBankAccount NUMBER(3,0) DEFAULT NULL,
  SettlementMDN VARCHAR2(255) DEFAULT NULL,
  SettlementAccountNumber VARCHAR2(255) DEFAULT NULL,
  ApprovalState NUMBER(10,0) DEFAULT NULL,
  ApproveOrRejectTime TIMESTAMP DEFAULT NULL,
  ApprovedOrRejectedBy VARCHAR2(255) DEFAULT NULL,
  ApproveOrRejectComment VARCHAR2(255) DEFAULT NULL,
  CONSTRAINT FK_close_acct_setl_mdn_MDNID FOREIGN KEY (MDNID) REFERENCES subscriber_mdn (ID)
  );
drop SEQUENCE close_acct_setl_mdn_ID_SEQ;
CREATE SEQUENCE  close_acct_setl_mdn_ID_SEQ MINVALUE 1 MAXVALUE 999999999999999999999999 INCREMENT BY 1  NOCYCLE ;

-- Create Table money_clearance_graved
drop table money_clearance_graved;
CREATE TABLE money_clearance_graved (
         ID NUMBER(19,0) NOT NULL PRIMARY KEY,
         Version NUMBER(10,0) NOT NULL,
		 LastUpdateTime TIMESTAMP NOT NULL,
         UpdatedBy VARCHAR2(255) NOT NULL,
		 CreateTime TIMESTAMP NOT NULL,
         CreatedBy VARCHAR2(255) NOT NULL,
		 MDNID NUMBER(19,0) NOT NULL,
		 PocketID NUMBER(19,0) NOT NULL,
		 SctlId NUMBER(19,0) NOT NULL,
		 Amount NUMBER(25,4) NOT NULL,
		 RefundMDNID NUMBER(19,0),
		 RefundAccountNumber VARCHAR2(16),
		 RefundPocketID NUMBER(19,0),
		 RefundSctlID NUMBER(19,0),
		 MCStatus NUMBER(11) NOT NULL,
		 CONSTRAINT FK_mcg_subscriber_mdn_ID Foreign Key (MDNID) references subscriber_mdn(ID),
		 CONSTRAINT FK_mcg_pocket_ID Foreign Key (PocketID) references pocket(ID),
		 CONSTRAINT FK_mcg_sctl_ID Foreign Key (SctlId) references service_charge_txn_log(ID),
		 CONSTRAINT FK_mcg_refund_mdn_ID Foreign Key (RefundMDNID) references subscriber_mdn(ID),
		 CONSTRAINT FK_mcg_refund_pocket_ID Foreign Key (RefundPocketID) references pocket(ID),
		 CONSTRAINT FK_mcg_refund_sctl_ID Foreign Key (RefundSctlID) references service_charge_txn_log(ID),
		 CONSTRAINT unique_mcg UNIQUE (MDNID,PocketID) 		  
       );
drop SEQUENCE  money_clearance_graved_ID_SEQ;
CREATE SEQUENCE  money_clearance_graved_ID_SEQ MINVALUE 1 MAXVALUE 999999999999999999999999 INCREMENT BY 1  NOCYCLE ;
	   

DELETE FROM enum_text where TagID=8019 and  EnumCode='0';	   
INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','MCStatus','8019','0','INITIALIZED','INITIALIZED');

DELETE FROM enum_text where TagID=8019 and  EnumCode='1';	   
INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','MCStatus','8019','1','REFUNDED','REFUNDED');

DELETE FROM enum_text where TagID=8019 and  EnumCode='2';	   
INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','MCStatus','8019','2','MOVED_TO_NATIONAL_TREASURY','MOVED TO NATIONAL TREASURY');


DELETE FROM system_parameters WHERE parametername='retired.subscriber.system.collector.pocket';
INSERT INTO system_parameters (version, lastupdatetime, updatedby, createtime, createdby, parametername, parametervalue, description) VALUES (1,sysdate,'System',sysdate,'system','retired.subscriber.system.collector.pocket','-1','retired subscriber balance amount will be moved to this pocket');

DELETE FROM system_parameters WHERE parametername='national.treasury.partner.code';
INSERT INTO system_parameters (version, lastupdatetime, updatedby, createtime, createdby, parametername, parametervalue, description) VALUES (1,sysdate,'System',sysdate,'system','national.treasury.partner.code','-1','Partner Code of Partner associated with National Treasury');

DELETE FROM system_parameters WHERE parametername='days.to.national.treasury.of.graved';
INSERT INTO system_parameters (version, lastupdatetime, updatedby, createtime, createdby, parametername, parametervalue, description) VALUES (1,sysdate,'System',sysdate,'system','days.to.national.treasury.of.graved','180','Days to National Treasury after graved');

DELETE FROM service_transaction where serviceid=(select id from service where servicename='Wallet') and transactiontypeid=(select id from transaction_type where transactionname='Refund');
DELETE FROM transaction_type where transactionname = 'Refund'; 
INSERT INTO transaction_type VALUES (transaction_type_id_seq.nextval,1,sysdate,'System',sysdate,'System',1,'Refund','Refund Retired Subscriber Money');
INSERT INTO service_transaction VALUES (service_transaction_id_seq.nextval,1,sysdate,'System',sysdate,'System',1, (select id from service where servicename = 'Wallet'), (select id from transaction_type where transactionname = 'Refund'), 0);


DELETE FROM service_transaction where serviceid=(select id from service where servicename='Wallet') and transactiontypeid=(select id from transaction_type where transactionname='TransferToSystem');
DELETE FROM transaction_type where transactionname = 'TransferToSystem'; 
INSERT INTO transaction_type VALUES (transaction_type_id_seq.nextval,1,sysdate,'System',sysdate,'System',1,'TransferToSystem','MoveRetiredSubscriberBalanceMoney');
INSERT INTO service_transaction VALUES (service_transaction_id_seq.nextval,1,sysdate,'System',sysdate,'System',1, (select id from service where servicename = 'Wallet'), (select id from transaction_type where transactionname = 'TransferToSystem'),0);



DELETE FROM service_transaction where serviceid=(select id from service where servicename='Wallet') and transactiontypeid=(select id from transaction_type where transactionname='TransferToTreasury');
DELETE FROM transaction_type where transactionname = 'TransferToTreasury'; 
INSERT INTO transaction_type VALUES (transaction_type_id_seq.nextval,1,sysdate,'System',sysdate,'System',1,'TransferToTreasury','MoveMoneyToNationalTreasury');
INSERT INTO service_transaction VALUES (service_transaction_id_seq.nextval,1,sysdate,'System',sysdate,'System',1, (select id from service where servicename = 'Wallet'), (select id from transaction_type where transactionname = 'TransferToTreasury'),0);

commit;