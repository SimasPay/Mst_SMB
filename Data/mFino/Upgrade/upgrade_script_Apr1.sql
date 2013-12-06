use mfino;

INSERT INTO `notification` (`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,`Language`,`Status`,`StatusTime`,`AccessCode`,`SMSNotificationCode`,`CompanyID`) VALUES

(now(),"System",now(),"System",0,1,557,"EMoneyReversalRequestCompleted",1,"eMoney Reversal of $(Amount) on $(TransactionDateTime) to $(ReceiverMDN). REF: $(TransactionID) is Successful",null,0,0,"2010-09-24 11:08:28",null,null,1),

(now(),"System",now(),"System",0,1,557,"EMoneyReversalRequestCompleted",2,"eMoney Reversal of $(Amount) on $(TransactionDateTime) to $(ReceiverMDN). REF: $(TransactionID) is Successful",null,0,0,"2010-09-24 11:08:28",null,null,1),

(now(),"System",now(),"System",0,1,557,"EMoneyReversalRequestCompleted",4,"eMoney Reversal of $(Amount) on $(TransactionDateTime) to $(ReceiverMDN). REF: $(TransactionID) is Successful",null,0,0,"2010-09-24 11:08:28",null,null,1),

(now(),"System",now(),"System",0,1,557,"EMoneyReversalRequestCompleted",8,"eMoney Reversal of $(Amount) on $(TransactionDateTime) to $(ReceiverMDN). REF: $(TransactionID) is Successful",null,0,0,"2010-09-24 11:08:28",null,null,1),

(now(),"System",now(),"System",0,1,557,"EMoneyReversalRequestCompleted",16,"eMoney Reversal of $(Amount) on $(TransactionDateTime) to $(ReceiverMDN). REF: $(TransactionID) is Successful",null,0,0,now(),null,null,1),

(now(),"System",now(),"System",0,1,557,"EMoneyReversalRequestCompleted",1,"Terima kasih atas pengisian pulsa sebesar $(Amount) pada tanggal $(TransactionDateTime) ke $(ReceiverMDN). REF: $(TransactionID)",null,1,0,now(),null,null,1),

(now(),"System",now(),"System",0,1,557,"EMoneyReversalRequestCompleted",2,"Terima kasih atas pengisian pulsa sebesar $(Amount) pada tanggal $(TransactionDateTime) ke $(ReceiverMDN). REF: $(TransactionID)",null,1,0,now(),null,null,1),

(now(),"System",now(),"System",0,1,557,"EMoneyReversalRequestCompleted",4,"Terima kasih atas pengisian pulsa sebesar $(Amount) pada tanggal $(TransactionDateTime) ke $(ReceiverMDN). REF: $(TransactionID)",null,1,0,now(),null,null,1),

(now(),"System",now(),"System",0,1,557,"EMoneyReversalRequestCompleted",8,"Terima kasih atas pengisian pulsa sebesar $(Amount) pada tanggal $(TransactionDateTime) ke $(ReceiverMDN). REF: $(TransactionID)",null,1,0,"2010-09-24 11:08:30",null,null,1),

(now(),"System",now(),"System",0,1,557,"EMoneyReversalRequestCompleted",16,"Terima kasih atas pengisian pulsa sebesar $(Amount) pada tanggal $(TransactionDateTime) ke $(ReceiverMDN). REF: $(TransactionID)",null,1,0,"2010-09-24 11:08:30",null,null,1)


alter table `merchant_prefix_code` add column `VAServiceName` varchar(255) NOT NULL;




