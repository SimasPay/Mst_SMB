use mfino;

INSERT INTO `report_parameters` (`Version`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`ParameterName`,`ParameterValue`,`Description`) VALUES 
 (1,now(),'system',now(),'system','AccountProfileChangeReport_#','1500',''),
 (1,now(),'system',now(),'system','AccountProfileChangeReport_Date','2500',''),
 (1,now(),'system',now(),'system','AccountProfileChangeReport_SubscriberMdn','3200',''),
 (1,now(),'system',now(),'system','AccountProfileChangeReport_LastName','4000',''),
 (1,now(),'system',now(),'system','AccountProfileChangeReport_FirstName','4000',''),	
 (1,now(),'system',now(),'system','AccountProfileChangeReport_CurrentAccounttype','2500',''),
 (1,now(),'system',now(),'system','AccountProfileChangeReport_LastTransactionTime','2500',''),
 (1,now(),'system',now(),'system','AccountProfileChangeReport_LasttransactionId','2500','');

INSERT INTO `report_parameters` (`Version`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`ParameterName`,`ParameterValue`,`Description`) VALUES 
 (1,now(),'system',now(),'system','AgentClassificationReport_#','1500',''),
 (1,now(),'system',now(),'system','AgentClassificationReport_City','5000',''),
 (1,now(),'system',now(),'system','AgentClassificationReport_Total Number','3500',''),
 (1,now(),'system',now(),'system','AgentClassificationReport_SuperAgent','4000',''),
 (1,now(),'system',now(),'system','AgentClassificationReport_DirectAgents','4000',''),	
 (1,now(),'system',now(),'system','AgentClassificationReport_Retailers','4000','');

INSERT INTO `report_parameters` (`Version`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`ParameterName`,`ParameterValue`,`Description`) VALUES 
 (1,now(),'system',now(),'system','AgentSalesCommisionReport_IsLandscape','true',''),
 (1,now(),'system',now(),'system','AgentSalesCommisionReport_#','1500',''),
 (1,now(),'system',now(),'system','AgentSalesCommisionReport_MDN','3200',''),
 (1,now(),'system',now(),'system','AgentSalesCommisionReport_Agent Name','4000','');

INSERT INTO `report_parameters` (`Version`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`ParameterName`,`ParameterValue`,`Description`) VALUES 
 (1,now(),'system',now(),'system','AgentsSettlementReport_#','1500',''),
 (1,now(),'system',now(),'system','AgentsSettlementReport_Agent Name','4000',''),
 (1,now(),'system',now(),'system','AgentsSettlementReport_SourceWalletID','4000',''),
 (1,now(),'system',now(),'system','AgentsSettlementReport_SourceWalletType','4000',''),
 (1,now(),'system',now(),'system','AgentsSettlementReport_DestinationWalletId','4000',''),
 (1,now(),'system',now(),'system','AgentsSettlementReport_DestinationWalletType','4000',''),
 (1,now(),'system',now(),'system','AgentsSettlementReport_Amount','2500','');

INSERT INTO `report_parameters` (`Version`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`ParameterName`,`ParameterValue`,`Description`) VALUES 
 (1,now(),'system',now(),'system','AggregatevalueAndTransactionsperSubscriberReport_#','1500',''),
 (1,now(),'system',now(),'system','AggregatevalueAndTransactionsperSubscriberReport_SubscriberMdn','3200',''),
 (1,now(),'system',now(),'system','AggregatevalueAndTransactionsperSubscriberReport_TransactionCount','4000',''),
 (1,now(),'system',now(),'system','AggregatevalueAndTransactionsperSubscriberReport_TransactionAmount','4000','');

INSERT INTO `report_parameters` (`Version`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`ParameterName`,`ParameterValue`,`Description`) VALUES 
 (1,now(),'system',now(),'system','B2E2BTransactionReport_#','1500',''),
 (1,now(),'system',now(),'system','B2E2BTransactionReport_Date','2500',''),
 (1,now(),'system',now(),'system','B2E2BTransactionReport_SubscriberMdn','3200',''),
 (1,now(),'system',now(),'system','B2E2BTransactionReport_AccountId','3200',''),
 (1,now(),'system',now(),'system','B2E2BTransactionReport_B2E TotalAmount','4000',''),	
 (1,now(),'system',now(),'system','B2E2BTransactionReport_E2B TotalAmount','4000','');

INSERT INTO `report_parameters` (`Version`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`ParameterName`,`ParameterValue`,`Description`) VALUES 
 (1,now(),'system',now(),'system','ConsolidatedSalesReport_#','1500',''),
 (1,now(),'system',now(),'system','ConsolidatedSalesReport_Date','2500',''),
 (1,now(),'system',now(),'system','ConsolidatedSalesReport_Service name','3000',''),
 (1,now(),'system',now(),'system','ConsolidatedSalesReport_Transaction Type','2500',''),
 (1,now(),'system',now(),'system','ConsolidatedSalesReport_Access medium','3000',''),	
 (1,now(),'system',now(),'system','ConsolidatedSalesReport_Total funds collected','3000',''),
 (1,now(),'system',now(),'system','ConsolidatedSalesReport_Fixed Charge','2500','');

INSERT INTO `report_parameters` (`Version`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`ParameterName`,`ParameterValue`,`Description`) VALUES 
 (1,now(),'system',now(),'system','CustomerRegistrationReport_IsLandscape','true',''),
 (1,now(),'system',now(),'system','CustomerRegistrationReport_#','1500',''),
 (1,now(),'system',now(),'system','CustomerRegistrationReport_Date','2500',''),
 (1,now(),'system',now(),'system','CustomerRegistrationReport_Customer MDN','3200',''),
 (1,now(),'system',now(),'system','CustomerRegistrationReport_No. Of Pockets','1500',''),
 (1,now(),'system',now(),'system','CustomerRegistrationReport_Registration Medium','3000',''),	
 (1,now(),'system',now(),'system','CustomerRegistrationReport_Customer Type','2500',''),
 (1,now(),'system',now(),'system','CustomerRegistrationReport_Account type','2500',''),
 (1,now(),'system',now(),'system','CustomerRegistrationReport_Agent Registered','2500',''),
 (1,now(),'system',now(),'system','CustomerRegistrationReport_WalletTypes','2500','');

INSERT INTO `report_parameters` (`Version`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`ParameterName`,`ParameterValue`,`Description`) VALUES 
 (1,now(),'system',now(),'system','DailyLimitUtilizationReport_IsLandscape','true',''),
 (1,now(),'system',now(),'system','DailyLimitUtilizationReport_#','1500',''),
 (1,now(),'system',now(),'system','DailyLimitUtilizationReport_Date','2500',''),
 (1,now(),'system',now(),'system','DailyLimitUtilizationReport_SubscriberMdn','3200',''),
 (1,now(),'system',now(),'system','DailyLimitUtilizationReport_LastName','3000',''),
 (1,now(),'system',now(),'system','DailyLimitUtilizationReport_FirstName','3000',''),	
 (1,now(),'system',now(),'system','DailyLimitUtilizationReport_PartnerCode','1000',''),
 (1,now(),'system',now(),'system','DailyLimitUtilizationReport_PartnerName','2500',''),
 (1,now(),'system',now(),'system','DailyLimitUtilizationReport_WalletID','2500',''),
 (1,now(),'system',now(),'system','DailyLimitUtilizationReport_CurrentBalance','2500',''),
 (1,now(),'system',now(),'system','DailyLimitUtilizationReport_CurrentDailyExpenditure','2500',''),
 (1,now(),'system',now(),'system','DailyLimitUtilizationReport_LastTransactionTime','2500','');

INSERT INTO `report_parameters` (`Version`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`ParameterName`,`ParameterValue`,`Description`) VALUES 
 (1,now(),'system',now(),'system','FundMovementReport_IsLandscape','true',''),
 (1,now(),'system',now(),'system','FundMovementReport_#','1500',''),
 (1,now(),'system',now(),'system','FundMovementReport_Date','2500',''),
 (1,now(),'system',now(),'system','FundMovementReport_Source MDN','3200',''),
 (1,now(),'system',now(),'system','FundMovementReport_Destination MDN','3200',''),
 (1,now(),'system',now(),'system','FundMovementReport_Amount','2500',''),	
 (1,now(),'system',now(),'system','FundMovementReport_Source Balance Before','2500',''),
 (1,now(),'system',now(),'system','FundMovementReport_Source Balance After','2500',''),
 (1,now(),'system',now(),'system','FundMovementReport_Destination Balance Before','2500',''),
 (1,now(),'system',now(),'system','FundMovementReport_Destination Balance After','2500',''),
 (1,now(),'system',now(),'system','FundMovementReport_Transaction type','3000',''),
 (1,now(),'system',now(),'system','FundMovementReport_Status','3000',''),
 (1,now(),'system',now(),'system','FundMovementReport_Reference ID','2500','');

INSERT INTO `report_parameters` (`Version`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`ParameterName`,`ParameterValue`,`Description`) VALUES 
 (1,now(),'system',now(),'system','KinInformationMissingAccountReport_#','1500',''),
 (1,now(),'system',now(),'system','KinInformationMissingAccountReport_SubscriberMdn','4000',''),
 (1,now(),'system',now(),'system','KinInformationMissingAccountReport_AccountID','4000',''),
 (1,now(),'system',now(),'system','KinInformationMissingAccountReport_AccountType','4000',''),
 (1,now(),'system',now(),'system','KinInformationMissingAccountReport_Status','4000','');	

INSERT INTO `report_parameters` (`Version`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`ParameterName`,`ParameterValue`,`Description`) VALUES 
 (1,now(),'system',now(),'system','Merchant1_TransactionsReport_IsLandscape','true',''),
 (1,now(),'system',now(),'system','Merchant1_TransactionsReport_#','1500',''),
 (1,now(),'system',now(),'system','Merchant1_TransactionsReport_Date','2500',''),
 (1,now(),'system',now(),'system','Merchant1_TransactionsReport_Source MDN','3200',''),
 (1,now(),'system',now(),'system','Merchant1_TransactionsReport_NameOfSubscriber','4000',''),
 (1,now(),'system',now(),'system','Merchant1_TransactionsReport_Amount','2500',''),	
 (1,now(),'system',now(),'system','Merchant1_TransactionsReport_Transaction type','2500',''),
 (1,now(),'system',now(),'system','Merchant1_TransactionsReport_Status','2500',''),
 (1,now(),'system',now(),'system','Merchant1_TransactionsReport_Reference ID','2500',''),
 (1,now(),'system',now(),'system','Merchant1_TransactionsReport_Bill details','2500','');

INSERT INTO `report_parameters` (`Version`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`ParameterName`,`ParameterValue`,`Description`) VALUES 
 (1,now(),'system',now(),'system','mfino_TransactionsReport_IsLandscape','true',''),
 (1,now(),'system',now(),'system','mfino_TransactionsReport_#','1500',''),
 (1,now(),'system',now(),'system','mfino_TransactionsReport_Date','2500',''),
 (1,now(),'system',now(),'system','mfino_TransactionsReport_Source MDN','3200',''),
 (1,now(),'system',now(),'system','mfino_TransactionsReport_NameOfSubscriber','4000',''),
 (1,now(),'system',now(),'system','mfino_TransactionsReport_Amount','2500',''),	
 (1,now(),'system',now(),'system','mfino_TransactionsReport_Transaction type','2500',''),
 (1,now(),'system',now(),'system','mfino_TransactionsReport_Status','2500',''),
 (1,now(),'system',now(),'system','mfino_TransactionsReport_Reference ID','2500',''),
 (1,now(),'system',now(),'system','mfino_TransactionsReport_Bill details','2500','');

INSERT INTO `report_parameters` (`Version`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`ParameterName`,`ParameterValue`,`Description`) VALUES 
 (1,now(),'system',now(),'system','Miller_TransactionsReport_IsLandscape','true',''),
 (1,now(),'system',now(),'system','Miller_TransactionsReport_#','1500',''),
 (1,now(),'system',now(),'system','Miller_TransactionsReport_Date','2500',''),
 (1,now(),'system',now(),'system','Miller_TransactionsReport_Source MDN','3200',''),
 (1,now(),'system',now(),'system','Miller_TransactionsReport_NameOfSubscriber','4000',''),
 (1,now(),'system',now(),'system','Miller_TransactionsReport_Amount','2500',''),	
 (1,now(),'system',now(),'system','Miller_TransactionsReport_Transaction type','2500',''),
 (1,now(),'system',now(),'system','Miller_TransactionsReport_Status','2500',''),
 (1,now(),'system',now(),'system','Miller_TransactionsReport_Reference ID','2500',''),
 (1,now(),'system',now(),'system','Miller_TransactionsReport_Bill details','2500','');

INSERT INTO `report_parameters` (`Version`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`ParameterName`,`ParameterValue`,`Description`) VALUES 
 (1,now(),'system',now(),'system','MobileMoneyFailedTransactionReport_#','1500',''),
 (1,now(),'system',now(),'system','MobileMoneyFailedTransactionReport_DestinationMdn','4000',''),
 (1,now(),'system',now(),'system','MobileMoneyFailedTransactionReport_Date','2500',''),
 (1,now(),'system',now(),'system','MobileMoneyFailedTransactionReport_TransactionType','3000',''),
 (1,now(),'system',now(),'system','MobileMoneyFailedTransactionReport_TransactionAmount','3000',''),
 (1,now(),'system',now(),'system','MobileMoneyFailedTransactionReport_RefID','3000',''),
 (1,now(),'system',now(),'system','MobileMoneyFailedTransactionReport_FailureReason','6000','');

INSERT INTO `report_parameters` (`Version`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`ParameterName`,`ParameterValue`,`Description`) VALUES 
 (1,now(),'system',now(),'system','MobileMoneyRepeatedTransactionsReport_#','1500',''),
 (1,now(),'system',now(),'system','MobileMoneyRepeatedTransactionsReport_SubscriberMdn','4000',''),
 (1,now(),'system',now(),'system','MobileMoneyRepeatedTransactionsReport_Date','2500',''),
 (1,now(),'system',now(),'system','MobileMoneyRepeatedTransactionsReport_TransactionType','3000',''),
 (1,now(),'system',now(),'system','MobileMoneyRepeatedTransactionsReport_TransactionAmount','3000',''),
 (1,now(),'system',now(),'system','MobileMoneyRepeatedTransactionsReport_Charge','3000',''),
 (1,now(),'system',now(),'system','MobileMoneyRepeatedTransactionsReport_DestinationMdn','4000',''),
 (1,now(),'system',now(),'system','MobileMoneyRepeatedTransactionsReport_Status','3000','');

INSERT INTO `report_parameters` (`Version`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`ParameterName`,`ParameterValue`,`Description`) VALUES 
 (1,now(),'system',now(),'system','MoneyAvailableReport_#','1500',''),
 (1,now(),'system',now(),'system','MoneyAvailableReport_PartnerName','4000',''),
 (1,now(),'system',now(),'system','MoneyAvailableReport_Channel Partner MDN','3200',''),
 (1,now(),'system',now(),'system','MoneyAvailableReport_Available Balance','3000',''),
 (1,now(),'system',now(),'system','MoneyAvailableReport_InClearing Balance','3000',''),
 (1,now(),'system',now(),'system','MoneyAvailableReport_Status','3000',''),
 (1,now(),'system',now(),'system','MoneyAvailableReport_LastTransactionTime','3000','');

INSERT INTO `report_parameters` (`Version`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`ParameterName`,`ParameterValue`,`Description`) VALUES 
 (1,now(),'system',now(),'system','OverLimitTransactionReport_#','1500',''),
 (1,now(),'system',now(),'system','OverLimitTransactionReport_ReferenceID','3000',''),
 (1,now(),'system',now(),'system','OverLimitTransactionReport_SubscriberMdn','3200',''),
 (1,now(),'system',now(),'system','OverLimitTransactionReport_DestinationMdn','3200',''),
 (1,now(),'system',now(),'system','OverLimitTransactionReport_Amount','3000',''),
 (1,now(),'system',now(),'system','OverLimitTransactionReport_Charge','3000',''),
 (1,now(),'system',now(),'system','OverLimitTransactionReport_TransactionType','3000',''),
 (1,now(),'system',now(),'system','OverLimitTransactionReport_TransactionTime','3000','');

INSERT INTO `report_parameters` (`Version`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`ParameterName`,`ParameterValue`,`Description`) VALUES 
 (1,now(),'system',now(),'system','PartnerClassificationReport_#','1500',''),
 (1,now(),'system',now(),'system','PartnerClassificationReport_City','4500',''),
 (1,now(),'system',now(),'system','PartnerClassificationReport_Total Number','3000',''),
 (1,now(),'system',now(),'system','PartnerClassificationReport_Tellers','3000',''),
 (1,now(),'system',now(),'system','PartnerClassificationReport_Merchants','3000','');

INSERT INTO `report_parameters` (`Version`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`ParameterName`,`ParameterValue`,`Description`) VALUES 
 (1,now(),'system',now(),'system','PartnersSettlementReport_#','1500',''),
 (1,now(),'system',now(),'system','PartnersSettlementReport_Partner Name','4000',''),
 (1,now(),'system',now(),'system','PartnersSettlementReport_SourceWalletID','3000',''),
 (1,now(),'system',now(),'system','PartnersSettlementReport_SourceWalletType','3000',''),
 (1,now(),'system',now(),'system','PartnersSettlementReport_DestinationWalletId','3000',''),
 (1,now(),'system',now(),'system','PartnersSettlementReport_DestinationWalletType','3000',''),
 (1,now(),'system',now(),'system','PartnersSettlementReport_Amount','2500','');

INSERT INTO `report_parameters` (`Version`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`ParameterName`,`ParameterValue`,`Description`) VALUES 
 (1,now(),'system',now(),'system','PartnerTransactionReport_#','1500',''),
 (1,now(),'system',now(),'system','PartnerTransactionReport_Partner Name','3000',''),
 (1,now(),'system',now(),'system','PartnerTransactionReport_Credit Amount','2500',''),
 (1,now(),'system',now(),'system','PartnerTransactionReport_Debit Amount','2500','');

INSERT INTO `report_parameters` (`Version`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`ParameterName`,`ParameterValue`,`Description`) VALUES 
 (1,now(),'system',now(),'system','PendingTransactionReport_#','1500',''),
 (1,now(),'system',now(),'system','PendingTransactionReport_MDN','2500',''),
 (1,now(),'system',now(),'system','PendingTransactionReport_WalletType','2500',''),
 (1,now(),'system',now(),'system','PendingTransactionReport_Transaction Date','2500',''),
 (1,now(),'system',now(),'system','PendingTransactionReport_TransactionType','2500','');

INSERT INTO `report_parameters` (`Version`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`ParameterName`,`ParameterValue`,`Description`) VALUES 
 (1,now(),'system',now(),'system','RepeatedTransactionsPerTransactionTypeReport_#','1500',''),
 (1,now(),'system',now(),'system','RepeatedTransactionsPerTransactionTypeReport_SubscriberMdn','4000',''),
 (1,now(),'system',now(),'system','RepeatedTransactionsPerTransactionTypeReport_DestinationMdn','4000',''),
 (1,now(),'system',now(),'system','RepeatedTransactionsPerTransactionTypeReport_TransactionCount','2500',''),
 (1,now(),'system',now(),'system','RepeatedTransactionsPerTransactionTypeReport_TransactionAmount','4500','');

INSERT INTO `report_parameters` (`Version`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`ParameterName`,`ParameterValue`,`Description`) VALUES 
 (1,now(),'system',now(),'system','ResolvedTransactionReport_#','1500',''),
 (1,now(),'system',now(),'system','ResolvedTransactionReport_UserName','4000',''),
 (1,now(),'system',now(),'system','ResolvedTransactionReport_SubscriberMdn','4000',''),
 (1,now(),'system',now(),'system','ResolvedTransactionReport_RefID','4000',''),
 (1,now(),'system',now(),'system','ResolvedTransactionReport_TransactionCount','2500',''),
 (1,now(),'system',now(),'system','ResolvedTransactionReport_TransactionAmount','2500',''),
 (1,now(),'system',now(),'system','ResolvedTransactionReport_TransactionTime','2500',''),
 (1,now(),'system',now(),'system','ResolvedTransactionReport_ResolvedTime','2500','');

INSERT INTO `report_parameters` (`Version`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`ParameterName`,`ParameterValue`,`Description`) VALUES 
 (1,now(),'system',now(),'system','ServiceChargeReport_Date','4000',''),
 (1,now(),'system',now(),'system','ServiceChargeReport_Service name','4500',''),
 (1,now(),'system',now(),'system','ServiceChargeReport_Fixed Charge','3000','');

INSERT INTO `report_parameters` (`Version`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`ParameterName`,`ParameterValue`,`Description`) VALUES 
 (1,now(),'system',now(),'system','SubscriberClassificationReport_#','1500',''),
 (1,now(),'system',now(),'system','SubscriberClassificationReport_City','4500',''),
 (1,now(),'system',now(),'system','SubscriberClassificationReport_Total Number','4000',''),
 (1,now(),'system',now(),'system','SubscriberClassificationReport_Banked','4000',''),
 (1,now(),'system',now(),'system','SubscriberClassificationReport_Semi Banked','4000',''),
 (1,now(),'system',now(),'system','SubscriberClassificationReport_Unbanked','4000','');

INSERT INTO `report_parameters` (`Version`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`ParameterName`,`ParameterValue`,`Description`) VALUES 
 (1,now(),'system',now(),'system','TransactionReport_#','1500',''),
 (1,now(),'system',now(),'system','TransactionReport_Date','2500',''),
 (1,now(),'system',now(),'system','TransactionReport_Service Name','4000',''),
 (1,now(),'system',now(),'system','TransactionReport_Total Transaction','1500',''),
 (1,now(),'system',now(),'system','TransactionReport_No of Failed transaction','1500',''),
 (1,now(),'system',now(),'system','TransactionReport_No of Successful transaction','1500',''),
 (1,now(),'system',now(),'system','TransactionReport_No of pending transaction','1500',''),
 (1,now(),'system',now(),'system','TransactionReport_No of Successful transaction','2500','');

INSERT INTO `report_parameters` (`Version`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`ParameterName`,`ParameterValue`,`Description`) VALUES 
 (1,now(),'system',now(),'system','UpdatedAccountsReport_#','1500',''),
 (1,now(),'system',now(),'system','UpdatedAccountsReport_Date','2500',''),
 (1,now(),'system',now(),'system','UpdatedAccountsReport_SubscriberMdn','3200',''),
 (1,now(),'system',now(),'system','UpdatedAccountsReport_LastName','2500',''),
 (1,now(),'system',now(),'system','UpdatedAccountsReport_FirstName','2500',''),
 (1,now(),'system',now(),'system','UpdatedAccountsReport_PartnerName','2500',''),
 (1,now(),'system',now(),'system','UpdatedAccountsReport_CurrentBalance','2500',''),
 (1,now(),'system',now(),'system','UpdatedAccountsReport_LastTransactionTime','2500','');

INSERT INTO `report_parameters` (`Version`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`ParameterName`,`ParameterValue`,`Description`) VALUES 
 (1,now(),'system',now(),'system','UserRolesAndRightsReport_#','1500',''),
 (1,now(),'system',now(),'system','UserRolesAndRightsReport_UserName','4000',''),
 (1,now(),'system',now(),'system','UserRolesAndRightsReport_Role','4000',''),
 (1,now(),'system',now(),'system','UserRolesAndRightsReport_LastUpdatedTime','4000',''),
 (1,now(),'system',now(),'system','UserRolesAndRightsReport_UpdatedBy','4000','');

INSERT INTO `report_parameters` (`Version`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`ParameterName`,`ParameterValue`,`Description`) VALUES 
 (1,now(),'system',now(),'system','ZMMAccountDeactiveReport_#','1500',''),
 (1,now(),'system',now(),'system','ZMMAccountDeactiveReport_SubscriberMdn','3500',''),
 (1,now(),'system',now(),'system','ZMMAccountDeactiveReport_AccountID','4000',''),
 (1,now(),'system',now(),'system','ZMMAccountDeactiveReport_LastTransactionTime','4000',''),
 (1,now(),'system',now(),'system','ZMMAccountDeactiveReport_LasttransactionId','4000','');