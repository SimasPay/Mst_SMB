

-- Procedure for modifying the notification text messages
-- Its a one-time activity

DROP PROCEDURE IF EXISTS suffix_nc_token_to_notification_text;
delimiter //
Create Procedure suffix_nc_token_to_notification_text()
Begin
	Declare notificationID BIGINT;
	Declare notificationCode,notificationMethod INTEGER;
	Declare notificationCodeName VARCHAR(255);
	Declare notificationText LONGTEXT;
	Declare no_more_records INT Default 0;
	
	-- Cursor contains the data from notification table
	Declare Notification_Cur Cursor for select
		n.ID,
		n.Code, 
		n.CodeName, 
		n.NotificationMethod,
		n.Text	
		from
		notification n;
	
	Declare Continue Handler for NOT FOUND Set no_more_records = 1;	

	Open Notification_Cur;
	LOOP1: loop
		Fetch Notification_Cur into notificationID, notificationCode, notificationCodeName, notificationMethod, notificationText;
		if (no_more_records = 1) then 
			close Notification_Cur;
			leave LOOP1;
		end if;
    
		-- Updating the text messages of notification table.
		SELECT REPLACE(notificationText, '$(NotificationCode)', '') into notificationText;
		SET notificationText = TRIM(notificationText);
		SELECT CONCAT(notificationText, ' $(NotificationCode)') into notificationText;
		UPDATE notification SET Text=notificationText where ID = notificationID;

	End loop LOOP1;
End;
//
delimiter ;

-- Creating the event to call the procedure
CALL suffix_nc_token_to_notification_text();

DROP PROCEDURE suffix_nc_token_to_notification_text;