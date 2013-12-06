-- Updating the start value for the existing sequences as there is default entry with id '1' is given.

PROMPT Drop Existing Sequences ........................
DROP SEQUENCE expiration_type_ID_SEQ;
DROP SEQUENCE purpose_ID_SEQ;
DROP SEQUENCE fund_definition_ID_SEQ;

PROMPT Create Sequences..............

DECLARE 
  command1 varchar(255);
  part1 varchar(100) := ' MINVALUE 1 MAXVALUE 999999999999999999999999 INCREMENT BY 1  NOCYCLE';
BEGIN
  select 'CREATE SEQUENCE expiration_type_ID_SEQ start with ' ||  (nvl(max(id), 0)+1) || part1 into command1 from expiration_type;
  execute immediate command1; 
  select 'CREATE SEQUENCE purpose_ID_SEQ start with ' ||  (nvl(max(id), 0)+1) || part1 into command1 from purpose;
  execute immediate command1;
  select 'CREATE SEQUENCE fund_definition_ID_SEQ start with ' ||  (nvl(max(id), 0)+1) || part1 into command1 from fund_definition;
  execute immediate command1;
END;

/

commit;
