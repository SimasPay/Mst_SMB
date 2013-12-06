
DROP TABLE IF EXISTS `actor_channel_mapping`;
CREATE TABLE `actor_channel_mapping` (
  ID bigint(20) NOT NULL AUTO_INCREMENT,
  Version int(11) NOT NULL,
  LastUpdateTime datetime NOT NULL,
  UpdatedBy varchar(255) NOT NULL DEFAULT ' ',
  CreateTime datetime NOT NULL,
  CreatedBy varchar(255) NOT NULL,
  IsAllowed tinyint(4) NOT NULL,
  SubscriberType int(11) NOT NULL,
  BusinessPartnerType int(11) DEFAULT NULL,
  KYCLevel bigint(20) DEFAULT NULL,  
  ServiceID bigint(20) NOT NULL,
  TransactionTypeID bigint(20) NOT NULL,
  ChannelCodeID bigint(20) NOT NULL,
  GroupID int(10) UNSIGNED DEFAULT NULL,
  PRIMARY KEY (`ID`),
  CONSTRAINT UNIQUE_acmapping_subscriber UNIQUE (SubscriberType, KYCLevel, ServiceID, TransactionTypeID, ChannelCodeID, GroupID),
  CONSTRAINT UNIQUE_acmapping_partner UNIQUE (BusinessPartnerType, ServiceID, TransactionTypeID, ChannelCodeID, GroupID),
  CONSTRAINT FK_acmapping_serviceid FOREIGN KEY (ServiceID) REFERENCES service(ID),
  CONSTRAINT FK_acmapping_transaction_type FOREIGN KEY (TransactionTypeID) REFERENCES transaction_type(ID),
  CONSTRAINT FK_acmapping_kyc_level FOREIGN KEY (KYCLevel) REFERENCES kyc_level(ID),
  CONSTRAINT FK_acmapping_channelcode FOREIGN KEY (ChannelCodeID) REFERENCES channel_code(ID),
  CONSTRAINT FK_acmapping_groupid FOREIGN KEY (GroupID) REFERENCES groups(ID)
 )ENGINE=InnoDB DEFAULT CHARSET=utf8;