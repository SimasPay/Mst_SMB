DROP TABLE IF EXISTS mfs_ledger;
CREATE TABLE mfs_ledger (
  ID bigint(20) NOT NULL AUTO_INCREMENT,
  Version int(11) NOT NULL,
  LastUpdateTime datetime NOT NULL,
  UpdatedBy varchar(255) NOT NULL DEFAULT ' ',
  CreateTime datetime NOT NULL,
  CreatedBy varchar(255) NOT NULL,
  SctlId bigint(20) NOT NULL,
  CommodityTransferID bigint(20) NOT NULL,
  PocketID bigint(20) NOT NULL,
  Amount decimal(25,4) NOT NULL,
  LedgerType varchar(25) NOT NULL,
  LedgerStatus varchar(25) NOT NULL,
  PRIMARY KEY (ID)
 )ENGINE=InnoDB DEFAULT CHARSET=utf8;