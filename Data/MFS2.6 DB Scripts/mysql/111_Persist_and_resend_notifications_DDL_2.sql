 
ALTER TABLE `notification_log` MODIFY COLUMN `NotificationMethod` INTEGER NOT NULL;

ALTER TABLE `notification_log` MODIFY COLUMN `SourceAddress` VARCHAR(255) NOT NULL;

ALTER TABLE `notification_log` MODIFY COLUMN `NotificationReceiverType` INTEGER NOT NULL;

