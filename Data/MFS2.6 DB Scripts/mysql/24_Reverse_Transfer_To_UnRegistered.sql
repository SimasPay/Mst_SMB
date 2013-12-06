
Alter Table bulk_upload add column RevertAmount Decimal(25,4);

-- Define the expiry time for transfer to UnRegistered system parameter
Delete from system_parameters where ParameterName = 'transfer.to.unregistered.expiry.time';
INSERT INTO system_parameters (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, ParameterName, ParameterValue, Description) VALUES (1,now(),'System',now(),'system','transfer.to.unregistered.expiry.time','48','Transfer to UnRegistered Expiry Time (Hrs)');

-- Define the Reverse Charge system parameter
Delete from system_parameters where ParameterName = 'reverse.charge.for.expired.transfer.to.unregistered';
INSERT INTO system_parameters (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, ParameterName, ParameterValue, Description) VALUES (1,now(),'System',now(),'system','reverse.charge.for.expired.transfer.to.unregistered','true','Reverse Charge foe Expired Transfer to UnRegistered');

-- Inserting Enum_text data
delete from enum_text where tagid=5415 and enumcode='4';
INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',now(),'system',now(),'system','0','TransactionsTransferStatus','5415','4','Expired','Expired');

delete from enum_text where tagid=5415 and enumcode='5';
INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',now(),'system',now(),'system','0','TransactionsTransferStatus','5415','5','Reversed','Reversed');

-- Update Notification messages
update notification set text='Dear Customer, Failed / Expired Transfers amount $(Currency) $(Amount) for Your Bulk Transfer Request $(BulkTransferID) is credited to souce pocket.' where code=701;