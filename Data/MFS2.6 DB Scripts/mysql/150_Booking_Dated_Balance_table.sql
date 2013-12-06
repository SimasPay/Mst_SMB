
DROP TABLE IF EXISTS `booking_dated_balance`;
CREATE TABLE `booking_dated_balance` (
  ID bigint(20) NOT NULL AUTO_INCREMENT,
  Version int(11) NOT NULL,
  LastUpdateTime datetime NOT NULL,
  UpdatedBy varchar(255) NOT NULL DEFAULT ' ',
  CreateTime datetime NOT NULL,
  CreatedBy varchar(255) NOT NULL,
  PocketID bigint(20) NOT NULL,
  BookingDate datetime NOT NULL,
  OpeningBalance varchar(255) NOT NULL,
  ClosingBalance varchar(255) NOT NULL,
  TotalCredit decimal(25,4) NOT NULL,
  TotalDebit decimal(25,4) NOT NULL,
  NetTurnOver decimal(25,4) NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY booking_bal_unique (PocketID, BookingDate)  
 )ENGINE=InnoDB DEFAULT CHARSET=utf8;