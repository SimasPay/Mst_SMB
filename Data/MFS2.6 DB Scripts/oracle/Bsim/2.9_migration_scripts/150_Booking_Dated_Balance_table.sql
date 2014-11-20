
CREATE TABLE booking_dated_balance (
  ID number(19,0) NOT NULL PRIMARY KEY,
  Version number(10,0) NOT NULL,
  LastUpdateTime TIMESTAMP NOT NULL,
  UpdatedBy varchar2(255) NOT NULL,
  CreateTime TIMESTAMP NOT NULL,
  CreatedBy varchar2(255) NOT NULL,
  PocketID number(19,0) NOT NULL,
  BookingDate TIMESTAMP NOT NULL,
  OpeningBalance varchar2(255) NOT NULL,
  ClosingBalance varchar2(255) NOT NULL,
  TotalCredit number(25,4) NOT NULL,
  TotalDebit number(25,4) NOT NULL,
  NetTurnOver number(25,4) NOT NULL,
  CONSTRAINT booking_bal_unique_1 UNIQUE (PocketID, BookingDate)
 );
 
CREATE SEQUENCE  booking_dated_balance_ID_SEQ  
  MINVALUE 1 MAXVALUE 999999999999999999999999 INCREMENT BY 1  NOCYCLE ; 
  
commit;