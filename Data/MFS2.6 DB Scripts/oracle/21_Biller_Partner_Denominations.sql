-- Create Columns BillerType and IntegrationCode to mfsbiller_partner_map
ALTER TABLE mfsbiller_partner_map ADD BillerPartnerType number(10);
ALTER TABLE mfsbiller_partner_map ADD IntegrationCode VARCHAR2(255 char);

-- Create Table mfs_denominations
CREATE TABLE mfs_denominations (
  ID number(19,0) NOT NULL PRIMARY KEY,
  Version number(10,0) NOT NULL,
  LastUpdateTime TIMESTAMP NOT NULL,
  UpdatedBy varchar2(255) NOT NULL,
  CreateTime TIMESTAMP NOT NULL,
  CreatedBy varchar2(255) NOT NULL,
  MFSID number(19,0) NOT NULL,
  Denomination number(19,0) NOT NULL,
  Description varchar2(255) DEFAULT NULL,
  ProductCode varchar2(255) DEFAULT NULL);


INSERT INTO enum_text (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Language, TagName, TagID, EnumCode, EnumValue, DisplayText) values
(1,sysdate,'system',sysdate,'system',0,'BillerPartnerType',7040,'0','Payment_Partial','Payment_Partial');
INSERT INTO enum_text (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Language, TagName, TagID, EnumCode, EnumValue, DisplayText) values
(1,sysdate,'system',sysdate,'system',0,'BillerPartnerType',7040,'1','Payment_Full','Payment_Full');
INSERT INTO enum_text (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Language, TagName, TagID, EnumCode, EnumValue, DisplayText) values
(1,sysdate,'system',sysdate,'system',0,'BillerPartnerType',7040,'2','Topup_Free','Topup_Free');
INSERT INTO enum_text (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Language, TagName, TagID, EnumCode, EnumValue, DisplayText) values
(1,sysdate,'system',sysdate,'system',0,'BillerPartnerType',7040,'3','Topup_Denomination','Topup_Denomination');

commit;