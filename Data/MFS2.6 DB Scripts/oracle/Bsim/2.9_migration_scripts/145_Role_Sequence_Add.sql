DECLARE 
  command1 varchar(255);
  part1 varchar(100) := ' MINVALUE 1 MAXVALUE 999999999999999999999999 INCREMENT BY 1  NOCYCLE';
BEGIN
  select 'CREATE SEQUENCE role_ID_SEQ start with ' ||  (nvl(max(id), 0)+1) || part1 into command1 from role;
  execute immediate command1;  
  
END;

/

commit;