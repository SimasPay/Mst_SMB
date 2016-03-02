alter table pocket_template add InterestRate number(25,4) default 0;

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
 
delete from system_parameters where ParameterName = 'customer.balance.fee'; 
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
 
ALTER TABLE monthly_balance add CONSTRAINT mb_pkt_mon_year_unique UNIQUE (PocketID, Month, Year);
 
ALTER TABLE agent_commission_fee add CONSTRAINT acf_partner_mon_year_unique UNIQUE (PartnerID, Month, Year);

delete from system_parameters where ParameterName = 'open.account.fee.to.agent';
INSERT INTO system_parameters (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, ParameterName, ParameterValue, Description) VALUES (1,sysdate,'System',sysdate,'system','open.account.fee.to.agent', '1000', 'Specifies the fee that gets the agent as opening an account for subscriber');

 DECLARE 
  command1 varchar(255);
  part1 varchar(100) := ' MINVALUE 1 MAXVALUE 999999999999999999999999 INCREMENT BY 1  NOCYCLE';
BEGIN
  select 'CREATE SEQUENCE monthly_balance_ID_SEQ start with ' ||  (nvl(max(id), 0)+1) || part1 into command1 from monthly_balance;
  execute immediate command1; 
  
    select 'CREATE SEQUENCE agent_commission_fee_ID_SEQ start with ' ||  (nvl(max(id), 0)+1) || part1 into command1 from agent_commission_fee;
  execute immediate command1;
  
END;

/

commit;
