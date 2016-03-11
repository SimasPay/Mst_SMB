ALTER TABLE PARTNER ADD  (CLOSECOMMENTS VARCHAR2(512)) ;


INSERT INTO SYSTEM_PARAMETERS (ID, VERSION, LASTUPDATETIME, UPDATEDBY, CREATETIME, CREATEDBY, PARAMETERNAME, PARAMETERVALUE, DESCRIPTION) 
 VALUES (system_parameters_ID_SEQ.NEXTVAL,1,SYSDATE,'System',SYSDATE,'system','max.agent.account.closing.amount','100','Maximum Amount that can exists when the Agent Account is getting closed');
 
 DELETE FROM notification WHERE CODE = 2164;

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2164', 'AgentClosingInquirySuccess', '1', 'Agent Closing Inquiry Success', '0', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2164', 'AgentClosingInquirySuccess', '2', 'Agent Closing Inquiry Success', '0', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2164', 'AgentClosingInquirySuccess', '4', 'Agent Closing Inquiry Success', '0', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2164', 'AgentClosingInquirySuccess', '8', 'Agent Closing Inquiry Success', '0', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2164', 'AgentClosingInquirySuccess', '16', 'Agent Closing Inquiry Success', '0', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2164', 'AgentClosingInquirySuccess', '1', 'Agent Closing Inquiry Success', '1', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2164', 'AgentClosingInquirySuccess', '2', 'Agent Closing Inquiry Success', '1', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2164', 'AgentClosingInquirySuccess', '4', 'Agent Closing Inquiry Success', '1', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2164', 'AgentClosingInquirySuccess', '8', 'Agent Closing Inquiry Success', '1', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2164', 'AgentClosingInquirySuccess', '16', 'Agent Closing Inquiry Success', '1', '0', sysdate, '1', '1');


DELETE FROM notification WHERE CODE = 2165;

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2165', 'AgentClosingInquiryFailed', '1', 'Agent Closing Inquiry Failed', '0', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2165', 'AgentClosingInquiryFailed', '2', 'Agent Closing Inquiry Failed', '0', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2165', 'AgentClosingInquiryFailed', '4', 'Agent Closing Inquiry Failed', '0', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2165', 'AgentClosingInquiryFailed', '8', 'Agent Closing Inquiry Failed', '0', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2165', 'AgentClosingInquiryFailed', '16', 'Agent Closing Inquiry Failed', '0', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2165', 'AgentClosingInquiryFailed', '1', 'Agent Closing Inquiry Failed', '1', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2165', 'AgentClosingInquiryFailed', '2', 'Agent Closing Inquiry Failed', '1', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2165', 'AgentClosingInquiryFailed', '4', 'Agent Closing Inquiry Failed', '1', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2165', 'AgentClosingInquiryFailed', '8', 'Agent Closing Inquiry Failed', '1', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2165', 'AgentClosingInquiryFailed', '16', 'Agent Closing Inquiry Failed', '1', '0', sysdate, '1', '1');


DELETE FROM notification WHERE CODE = 2165;

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2165', 'AgentClosingSuccess', '1', 'Agent Account Closing Success', '0', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2165', 'AgentClosingSuccess', '2', 'Agent Account Closing Success', '0', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2165', 'AgentClosingSuccess', '4', 'Agent Account Closing Success', '0', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2165', 'AgentClosingSuccess', '8', 'Agent Account Closing Success', '0', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2165', 'AgentClosingSuccess', '16', 'Agent Account Closing Success', '0', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2165', 'AgentClosingSuccess', '1', 'Agent Account Closing Success', '1', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2165', 'AgentClosingSuccess', '2', 'Agent Account Closing Success', '1', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2165', 'AgentClosingSuccess', '4', 'Agent Account Closing Success', '1', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2165', 'AgentClosingSuccess', '8', 'Agent Account Closing Success', '1', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2165', 'AgentClosingSuccess', '16', 'Agent Account Closing Success', '1', '0', sysdate, '1', '1');


DELETE FROM notification WHERE CODE = 2166;

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2166', 'AgentClosingFailed', '1', 'Agent Account Closing Failed', '0', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2166', 'AgentClosingFailed', '2', 'Agent Account Closing Failed', '0', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2166', 'AgentClosingFailed', '4', 'Agent Account Closing Failed', '0', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2166', 'AgentClosingFailed', '8', 'Agent Account Closing Failed', '0', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2166', 'AgentClosingFailed', '16', 'Agent Account Closing Failed', '0', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2166', 'AgentClosingFailed', '1', 'Agent Account Closing Failed', '1', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2166', 'AgentClosingFailed', '2', 'Agent Account Closing Failed', '1', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2166', 'AgentClosingFailed', '4', 'Agent Account Closing Failed', '1', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2166', 'AgentClosingFailed', '8', 'Agent Account Closing Failed', '1', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2166', 'AgentClosingFailed', '16', 'Agent Account Closing Failed', '1', '0', sysdate, '1', '1');

COMMIT;