-- Procedure for modifying the notification text messages
-- Its a one-time activity

CREATE OR REPLACE Procedure prefix_nc_token_to_text
   
IS
	notificationID notification.ID%TYPE;
	notificationCode notification.Code%TYPE;
	notificationMethod notification.NotificationMethod%TYPE;
	notificationCodeName notification.CodeName%TYPE;
	notificationText notification.Text%TYPE;
	CURSOR notification_cursor is  select ID, Code, CodeName, NotificationMethod, Text from notification;
  
BEGIN

	For nc in notification_cursor loop
		notificationID := nc.ID;
		notificationCode := nc.Code;
		notificationMethod := nc.NotificationMethod;
		notificationCodeName := nc.CodeName;
		notificationText := concat('$(NotificationCode) ', trim(replace(nc.Text, '$(NotificationCode)', '')));
		UPDATE notification SET Text=notificationText where ID = notificationID;
		commit;
	End loop;

END;
/

CALL prefix_nc_token_to_text();

DROP PROCEDURE prefix_nc_token_to_text;






















commit;