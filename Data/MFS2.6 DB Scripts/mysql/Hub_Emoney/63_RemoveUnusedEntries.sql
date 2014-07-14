-- Script to remove unused transaction types, system parameters and permissions
Delete from system_parameters where ParameterName IN ('INCODE_1', 'android.subapp.version', 'javame.subapp.version', 'ios.subapp.version', 'bb.subapp.version', 'android.agentapp.version', 'javame.agentapp.version', 'ios.agentapp.version', 'bb.agentapp.version', 'subapp.url', 'agentapp.url', 'fac.prefix.value',
 'maximum.value.of.cashout.at.atm', 'mdnlength.with.countrycode', 'nibss.inter.emoney.transfer.code',
 'sms.interval.inactive.bulkupload', 'android.subapp.minvalidversion', 'javame.subapp.minvalidversion', 'ios.subapp.minvalidversion', 'bb.subapp.minvalidversion', 'android.agentapp.minvalidversion', 'javame.agentapp.minvalidversion', 'ios.agentapp.minvalidversion',
 'bb.agentapp.minvalidversion', 'allowed.partners.toregister.throughapi', 'partner.register.throughapi.approvalrequired', 'category.postpaid',
 'category.prepaid');

delete from service_transaction where transactiontypeid in (select id from transaction_type where TransactionName IN ('Activation', 'TransactionStatus',
 'CashIn', 'SubscriberRegistration', 'AgentActivation', 'Purchase', 'AgentToAgentTransfer','SendReceipt','FundReimburse', 'ReverseTransaction',
 'CashOutToUnRegistered', 'BulkTransfer', 'SubBulkTransfer', 'SettleBulkTransfer', 'TellerEMoneyClearance', 'ReverseCharge', 'CashOutAtATM', 'BillInquiry'
 , 'ChangeSettings', 'Adjustments', 'FundAllocation', 'FundWithdrawal', 'Refund', 'FundReversal', 'InterEmoneyTransfer', 'SubscriberRegistrationThroughWeb',
 'Reactivation', 'PartnerRegistrationThroughAPI', 'AirtimePinPurchase', 'NFCCardUnlink', 'NFCPocketBalance', 'NFCCardTopup', 'NFCCardTopupReversal'));

delete from rule_key where transactiontypeid  in (select id from transaction_type where TransactionName IN ('Activation', 'TransactionStatus', 'CashIn', 
'SubscriberRegistration', 'AgentActivation', 'Purchase', 'AgentToAgentTransfer', 'SendReceipt','FundReimburse', 'ReverseTransaction', 'CashOutToUnRegistered',
 'BulkTransfer', 'SubBulkTransfer', 'SettleBulkTransfer', 'TellerEMoneyClearance', 'ReverseCharge', 'CashOutAtATM', 'BillInquiry', 'ChangeSettings',
 'Adjustments', 'FundAllocation', 'FundWithdrawal', 'Refund', 'FundReversal', 'InterEmoneyTransfer', 'SubscriberRegistrationThroughWeb',
'Reactivation', 'PartnerRegistrationThroughAPI', 'AirtimePinPurchase', 'NFCCardUnlink', 'NFCPocketBalance', 'NFCCardTopup', 'NFCCardTopupReversal'));

Delete from transaction_type where TransactionName IN ('Activation', 'TransactionStatus', 'CashIn', 'SubscriberRegistration', 'AgentActivation', 'Purchase', 
 'AgentToAgentTransfer','SendReceipt','FundReimburse', 'ReverseTransaction', 'CashOutToUnRegistered', 'BulkTransfer', 'SubBulkTransfer', 'SettleBulkTransfer', 
 'TellerEMoneyClearance', 'ReverseCharge', 'CashOutAtATM', 'BillInquiry', 'ChangeSettings', 'Adjustments', 'FundAllocation', 'FundWithdrawal', 'Refund', 
 'FundReversal', 'InterEmoneyTransfer', 'SubscriberRegistrationThroughWeb', 'Reactivation', 'PartnerRegistrationThroughAPI', 'AirtimePinPurchase', 'NFCCardUnlink', 'NFCPocketBalance', 'NFCCardTopup', 'NFCCardTopupReversal');

delete from role_permission where permission in (select permission from permission_item where ItemID IN ('sub.details.resetpin', 'sub.balance.emptyButton', 
 'sub.reset.otp', 'subscriber.cashin', 'subscriber.cashout', 'notification.grid.add', 'bulkUpload', 'bulkUpload.grid.add', 'brand', 'brand.grid.add', 
 'servicepartner.reset.otp', 'partner.resetpin', 'partner.changepin', 'pinprompt', 'chargeTransaction.reverse', 'chargeTransaction.approve'
 , 'chargeTransaction.resendAccessCode', 'bulktransfer', 'bulktransfer.approve', 'bulktransfer.cancel', 'bulktransfer.verify', 'olap', 'distributionHierarchy'
 , 'dct.distribute.amount', 'systemParameters.add', 'teller', 'teller.sub.pocket.add', 'fundDefinitions', 'fundDefinitions.add', 'fundDefinitions.edit', 
 'purpose.add', 'expiry.add', 'transactionMonitor', 'actorChannelMapping', 'actorChannelMapping.add', 'actorChannelMapping.edit', 'adjustments', 
 'adjustments.apply', 'adjustments.approve', 'bulktransfer.upload', 'partner.transfer', 'appUploader', 'enumPage', 'enum.grid.add', 'enum.grid.edit', 'servicepartner.details.distribute'));

Delete from permission_item where ItemID IN ('sub.details.resetpin', 'sub.balance.emptyButton', 'sub.reset.otp', 'subscriber.cashin', 'subscriber.cashout',
 'notification.grid.add', 'bulkUpload', 'bulkUpload.grid.add', 'brand', 'brand.grid.add', 'servicepartner.reset.otp', 'partner.resetpin', 'partner.changepin',
 'pinprompt', 'chargeTransaction.reverse', 'chargeTransaction.approve', 'chargeTransaction.resendAccessCode', 'bulktransfer', 
 'bulktransfer.approve', 'bulktransfer.cancel', 'bulktransfer.verify', 'olap', 'distributionHierarchy', 'dct.distribute.amount', 'systemParameters.add', 
 'teller', 'teller.sub.pocket.add', 'fundDefinitions', 'fundDefinitions.add', 'fundDefinitions.edit', 'purpose.add', 'expiry.add', 'transactionMonitor', 
 'actorChannelMapping', 'actorChannelMapping.add', 'actorChannelMapping.edit', 'adjustments', 'adjustments.apply', 'adjustments.approve', 
 'bulktransfer.upload', 'partner.transfer', 'appUploader', 'enumPage', 'enum.grid.add', 'enum.grid.edit', 'servicepartner.details.distribute');

Delete from permission_group where PermissionGroupName IN ('Enum Page', 'Bulk Upload', 'MNO Params', 'Bulk Transfer', 'Analytics', 'Bank Teller', 
'Funding For Agent', 'Fund Definitions', 'Transaction Monitor', 'Actor Channel Mapping', 'App Uploader', 'Adjustments');
