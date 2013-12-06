
CREATE TABLE role (
  ID NUMBER(19,0) NOT NULL PRIMARY KEY,
  Version NUMBER(10,0) NOT NULL,
  LastUpdateTime TIMESTAMP NOT NULL,
  UpdatedBy VARCHAR2(255) NOT NULL,
  CreateTime TIMESTAMP NOT NULL,
  CreatedBy VARCHAR2(255) NOT NULL,
  EnumCode VARCHAR2(255 CHAR) NOT NULL,
  EnumValue VARCHAR2(255 CHAR) NOT NULL,
  DisplayText VARCHAR2(255 CHAR) DEFAULT NULL,
  IsSystemUser NUMBER(3,0) NOT NULL  
);

INSERT INTO role (ID, Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, EnumCode, EnumValue, DisplayText, IsSystemUser) 
VALUES (1,1,sysdate,'System',sysdate,'system','1','Master_Admin','Master_Admin',1);
INSERT INTO role (ID, Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, EnumCode, EnumValue, DisplayText, IsSystemUser)
VALUES (2,1,sysdate,'System',sysdate,'system','2','System_Admin','System_Admin',1);
INSERT INTO role (ID, Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, EnumCode, EnumValue, DisplayText, IsSystemUser)
VALUES (3,1,sysdate,'System',sysdate,'system','3','Gallery_Admin','Gallery_Admin',1);
INSERT INTO role (ID, Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, EnumCode, EnumValue, DisplayText, IsSystemUser)
VALUES (4,1,sysdate,'System',sysdate,'system','4','Gallery_Manager','Gallery_Manager',1);
INSERT INTO role (ID, Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, EnumCode, EnumValue, DisplayText, IsSystemUser)
VALUES (5,1,sysdate,'System',sysdate,'system','5','Merchant_Support','Merchant_Support',1);
INSERT INTO role (ID, Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, EnumCode, EnumValue, DisplayText, IsSystemUser)
VALUES (6,1,sysdate,'System',sysdate,'system','6','Bulk_Upload_Support','Bulk_Upload_Support',1);
INSERT INTO role (ID, Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, EnumCode, EnumValue, DisplayText, IsSystemUser)
VALUES (7,1,sysdate,'System',sysdate,'system','7','Sales_Admin','Sales_Admin',1);
INSERT INTO role (ID, Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, EnumCode, EnumValue, DisplayText, IsSystemUser)
VALUES (8,1,sysdate,'System',sysdate,'system','8','Finance_Treasury','Audit',1);
INSERT INTO role (ID, Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, EnumCode, EnumValue, DisplayText, IsSystemUser)
VALUES (9,1,sysdate,'System',sysdate,'system','9','Finance_Admin','Finance_Admin',1);
INSERT INTO role (ID, Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, EnumCode, EnumValue, DisplayText, IsSystemUser)
VALUES (10,1,sysdate,'System',sysdate,'system','10','Customer_Care','Customer_Care',1);
INSERT INTO role (ID, Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, EnumCode, EnumValue, DisplayText, IsSystemUser)
VALUES (11,1,sysdate,'System',sysdate,'system','11','Customer_Care_Manager','Customer_Care_Manager',1);
INSERT INTO role (ID, Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, EnumCode, EnumValue, DisplayText, IsSystemUser)
VALUES (12,1,sysdate,'System',sysdate,'system','12','Reviewer','Reviewer',1);
INSERT INTO role (ID, Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, EnumCode, EnumValue, DisplayText, IsSystemUser)
VALUES (13,1,sysdate,'System',sysdate,'system','13','Operation_Support','Operation_Support',1);
INSERT INTO role (ID, Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, EnumCode, EnumValue, DisplayText, IsSystemUser)
VALUES (14,1,sysdate,'System',sysdate,'system','14','Merchant','Merchant',0);
INSERT INTO role (ID, Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, EnumCode, EnumValue, DisplayText, IsSystemUser)
VALUES (15,1,sysdate,'System',sysdate,'system','15','Finance_Support','Finance',1);
INSERT INTO role (ID, Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, EnumCode, EnumValue, DisplayText, IsSystemUser)
VALUES (16,1,sysdate,'System',sysdate,'system','16','Omnibus_Support','Omnibus_Support',1);
INSERT INTO role (ID, Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, EnumCode, EnumValue, DisplayText, IsSystemUser)
VALUES (17,1,sysdate,'System',sysdate,'system','17','Finance_Discount','Finance_Discount',1);
INSERT INTO role (ID, Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, EnumCode, EnumValue, DisplayText, IsSystemUser)
VALUES (18,1,sysdate,'System',sysdate,'system','18','Subscriber','Subscriber',0);
INSERT INTO role (ID, Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, EnumCode, EnumValue, DisplayText, IsSystemUser)
VALUES (19,1,sysdate,'System',sysdate,'system','19','Credit_Card_Reviewer','Credit_Card_Reviewer',1);
INSERT INTO role (ID, Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, EnumCode, EnumValue, DisplayText, IsSystemUser)
VALUES (20,1,sysdate,'System',sysdate,'system','20','Bank_Customer_Care','Bank_Customer_Care',1);
INSERT INTO role (ID, Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, EnumCode, EnumValue, DisplayText, IsSystemUser)
VALUES (21,1,sysdate,'System',sysdate,'system','21','Bank_Customer_Care_Manager','Bank_Customer_Care_Manager',1);
INSERT INTO role (ID, Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, EnumCode, EnumValue, DisplayText, IsSystemUser)
VALUES (22,1,sysdate,'System',sysdate,'system','22','Service_Partner','Partner',0);
INSERT INTO role (ID, Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, EnumCode, EnumValue, DisplayText, IsSystemUser)
VALUES (23,1,sysdate,'System',sysdate,'system','23','Business_Partner','Agent',0);
INSERT INTO role (ID, Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, EnumCode, EnumValue, DisplayText, IsSystemUser)
VALUES (24,1,sysdate,'System',sysdate,'system','24','BankTeller','BankTeller',0);
INSERT INTO role (ID, Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, EnumCode, EnumValue, DisplayText, IsSystemUser)
VALUES (25,1,sysdate,'System',sysdate,'system','25','Approver','Approver',1);
INSERT INTO role (ID, Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, EnumCode, EnumValue, DisplayText, IsSystemUser)
VALUES (26,1,sysdate,'System',sysdate,'system','26','Corporate_User','Corporate User',0);

commit;