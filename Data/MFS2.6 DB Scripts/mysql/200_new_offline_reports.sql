
 DELETE FROM offline_report_receiver;
 
 DELETE FROM offline_report;
 
 ALTER TABLE offline_report ADD COLUMN IsDaily tinyint(4) DEFAULT '0';
 
 ALTER TABLE offline_report ADD COLUMN IsMonthly tinyint(4) DEFAULT '0'; 

 INSERT INTO offline_report (Version,LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Name,ReportSql,ReportClass,TriggerEnable,IsDaily,IsMonthly) VALUES (1,now(),'system',now(),'system','Subscriber Details Report',NULL,'SubscriberDetailsReport',0,1,0);
 
 INSERT INTO offline_report (Version,LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Name,ReportSql,ReportClass,TriggerEnable,IsDaily,IsMonthly) VALUES (1,now(),'system',now(),'system','Emoney Cashin Report',NULL,'EmoneyCashinReport',0,1,0);
 
 INSERT INTO offline_report (Version,LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Name,ReportSql,ReportClass,TriggerEnable,IsDaily,IsMonthly) VALUES (1,now(),'system',now(),'system','Emoney Financial Transaction Report',NULL,'EmoneyFinancialTransactionReport',0,1,0);
 
 INSERT INTO offline_report (Version,LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Name,ReportSql,ReportClass,TriggerEnable,IsDaily,IsMonthly) VALUES (1,now(),'system',now(),'system','Unclaimed Money Transfer Report',NULL,'UnclaimedMoneyTransferReport',0,1,0);
 
 INSERT INTO offline_report (Version,LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Name,ReportSql,ReportClass,TriggerEnable,IsDaily,IsMonthly) VALUES (1,now(),'system',now(),'system','Emoney NonFinancial Transaction Report',NULL,'EmoneyNonFinancialTransactionReport',0,1,0);
 
 INSERT INTO offline_report (Version,LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Name,ReportSql,ReportClass,TriggerEnable,IsDaily,IsMonthly) VALUES (1,now(),'system',now(),'system','Purchase Report',NULL,'PurchaseReport',0,1,0);
 
 INSERT INTO offline_report (Version,LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Name,ReportSql,ReportClass,TriggerEnable,IsDaily,IsMonthly) VALUES (1,now(),'system',now(),'system','Bills Payment Report',NULL,'BillsPaymentReport',0,1,0);
 
 INSERT INTO offline_report (Version,LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Name,ReportSql,ReportClass,TriggerEnable,IsDaily,IsMonthly) VALUES (1,now(),'system',now(),'system','Partners Report',NULL,'PartnersReport',0,1,0);
 
 INSERT INTO offline_report (Version,LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Name,ReportSql,ReportClass,TriggerEnable,IsDaily,IsMonthly) VALUES (1,now(),'system',now(),'system','Partners Transaction Report',NULL,'PartnersTransactionReport',0,1,0);
 
 INSERT INTO offline_report (Version,LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Name,ReportSql,ReportClass,TriggerEnable,IsDaily,IsMonthly) VALUES (1,now(),'system',now(),'system','Partners Transaction Cumulative Report',NULL,'PartnersTransactionCumulativeReport',0,1,0);
 
 INSERT INTO offline_report (Version,LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Name,ReportSql,ReportClass,TriggerEnable,IsDaily,IsMonthly) VALUES (1,now(),'system',now(),'system','Billers Transaction Cumulative Report',NULL,'BillersTransactionCumulativeReport',0,1,0);
 
 INSERT INTO offline_report (Version,LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Name,ReportSql,ReportClass,TriggerEnable,IsDaily,IsMonthly) VALUES (1,now(),'system',now(),'system','Pending Emoney Transactions Report',NULL,'PendingEmoneyTransactionsReport',0,1,0);
 
 INSERT INTO offline_report (Version,LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Name,ReportSql,ReportClass,TriggerEnable,IsDaily,IsMonthly) VALUES (1,now(),'system',now(),'system','Partners Settlement Report',NULL,'PartnersSettlementReport',0,1,0);
 
 INSERT INTO offline_report (Version,LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Name,ReportSql,ReportClass,TriggerEnable,IsDaily,IsMonthly) VALUES (1,now(),'system',now(),'system','Resolved EMoney Transactions Report',NULL,'ResolvedEMoneyTransactionsReport',0,1,0);
 
 INSERT INTO offline_report (Version,LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Name,ReportSql,ReportClass,TriggerEnable,IsDaily,IsMonthly) VALUES (1,now(),'system',now(),'system','Emoney Cashout Report',NULL,'EmoneyCashoutReport',0,1,0);
 
 INSERT INTO offline_report (Version,LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Name,ReportSql,ReportClass,TriggerEnable,IsDaily,IsMonthly) VALUES (1,now(),'system',now(),'system','AML Report',NULL,'AMLReport',0,1,0);
 
 INSERT INTO offline_report (Version,LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Name,ReportSql,ReportClass,TriggerEnable,IsDaily,IsMonthly) VALUES (1,now(),'system',now(),'system','Omnibus Transaction Report',NULL,'OmnibusTransactionReport',0,1,0);
 
 INSERT INTO offline_report (Version,LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Name,ReportSql,ReportClass,TriggerEnable,IsDaily,IsMonthly) VALUES (1,now(),'system',now(),'system','Emoney MovementReport-L1SummaryPerStatus',NULL,'EmoneyMovementReport-L1SummaryPerStatus',0,1,0);
 
 INSERT INTO offline_report (Version,LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Name,ReportSql,ReportClass,TriggerEnable,IsDaily,IsMonthly) VALUES (1,now(),'system',now(),'system','EmoneyMovementReport-L1SummaryPerSubscriber',NULL,'EmoneyMovementReport-L1SummaryPerSubscriber',0,1,0);
 
 INSERT INTO offline_report (Version,LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Name,ReportSql,ReportClass,TriggerEnable,IsDaily,IsMonthly) VALUES (1,now(),'system',now(),'system','EmoneyMovementReport-L2AdditionSummaryPerSubscriber',NULL,'EmoneyMovementReport-L2AdditionSummaryPerSubscriber',0,1,0);
 
 INSERT INTO offline_report (Version,LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Name,ReportSql,ReportClass,TriggerEnable,IsDaily,IsMonthly) VALUES (1,now(),'system',now(),'system','EmoneyMovementReport-L2DeductionSummaryPerSubscriber',NULL,'EmoneyMovementReport-L2DeductionSummaryPerSubscriber',0,1,0);
 
 
 