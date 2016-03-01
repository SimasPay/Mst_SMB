alter table pocket_template add (InterestRate number(25,4));

CREATE TABLE monthly_balance (
  ID number(19,0) NOT NULL PRIMARY KEY,
  Version number(10,0) NOT NULL,
  LastUpdateTime TIMESTAMP NOT NULL,
  UpdatedBy varchar2(255) NOT NULL,
  CreateTime TIMESTAMP NOT NULL,
  CreatedBy varchar2(255) NOT NULL,
  PocketID number(19,0) Not Null references pocket(ID),
  Month varchar2(20) Not Null, 
  Year number(10,0) not null,
  AverageMonthlyBalance number(25,4) Not null,
  InterestCalculated number(25,4) Not null,
  AgentCommissionCalculated number(25,4) Not null
 );
 
 INSERT INTO system_parameters (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, ParameterName, ParameterValue, Description) VALUES (1,sysdate,'System',sysdate,'system','customer.balance.fee', '1', 'Specifies the customer balance fee rate');
 
 CREATE TABLE agent_commission_fee (
  ID number(19,0) NOT NULL PRIMARY KEY,
  Version number(10,0) NOT NULL,
  LastUpdateTime TIMESTAMP NOT NULL,
  UpdatedBy varchar2(255) NOT NULL,
  CreateTime TIMESTAMP NOT NULL,
  CreatedBy varchar2(255) NOT NULL,
  PartnerID number(19,0) Not Null,
  Month varchar2(20) Not Null, 
  Year number(10,0) not null,
  CustomerBalanceFee number(25,4) Not null,
  OpenAccountFee number(25,4) Not null
 );
 
 commit;
