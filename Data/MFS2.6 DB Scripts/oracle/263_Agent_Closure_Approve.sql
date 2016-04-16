

INSERT INTO permission_item (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy,Permission,ItemType,ItemID,FieldID,Action,permissiongroupid,Description) VALUES('1', sysdate, 'system', sysdate, 'system', 10246,1,'agent.approveClose','default','default',1,'Approve Agent Closing');

INSERT INTO role_permission (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Role, Permission) VALUES('1', sysdate, 'system', sysdate, 'system', '25','10246');

INSERT INTO permission_item (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy,Permission,ItemType,ItemID,FieldID,Action,permissiongroupid,Description) VALUES('1', sysdate, 'system', sysdate, 'system', 10247,1,'close.account','default','default',1,'Closing Account');

INSERT INTO role_permission (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Role, Permission) VALUES('1', sysdate, 'system', sysdate, 'system', '1','10247');


ALTER TABLE PARTNER  ADD CLOSEACCTSTATUS NUMBER;
ALTER TABLE PARTNER  ADD CLOSEACCTAPPROVEDBY VARCHAR2(255);
ALTER TABLE PARTNER  ADD CLOSEACCTTIME TIMESTAMP(0);
ALTER TABLE PARTNER  ADD CLOSEAPPROVERCOMMENTS VARCHAR2(255);

DELETE FROM notification WHERE CODE = 2169;

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2169', 'AgentPlacedClosingRequest', '1', 'Agent Cannot perform any Transactions due to Closing Request Placed', '0', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2169', 'AgentPlacedClosingRequest', '2', 'Agent Cannot perform any Transactions due to Closing Request Placed', '0', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2169', 'AgentPlacedClosingRequest', '4', 'Agent Cannot perform any Transactions due to Closing Request Placed', '0', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2169', 'AgentPlacedClosingRequest', '8', 'Agent Cannot perform any Transactions due to Closing Request Placed', '0', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2169', 'AgentPlacedClosingRequest', '16', 'Agent Cannot perform any Transactions due to Closing Request Placed', '0', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2169', 'AgentPlacedClosingRequest', '1', 'Agent Cannot perform any Transactions due to Closing Request Placed', '1', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2169', 'AgentPlacedClosingRequest', '2', 'Agent Cannot perform any Transactions due to Closing Request Placed', '1', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2169', 'AgentPlacedClosingRequest', '4', 'Agent Cannot perform any Transactions due to Closing Request Placed', '1', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2169', 'AgentPlacedClosingRequest', '8', 'Agent Cannot perform any Transactions due to Closing Request Placed', '1', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2169', 'AgentPlacedClosingRequest', '16', 'Agent Cannot perform any Transactions due to Closing 
Request Placed', '1', '0', sysdate, '1', '1');

DELETE FROM notification WHERE CODE = 2170;

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2170', 'AgentClosingRequestRejectedByApprover', '1', 'Agent Closing Request Rejected by Approver', '0', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2170', 'AgentClosingRequestRejectedByApprover', '2', 'Agent Closing Request Rejected by Approver', '0', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2170', 'AgentClosingRequestRejectedByApprover', '4', 'Agent Closing Request Rejected by Approver', '0', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2170', 'AgentClosingRequestRejectedByApprover', '8', 'Agent Closing Request Rejected by Approver', '0', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2170', 'AgentClosingRequestRejectedByApprover', '16', 'Agent Closing Request Rejected by Approver', '0', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2170', 'AgentClosingRequestRejectedByApprover', '1', 'Agent Closing Request Rejected by Approver', '1', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2170', 'AgentClosingRequestRejectedByApprover', '2', 'Agent Closing Request Rejected by Approver', '1', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2170', 'AgentClosingRequestRejectedByApprover', '4', 'Agent Closing Request Rejected by Approver', '1', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2170', 'AgentClosingRequestRejectedByApprover', '8', 'Agent Closing Request Rejected by Approver', '1', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2170', 'AgentClosingRequestRejectedByApprover', '16', 'Agent Closing Request Rejected by Approver', '1', '0', sysdate, '1', '1');

COMMIT;
