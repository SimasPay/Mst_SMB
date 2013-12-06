DROP TABLE IF EXISTS `mfino`.`channel_code`;
CREATE TABLE  `mfino`.`channel_code` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `ChannelCode` varchar(255) NOT NULL,
  `ChannelName` varchar(255) NOT NULL,
  `Description` varchar(255) NOT NULL,
  `ChannelSourceApplication` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `ChannelCode_UQ` (`ChannelCode`),
  UNIQUE KEY `ChannelName_UQ` (`ChannelName`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

insert ignore into channel_code(`Version`, `LastUpdateTime`, `UpdatedBy`, `CreateTime`, `CreatedBy`, `ChannelCode`, `ChannelName`, `Description`,
`ChannelSourceApplication`) values (0, now(),'System', now(), 'System', '0', 'Phone', 'Phone', 0);
insert ignore into channel_code(`Version`, `LastUpdateTime`, `UpdatedBy`, `CreateTime`, `CreatedBy`, `ChannelCode`, `ChannelName`, `Description`,
`ChannelSourceApplication`) values (0, now(),'System', now(), 'System', '1', 'Web', 'Web', 1);
insert ignore into channel_code(`Version`, `LastUpdateTime`, `UpdatedBy`, `CreateTime`, `CreatedBy`, `ChannelCode`, `ChannelName`, `Description`,
`ChannelSourceApplication`) values (0, now(),'System', now(), 'System', '2', 'WebService', 'WebService', 2);
insert ignore into channel_code(`Version`, `LastUpdateTime`, `UpdatedBy`, `CreateTime`, `CreatedBy`, `ChannelCode`, `ChannelName`, `Description`,
`ChannelSourceApplication`) values (0, now(),'System', now(), 'System', '3', 'BackEnd', 'BackEnd', 3);
insert ignore into channel_code(`Version`, `LastUpdateTime`, `UpdatedBy`, `CreateTime`, `CreatedBy`, `ChannelCode`, `ChannelName`, `Description`,
`ChannelSourceApplication`) values (0, now(),'System', now(), 'System', '4', 'BankChannel', 'BankChannel', 4);
insert ignore into channel_code(`Version`, `LastUpdateTime`, `UpdatedBy`, `CreateTime`, `CreatedBy`, `ChannelCode`, `ChannelName`, `Description`,
`ChannelSourceApplication`) values (0, now(),'System', now(), 'System', '5', 'MobileBrowser', 'MobileBrowser', 5);
insert ignore into channel_code(`Version`, `LastUpdateTime`, `UpdatedBy`, `CreateTime`, `CreatedBy`, `ChannelCode`, `ChannelName`, `Description`,
`ChannelSourceApplication`) values (0, now(),'System', now(), 'System', '6', 'SMS', 'SMS', 6);
insert ignore into channel_code(`Version`, `LastUpdateTime`, `UpdatedBy`, `CreateTime`, `CreatedBy`, `ChannelCode`, `ChannelName`, `Description`,
`ChannelSourceApplication`) values (0, now(),'System', now(), 'System', '7', 'WebAPI', 'WebAPI', 7);
insert ignore into channel_code(`Version`, `LastUpdateTime`, `UpdatedBy`, `CreateTime`, `CreatedBy`, `ChannelCode`, `ChannelName`, `Description`,
`ChannelSourceApplication`) values (0, now(),'System', now(), 'System', '8', 'Reserved8', 'Reserved8', 8);
insert ignore into channel_code(`Version`, `LastUpdateTime`, `UpdatedBy`, `CreateTime`, `CreatedBy`, `ChannelCode`, `ChannelName`, `Description`,
`ChannelSourceApplication`) values (0, now(),'System', now(), 'System', '9', 'Reserved9', 'Reserved9', 9);
insert ignore into channel_code(`Version`, `LastUpdateTime`, `UpdatedBy`, `CreateTime`, `CreatedBy`, `ChannelCode`, `ChannelName`, `Description`,
`ChannelSourceApplication`) values (0, now(),'System', now(), 'System', '10', 'Reserved10', 'Reserved10', 10);

INSERT INTO `notification` (`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,`Language`,`Status`,`StatusTime`,`AccessCode`,`SMSNotificationCode`,`CompanyID`) VALUES
(now(),"System",now(),"System",0,1,552,"SubscriberSettingsChangeComplete",4,"Your settings change has been successful",null,0,0,now(),null,null,1),
(now(),"System",now(),"System",0,1,552,"SubscriberSettingsChangeComplete",4,"Your settings change has been successful",null,0,0,now(),null,null,2),
(now(),"System",now(),"System",0,1,552,"SubscriberSettingsChangeComplete",4,"Your settings change has been successful",null,1,0,now(),null,null,1),
(now(),"System",now(),"System",0,1,552,"SubscriberSettingsChangeComplete",4,"Your settings change has been successful",null,1,0,now(),null,null,2);

INSERT INTO `notification` (`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,`Language`,`Status`,`StatusTime`,`AccessCode`,`SMSNotificationCode`,`CompanyID`) VALUES
(now(),"System",now(),"System",0,1,553,"NoValidEmailExistingForSubscriber",4,"Subscriber doesn't have a valid emailID",null,0,0,now(),null,null,1),
(now(),"System",now(),"System",0,1,553,"NoValidEmailExistingForSubscriber",4,"Subscriber doesn't have a valid emailID",null,0,0,now(),null,null,2),
(now(),"System",now(),"System",0,1,553,"NoValidEmailExistingForSubscriber",4,"Subscriber doesn't have a valid emailID",null,1,0,now(),null,null,1),
(now(),"System",now(),"System",0,1,553,"NoValidEmailExistingForSubscriber",4,"Subscriber doesn't have a valid emailID",null,1,0,now(),null,null,2);