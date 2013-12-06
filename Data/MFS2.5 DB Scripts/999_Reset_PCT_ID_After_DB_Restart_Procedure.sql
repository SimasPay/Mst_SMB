-- Execute this procedure after DB Restart to reset the Pending_commodity_transfer id to max value of Commodity_tranfsre or Pending_commodity_transfer
delimiter //
Drop Procedure if exists Reset_PCT_ID_After_DB_Restart //
Create Procedure Reset_PCT_ID_After_DB_Restart()
Begin
	Declare maxID, pct_max, ct_max BIGINT;

	SELECT MAX(ID) into pct_max FROM pending_commodity_transfer;
	if (pct_max > 0) then
		set pct_max = pct_max + 1;
		SELECT MAX(ID)+ 1 into ct_max FROM commodity_transfer;

		if (pct_max > ct_max) then
			set maxID = pct_max;
		else
			set maxID = ct_max;
		end if;

		set @sql = concat('ALTER TABLE pending_commodity_transfer AUTO_INCREMENT = ', maxID);

		PREPARE stmt FROM @sql;
		EXECUTE stmt;
	end if;
End;
//
delimiter ;