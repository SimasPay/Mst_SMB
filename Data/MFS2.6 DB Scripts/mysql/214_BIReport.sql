
 INSERT INTO offline_report (Version,LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Name,ReportSql,ReportClass,TriggerEnable,IsDaily,IsMonthly,IsOnlineReport) VALUES (1,now(),'system',now(),'system','BIReport',NULL,'BIReport',0,0,1,0);
 
 
DROP TABLE IF EXISTS cashin_first_time;
CREATE TABLE cashin_first_time (
  ID bigint(20) NOT NULL AUTO_INCREMENT,
  Version int(11) NOT NULL,
  LastUpdateTime datetime NOT NULL,
  UpdatedBy varchar(255) NOT NULL DEFAULT ' ',
  CreateTime datetime NOT NULL,
  CreatedBy varchar(255) NOT NULL,
  MDNID bigint(20),
  MDN varchar(255),
  SCTLID bigint(20),
  TransactionAmount DECIMAL(25,4) DEFAULT 0,
  PRIMARY KEY (ID)
 )ENGINE=InnoDB DEFAULT CHARSET=utf8;
 
ALTER TABLE `subscriber_mdn` 
ADD COLUMN `CashinFirstTimeID` bigint(20) DEFAULT NULL;