

--
-- Table structure for table `role`
--

DROP TABLE IF EXISTS `role`;
CREATE TABLE `role` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `EnumCode` varchar(255) NOT NULL,
  `EnumValue` varchar(255) NOT NULL,
  `DisplayText` varchar(255) DEFAULT NULL,
  `IsSystemUser` tinyint(4) NOT NULL,
  PRIMARY KEY (`ID`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
  
INSERT INTO `role` (`Version`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`EnumCode`,`EnumValue`,`DisplayText`,`IsSystemUser`) 
VALUES (1,now(),"System",now(),"System",1,"Master_Admin","Master_Admin",1);
INSERT INTO `role` (`Version`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`EnumCode`,`EnumValue`,`DisplayText`,`IsSystemUser`) 
VALUES (1,now(),"System",now(),"System",2,"System_Admin","System_Admin",1);
INSERT INTO `role` (`Version`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`EnumCode`,`EnumValue`,`DisplayText`,`IsSystemUser`) 
VALUES (1,now(),"System",now(),"System",3,"Gallery_Admin","Gallery_Admin",1);
INSERT INTO `role` (`Version`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`EnumCode`,`EnumValue`,`DisplayText`,`IsSystemUser`) 
VALUES (1,now(),"System",now(),"System",4,"Gallery_Manager","Gallery_Manager",1);
INSERT INTO `role` (`Version`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`EnumCode`,`EnumValue`,`DisplayText`,`IsSystemUser`) 
VALUES (1,now(),"System",now(),"System",5,"Merchant_Support","Merchant_Support",1);
INSERT INTO `role` (`Version`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`EnumCode`,`EnumValue`,`DisplayText`,`IsSystemUser`) 
VALUES (1,now(),"System",now(),"System",6,"Bulk_Upload_Support","Bulk_Upload_Support",1);
INSERT INTO `role` (`Version`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`EnumCode`,`EnumValue`,`DisplayText`,`IsSystemUser`) 
VALUES (1,now(),"System",now(),"System",7,"Sales_Admin","Sales_Admin",1);
INSERT INTO `role` (`Version`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`EnumCode`,`EnumValue`,`DisplayText`,`IsSystemUser`) 
VALUES (1,now(),"System",now(),"System",8,"Finance_Treasury","Audit",1);
INSERT INTO `role` (`Version`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`EnumCode`,`EnumValue`,`DisplayText`,`IsSystemUser`) 
VALUES (1,now(),"System",now(),"System",9,"Finance_Admin","Finance_Admin",1);
INSERT INTO `role` (`Version`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`EnumCode`,`EnumValue`,`DisplayText`,`IsSystemUser`) 
VALUES (1,now(),"System",now(),"System",10,"Customer_Care","Customer_Care",1);
INSERT INTO `role` (`Version`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`EnumCode`,`EnumValue`,`DisplayText`,`IsSystemUser`) 
VALUES (1,now(),"System",now(),"System",11,"Customer_Care_Manager","Customer_Care_Manager",1);
INSERT INTO `role` (`Version`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`EnumCode`,`EnumValue`,`DisplayText`,`IsSystemUser`) 
VALUES (1,now(),"System",now(),"System",12,"Reviewer","Reviewer",1);
INSERT INTO `role` (`Version`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`EnumCode`,`EnumValue`,`DisplayText`,`IsSystemUser`) 
VALUES (1,now(),"System",now(),"System",13,"Operation_Support","Operation_Support",1);
INSERT INTO `role` (`Version`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`EnumCode`,`EnumValue`,`DisplayText`,`IsSystemUser`) 
VALUES (1,now(),"System",now(),"System",14,"Merchant","Merchant",0);
INSERT INTO `role` (`Version`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`EnumCode`,`EnumValue`,`DisplayText`,`IsSystemUser`) 
VALUES (1,now(),"System",now(),"System",15,"Finance_Support","Finance",1);
INSERT INTO `role` (`Version`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`EnumCode`,`EnumValue`,`DisplayText`,`IsSystemUser`) 
VALUES (1,now(),"System",now(),"System",16,"Omnibus_Support","Omnibus_Support",1);
INSERT INTO `role` (`Version`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`EnumCode`,`EnumValue`,`DisplayText`,`IsSystemUser`) 
VALUES (1,now(),"System",now(),"System",17,"Finance_Discount","Finance_Discount",1);
INSERT INTO `role` (`Version`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`EnumCode`,`EnumValue`,`DisplayText`,`IsSystemUser`) 
VALUES (1,now(),"System",now(),"System",18,"Subscriber","Subscriber",0);
INSERT INTO `role` (`Version`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`EnumCode`,`EnumValue`,`DisplayText`,`IsSystemUser`) 
VALUES (1,now(),"System",now(),"System",19,"Credit_Card_Reviewer","Credit_Card_Reviewer",1);
INSERT INTO `role` (`Version`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`EnumCode`,`EnumValue`,`DisplayText`,`IsSystemUser`) 
VALUES (1,now(),"System",now(),"System",20,"Bank_Customer_Care","Bank_Customer_Care",1);
INSERT INTO `role` (`Version`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`EnumCode`,`EnumValue`,`DisplayText`,`IsSystemUser`) 
VALUES (1,now(),"System",now(),"System",21,"Bank_Customer_Care_Manager","Bank_Customer_Care_Manager",1);
INSERT INTO `role` (`Version`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`EnumCode`,`EnumValue`,`DisplayText`,`IsSystemUser`) 
VALUES (1,now(),"System",now(),"System",22,"Service_Partner","Partner",0);
INSERT INTO `role` (`Version`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`EnumCode`,`EnumValue`,`DisplayText`,`IsSystemUser`) 
VALUES (1,now(),"System",now(),"System",23,"Business_Partner","Agent",0);
INSERT INTO `role` (`Version`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`EnumCode`,`EnumValue`,`DisplayText`,`IsSystemUser`) 
VALUES (1,now(),"System",now(),"System",24,"BankTeller","BankTeller",0);
INSERT INTO `role` (`Version`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`EnumCode`,`EnumValue`,`DisplayText`,`IsSystemUser`) 
VALUES (1,now(),"System",now(),"System",25,"Approver","Approver",1);
INSERT INTO `role` (`Version`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`EnumCode`,`EnumValue`,`DisplayText`,`IsSystemUser`) 
VALUES (1,now(),"System",now(),"System",26,"Corporate_User","Corporate User",0);
