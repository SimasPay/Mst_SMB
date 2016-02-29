

  CREATE TABLE PRODUCT_REFERRAL
   (ID NUMBER(19,0) NOT NULL PRIMARY KEY, 
	VERSION NUMBER(10,0) NOT NULL , 
	LASTUPDATETIME TIMESTAMP DEFAULT sysdate, 
	UPDATEDBY VARCHAR2(255), 
	CREATETIME TIMESTAMP DEFAULT sysdate NOT NULL , 
	CREATEDBY VARCHAR2(255) NOT NULL , 
	AGENTMDN VARCHAR2(255) NOT NULL,
	FULLNAME VARCHAR2(255) NOT NULL , 
	SUBSCRIBERMDN VARCHAR2(255) NOT NULL , 
	EMAIL VARCHAR2(255)  , 
	PRODUCTDESIRED VARCHAR2(255) NOT NULL , 
	OTHERS VARCHAR2(255)	
   );

 DECLARE 
  COMMAND1 VARCHAR(255);
  PART1 VARCHAR(100) := ' MINVALUE 1 MAXVALUE 999999999999999999999999 INCREMENT BY 1  NOCYCLE';
BEGIN
  SELECT 'CREATE SEQUENCE PRODUCT_REFERRAL_ID_SEQ start with ' ||  (NVL(MAX(ID), 0)+1) || PART1 INTO COMMAND1 FROM PRODUCT_REFERRAL;
   EXECUTE IMMEDIATE COMMAND1; 
END;
/

DELETE FROM notification WHERE CODE = 2133;
INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2133', 'ProductReferralSuccess', '1', 'Product Referral Success', '0', '0', sysdate, '1', '1');
INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2133', 'ProductReferralSuccess', '2', 'Product Referral Success', '0', '0', sysdate, '1', '1');
INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2133', 'ProductReferralSuccess', '4', 'Product Referral Success', '0', '0', sysdate, '1', '1');
INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2133', 'ProductReferralSuccess', '8', 'Product Referral Success', '0', '0', sysdate, '1', '1');
INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2133', 'ProductReferralSuccess', '16', 'Product Referral Success', '0', '0', sysdate, '1', '1');
INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2133', 'ProductReferralSuccess', '1', 'Product Referral Success', '1', '0', sysdate, '1', '1');
INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2133', 'ProductReferralSuccess', '2', 'Product Referral Success', '1', '0', sysdate, '1', '1');
INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2133', 'ProductReferralSuccess', '4', 'Product Referral Success', '1', '0', sysdate, '1', '1');
INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2133', 'ProductReferralSuccess', '8', 'Product Referral Success', '1', '0', sysdate, '1', '1');
INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2133', 'ProductReferralSuccess', '16', 'Product Referral Success', '1', '0', sysdate, '1', '1');
DELETE FROM notification WHERE CODE = 2134;
INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2134', 'ProductReferralFailed', '1', 'Product Referral Failed', '0', '0', sysdate, '1', '1');
INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2134', 'ProductReferralFailed', '2', 'Product Referral Failed', '0', '0', sysdate, '1', '1');
INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2134', 'ProductReferralFailed', '4', 'Product Referral Failed', '0', '0', sysdate, '1', '1');
INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2134', 'ProductReferralFailed', '8', 'Product Referral Failed', '0', '0', sysdate, '1', '1');
INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2134', 'ProductReferralFailed', '16', 'Product Referral Failed', '0', '0', sysdate, '1', '1');
INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2134', 'ProductReferralFailed', '1', 'Product Referral Failed', '1', '0', sysdate, '1', '1');
INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2134', 'ProductReferralFailed', '2', 'Product Referral Failed', '1', '0', sysdate, '1', '1');
INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2134', 'ProductReferralFailed', '4', 'Product Referral Failed', '1', '0', sysdate, '1', '1');
INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2134', 'ProductReferralFailed', '8', 'Product Referral Failed', '1', '0', sysdate, '1', '1');
INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2134', 'ProductReferralFailed', '16', 'Product Referral Failed', '1', '0', sysdate, '1', '1');
commit;