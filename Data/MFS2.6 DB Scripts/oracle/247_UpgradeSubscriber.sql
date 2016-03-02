ALTER TABLE subscriber_mdn  ADD UPGRADEACCTSTATUS number;
ALTER TABLE subscriber_mdn  ADD UPGRADEACCTAPPROVEDBY VARCHAR2(255);
ALTER TABLE subscriber_mdn  ADD UPGRADEACCTTIME TIMESTAMP(0);
ALTER TABLE subscriber_mdn  ADD UPGRADEACCTCOMMENTS VARCHAR2(255);

INSERT INTO permission_item (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy,Permission,ItemType,ItemID,FieldID,Action,permissiongroupid,Description) VALUES('1', sysdate, 'system', sysdate, 'system', 10244,1,'sub.details.upgrade','default','default',1,'Upgrade LakuPandai Subscriber');
INSERT INTO permission_item (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy,Permission,ItemType,ItemID,FieldID,Action,permissiongroupid,Description) VALUES('1', sysdate, 'system', sysdate, 'system', 10245,1,'sub.approveUpgrade','default','default',1,'Approve LakuPandai SubscriberUpgrade');

INSERT INTO role_permission (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Role, Permission) VALUES('1', sysdate, 'system', sysdate, 'system', '1','10244');
INSERT INTO role_permission (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Role, Permission) VALUES('1', sysdate, 'system', sysdate, 'system', '25','10245');

INSERT INTO transaction_type VALUES (transaction_type_id_seq.nextval,1,sysdate,'System',sysdate,'System',1,'SubscriberUpgrade','SubscriberUpgrade');



DELETE FROM notification WHERE CODE = 2161;

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2161', 'SubscriberUpgradeRequestInitialized', '1', 'Your Subscriber Upgrade Request is Intialized.', '0', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2161', 'SubscriberUpgradeRequestInitialized', '2', 'Your Subscriber Upgrade Request is Intialized.', '0', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2161', 'SubscriberUpgradeRequestInitialized', '4', 'Your Subscriber Upgrade Request is Intialized.', '0', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2161', 'SubscriberUpgradeRequestInitialized', '8', 'Your Subscriber Upgrade Request is Intialized.', '0', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2161', 'SubscriberUpgradeRequestInitialized', '16', 'Your Subscriber Upgrade Request is Intialized.', '0', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2161', 'SubscriberUpgradeRequestInitialized', '1', 'Your Subscriber Upgrade Request is Intialized.', '1', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2161', 'SubscriberUpgradeRequestInitialized', '2', 'Your Subscriber Upgrade Request is Intialized.', '1', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2161', 'SubscriberUpgradeRequestInitialized', '4', 'Your Subscriber Upgrade Request is Intialized.', '1', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2161', 'SubscriberUpgradeRequestInitialized', '8', 'Your Subscriber Upgrade Request is Intialized.', '1', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2161', 'SubscriberUpgradeRequestInitialized', '16', 'Your Subscriber Upgrade Request is Intialized.', '1', '0', sysdate, '1', '1');

DELETE FROM notification WHERE CODE = 2162;

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2162', 'SubscriberUpgradeRequestApproved', '1', 'Your Subscriber Upgrade Request is Approved.', '0', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2162', 'SubscriberUpgradeRequestApproved', '2', 'Your Subscriber Upgrade Request is Approved.', '0', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2162', 'SubscriberUpgradeRequestApproved', '4', 'Your Subscriber Upgrade Request is Approved.', '0', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2162', 'SubscriberUpgradeRequestApproved', '8', 'Your Subscriber Upgrade Request is Approved.', '0', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2162', 'SubscriberUpgradeRequestApproved', '16', 'Your Subscriber Upgrade Request is Approved.', '0', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2162', 'SubscriberUpgradeRequestApproved', '1', 'Your Subscriber Upgrade Request is Approved.', '1', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2162', 'SubscriberUpgradeRequestApproved', '2', 'Your Subscriber Upgrade Request is Approved.', '1', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2162', 'SubscriberUpgradeRequestApproved', '4', 'Your Subscriber Upgrade Request is Approved.', '1', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2162', 'SubscriberUpgradeRequestApproved', '8', 'Your Subscriber Upgrade Request is Approved.', '1', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2162', 'SubscriberUpgradeRequestApproved', '16', 'Your Subscriber Upgrade Request is Approved.', '1', '0', sysdate, '1', '1');


DELETE FROM notification WHERE CODE = 2163;

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2163', 'SubscriberUpgradeRequestRejected', '1', 'Your Subscriber Upgrade Request is Rejected.', '0', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2163', 'SubscriberUpgradeRequestRejected', '2', 'Your Subscriber Upgrade Request is Rejected.', '0', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2163', 'SubscriberUpgradeRequestRejected', '4', 'Your Subscriber Upgrade Request is Rejected.', '0', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2163', 'SubscriberUpgradeRequestRejected', '8', 'Your Subscriber Upgrade Request is Rejected.', '0', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2163', 'SubscriberUpgradeRequestRejected', '16', 'Your Subscriber Upgrade Request is Rejected.', '0', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2163', 'SubscriberUpgradeRequestRejected', '1', 'Your Subscriber Upgrade Request is Rejected.', '1', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2163', 'SubscriberUpgradeRequestRejected', '2', 'Your Subscriber Upgrade Request is Rejected.', '1', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2163', 'SubscriberUpgradeRequestRejected', '4', 'Your Subscriber Upgrade Request is Rejected.', '1', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2163', 'SubscriberUpgradeRequestRejected', '8', 'Your Subscriber Upgrade Request is Rejected.', '1', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2163', 'SubscriberUpgradeRequestRejected', '16', 'Your Subscriber Upgrade Request is Rejected.', '1', '0', sysdate, '1', '1');

commit;