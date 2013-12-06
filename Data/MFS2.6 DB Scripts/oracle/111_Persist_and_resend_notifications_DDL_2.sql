
ALTER TABLE notification_log MODIFY NotificationMethod NOT NULL;

ALTER TABLE notification_log MODIFY SourceAddress NOT NULL;

ALTER TABLE notification_log MODIFY NotificationReceiverType NOT NULL;

commit;
