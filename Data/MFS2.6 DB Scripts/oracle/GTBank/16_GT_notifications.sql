update notification set text='Dear $(SenderFirstName) $(SenderLastName), please note that your transaction on $(TransactionDateTime) was not successful . This was due to an error that occurred while processing your request. For more info, call $(CustomerServiceShortCode). TRANS ID $(TransactionID)' where code = 0;

update notification set text='Dear $(SenderFirstName) $(SenderLastName), please note that your  Account Balance as at  $(CurrentDateTime) is $(BankAccountCurrency) $(BankAccountBalanceValue). TRANS ID $(TransactionID)' where code = 4;   

update notification set text='Dear $(SenderFirstName) $(SenderLastName), please note that your transaction on $(TransactionDateTime) was not successful. This was due to an error that occurred while processing your request. Info, call $(CustomerServiceShortCode). TRANS ID $(TransactionID)' where code = 5;   

update notification set text='Dear $(SenderFirstName) $(SenderLastName), please note that your transaction on $(TransactionDateTime) was not successful. This is due to an error that occurred while processing your request. For more info, call $(CustomerServiceShortCode). TRANS ID $(TransactionID)' where code = 6;   

update notification set text='Dear $(SenderFirstName) $(SenderLastName), please note that your account is currently inactive. Kindly activate your account or call Customer Care: $(CustomerServiceShortCode) to speak with an agent.' where code = 7;   

update notification set text='Dear User, You are currently not registered as a GTBank MM user. To Register, Please send your full name to $(CustomerServiceShortCode)' where code = 11;  

update notification set text='Dear $(SenderFirstName) $(SenderLastName), please note that your transaction on $(TransactionDateTime) was not successful. This was due to an error that occurred while processing your request. For more info, call $(CustomerServiceShortCode). TRANS ID $(TransactionID)' where code = 12;  

update notification set text='Dear $(SenderFirstName) $(SenderLastName), please note that your transaction on $(TransactionDateTime) was not successful. This was due to an error that occurred while processing your request. For more info, call $(CustomerServiceShortCode). TRANS ID $(TransactionID)' where code = 14;  

update notification set text='Dear $(SenderFirstName) $(SenderLastName), please note that your transaction on $(TransactionDateTime) was not successful. This was due to an error that occurred while processing your request. For more info, call $(CustomerServiceShortCode). TRANS ID $(TransactionID)' where code = 15;  

update notification set text='Dear $(SenderFirstName) $(SenderLastName), please note that your transaction was not successful due to insufficient funds in your account. kindly fund your GTBank MM account and try again.' where code = 16;  

update notification set text='Dear $(SenderFirstName) $(SenderLastName), we are sorry to inform you that your transaction on $(TransactionDateTime) was not successful due to maximum balance exceeded. Please retry using a suitable amount. To speak to an agent, call $(CustomerServiceShortCode)  TRANS ID $(TransactionID)' where code = 17;  

update notification set text='Dear $(SenderFirstName) $(SenderLastName), we are sorry to inform you that your transaction on $(TransactionDateTime) was not successful because you have exceeded the maximum number of transactions for the day. Please try again tomorrow. To speak to an agent, call $(CustomerServiceShortCode)  TRANS ID $(TransactionID)' where code = 18;  

update notification set text='Dear $(SenderFirstName) $(SenderLastName), we are sorry to inform you that your transaction on $(TransactionDateTime) was not successful because you have exceeded the maximum number of transactions for the week. Please try again next week. To speak to an agent, $(CustomerServiceShortCode)  TRANS ID $(TransactionID)' where code = 19;  

update notification set text='Dear $(SenderFirstName) $(SenderLastName), we are sorry to inform you that your transaction on $(TransactionDateTime) was not successful because you have exceeded the maximum number of transactions for the month. Please try again next month. To speak to an agent, Call $(CustomerServiceShortCode)  TRANS ID $(TransactionID)' where code = 20;  

update notification set text='Dear $(SenderFirstName) $(SenderLastName), we are sorry to inform you that your transaction on $(TransactionDateTime) was not successful because you have exceeded the maximum  transaction amount for the day. Please Please try again tomorrow. To speak to an agent, call $(CustomerServiceShortCode)  TRANS ID $(TransactionID)' where code = 21;  

update notification set text='Dear $(SenderFirstName) $(SenderLastName), we are sorry to inform you that your transaction on $(TransactionDateTime) was not successful because you have exceeded the maximum  transaction amount for the week. Please Please try again next week. To speak to an agent, call $(CustomerServiceShortCode)  TRANS ID $(TransactionID)' where code = 22;  

update notification set text='Dear $(SenderFirstName) $(SenderLastName), we are sorry to inform you that your transaction on $(TransactionDateTime) was not successful because you have exceeded the maximum  transaction amount for the month. Please try again next month. To speak to an agent, call $(CustomerServiceShortCode)  TRANS ID $(TransactionID)' where code = 23;  

update notification set text='Dear $(SenderFirstName) $(SenderLastName), please note that your transaction on $(TransactionDateTime) was not successful. This is due to an error that occurred while processing your request. For more info, call $(CustomerServiceShortCode). TRANS ID $(TransactionID)' where code = 24;  

update notification set text='Dear $(SenderFirstName) $(SenderLastName), please note that you have successfully changed your GTBank MM PIN on $(TransactionDateTime).' where code = 26;  

update notification set text='Dear $(SenderFirstName) $(SenderLastName), we are sorry to inform you that your account has been blocked. Please call Customer Care: $(CustomerServiceShortCode) for more information.' where code = 27;  

update notification set text='Dear $(SenderFirstName) $(SenderLastName), please note that your request is being processed. Thank you. TRANS ID $(TransactionID)' where code = 28;  

update notification set text='Dear $(SenderFirstName) $(SenderLastName), please note that you have entered an Incorrect GTBank MM PIN. Please try again. You have $(NumberOfTriesLeft) Tries left. TRANS ID $(TransactionID)' where code = 29;

update notification set text='Dear $(SenderFirstName) $(SenderLastName), we are sorry to inform you that your transaction on $(TransactionDateTime) was not successful because your Dompet service is inactive. To activate,  go to Activate M-Commerce menu. For more info, please call $(CustomerServiceShortCode). TRANS ID $(TransactionID)' where code = 30;  

update notification set text='Dear $(SenderFirstName) $(SenderLastName), we are sorry to inform you that your transaction on $(TransactionDateTime) was not successful because your $(PocketType) service has not yet been activated. To activate, go to Activate M-Commerce menu. For more info, please call $(CustomerServiceShortCode). TRANS ID $(TransactionID)' where code = 32;  

update notification set text='Dear $(SenderFirstName) $(SenderLastName), please note that the history of your last 3 transactions is being processed. Thank you. TRANS ID $(TransactionID)' where code = 36;  

update notification set text='Dear $(SenderFirstName) $(SenderLastName), please note that your transaction on $(TransactionDateTime) was not successful. This was due to an error that occurred while processing your request. For more info, please call $(CustomerServiceShortCode). TRANS ID $(TransactionID)' where code = 37;  

update notification set text='Dear $(SenderFirstName) $(SenderLastName), please note that the history of your last 3 transactions is being processed. Thank you. TRANS ID $(TransactionID)' where code = 40;  

update notification set text='Dear $(SenderFirstName) $(SenderLastName), please note that your balance enquiry is being processed. Thank you' where code = 41;  

update notification set text='Dear $(SenderFirstName) $(SenderLastName), please note that you have successfully reset your  GTBank MM PIN on $(TransactionDateTime).' where code = 47;  

update notification set text='Congratulations $(SenderFirstName) $(SenderLastName), you have successfully activated your GTBank Mobile Money account. Enjoy banking at your finger tips. For more info, please call $(CustomerServiceShortCode). TRANS ID $(TransactionID)' where code = 52;  

update notification set text='Dear $(SenderFirstName) $(SenderLastName), please note that your Account has already been activated.  For more info, please call $(CustomerServiceShortCode). TRANS ID $(TransactionID)' where code = 54;  

update notification set text='Dear $(SenderFirstName) $(SenderLastName), please note that your transaction on $(TransactionDateTime) was not successful. This was due to an error that occurred while processing your request. For more info, call $(CustomerServiceShortCode). TRANS ID $(TransactionID)' where code = 55;  

update notification set text='Dear $(SenderFirstName) $(SenderLastName), please note that your GTBank account transfer to $(BankName) bank account is being processed. Thank you' where code = 61;  

update notification set text='Dear $(SenderFirstName) $(SenderLastName), please note that your GTBank account transfer to $(BankName) bank account on $(TransactionDateTime) was not successful. This is due to an error that occurred while processing your request. For more info, please call $(CustomerServiceShortCode). TRANS ID $(TransactionID)' where code = 62;

update notification set text='$(BankName) $(BankAccountTransactionDate) $(BankAccountTransactionType) $(BankAccountCurrency) $(BankAccountTransactionAmount) ' where code = 67;  

update notification set text='Dear $(SenderFirstName) $(SenderLastName), please note that your balance enquiry on $(TransactionDateTime) was not successful. This was due to an error that occurred while processing your request. For more info, call $(CustomerServiceShortCode). TRANS ID $(TransactionID)' where code = 68;  

update notification set text='Dear $(SenderFirstName) $(SenderLastName), please note that your transaction on $(TransactionDateTime) was not successful. This was due to an error that occurred while processing your request. For more info, call $(CustomerServiceShortCode). TRANS ID $(TransactionID)' where code = 69;  

update notification set text='Dear $(SenderFirstName) $(SenderLastName), please note that your transaction on $(TransactionDateTime) was not successful. This was due to an error that occurred while processing your request. For more info, call $(CustomerServiceShortCode). TRANS ID $(TransactionID)' where code = 70;  

update notification set text='Dear $(SenderFirstName) $(SenderLastName), please note that your transaction on $(TransactionDateTime) was not successful. This is due to an error that occurred while processing your request. For more info, call $(CustomerServiceShortCode). TRANS ID $(TransactionID)' where code = 71;  

update notification set text='Dear $(SenderFirstName) $(SenderLastName), please note that your transfer $(Currency) $(Amount) to $(ReceiverMDN) - $(ReceiverAccountName) and Service Charge $(Currency) $(serviceCharge)  has been processed. ParentTransactionID $(ParentTransactionID) REF: $(TransferID)' where code = 72;  

update notification set text='Dear $(SenderFirstName) $(SenderLastName), please note that your transaction on $(TransactionDateTime) was not successful. This was due to an error that occurred while processing your request. For more info, call $(CustomerServiceShortCode). TRANS ID $(TransactionID)' where code = 73;  

update notification set text='Dear $(SenderFirstName) $(SenderLastName), you have successfully transferred $(Currency) $(Amount) to $(ReceiverMDN) and  Service Charge $(Currency) $(serviceCharge). Transaction ID: $(TransferID).' where code = 81;  

update notification set text='Dear $(SenderFirstName) $(SenderLastName), please note that your transaction on $(TransactionDateTime) was not successful. This is due to an error that occurred while processing your request. For more info, call $(CustomerServiceShortCode).TRANS ID $(TransactionID) ' where code = 82;  

update notification set text='Dear $(SenderFirstName) $(SenderLastName), please note that your transaction on $(TransactionDateTime) was not successful. This was due to an error that occurred while processing your request. For more info, call $(CustomerServiceShortCode). TRANS ID $(TransactionID)' where code = 94;  

update notification set text='Dear $(SenderFirstName) $(SenderLastName), please note that your transaction on $(TransactionDateTime) was not successful. This was due to an error that occurred while processing your request. For more info, call $(CustomerServiceShortCode). TRANS ID $(TransactionID)' where code = 95;  

update notification set text='Dear $(SenderFirstName) $(SenderLastName), please note that your transaction on $(TransactionDateTime) was not successful. This was due to an error that occurred while processing your request. For more info, call $(CustomerServiceShortCode). TRANS ID $(TransactionID)' where code = 96;  

update notification set text='Dear $(SenderFirstName) $(SenderLastName), please note that your GTBank MM activation was not successful because you exceeded the number of digits for your PIN. Please note that your PIN must be $(PINMaximumDigit) digits long. Kindly try again. For info, call $(CustomerServiceShortCode).' where code = 99;  

update notification set text='Dear $(SenderFirstName) $(SenderLastName), we are sorry to inform you that your transaction on $(TransactionDateTime) was not successful because the amount you entered exceeds the maximum transaction limit $(Currency) $(MAXTxnLimit). Please call $(CustomerServiceShortCode) for more information. TRANS ID $(TransactionID)' where code = 106; 

update notification set text='Dear $(SenderFirstName) $(SenderLastName), we are sorry to inform you that your transaction on $(TransactionDateTime) was not successful because the amount you entered is less than the minimum transaction limit NGN $(MINTxnLimit). Please call $(CustomerServiceShortCode) for more information. TRANS ID $(TransactionID)' where code = 107; 

update notification set text='Dear $(SenderFirstName) $(SenderLastName), please note that your transaction is being processed. ' where code = 110; 

update notification set text='Service unavailable at the moment. Please try again later.' where code = 111; 

update notification set text='Dear $(SenderFirstName) $(SenderLastName), please note that your transaction on $(TransactionDateTime) was not successful. This was due to an error that occurred while processing your request. For more info, call $(CustomerServiceShortCode). TRANS ID $(TransactionID)' where code = 114; 

update notification set text='ERROR: Inactive Account. The $(ReceiverAccountName) account $(DestMDN) is currently inactive. Request $(ReceiverAccountName) to activate account.' where code = 128; 

update notification set text='ERROR: Blocked Account. The $(SenderFirstName) $(SenderLastName) account $(DestMDN) is blocked. Request $(ReceiverFirstName) $(ReceiverLastName) to contact Customer Care at: $(CustomerServiceShortCode)' where code = 129; 

update notification set text='Invalid file type. Please retry using the correct file type.' where code = 152; 

update notification set text='Invalid file format. Please retry using the correct file format' where code = 153; 

update notification set text='Source number in file does not exist. Please retry using the correct number' where code = 154; 

update notification set text='Dear $(SenderFirstName) $(SenderLastName), please note that your transaction on $(TransactionDateTime) was not successful. This was due to an error that occurred while processing your request. For more info, call $(CustomerServiceShortCode).TRANS ID $(TransactionID)' where code = 155; 

update notification set text='Dear $(SenderFirstName) $(SenderLastName), please note that your transaction on $(TransactionDateTime) was not successful. This was due to an error that occurred while processing your request. For more info, call $(CustomerServiceShortCode). TRANS ID $(TransactionID)' where code = 156; 

update notification set text='Dear $(SenderFirstName) $(SenderLastName), please note that your Bulk upload transaction on $(TransactionDateTime)  was successful.  TRANS ID $(TransactionID)' where code = 157; 

update notification set text='Dear $(SenderFirstName) $(SenderLastName), please note that your transaction on $(TransactionDateTime) was not successful. This was due to an error that occurred while processing your request. For more info, call $(CustomerServiceShortCode). TRANS ID $(TransactionID)' where code = 174; 

update notification set text='Dear $(SenderFirstName) $(SenderLastName), please note that this bulk transaction cannot be completed because the file has already been uploaded.' where code = 176; 

update notification set text='Dear $(SenderFirstName) $(SenderLastName), please note that your request to change your GTBank MM PIN on $(TransactionDateTime) was not successful. Please try again . For more info, please call $(CustomerServiceShortCode).  TRANS ID $(TransactionID)' where code = 209; 

update notification set text='Dear $(SenderFirstName) $(SenderLastName), we are sorry to inform you that your transaction on $(TransactionDateTime) was not successful because you entered an invalid amount. Please try again with the correct amount. For more info, call $(CustomerServiceShortCode). TRANS ID $(TransactionID)' where code = 212; 

update notification set text='Dear $(SenderFirstName) $(SenderLastName), we are sorry to inform you that your transaction on $(TransactionDateTime) was not successful due to insufficient funds.  For more Info,  please call $(CustomerServiceShortCode). TRANS ID $(TransactionID)' where code = 216; 

update notification set text='Dear $(SenderFirstName) $(SenderLastName), we are sorry to inform you that your request to check your last 3 transactions on $(TransactionDateTime) was not successful. Please try again . For more info, call $(CustomerServiceShortCode).' where code = 217; 

update notification set text='Dear $(SenderFirstName) $(SenderLastName), we are sorry to inform you that your GTBank MM wallet has been suspended on $(TransactionDateTime). To reactivate, please call $(CustomerServiceShortCode). ' where code = 225; 

update notification set text='Dear $(SenderFirstName) $(SenderLastName), please note that your new GTBank MM PIN is $(SubscriberPIN). For security reasons, kindly change your new PIN immediately by selecting change pin' where code = 277; 

update notification set text='Dear $(SenderFirstName) $(SenderLastName), you have successfully transferred $(Currency) $(Amount) to $(ReceiverMDN) and  the Service Charge is $(Currency) $(serviceCharge). Your balance as at $(TransactionDateTime) is $(Currency) $(CommodityBalanceValue). TRANS ID $(TransactionID)' where code = 293; 

update notification set text='Dear $(ReceiverFirstName) $(ReceiverLastName), please note that $(SenderMDN) has transferred $(Currency) $(Amount) to your GTBank MM account. Your balance as at $(TransactionDateTime) is $(Currency) $(DestinationMDNBalance). Transaction ID: $(TransferID)' where code = 294; 

update notification set text='Dear $(SenderFirstName) $(SenderLastName), we are sorry to inform you that your transfer on  $(TransactionDateTime) was not successful because you cannot transfer to yourself. Please try again using a valid number. For more info, please call $(CustomerServiceShortCode). TRANS ID $(TransactionID)' where code = 295; 

update notification set text='Dear $(SenderFirstName) $(SenderLastName), please note that you have successfully deposited a cash sum of $(Currency) $(Amount) into $(ReceiverMDN) account and the Service Charge is $(Currency) $(serviceCharge). Your current balance is $(Currency) $(CommodityBalanceValue) on $(TransactionDateTime). TRANS ID $(TransactionID)' where code = 296; 

update notification set text='Dear $(ReceiverFirstName) $(ReceiverLastName), please note that your account has been credited with the sum of $(Currency) $(Amount)by $(SenderMDN) . Your current balance is $(Currency) $(DestinationMDNBalance) on $(TransactionDateTime). Transaction ID: $(TransferID)' where code = 297; 

update notification set text='Dear $(SenderFirstName) $(SenderLastName), please note that you have successfully withdrawn the sum of $(Currency) $(Amount) and the Service Charge is $(Currency) $(serviceCharge). Your current balance is $(Currency) $(CommodityBalanceValue) on $(TransactionDateTime). Transaction ID: $(TransferID)' where code = 298; 

update notification set text='Dear $(ReceiverFirstName) $(ReceiverLastName), please note that you have successfully paid the cash sum of $(Currency) $(Amount) to $(SenderFirstName) $(SenderLastName) $(SenderMDN). Your current balance is $(Currency) $(DestinationMDNBalance) on $(TransactionDateTime). Transaction ID: $(TransferID)' where code = 299; 

update notification set text='Dear $(SenderFirstName) $(SenderLastName), we are sorry to inform you that your transfer on $(TransactionDateTime) was not successful because your GTBank MM service is temporarily suspended. To unlock, please call $(CustomerServiceShortCode). TRANS ID $(TransactionID)' where code = 300; 

update notification set text='Dear $(SenderFirstName) $(SenderLastName), we are sorry to inform you that your transaction on $(TransactionDateTime) was not successful because the receiver''s GTBank MM service is temporarily suspended. TRANS ID $(TransactionID)' where code = 301;

update notification set text='Dear $(SenderFirstName) $(SenderLastName) please note that you have successfully transferred the sum of N$(Amount) on $(TransactionDateTime) to $(ReceiverMDN) and  the Service Charge is $(Currency) $(serviceCharge). Transaction ID: $(TransferID)' where code = 305; 

update notification set text='Dear $(ReceiverFirstName) $(ReceiverLastName), please note that you have received $(Currency) $(Amount) on $(TransactionDateTime) from $(BankName). Transaction ID: $(TransferID)' where code = 306; 

update notification set text='Dear $(SenderFirstName) $(SenderLastName), we are pleased to inform you that your transfer of $(Currency) $(Amount) to $(ReceiverBankAccount) on $(TransactionDateTime) was successful. The Service Charge is $(Currency) $(serviceCharge).Transaction ID: $(TransferID)' where code = 307;

update notification set text='Dear $(SenderFirstName) $(SenderLastName), please note that your transaction on $(TransactionDateTime) was not successful. This is due to an error that occurred while processing your request. For more info, call $(CustomerServiceShortCode). TRANS ID $(TransactionID) ' where code = 531; 

update notification set text='Dear $(SenderFirstName) $(SenderLastName),  We are pleased to inform you that you have been successfully registered for the GTBank MM service. To activate your account, please use your Activation Code: $(OneTimePin) ' where code = 626; 

update notification set text='Dear $(SenderFirstName) $(SenderLastName), We are pleased to inform you that your GTBank MM account has been successfully upgraded to $(KycLevel) status.  Thank you' where code = 627; 

update notification set text='Dear $(SenderFirstName) $(SenderLastName), please note that the request to upgrade your account to $(KycLevel) has been declined.  Please call $(CustomerServiceShortCode) to speak with an agent. ' where code = 628; 

update notification set text='Dear $(SenderFirstName) $(SenderLastName), please note your one Time Password has Expired.  ' where code = 629; 

update notification set text='Dear $(SenderFirstName) $(SenderLastName), please note that your registration for the GTBank MM is being processed. Thank you ' where code = 635; 

update notification set text='Dear $(SenderFirstName) $(SenderLastName), please note that your account has been blocked because you have exceeded the maximum number of PIN tries. To reset your GTBank MM PIN, Please call Customer Care: $(CustomerServiceShortCode) for more information.' where code = 2006;

update notification set text='Dear $(SenderFirstName) $(SenderLastName), please note that you entered an Incorrect Activation Code. Kindly verify your Activation code and try again. Thank you' where code = 2010;

update notification set text='Dear $(SenderFirstName) $(SenderLastName), please note that your your new otp is $(OneTimePin). Thank you' where code = 655; 

update notification set text='Dear $(SenderFirstName) $(SenderLastName), To activate, please click the link $(AppURL)/activation.htm?service=$(Service)&' || 'userid=$(SourceMDN)&' || 'otp=$(OneTimePin)' where code = 657;

update notification set text='Dear $(SenderFirstName) $(SenderLastName), Do you want to top up $(ReceiverMDN) with N$(Amount). Transaction ID: $(TransferID) ' where code = 660; 

update notification set text='Dear $(SenderFirstName) $(SenderLastName), please note that your Topup of N$(Amount) on $(ReceiverMDN) was successful. The Service Charge is N$(ServiceCharge) and your closing Balance is N$(CommodityBalanceValue). Thank you. Transaction ID: $(TransferID) ' where code = 661; 

update notification set text='Dear $(SenderFirstName) $(SenderLastName), please note that your recharge on $(ReceiverMDN) was not sucessful. The amount will be reverted in 24 hours. Thank you. TRANS ID $(TransactionID).' where code = 662; 

update notification set text='Dear $(SenderFirstName) $(SenderLastName), please note that your New pin is not strong enough. Kindly try another one' where code = 663; 

update notification set text='Dear $(SenderFirstName) $(SenderLastName), please note that you have been debited with the sum of $(Currency) $(Amount) as Reversal of Transaction TRANS ID $(OriginalTransferID)   .Thank you' where code = 666; 

update notification set text='Dear $(ReceiverFirstName) $(ReceiverLastName), please note that you have been credited with the sum of $(Currency) $(Amount) as a Reversal of Transaction TRANS ID $(OriginalTransferID)  . Thank you' where code = 667; 

update notification set text='Dear $(CustomerName), please note that your reversal of TRANS ID $(OriginalTransferID)  has been initiated.' where code = 668; 

update notification set text='Dear $(CustomerName), please note that your Reversal of TRANS ID $(OriginalTransferID) was not successful.' where code = 669; 

update notification set text='Dear $(CustomerName), please note that your Reversal of TRANS ID $(OriginalTransferID) was not successful.' where code = 670; 

update notification set text='Dear $(SenderFirstName) $(SenderLastName), we are sorry to inform you that your transfer to $(BankName) was not successful.  Please call $(CustomerServiceShortCode) to speak with an agent. TRANS ID $(TransactionID) ' where code = 675; 

update notification set text='Dear $(SenderFirstName) $(SenderLastName), we are sorry to inform you that your transfer to $(BankAccountNumber) was not successful.  Please call $(CustomerServiceShortCode)to speak with an agent. Transaction ID: $(TransferID) ' where code = 682; 

update notification set text='Dear $(SenderFirstName) $(SenderLastName), please note that your Transfer to TRANS ID $(TransactionID) could not be completed. The financial Insititution is not availbale, please try again later. TRANS ID $(TransactionID) ' where code = 683; 

update notification set text='Dear $(SenderFirstName) $(SenderLastName), kindly confirm if you requested to transfer $(Currency) $(Amount) to $(BankAccountNumber) with Service Charge $(Currency) $(serviceCharge)  ParentTransactionID $(ParentTransactionID) REF: $(TransferID)' where code = 684; 

update notification set text='Transaction ID: $(TransferID). Dear $(SenderFirstName) $(SenderLastName), please note that you have successfully transferred $(Currency) $(Amount) to $(BankAccountNumber) and  the Service Charge is $(Currency) $(serviceCharge)' where code = 685; 

update notification set text='Dear $(SenderFirstName) $(SenderLastName), please note that we could not process your request for a transfer to $(DestMDN) because the reciever status is invalid.' where code = 686; 

update notification set text='Dear $(SenderFirstName) $(SenderLastName), please provide First and Last Name to process your request for a transfer to $(DestMDN).' where code = 687; 

update notification set text='Dear Mobile User,  please note that the MDN used is currently not registered as a GTBank MM. Visit nearest GT branch to learn more.' where code = 671; 

update notification set text='Dear Mobile User, please note that your phone number $(SourceMDN) is currently not registered for GTBank MM. To Register, Please send your full name to $(CustomerServiceShortCode) ' where code = 672; 

update notification set text='Dear Mobile User, please note that your phone number $(SourceMDN) is currently not registered  for GTBank MM. To Register, Please send your full name to $(CustomerServiceShortCode) ' where code = 673; 

update notification set text='Dear $(SenderFirstName) $(SenderLastName), kindly confirm that your transfer request of N$(Amount) to $(ReceiverMDN) will attract an extra N$(Servicecharge) charge to complete the transaction. Transaction ID: $(TransferID) ' where code = 676; 

update notification set text='Dear $(SenderFirstName) $(SenderLastName), please note that you have successfully transferred $(Currency) $(Amount) to $(ReceiverMDN) and the Charge is $(Currency) $(serviceCharge). Your Balance as at $(TransactionDateTime) is $(Currency) $(CommodityBalanceValue).Transaction ID: $(TransferID)' where code = 678; 

update notification set text='Dear $(ReceiverFirstName) $(ReceiverLastName), please note that you have received $(Currency) $(Amount) from $(SenderMDN).Fund access code is $(OneTimePin). TRANS ID $(TransactionID) ' where code = 679; 

update notification set text='Dear $(SenderFirstName) $(SenderLastName), please note that you have successfully Cashed-Out from Agent $(PartnerCode) with $(Currency) $(Amount).Transaction ID: $(TransferID)' where code = 680; 

update notification set text='Dear $(ReceiverFirstName) $(ReceiverLastName), please note that you have successfully cashed-out to $(SenderFirstName) $(SenderLastName) $(SenderMDN) with $(Currency) $(Amount). Your balance as at $(TransactionDateTime) is $(Currency) $(DestinationMDNBalance). Transaction ID: $(TransferID)' where code = 681; 

update notification set text='Transaction ID: $(TransferID). Dear $(SenderFirstName) $(SenderLastName), please note that your Bulk Transfer Request $(BulkTransferID) processed on $(TransactionDateTime) has been completed.' where code = 691; 

update notification set text='Transaction ID: $(TransferID). Dear $(ReceiverFirstName) $(ReceiverLastName), please note that you have received $(Currency) $(Amount) as part of a Bulk Transfer from $(SenderMDN). Your balance as at $(TransactionDateTime) is $(Currency) $(DestinationMDNBalance).' where code = 693; 

update notification set text='Dear $(CustomerName), please note that your Fund access code is $(OneTimePin)' where code = 695; 

update notification set text='Dear $(CustomerName), please note that your Bulk Transfer Request $(BulkTransferID) was not successful.' where code = 696; 

update notification set text='REF ID: $(TransferID). Dear $(SenderFirstName) $(SenderLastName), please note that your payment to Biller $(BillerCode) $(InvoiceNumber) with $(Currency)$(Amount),ServiceCharge $(Currency)$(serviceCharge) is successful' where code = 697; 

update notification set text='Dear $(SenderFirstName) $(SenderLastName), please note that your payment to biller $(BillerCode) towards invoice number $(InvoiceNumber) is pending. Confirmation will be sent in 24 hours.' where code = 698; 

update notification set text='Dear $(SenderFirstName) $(SenderLastName), please note that the amount you entered is Invalid . Kindly enter an amount  between $(minAmount) $(Currency) and $(maxAmount) $(Currency).' where code = 699; 

update notification set text='Dear $(SenderFirstName) $(SenderLastName), You requested to cash out amount $(Currency) $(Amount) from ATM and Service Charge $(Currency) $(serviceCharge).' where code = 708; 

commit;
