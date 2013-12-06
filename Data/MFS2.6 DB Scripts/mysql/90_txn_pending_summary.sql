CREATE TABLE txn_pending_summary (
  ID bigint(20) NOT NULL AUTO_INCREMENT,
  Version INT(11) NOT NULL,
  LastUpdateTime DATETIME NOT NULL,
  UpdatedBy varchar(255) NOT NULL DEFAULT ' ',
  CreateTime DATETIME NOT NULL,
  CreatedBy varchar(255) NOT NULL,
  SCTLID int(20) NOT NULL,
  CSRAction int(11) DEFAULT NULL,
  CSRActionTime DATETIME DEFAULT NULL,
  CSRUserID bigint(20) DEFAULT NULL,
  CSRUserName varchar(255) DEFAULT NULL,
  CSRComment varchar(255) DEFAULT NULL,
   PRIMARY KEY(ID)
 )ENGINE=InnoDB DEFAULT CHARSET=latin1;