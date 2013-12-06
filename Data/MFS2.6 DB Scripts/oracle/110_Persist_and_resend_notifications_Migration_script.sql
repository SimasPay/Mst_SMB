-- Procedure for populating newly created columns of notification_log table with default values
-- Its a one-time activity

CREATE OR REPLACE Procedure insert_default_values
   
IS
	notificationLogID notification_log.ID%TYPE;
	sctl_id notification_log.SctlID%TYPE;
	srcMDN notification_log.SourceAddress%TYPE;
	CURSOR notification_log_cursor is  select ID, SctlID from notification_log where NotificationMethod is NULL;

BEGIN	
	For nl in notification_log_cursor loop
		notificationLogID := nl.ID;
		sctl_id := nl.SctlID;
		SELECT sctl.SourceMDN INTO srcMDN FROM service_charge_txn_log sctl WHERE sctl.ID = sctl_id;
		UPDATE notification_log SET NotificationReceiverType=1, NotificationMethod=1, SourceAddress=srcMDN where ID=notificationLogID;
		commit;
	End loop;

END;
/

CALL insert_default_values();

DROP PROCEDURE insert_default_values;






















commit;