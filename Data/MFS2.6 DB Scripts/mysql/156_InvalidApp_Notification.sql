DELETE FROM `notification` WHERE Code=2076;

INSERT INTO `notification` (`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,`Language`,`Status`,`StatusTime`,`AccessCode`,`SMSNotificationCode`,`CompanyID`) VALUES (now(),"System",now(),"System",0,1,2076,"InvalidAppVersion",1,"Please update to new version as current app is no longer valid.",null,0,0,now(),null,null,1);

INSERT INTO `notification` (`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,`Language`,`Status`,`StatusTime`,`AccessCode`,`SMSNotificationCode`,`CompanyID`) VALUES (now(),"System",now(),"System",0,1,2076,"InvalidAppVersion",2,"Please update to new version as current app is no longer valid.",null,0,0,now(),null,null,1);

INSERT INTO `notification` (`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,`Language`,`Status`,`StatusTime`,`AccessCode`,`SMSNotificationCode`,`CompanyID`) VALUES (now(),"System",now(),"System",0,1,2076,"InvalidAppVersion",4,"Please update to new version as current app is no longer valid.",null,0,0,now(),null,null,1);

INSERT INTO `notification` (`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,`Language`,`Status`,`StatusTime`,`AccessCode`,`SMSNotificationCode`,`CompanyID`) VALUES (now(),"System",now(),"System",0,1,2076,"InvalidAppVersion",8,"Please update to new version as current app is no longer valid.",null,0,0,now(),null,null,1);

INSERT INTO `notification` (`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,`Language`,`Status`,`StatusTime`,`AccessCode`,`SMSNotificationCode`,`CompanyID`) VALUES (now(),"System",now(),"System",0,1,2076,"InvalidAppVersion",16,"Please update to new version as current app is no longer valid.",null,0,0,now(),null,null,1);

insert into system_parameters (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, ParameterName, ParameterValue, Description)
VALUES 
(1,now(),'System',now(),'system','android.subapp.minvalidversion','1','android subapp minimum valid version number'),
(1,now(),'System',now(),'system','javame.subapp.minvalidversion','1','javame subapp minimum valid version number'),
(1,now(),'System',now(),'system','ios.subapp.minvalidversion','1','ios subapp minimum valid version number'),
(1,now(),'System',now(),'system','bb.subapp.minvalidversion','1','bb subapp minimum valid version number'),
(1,now(),'System',now(),'system','android.agentapp.minvalidversion','1','android agentapp minimum valid version number'),
(1,now(),'System',now(),'system','javame.agentapp.minvalidversion','1','javame agentapp minimum valid version number'),
(1,now(),'System',now(),'system','ios.agentapp.minvalidversion','1','ios agentapp minimum valid version number'),
(1,now(),'System',now(),'system','bb.agentapp.minvalidversion','1','bb agentapp minimum valid version number');