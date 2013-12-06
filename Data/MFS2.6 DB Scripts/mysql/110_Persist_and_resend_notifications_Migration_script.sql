
-- Procedure for populating newly created columns of notification_log table with default values
-- Its a one-time activity

DROP PROCEDURE IF EXISTS populate_new_columns_with_default_values;
delimiter //
Create Procedure populate_new_columns_with_default_values()
Begin
	Declare notificationLogID BIGINT;
	Declare sctlId BIGINT;
	Declare srcMDN VARCHAR(255);
	Declare no_more_records INT Default 0;
	
	-- Cursor contains the data from notification_log table
	Declare Notification_Log_Cur Cursor for select
		n.ID,
		n.SctlID	
		from
		notification_log n
		where n.NotificationMethod is NULL;
	
	Declare Continue Handler for NOT FOUND Set no_more_records = 1;	

	Open Notification_Log_Cur;
	LOOP1: loop
		Fetch Notification_Log_Cur into notificationLogID, sctlId;
		if (no_more_records = 1) then 
			close Notification_Log_Cur;
			leave LOOP1;
		end if;

		SELECT sctl.SourceMDN INTO srcMDN FROM service_charge_txn_log sctl WHERE sctl.ID = sctlId;
		UPDATE notification_log SET NotificationReceiverType=1, NotificationMethod=1, SourceAddress=srcMDN where ID=notificationLogID;

	End loop LOOP1;
End;
//
delimiter ;

-- Creating the event to call the procedure
CALL populate_new_columns_with_default_values();

DROP PROCEDURE populate_new_columns_with_default_values;