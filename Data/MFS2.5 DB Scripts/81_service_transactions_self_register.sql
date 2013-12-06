use mfino;

DELETE from service_transaction where ServiceID = (select id from service where ServiceName='Account') and TransactionTypeID=(select id from transaction_type where TransactionName='SubscriberRegistration');

INSERT INTO `service_transaction` (`Version`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`MSPID`,`ServiceID`, `TransactionTypeID`) VALUES
(1,now(),'system',now(),'system',1,(select id from service where ServiceName='Account'), (select id from transaction_type where TransactionName='SubscriberRegistration'));

delete from notification where code=705;

INSERT INTO `notification` (`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,`Language`,`Status`,`StatusTime`,`AccessCode`,`SMSNotificationCode`,`CompanyID`) VALUES (now(),"System",now(),"System",0,1,705,"SubscriberRegistrationfailed",1,"Dear Customer, your request for registering phonenumber $(DestMDN) could not be completed.",null,0,0,now(),null,null,1);

INSERT INTO `notification` (`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,`Language`,`Status`,`StatusTime`,`AccessCode`,`SMSNotificationCode`,`CompanyID`) VALUES (now(),"System",now(),"System",0,1,705,"SubscriberRegistrationfailed",2,"Dear Customer, your request for registering phonenumber $(DestMDN) could not be completed.",null,0,0,now(),null,null,1);

INSERT INTO `notification` (`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,`Language`,`Status`,`StatusTime`,`AccessCode`,`SMSNotificationCode`,`CompanyID`) VALUES (now(),"System",now(),"System",0,1,705,"SubscriberRegistrationfailed",4,"Dear Customer, your request for registering phonenumber $(DestMDN) could not be completed.",null,0,0,now(),null,null,1);

INSERT INTO `notification` (`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,`Language`,`Status`,`StatusTime`,`AccessCode`,`SMSNotificationCode`,`CompanyID`) VALUES (now(),"System",now(),"System",0,1,705,"SubscriberRegistrationfailed",8,"Dear Customer, your request for registering phonenumber $(DestMDN) could not be completed.",null,0,0,now(),null,null,1);

INSERT INTO `notification` (`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,`Language`,`Status`,`StatusTime`,`AccessCode`,`SMSNotificationCode`,`CompanyID`) VALUES (now(),"System",now(),"System",0,1,705,"SubscriberRegistrationfailed",16,"Dear Customer, your request for registering phonenumber $(DestMDN) could not be completed.",null,0,0,now(),null,null,1);

