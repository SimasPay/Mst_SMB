create or replace procedure bookdated_balance_migration
is

initial_value number(10):= 1;
mfs_ledger_CURRVAL number(10);
 

BEGIN
DECLARE      
   CURSOR old_ledgers
   IS
      SELECT x.lastupdatetime,x.createtime,y.sctlid,x.commoditytransferid,x.sourcepocketid,x.destpocketid,x.amount
        FROM ledger x,chargetxn_transfer_map y
		where x.commoditytransferid=y.commoditytransferid;

BEGIN
 FOR old_ledger 
   IN old_ledgers
   LOOP
     insert into mfs_ledger(ID,VERSION,LASTUPDATETIME,UPDATEDBY,CREATETIME,CREATEDBY,SCTLID,COMMODITYTRANSFERID,POCKETID,AMOUNT,LEDGERTYPE,LEDGERSTATUS)
      values(initial_value,0,old_ledger.lastupdatetime,'Migration',old_ledger.createtime,'Migration',old_ledger.sctlid,old_ledger.commoditytransferid,old_ledger.sourcepocketid,old_ledger.amount,'Dr.','U');
      initial_value:=initial_value+1;
      SELECT mfs_ledger_ID_SEQ.NEXTVAL into mfs_ledger_CURRVAL FROM DUAL;
      EXECUTE IMMEDIATE 'ALTER SEQUENCE mfs_ledger_ID_SEQ INCREMENT BY 1';
	  insert into mfs_ledger(ID,VERSION,LASTUPDATETIME,UPDATEDBY,CREATETIME,CREATEDBY,SCTLID,COMMODITYTRANSFERID,POCKETID,AMOUNT,LEDGERTYPE,LEDGERSTATUS)
      values(initial_value,0,old_ledger.lastupdatetime,'Migration',old_ledger.createtime,'Migration',old_ledger.sctlid,old_ledger.commoditytransferid,old_ledger.destpocketid,old_ledger.amount,'Cr.','U');
      initial_value:=initial_value+1;
      SELECT mfs_ledger_ID_SEQ.NEXTVAL into mfs_ledger_CURRVAL FROM DUAL;
      EXECUTE IMMEDIATE 'ALTER SEQUENCE mfs_ledger_ID_SEQ INCREMENT BY 1';
   END LOOP;  
END;

 EXCEPTION
WHEN OTHERS THEN
   raise_application_error(-20001,'An error was encountered - '||SQLCODE||' -ERROR- '||SQLERRM);  
END;

