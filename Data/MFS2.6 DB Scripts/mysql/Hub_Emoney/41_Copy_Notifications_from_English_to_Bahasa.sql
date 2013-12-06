
-- Procedure for creating the notification text messages in Bahasa
-- Its a one-time activity

DELIMITER $$
DROP FUNCTION IF EXISTS `replace_ci`$$
CREATE FUNCTION `replace_ci` ( str TEXT,needle CHAR(255),str_rep CHAR(255))
    RETURNS TEXT
    DETERMINISTIC
    BEGIN
        DECLARE return_str LONGTEXT DEFAULT '';
        DECLARE lower_str LONGTEXT;
        DECLARE lower_needle LONGTEXT;
        DECLARE pos INT DEFAULT 1;
        DECLARE old_pos INT DEFAULT 1;
        SELECT lower(str) INTO lower_str;
        SELECT lower(needle) INTO lower_needle;
        SELECT locate(lower_needle, lower_str, pos) INTO pos;
        WHILE pos > 0 DO
            SELECT concat(return_str, substr(str, old_pos, pos-old_pos), str_rep) INTO return_str;
            SELECT pos + char_length(needle) INTO pos;
            SELECT pos INTO old_pos;
            SELECT locate(lower_needle, lower_str, pos) INTO pos;
        END WHILE;
        SELECT concat(return_str, substr(str, old_pos, char_length(str))) INTO return_str;
        RETURN return_str;
END$$
DELIMITER ;

DROP PROCEDURE IF EXISTS copy_notifications_to_bahasa;
delimiter //
Create Procedure copy_notifications_to_bahasa()
Begin
	Declare notificationID, MSPID, companyID BIGINT;
	Declare notificationCode,notificationMethod, lang, notificationStatus INTEGER;
	Declare notificationCodeName, accessCode, smsNotificationCode VARCHAR(255);
	Declare notificationText, STKML LONGTEXT;
	Declare no_more_records INT Default 0;
	Declare isActive TINYINT(4);
	
	-- Cursor contains the data from notification table
	Declare Notification_Cur Cursor for select
		n.ID,
		n.MSPID,
		n.Code, 
		n.CodeName, 
		n.NotificationMethod,
		n.Text,
		n.STKML,
		n.Language,
		n.Status,
		n.AccessCode,
		n.SMSNotificationCode,
		n.CompanyID,
		n.IsActive
		from
		notification n where n.language = 0;
	
	Declare Continue Handler for NOT FOUND Set no_more_records = 1;	
	Delete from notification where language = 1;
	Open Notification_Cur;
	LOOP1: loop
		Fetch Notification_Cur into notificationID, MSPID, notificationCode, notificationCodeName, notificationMethod, notificationText, STKML, lang, 
		notificationStatus, accessCode, smsNotificationCode, companyID, isactive;
		if (no_more_records = 1) then 
			close Notification_Cur;
			leave LOOP1;
		end if;
    
		SELECT replace_ci(notificationText, 'eaZyMoney', 'Uangku') into notificationText;
		SELECT replace_ci(notificationText, 'BSIM', 'Uangku') into notificationText;
		UPDATE notification SET Text=notificationText where ID = notificationID;
		INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, STKML, Language, Status, StatusTime, AccessCode, SMSNotificationCode, CompanyID, IsActive) 
		VALUES (0,now(),'System',now(),'System',MSPID,notificationCode,notificationCodeName,notificationMethod, notificationText, STKML, 1 ,notificationStatus, now(), accessCode, smsNotificationCode, companyID, isactive);

	End loop LOOP1;
End;
//
delimiter ;

-- Creating the event to call the procedure
CALL copy_notifications_to_bahasa();

DROP PROCEDURE copy_notifications_to_bahasa;