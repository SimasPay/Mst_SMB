

Delete from system_parameters where ParameterName IN ('fac.prefix.value','nibss.inter.emoney.transfer.code');

Delete from transaction_type where TransactionName IN ('AgentToAgentTransfer','SendReceipt','FundReimburse',
'ReverseTransaction',
'InterBankTransfer',
'BulkTransfer',
'SubBulkTransfer',
'SettleBulkTransfer',
'TellerEMoneyClearance',
'ReverseCharge',
'CashOutAtATM',
'Adjustments',
'FundAllocation',
'FundWithdrawal',
'Refund',
'FundReversal',
'PartnerRegistrationThroughAPI');

Delete from service where ServiceName IN ('TellerService');

Delete from permission_group where PermissionGroupName IN ('MNO Params',
'Distribution Hierarchy',
'Bulk Transfer',
'Analytics',
'Funding For Agent',
'Fund Definitions',
'Transaction Monitor',
'Actor Channel Mapping');

Delete from permission_item where ItemID IN (
'report',
'bulkUpload',
'bulkUpload.grid.add',
'brand',
'brand.grid.add',
'businessPartner',
'chargeTransaction.reverse',
'chargeTransaction.approve',
'chargeTransaction.resendAccessCode',
'bulktransfer',
'bulktransfer.approve',
'bulktransfer.cancel',
'bulktransfer.verify',
'olap',
'distributionHierarchy',
'dct.distribute.amount',
'teller',
'teller.sub.pocket.add',
'fundDefinitions',
'fundDefinitions.add',
'fundDefinitions.edit',
'purpose.add',
'expiry.add',
'transactionMonitor',
'actorChannelMapping',
'actorChannelMapping.add',
'actorChannelMapping.edit',
'adjustments',
'adjustments.apply',
'adjustments.approve',
'bulktransfer.upload');





