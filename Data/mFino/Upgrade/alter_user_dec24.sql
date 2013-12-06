use mfino;
alter table `user` add 
( SecurityQuestion varchar(255) NULL, 
  SecurityAnswer varchar(255) NULL, 
  ConfirmationTime datetime NULL,
  UserActivationTime datetime NULL, 
  RejectionTime datetime NULL, 
  ExpirationTime datetime NULL,
  ConfirmationCode varchar(255) NULL,
  DateOfBirth datetime NULL
);

alter table `subscriber` add
(
  `SubscriberUserID` bigint(20) DEFAULT NULL,
  KEY `FK_Subscriber_UserBySubscriberUserID` (`SubscriberUserID`),
  CONSTRAINT `FK_Subscriber_UserBySubscriberUserID` FOREIGN KEY (`SubscriberUserID`) REFERENCES `user` (`ID`)
);
