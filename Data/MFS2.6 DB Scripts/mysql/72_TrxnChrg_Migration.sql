
-- Procedure for creating the TransactionCharge changes
-- Its a one-time activity

delimiter //
Drop Procedure if exists Trxn_Charge_Proc //
Create Procedure Trxn_Charge_Proc()
Begin
	Declare tcID BIGINT;
	Declare srcCommision, dstCommision, regPartnerCommision decimal(25,4);
	Declare no_more_records INT Default 0;
	Declare currentTime DATETIME Default Current_timestamp;

	-- Cursor contains the data from transaction_charge tables
	Declare SR_Cur Cursor for select
	  tc.ID,
		tc.SourceCommision, 
    tc.DestCommision, 
    tc.RegisteringPartnerCommision
		from
		transaction_charge tc;
	
	Declare Continue Handler for NOT FOUND Set no_more_records = 1;	

	Open SR_Cur;
	LOOP1: loop
		Fetch SR_Cur into tcID,srcCommision, dstCommision, regPartnerCommision;
			
		if (no_more_records = 1) then 
			close SR_Cur;
			leave LOOP1;
		end if;
    
		-- Inserting the row into share_partner table.
		IF srcCommision IS NOT NULL THEN
	 	            insert into share_partner(Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, 
                                    TransactionChargeID, PartnerID, SharePercentage, ShareType, ShareHolderType)
		                        values(0,currentTime,'System',currentTime,'System',1,
                                    tcID, NULL, srcCommision, 1, 1);
    END IF;
                                    
    IF dstCommision IS NOT NULL THEN
  	 	          insert into share_partner(Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, 
                                  TransactionChargeID, PartnerID, SharePercentage, ShareType, ShareHolderType)
	                        values(0,currentTime,'System',currentTime,'System',1,
                                  tcID, NULL, dstCommision, 1, 2);
    END IF;
                                  
    IF regPartnerCommision IS NOT NULL THEN
  	 	          insert into share_partner(Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, 
                                  TransactionChargeID, PartnerID, SharePercentage, ShareType, ShareHolderType)
	                        values(0,currentTime,'System',currentTime,'System',1,
                                  tcID, NULL, regPartnerCommision, 1, 3);
                                    
    END IF;
	
	End loop LOOP1;
End;
//
delimiter ;


UPDATE `share_partner` SET ShareType=1;
UPDATE `share_partner` SET ShareHolderType=4 where PartnerID IS NOT NULL;

-- Creating the event to call the procedure
CALL Trxn_Charge_Proc();

alter table `transaction_charge` DROP COLUMN RegisteringPartnerCommision;
alter table `transaction_charge` DROP COLUMN DestCommision;
alter table `transaction_charge` DROP COLUMN SourceCommision;

