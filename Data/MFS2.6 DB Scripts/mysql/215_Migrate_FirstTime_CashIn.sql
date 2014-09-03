DROP PROCEDURE IF EXISTS first_time_cashin ;

DELIMITER //

CREATE PROCEDURE first_time_cashin () 
BEGIN
  DECLARE done INT DEFAULT 0;
  DECLARE sctl_create_time DATETIME ;
  DECLARE mdnid BIGINT ;
  DECLARE destmdn VARCHAR (255) ;
  DECLARE sctl_id BIGINT ;
  DECLARE transactionamt DECIMAL(25,4) ;
   DECLARE last_insert_id BIGINT ;
  
  
  DECLARE prev_first_cashin CURSOR FOR 
  SELECT 
    sctl.CreateTime, sm.ID AS MDNID, sctl.DestMDN, sctl.ID, sctl.TransactionAmount
  FROM service_charge_txn_log sctl, subscriber_mdn sm
  
  WHERE sctl.DestMDN = sm.MDN AND sctl.TransactionTypeID IN 
			     	(SELECT tt.ID FROM transaction_type tt WHERE tt.TransactionName like '%cashin%') 
			     	AND sctl.Status = 4 GROUP BY sctl.DestMDN ORDER BY sctl.ID;
  
  
  
  DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = 1;
  OPEN prev_first_cashin ;
  LOOP1 :
  LOOP
    FETCH prev_first_cashin INTO sctl_create_time,
	mdnid,
	destmdn,
    sctl_id,
	transactionamt;
    IF done = 1 THEN
            LEAVE LOOP1;
        END IF;
    INSERT INTO cashin_first_time (

      VERSION,
      LASTUPDATETIME,
      UPDATEDBY,
      CREATETIME,
      CREATEDBY,
      MDNID,
      MDN,
      SCTLID,
      TRANSACTIONAMOUNT
    ) 
    VALUES
      (

        0,
        NOW(),
        'Migration',
        sctl_create_time,
        'Migration',
        mdnid,
        destmdn,
        sctl_id,
        transactionamt
      ) ;
	  
	 SET last_insert_id = LAST_INSERT_ID();
	 
	 UPDATE subscriber_mdn smdn SET smdn.CashinFirstTimeID=last_insert_id WHERE smdn.ID = mdnid;
	  
  END LOOP LOOP1 ;
 CLOSE  prev_first_cashin;
END ;
//
DELIMITER ;
 CALL first_time_cashin();

DROP PROCEDURE first_time_cashin;
