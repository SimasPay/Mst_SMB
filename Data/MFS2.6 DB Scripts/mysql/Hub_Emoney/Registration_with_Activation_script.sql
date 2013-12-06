

-- Add RegistrationWithActivation Transaction name
INSERT INTO transaction_type(Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, TransactionName, DisplayName) VALUES (1,now(),'System',now(),'System',1,'RegistrationWithActivation','Registration With Activation');
INSERT INTO service_transaction(Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, ServiceId, TransactionTypeId) VALUES (1,now(),'System',now(),'System',1, (select id from service where ServiceName = 'Account'), (select id from transaction_type where TransactionName = 'RegistrationWithActivation'));

-- Insert New System Parameter
Delete from system_parameters where ParameterName = 'max.no.of.allowed.subscribers';
INSERT INTO system_parameters (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, ParameterName, ParameterValue, Description) VALUES (1,now(),'System',now(),'system','max.no.of.allowed.subscribers','14','Max no of allowed active subscribers');

-- Insert New Notification code
DELETE FROM `notification` WHERE Code=2019;

INSERT INTO `notification` (`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,`Language`,`Status`,`StatusTime`,`AccessCode`,`SMSNotificationCode`,`CompanyID`) VALUES (now(),"System",now(),"System",0,1,2019,"ActiveSubscribersReachedMaxLimit",1,"ERROR: Subscriber cannot be registered - Total no of active subscribers reached max limit.",null,0,0,now(),null,null,1);

INSERT INTO `notification` (`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,`Language`,`Status`,`StatusTime`,`AccessCode`,`SMSNotificationCode`,`CompanyID`) VALUES (now(),"System",now(),"System",0,1,2019,"ActiveSubscribersReachedMaxLimit",2,"ERROR: Subscriber cannot be registered - Total no of active subscribers reached max limit.",null,0,0,now(),null,null,1);

INSERT INTO `notification` (`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,`Language`,`Status`,`StatusTime`,`AccessCode`,`SMSNotificationCode`,`CompanyID`) VALUES (now(),"System",now(),"System",0,1,2019,"ActiveSubscribersReachedMaxLimit",4,"ERROR: Subscriber cannot be registered - Total no of active subscribers reached max limit.",null,0,0,now(),null,null,1);

INSERT INTO `notification` (`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,`Language`,`Status`,`StatusTime`,`AccessCode`,`SMSNotificationCode`,`CompanyID`) VALUES (now(),"System",now(),"System",0,1,2019,"ActiveSubscribersReachedMaxLimit",8,"ERROR: Subscriber cannot be registered - Total no of active subscribers reached max limit.",null,0,0,now(),null,null,1);

INSERT INTO `notification` (`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,`Language`,`Status`,`StatusTime`,`AccessCode`,`SMSNotificationCode`,`CompanyID`) VALUES (now(),"System",now(),"System",0,1,2019,"ActiveSubscribersReachedMaxLimit",16,"ERROR: Subscriber cannot be registered - Total no of active subscribers reached max limit.",null,0,0,now(),null,null,1);

commit;
