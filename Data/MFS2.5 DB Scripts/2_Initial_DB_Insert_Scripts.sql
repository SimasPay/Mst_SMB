use mfino;
INSERT INTO `mfino_service_provider` (`ID`,`Name`,`Description`,`Status`,`StatusTime`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`) VALUES
 (1,'First Provider','First Provider - Entered automatically by the system',1,now(),now(),'System',now(),'System',0);

 INSERT INTO `company` (`ID`,`Version`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`CompanyCode`,`CompanyName`,`CustomerServiceNumber`,`smsc`) VALUES 
 (1,1,now(),'system',now(),'system','1001','MFINO','837',NULL);

INSERT INTO `brand` (`ID`,`Version`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`CompanyID`,`InternationalCountryCode`,`PrefixCode`,`BrandName`,`MSPID`) VALUES 
 (1,1,now(),'system',now(),'system',1,'234','1','MFINO',1),
 (2,1,now(),'system',now(),'system',1,'234','2','MFINO1',1),
 (3,1,now(),'system',now(),'system',1,'234','3','MFINO2',1),
 (4,1,now(),'system',now(),'system',1,'234','4','MFINO3',1),
 (5,1,now(),'system',now(),'system',1,'234','5','MFINO4',1),
 (6,1,now(),'system',now(),'system',1,'234','6','MFINO5',1),
 (7,1,now(),'system',now(),'system',1,'234','7','MFINO6',1),
 (8,1,now(),'system',now(),'system',1,'234','8','MFINO7',1),
 (9,1,now(),'system',now(),'system',1,'234','9','MFINO8',1);

 INSERT INTO `channel_code` (`ID`,`Version`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`ChannelCode`,`ChannelName`,`Description`,`ChannelSourceApplication`) VALUES
 (1,0,now(),'System',now(),'System','1','Phone','Phone',1),
 (2,0,now(),'System',now(),'System','2','Web','Web',2),
 (3,0,now(),'System',now(),'System','3','BackEnd','BackEnd',3),
 (4,0,now(),'System',now(),'System','4','BankChannel','BankChannel',4),
 (5,0,now(),'System',now(),'System','5','MobileBrowser','MobileBrowser',5),
 (6,0,now(),'System',now(),'System','6','SMS','SMS',6),
 (7,0,now(),'System',now(),'System','7','WebAPI','WebAPI',7),
 (8,0,now(),'System',now(),'System','8','Reserved8','Reserved8',8),
 (9,0,now(),'System',now(),'System','9','Reserved9','Reserved9',9),
 (10,0,now(),'System',now(),'System','10','Reserved10','Reserved10',10);
  
 INSERT INTO `user` (`MSPID`,`ID`,`Username`,`Password`,`FirstName`,`LastName`,`Email`,`Language`,`Timezone`,`Restrictions`,`Status`,`StatusTime`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`FailedLoginCount`,`FirstTimeLogin`,`LastLoginTime`,`AdminComment`,`Version`,`Role`,`CompanyID`,`SecurityQuestion`,`SecurityAnswer`,`ConfirmationTime`,`UserActivationTime`,`RejectionTime`,`ExpirationTime`,`ConfirmationCode`,`DateOfBirth`,`ForgotPasswordCode`,`HomePhone`,`WorkPhone`,`OldHomePhone`,`OldWorkPhone`,`OldSecurityQuestion`,`OldSecurityAnswer`,`OldFirstName`,`OldLastName`) VALUES
 (1,1,'user','aff6199ad6276b2e7f4fa477b76b8082b2695826','MFino','MFino','mfino@mfino.com',0,'UTC',0,0,now(),now(),'System',now(),'System',0,0,NULL,'',1,1,1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
 (1,2,'Systemadm','a13ac483f2b2a718ee69f13d1731256ae7b02e8b','System','Admin','mfino@mfino.com',0,'UTC',0,0,now(),now(),'System',now(),'System',0,0,NULL,'',1,2,1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
 (1,3,'Approver','c14ede6c486d32721893931d55f7ee3b6d9392da','Approver','Admin','mfino@mfino.com',0,'UTC',0,0,now(),now(),'System',now(),'System',0,0,NULL,'',1,25,1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
 
INSERT IGNORE INTO `pocket_template` (`ID`,`Version`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`MSPID`,`Type`,`BankAccountCardType`,`Description`,`Commodity`,`CardPANSuffixLength`,`Units`,`Allowance`,`MaximumStoredValue`,`MinimumStoredValue`,`MaxAmountPerTransaction`,`MinAmountPerTransaction`,`MaxAmountPerDay`,`MaxAmountPerWeek`,`MaxAmountPerMonth`,`MaxTransactionsPerDay`,`MaxTransactionsPerWeek`,`MaxTransactionsPerMonth`,`MinTimeBetweenTransactions`,`BankCode`,`OperatorCode`,`BillingType`,`LowBalanceNotificationThresholdAmount`,`LowBalanceNotificationEnabled`,`WebTimeInterval`,`WebServiceTimeInterval`,`UTKTimeInterval`,`BankChannelTimeInterval`,`Denomination`,`MaxUnits`,`PocketCode`,`TypeOfCheck`,`RegularExpression`,`IsCollectorPocket`) VALUES 
 (1,0,now(),'System',now(),'System',1,1,NULL,'Emoney-UnBanked',4,NULL,NULL,0,'10000.0000','0.0000','1000.0000','10.0000','1000.0000','10000.0000','100000.0000',10,50,100,0,9999,NULL,NULL,'0.0000',0,0,0,0,0,NULL,NULL,'1',0,'',0),
 (2,0,now(),'System',now(),'System',1,1,NULL,'Emoney-SemiBanked',4,NULL,NULL,0,'100000.0000','0.0000','3000.0000','10.0000','10000.0000','1000000.0000','10000000.0000',30,100,1000,0,9999,NULL,NULL,'0.0000',0,0,0,0,0,NULL,NULL,'2',0,'',0),
 (3,0,now(),'System',now(),'System',1,1,NULL,'Emoney-FullyBanked',4,NULL,NULL,0,'100000.0000','0.0000','10000.0000','10.0000','50000.0000','10000000.0000','1000000000.0000',50,100,1000,0,9999,NULL,NULL,'0.0000',0,0,0,0,0,NULL,NULL,'3',0,'',0),
 (4,0,now(),'System',now(),'System',1,3,10,'BankAccount-Savings',4,6,NULL,0,'1000000.0000','0.0000','10000.0000','10.0000','10000000.0000','100000000.0000','10000000000.0000',100,1000,100000,0,152,NULL,NULL,'0.0000',0,0,0,0,0,NULL,NULL,'4',0,'',0),
 (5,0,now(),'System',now(),'System',1,1,NULL,'Emoney - Non transactionable',4,NULL,NULL,0,'500000.0000','0.0000','1000.0000','0.0000','50000.0000','250000.0000','500000.0000',1000,10000,100000,0,9999,NULL,NULL,'0.0000',0,0,0,0,0,NULL,NULL,'5',0,'',1),
 (6,0,now(),'System',now(),'System',1,3,20,'BankAccount-Checking',4,6,NULL,0,'1000000.0000','0.0000','10000.0000','10.0000','10000000.0000','100000000.0000','10000000000.0000',100,1000,100000,0,152,NULL,NULL,'0.0000',0,0,0,0,0,NULL,NULL,'6',0,'',0),
 (7,0,now(),'System',now(),'System',1,1,NULL,'Emoney-Suspense_Charges_Template',4,NULL,NULL,0,'10000000.0000','0.0000','10000.0000','0.0000','10000000.0000','100000000.0000','10000000000.0000',100000,10000000,100000000,0,9999,NULL,NULL,'0.0000',0,0,0,0,0,NULL,NULL,'7',0,'',0);

INSERT INTO `kyc_level` (`ID`,`Version`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`KYCLevel`,`KYCLevelName`,`PocketTemplateID`,`KYCLevelDescription`) VALUES 
 (1,1,now(),'System',now(),'System',1,'UnBanked',1,NULL),
 (2,1,now(),'System',now(),'System',2,'Semi Banked',2,NULL),
 (3,1,now(),'System',now(),'System',3,'Fully Banked',3,NULL);
 
 insert into  `kyc_fields` (
  ID,Version,LastUpdateTime,CreateTime,UpdatedBy,CreatedBy,KYCFieldsName,KYCFieldsLevelID,KYCFieldsDescription)
  values
  (1,1,now(),now(),'System','System','firstname',1,'FirstName'),
  (2,1,now(),now(),'System','System','lastname',1,'LastName'),
(3,1,now(),now(),'System','System','mobileno',1,'PhoneNo'),
(4,1,now(),now(),'System','System','dateofbirth',1,'Date of Birth'),
(5,1,now(),now(),'System','System','city',1,'City'),
(6,1,now(),now(),'System','System','currency',1,'Currency'),
(7,1,now(),now(),'System','System','subsrefaccount',2,'Subscriber Reference Account'),
(8,1,now(),now(),'System','System','kinname',1,'Next of Kin Name'),
(9,1,now(),now(),'System','System','kinmdn',1,'Next of Kin Mobile No'),
(10,1,now(),now(),'System','System','streetaddress',2,'StreetAddress'),
(11,1,now(),now(),'System','System','idtype',2,'IDType'),
(12,1,now(),now(),'System','System','idnumber',2,'IDNumber'),
(13,1,now(),now(),'System','System','expirationtime',2,'date of expiry'),
(14,1,now(),now(),'System','System','proofofaddress',2,'Proof of Address'),
(15,1,now(),now(),'System','System','creditcheck',3,'Creditcheck');
