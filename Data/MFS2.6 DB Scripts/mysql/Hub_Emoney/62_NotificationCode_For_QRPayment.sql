DELETE FROM `notification` where Code=2109;

-- English 2109

INSERT INTO `notification` (`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,`Language`,`Status`,`StatusTime`,`AccessCode`,`SMSNotificationCode`,`CompanyID`) VALUES (now(),"System",now(),"System",0,1,2109,"QRpaymentInquirySuccessful",1,"$(NotificationCode) You requested to top up $(ReceiverMDN) from $(BillerCode) with $(Currency) $(Amount), ServiceCharge $(Currency) $(serviceCharge). ParentTransactionID $(ParentTransactionID) REF: $(TransferID)",null,0,0,now(),null,null,1);

INSERT INTO `notification` (`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,`Language`,`Status`,`StatusTime`,`AccessCode`,`SMSNotificationCode`,`CompanyID`) VALUES (now(),"System",now(),"System",0,1,2109,"QRpaymentInquirySuccessful",2,"$(NotificationCode) You requested to top up $(ReceiverMDN) from $(BillerCode) with $(Currency) $(Amount), ServiceCharge $(Currency) $(serviceCharge). ParentTransactionID $(ParentTransactionID) REF: $(TransferID)",null,0,0,now(),null,null,1);

INSERT INTO `notification` (`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,`Language`,`Status`,`StatusTime`,`AccessCode`,`SMSNotificationCode`,`CompanyID`) VALUES (now(),"System",now(),"System",0,1,2109,"QRpaymentInquirySuccessful",4,"$(NotificationCode) You requested to top up $(ReceiverMDN) from $(BillerCode) with $(Currency) $(Amount), ServiceCharge $(Currency) $(serviceCharge). ParentTransactionID $(ParentTransactionID) REF: $(TransferID)",null,0,0,now(),null,null,1);

INSERT INTO `notification` (`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,`Language`,`Status`,`StatusTime`,`AccessCode`,`SMSNotificationCode`,`CompanyID`) VALUES (now(),"System",now(),"System",0,1,2109,"QRpaymentInquirySuccessful",8,"$(NotificationCode) You requested to top up $(ReceiverMDN) from $(BillerCode) with $(Currency) $(Amount), ServiceCharge $(Currency) $(serviceCharge). ParentTransactionID $(ParentTransactionID) REF: $(TransferID)",null,0,0,now(),null,null,1);

INSERT INTO `notification` (`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,`Language`,`Status`,`StatusTime`,`AccessCode`,`SMSNotificationCode`,`CompanyID`) VALUES (now(),"System",now(),"System",0,1,2109,"QRpaymentInquirySuccessful",16,"$(NotificationCode) You requested to top up $(ReceiverMDN) from $(BillerCode) with $(Currency) $(Amount), ServiceCharge $(Currency) $(serviceCharge). ParentTransactionID $(ParentTransactionID) REF: $(TransferID)",null,0,0,now(),null,null,1);

-- Bahasa 2109

INSERT INTO `notification` (`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,`Language`,`Status`,`StatusTime`,`AccessCode`,`SMSNotificationCode`,`CompanyID`) VALUES (now(),"System",now(),"System",0,1,2109,"QRpaymentInquirySuccessful",1,"Anda akan bayar $(BillerName) nomor $(InvoiceNumber) sebesar $(Currency) $(NominalAmount) dengan biaya $(Currency) $(serviceCharge).",null,1,0,now(),null,null,1);

INSERT INTO `notification` (`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,`Language`,`Status`,`StatusTime`,`AccessCode`,`SMSNotificationCode`,`CompanyID`) VALUES (now(),"System",now(),"System",0,1,2109,"QRpaymentInquirySuccessful",2,"Anda akan bayar $(BillerName) nomor $(InvoiceNumber) sebesar $(Currency) $(NominalAmount) dengan biaya $(Currency) $(serviceCharge).",null,1,0,now(),null,null,1);

INSERT INTO `notification` (`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,`Language`,`Status`,`StatusTime`,`AccessCode`,`SMSNotificationCode`,`CompanyID`) VALUES (now(),"System",now(),"System",0,1,2109,"QRpaymentInquirySuccessful",4,"Anda akan bayar $(BillerName) nomor $(InvoiceNumber) sebesar $(Currency) $(NominalAmount) dengan biaya $(Currency) $(serviceCharge).",null,1,0,now(),null,null,1);

INSERT INTO `notification` (`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,`Language`,`Status`,`StatusTime`,`AccessCode`,`SMSNotificationCode`,`CompanyID`) VALUES (now(),"System",now(),"System",0,1,2109,"QRpaymentInquirySuccessful",8,"Anda akan bayar $(BillerName) nomor $(InvoiceNumber) sebesar $(Currency) $(NominalAmount) dengan biaya $(Currency) $(serviceCharge).",null,1,0,now(),null,null,1);

INSERT INTO `notification` (`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,`Language`,`Status`,`StatusTime`,`AccessCode`,`SMSNotificationCode`,`CompanyID`) VALUES (now(),"System",now(),"System",0,1,2109,"QRpaymentInquirySuccessful",16,"Anda akan bayar $(BillerName) nomor $(InvoiceNumber) sebesar $(Currency) $(NominalAmount) dengan biaya $(Currency) $(serviceCharge).",null,1,0,now(),null,null,1);


DELETE FROM `notification` where Code=2110;

INSERT INTO `notification` (`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,`Language`,`Status`,`StatusTime`,`AccessCode`,`SMSNotificationCode`,`CompanyID`) VALUES (now(),"System",now(),"System",0,1,2110,"QRpaymentFailed",1,"Your request to pay $(BillerName) with $(Currency) $(NominalAmount) failed, Please try again later. $(OperatorMessage) REF: $(TransferID)",null,0,0,now(),null,null,1);

INSERT INTO `notification` (`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,`Language`,`Status`,`StatusTime`,`AccessCode`,`SMSNotificationCode`,`CompanyID`) VALUES (now(),"System",now(),"System",0,1,2110,"QRpaymentFailed",2,"Your request to pay $(BillerName) with $(Currency) $(NominalAmount) failed, Please try again later. $(OperatorMessage) REF: $(TransferID)",null,0,0,now(),null,null,1);

INSERT INTO `notification` (`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,`Language`,`Status`,`StatusTime`,`AccessCode`,`SMSNotificationCode`,`CompanyID`) VALUES (now(),"System",now(),"System",0,1,2110,"QRpaymentFailed",4,"Your request to pay $(BillerName) with $(Currency) $(NominalAmount) failed, Please try again later. $(OperatorMessage) REF: $(TransferID)",null,0,0,now(),null,null,1);

INSERT INTO `notification` (`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,`Language`,`Status`,`StatusTime`,`AccessCode`,`SMSNotificationCode`,`CompanyID`) VALUES (now(),"System",now(),"System",0,1,2110,"QRpaymentFailed",8,"Your request to pay $(BillerName) with $(Currency) $(NominalAmount) failed, Please try again later. $(OperatorMessage) REF: $(TransferID)",null,0,0,now(),null,null,1);

INSERT INTO `notification` (`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,`Language`,`Status`,`StatusTime`,`AccessCode`,`SMSNotificationCode`,`CompanyID`) VALUES (now(),"System",now(),"System",0,1,2110,"QRpaymentFailed",16,"Your request to pay $(BillerName) with $(Currency) $(NominalAmount) failed, Please try again later. $(OperatorMessage) REF: $(TransferID)",null,0,0,now(),null,null,1);

-- Bahasa 2110

INSERT INTO `notification` (`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,`Language`,`Status`,`StatusTime`,`AccessCode`,`SMSNotificationCode`,`CompanyID`) VALUES (now(),"System",now(),"System",0,1,2110,"QRpaymentFailed",1,"Bayar $(BillerName) nomor $(InvoiceNumber) sebesar $(Currency) $(NominalAmount) gagal. REF: $(TransferID)",null,1,0,now(),null,null,1);

INSERT INTO `notification` (`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,`Language`,`Status`,`StatusTime`,`AccessCode`,`SMSNotificationCode`,`CompanyID`) VALUES (now(),"System",now(),"System",0,1,2110,"QRpaymentFailed",2,"Bayar $(BillerName) nomor $(InvoiceNumber) sebesar $(Currency) $(NominalAmount) gagal. REF: $(TransferID)",null,1,0,now(),null,null,1);

INSERT INTO `notification` (`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,`Language`,`Status`,`StatusTime`,`AccessCode`,`SMSNotificationCode`,`CompanyID`) VALUES (now(),"System",now(),"System",0,1,2110,"QRpaymentFailed",4,"Bayar $(BillerName) nomor $(InvoiceNumber) sebesar $(Currency) $(NominalAmount) gagal. REF: $(TransferID)",null,1,0,now(),null,null,1);

INSERT INTO `notification` (`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,`Language`,`Status`,`StatusTime`,`AccessCode`,`SMSNotificationCode`,`CompanyID`) VALUES (now(),"System",now(),"System",0,1,2110,"QRpaymentFailed",8,"Bayar $(BillerName) nomor $(InvoiceNumber) sebesar $(Currency) $(NominalAmount) gagal. REF: $(TransferID)",null,1,0,now(),null,null,1);

INSERT INTO `notification` (`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,`Language`,`Status`,`StatusTime`,`AccessCode`,`SMSNotificationCode`,`CompanyID`) VALUES (now(),"System",now(),"System",0,1,2110,"QRpaymentFailed",16,"Bayar $(BillerName) nomor $(InvoiceNumber) sebesar $(Currency) $(NominalAmount) gagal. REF: $(TransferID)",null,1,0,now(),null,null,1);


DELETE FROM `notification` where Code=2111;

INSERT INTO `notification` (`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,`Language`,`Status`,`StatusTime`,`AccessCode`,`SMSNotificationCode`,`CompanyID`) VALUES (now(),"System",now(),"System",0,1,2111,"QRpaymentConfirmationSuccessful",1,"Your request to pay $(BillerName) with $(Currency) $(NominalAmount), ServiceCharge $(Currency) $(serviceCharge) is successful. $(VoucherToken) REF: $(TransferID)",null,0,0,now(),null,null,1);

INSERT INTO `notification` (`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,`Language`,`Status`,`StatusTime`,`AccessCode`,`SMSNotificationCode`,`CompanyID`) VALUES (now(),"System",now(),"System",0,1,2111,"QRpaymentConfirmationSuccessful",2,"Your request to pay $(BillerName) with $(Currency) $(NominalAmount), ServiceCharge $(Currency) $(serviceCharge) is successful. $(VoucherToken) REF: $(TransferID)",null,0,0,now(),null,null,1);

INSERT INTO `notification` (`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,`Language`,`Status`,`StatusTime`,`AccessCode`,`SMSNotificationCode`,`CompanyID`) VALUES (now(),"System",now(),"System",0,1,2111,"QRpaymentConfirmationSuccessful",4,"Your request to pay $(BillerName) with $(Currency) $(NominalAmount), ServiceCharge $(Currency) $(serviceCharge) is successful. $(VoucherToken) REF: $(TransferID)",null,0,0,now(),null,null,1);

INSERT INTO `notification` (`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,`Language`,`Status`,`StatusTime`,`AccessCode`,`SMSNotificationCode`,`CompanyID`) VALUES (now(),"System",now(),"System",0,1,2111,"QRpaymentConfirmationSuccessful",8,"Your request to pay $(BillerName) with $(Currency) $(NominalAmount), ServiceCharge $(Currency) $(serviceCharge) is successful. $(VoucherToken) REF: $(TransferID)",null,0,0,now(),null,null,1);

INSERT INTO `notification` (`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,`Language`,`Status`,`StatusTime`,`AccessCode`,`SMSNotificationCode`,`CompanyID`) VALUES (now(),"System",now(),"System",0,1,2111,"QRpaymentConfirmationSuccessful",16,"Your request to pay $(BillerName) with $(Currency) $(NominalAmount), ServiceCharge $(Currency) $(serviceCharge) is successful. $(VoucherToken) REF: $(TransferID)",null,0,0,now(),null,null,1);

-- Bahasa 2111


INSERT INTO `notification` (`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,`Language`,`Status`,`StatusTime`,`AccessCode`,`SMSNotificationCode`,`CompanyID`) VALUES (now(),"System",now(),"System",0,1,2111,"QRpaymentConfirmationSuccessful",1," Anda berhasil bayar $(BillerName) nomor $(InvoiceNumber) $(Currency) $(NominalAmount) dengan biaya $(Currency) $(serviceCharge). REF: $(TransferID)",null,1,0,now(),null,null,1);

INSERT INTO `notification` (`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,`Language`,`Status`,`StatusTime`,`AccessCode`,`SMSNotificationCode`,`CompanyID`) VALUES (now(),"System",now(),"System",0,1,2111,"QRpaymentConfirmationSuccessful",2," Anda berhasil bayar $(BillerName) nomor $(InvoiceNumber) $(Currency) $(NominalAmount) dengan biaya $(Currency) $(serviceCharge). REF: $(TransferID)",null,1,0,now(),null,null,1);

INSERT INTO `notification` (`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,`Language`,`Status`,`StatusTime`,`AccessCode`,`SMSNotificationCode`,`CompanyID`) VALUES (now(),"System",now(),"System",0,1,2111,"QRpaymentConfirmationSuccessful",4," Anda berhasil bayar $(BillerName) nomor $(InvoiceNumber) $(Currency) $(NominalAmount) dengan biaya $(Currency) $(serviceCharge). REF: $(TransferID)",null,1,0,now(),null,null,1);

INSERT INTO `notification` (`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,`Language`,`Status`,`StatusTime`,`AccessCode`,`SMSNotificationCode`,`CompanyID`) VALUES (now(),"System",now(),"System",0,1,2111,"QRpaymentConfirmationSuccessful",8," Anda berhasil bayar $(BillerName) nomor $(InvoiceNumber) $(Currency) $(NominalAmount) dengan biaya $(Currency) $(serviceCharge). REF: $(TransferID)",null,1,0,now(),null,null,1);

INSERT INTO `notification` (`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,`Language`,`Status`,`StatusTime`,`AccessCode`,`SMSNotificationCode`,`CompanyID`) VALUES (now(),"System",now(),"System",0,1,2111,"QRpaymentConfirmationSuccessful",16," Anda berhasil bayar $(BillerName) nomor $(InvoiceNumber) $(Currency) $(NominalAmount) dengan biaya $(Currency) $(serviceCharge). REF: $(TransferID)",null,1,0,now(),null,null,1);


DELETE FROM `notification` where Code=2112;

-- English 2112

INSERT INTO `notification` (`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,`Language`,`Status`,`StatusTime`,`AccessCode`,`SMSNotificationCode`,`CompanyID`) VALUES (now(),"System",now(),"System",0,1,2112,"QRPaymentCompletedToReceiver",1,"REF ID: $(TransferID). Received Payment from customer $(SenderMDN), $(InvoiceNumber) with $(Currency) $(Amount).",null,0,0,now(),null,null,1);

INSERT INTO `notification` (`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,`Language`,`Status`,`StatusTime`,`AccessCode`,`SMSNotificationCode`,`CompanyID`) VALUES (now(),"System",now(),"System",0,1,2112,"QRPaymentCompletedToReceiver",2,"REF ID: $(TransferID). Received Payment from customer $(SenderMDN), $(InvoiceNumber) with $(Currency) $(Amount).",null,0,0,now(),null,null,1);

INSERT INTO `notification` (`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,`Language`,`Status`,`StatusTime`,`AccessCode`,`SMSNotificationCode`,`CompanyID`) VALUES (now(),"System",now(),"System",0,1,2112,"QRPaymentCompletedToReceiver",4,"REF ID: $(TransferID). Received Payment from customer $(SenderMDN), $(InvoiceNumber) with $(Currency) $(Amount).",null,0,0,now(),null,null,1);

INSERT INTO `notification` (`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,`Language`,`Status`,`StatusTime`,`AccessCode`,`SMSNotificationCode`,`CompanyID`) VALUES (now(),"System",now(),"System",0,1,2112,"QRPaymentCompletedToReceiver",8,"REF ID: $(TransferID). Received Payment from customer $(SenderMDN), $(InvoiceNumber) with $(Currency) $(Amount).",null,0,0,now(),null,null,1);

INSERT INTO `notification` (`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,`Language`,`Status`,`StatusTime`,`AccessCode`,`SMSNotificationCode`,`CompanyID`) VALUES (now(),"System",now(),"System",0,1,2112,"QRPaymentCompletedToReceiver",16,"REF ID: $(TransferID). Received Payment from customer $(SenderMDN), $(InvoiceNumber) with $(Currency) $(Amount).",null,0,0,now(),null,null,1);

-- Bahasa 2112

INSERT INTO `notification` (`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,`Language`,`Status`,`StatusTime`,`AccessCode`,`SMSNotificationCode`,`CompanyID`) VALUES (now(),"System",now(),"System",0,1,2112,"QRPaymentCompletedToReceiver",1,"REF ID: $(TransferID). Received Payment from customer $(SenderMDN), $(InvoiceNumber) with $(Currency) $(Amount).",null,1,0,now(),null,null,1);

INSERT INTO `notification` (`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,`Language`,`Status`,`StatusTime`,`AccessCode`,`SMSNotificationCode`,`CompanyID`) VALUES (now(),"System",now(),"System",0,1,2112,"QRPaymentCompletedToReceiver",2,"REF ID: $(TransferID). Received Payment from customer $(SenderMDN), $(InvoiceNumber) with $(Currency) $(Amount).",null,1,0,now(),null,null,1);

INSERT INTO `notification` (`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,`Language`,`Status`,`StatusTime`,`AccessCode`,`SMSNotificationCode`,`CompanyID`) VALUES (now(),"System",now(),"System",0,1,2112,"QRPaymentCompletedToReceiver",4,"REF ID: $(TransferID). Received Payment from customer $(SenderMDN), $(InvoiceNumber) with $(Currency) $(Amount).",null,1,0,now(),null,null,1);

INSERT INTO `notification` (`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,`Language`,`Status`,`StatusTime`,`AccessCode`,`SMSNotificationCode`,`CompanyID`) VALUES (now(),"System",now(),"System",0,1,2112,"QRPaymentCompletedToReceiver",8,"REF ID: $(TransferID). Received Payment from customer $(SenderMDN), $(InvoiceNumber) with $(Currency) $(Amount).",null,1,0,now(),null,null,1);

INSERT INTO `notification` (`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,`Language`,`Status`,`StatusTime`,`AccessCode`,`SMSNotificationCode`,`CompanyID`) VALUES (now(),"System",now(),"System",0,1,2112,"QRPaymentCompletedToReceiver",16,"REF ID: $(TransferID). Received Payment from customer $(SenderMDN), $(InvoiceNumber) with $(Currency) $(Amount).",null,1,0,now(),null,null,1);

Update notification set isActive=0 where Code=2112;
