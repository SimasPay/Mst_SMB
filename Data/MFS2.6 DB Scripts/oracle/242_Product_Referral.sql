

  CREATE TABLE PRODUCT_REFERRAL
   (ID NUMBER(19,0) NOT NULL PRIMARY KEY, 
	VERSION NUMBER(10,0) NOT NULL , 
	LASTUPDATETIME TIMESTAMP DEFAULT sysdate, 
	UPDATEDBY VARCHAR2(255), 
	CREATETIME TIMESTAMP DEFAULT sysdate NOT NULL , 
	CREATEDBY VARCHAR2(255) NOT NULL , 
	AGENTMDN NUMBER(20,0) NOT NULL,
	FULLNAME VARCHAR2(40 CHAR) NOT NULL , 
	SUBSCRIBERMDN NUMBER(20,0) NOT NULL , 
	EMAIL VARCHAR2(30 CHAR)  , 
	PRODUCTDESIRED VARCHAR2(40 CHAR) NOT NULL , 
	OTHERS VARCHAR2(100 CHAR)	
   );
 
CREATE SEQUENCE  PRODUCT_REFERRAL_ID_SEQ  MINVALUE 1 MAXVALUE 999999999999999999999999 INCREMENT BY 1 START WITH 1 CACHE 20 NOORDER  NOCYCLE ;

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