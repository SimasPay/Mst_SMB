DELETE FROM ENUM_TEXT WHERE TagID = 6581;

INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',SYSDATE,'system',SYSDATE,'system','0','RelationShipType','6581','1','SIBLING','SIBLING');

INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',SYSDATE,'system',SYSDATE,'system','0','RelationShipType','6581','2','CHILD','CHILD');

INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',SYSDATE,'system',SYSDATE,'system','0','RelationShipType','6581','3','PARENT','PARENT');

INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',SYSDATE,'system',SYSDATE,'system','0','RelationShipType','6581','4','DESCENDENT','DESCENDENT');

INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',SYSDATE,'system',SYSDATE,'system','0','RelationShipType','6581','5','ANCESTOR','ANCESTOR');

INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',SYSDATE,'system',SYSDATE,'system','0','RelationShipType','6581','6','SAME_LEVEL','SAME_LEVEL');

INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',SYSDATE,'system',SYSDATE,'system','0','RelationShipType','6581','7','LOWER_LEVEL','LOWER_LEVEL');

INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',SYSDATE,'system',SYSDATE,'system','0','RelationShipType','6581','8','UPPER_LEVEL','UPPER_LEVEL');

INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',SYSDATE,'system',SYSDATE,'system','0','RelationShipType','6581','9','BELONGS_TO_TREE','BELONGS_TO_TREE');

INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',SYSDATE,'system',SYSDATE,'system','0','RelationShipType','6581','10','SUBSCRIBER','SUBSCRIBER');

delete from notification where code IN (2014, 2015);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (SYSDATE,'System',SYSDATE,'System',0,1,2014,'DCTRestriction',1,'ERROR: This transaction is not allowed due to restrictions on distribution chain template, Info, call $(CustomerServiceShortCode).',null,0,0,SYSDATE,null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (SYSDATE,'System',SYSDATE,'System',0,1,2014,'DCTRestriction',2,'ERROR: This transaction is not allowed due to restrictions on distribution chain template, Info, call $(CustomerServiceShortCode).',null,0,0,SYSDATE,null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (SYSDATE,'System',SYSDATE,'System',0,1,2014,'DCTRestriction',4,'ERROR: This transaction is not allowed due to restrictions on distribution chain template, Info, call $(CustomerServiceShortCode).',null,0,0,SYSDATE,null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (SYSDATE,'System',SYSDATE,'System',0,1,2014,'DCTRestriction',8,'ERROR: This transaction is not allowed due to restrictions on distribution chain template, Info, call $(CustomerServiceShortCode).',null,0,0,SYSDATE,null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (SYSDATE,'System',SYSDATE,'System',0,1,2014,'DCTRestriction',16,'ERROR: This transaction is not allowed due to restrictions on distribution chain template, Info, call $(CustomerServiceShortCode).',null,0,0,SYSDATE,null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (SYSDATE,'System',SYSDATE,'System',0,1,2015,'PartnerRestriction',1,'ERROR: Transaction not allowed due to restrictions, Info, call $(CustomerServiceShortCode).',null,0,0,SYSDATE,null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (SYSDATE,'System',SYSDATE,'System',0,1,2015,'PartnerRestriction',2,'ERROR: Transaction not allowed due to restrictions, Info, call $(CustomerServiceShortCode).',null,0,0,SYSDATE,null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (SYSDATE,'System',SYSDATE,'System',0,1,2015,'PartnerRestriction',4,'ERROR: Transaction not allowed due to restrictions, Info, call $(CustomerServiceShortCode).',null,0,0,SYSDATE,null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (SYSDATE,'System',SYSDATE,'System',0,1,2015,'PartnerRestriction',8,'ERROR: Transaction not allowed due to restrictions, Info, call $(CustomerServiceShortCode).',null,0,0,SYSDATE,null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (SYSDATE,'System',SYSDATE,'System',0,1,2015,'PartnerRestriction',16,'ERROR: Transaction not allowed due to restrictions, Info, call $(CustomerServiceShortCode).',null,0,0,SYSDATE,null,null,1);

delete from role_permission where Permission in (16001, 17001);

delete from permission_item where permission in (16001, 17001);

INSERT INTO permission_item (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy,Permission,ItemType,ItemID,FieldID,Action) VALUES('1', SYSDATE, 'system', SYSDATE, 'system', 16001,1,'Hierarchies','default','default');

INSERT INTO permission_item (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy,Permission,ItemType,ItemID,FieldID,Action) VALUES('1', SYSDATE, 'system', SYSDATE, 'system', 17001,1,'DistributionHierarchy','default','default');

INSERT INTO role_permission (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Role, Permission) VALUES('1', SYSDATE, 'system', SYSDATE, 'system', '1','16001');

INSERT INTO role_permission (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Role, Permission) VALUES('1', SYSDATE, 'system', SYSDATE, 'system', '22','16001');

INSERT INTO role_permission (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Role, Permission) VALUES('1', SYSDATE, 'system', SYSDATE, 'system', '23','16001');

INSERT INTO role_permission (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Role, Permission) VALUES('1', SYSDATE, 'system', SYSDATE, 'system', '1','17001');

INSERT INTO role_permission (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Role, Permission) VALUES('1', SYSDATE, 'system', SYSDATE, 'system', '22','17001');

INSERT INTO role_permission (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Role, Permission) VALUES('1', SYSDATE, 'system', SYSDATE, 'system', '23','17001');

commit;