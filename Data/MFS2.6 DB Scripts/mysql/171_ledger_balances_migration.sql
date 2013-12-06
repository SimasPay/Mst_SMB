DROP PROCEDURE IF EXISTS bookdated_balance_migration;
DELIMITER //
CREATE PROCEDURE bookdated_balance_migration()
BEGIN
DECLARE initial_value INT DEFAULT 0 ;
DECLARE done INT DEFAULT 0;
DECLARE lastupdateTime DATETIME;
DECLARE createTime DATETIME ;
DECLARE sctlId BIGINT ;
DECLARE commodityTransferId BIGINT ;
DECLARE sourcePocketId BIGINT;
DECLARE destinationPocketId BIGINT;
DECLARE amount DECIMAL ;
DECLARE old_ledgers CURSOR FOR SELECT x.lastupdatetime,x.createtime,y.sctlid,x.commoditytransferid,x.sourcepocketid,x.destpocketid,x.amount FROM ledger x,chargetxn_transfer_map y
		WHERE x.commoditytransferid=y.commoditytransferid;
DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = 1;		
SET initial_value=1;		
OPEN old_ledgers;	
Loop_Ledger:LOOP
 FETCH  old_ledgers INTO lastupdateTime , createTime,sctlId,commodityTransferId,sourcePocketId,destinationPocketId,amount;
    IF done = 1 THEN
            LEAVE Loop_Ledger;
        END IF;
     INSERT INTO mfs_ledger(ID,VERSION,LASTUPDATETIME,UPDATEDBY,CREATETIME,CREATEDBY,SCTLID,COMMODITYTRANSFERID,POCKETID,AMOUNT,LEDGERTYPE,LEDGERSTATUS)
      VALUES(initial_value,0,lastupdateTime,'Migration',createTime,'Migration',sctlId,commodityTransferId,sourcePocketId,amount,'Dr.','U');
      SET initial_value=initial_value+1;
	  INSERT INTO mfs_ledger(ID,VERSION,LASTUPDATETIME,UPDATEDBY,CREATETIME,CREATEDBY,SCTLID,COMMODITYTRANSFERID,POCKETID,AMOUNT,LEDGERTYPE,LEDGERSTATUS)
      VALUES(initial_value,0,lastupdateTime,'Migration',createTime,'Migration',sctlId,commodityTransferId,destinationPocketId,amount,'Cr.','U');
       SET initial_value=initial_value+1;
   END LOOP Loop_Ledger; 
CLOSE old_ledgers;    
END;
//
DELIMITER ;
CALL bookdated_balance_migration();

DROP PROCEDURE bookdated_balance_migration;
