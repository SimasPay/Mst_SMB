
update notification set text='Dear $(SenderFirstName) $(SenderLastName), Sorry, An error occurred while processing your request. REF: $(TransactionID)' where code = 0;

update notification set text='Dear $(SenderFirstName) $(SenderLastName), please note that your  Account Balance as at  $(CurrentDateTime) is $(BankAccountCurrency) $(BankAccountBalanceValue). TRANS ID $(TransactionID)' where code = 4;   

update notification set text='Dear $(SenderFirstName) $(SenderLastName), An error occurred while processing your request. REF: $(TransactionID)' where code = 5;   

update notification set text='Dear $(SenderFirstName) $(SenderLastName), Incomplete Details. REF: $(TransactionID)' where code = 6;   

update notification set text='Dear $(SenderFirstName) $(SenderLastName), Sorry, your account is currently INACTIVE. Please call $(CustomerServiceShortCode) for assistance.' where code = 7;   

update notification set text='Dear $(SenderFirstName) $(SenderLastName), You are currently not registered as a FortisBank MM user. To Register, Please send your full name to $(CustomerServiceShortCode)' where code = 11;  

update notification set text='Dear $(SenderFirstName) $(SenderLastName), Sorry, An error occurred while processing your request. REF: $(TransactionID)' where code = 12;  

update notification set text='Dear $(SenderFirstName) $(SenderLastName), Sorry, An error occurred while processing your request. REF: $(TransactionID)' where code = 14;  

update notification set text='Dear $(SenderFirstName) $(SenderLastName),Sorry, An error occurred while processing your request. REF: $(TransactionID)' where code = 15;  

update notification set text='Dear $(SenderFirstName) $(SenderLastName),Insufficient Funds. Kindly fund your account from an Fortis agent location and try again.' where code = 16;  

update notification set text='Dear $(SenderFirstName) $(SenderLastName),Sorry, this transaction exceeds the set limit on your account, please retry using a suitable amount REF: $(TransactionID)' where code = 17;  

update notification set text='Dear $(SenderFirstName) $(SenderLastName),Sorry, Transaction Failed. You have exceeded the maximum number of daily transactions. ' where code = 18;  

update notification set text='Dear $(SenderFirstName) $(SenderLastName), we are sorry to inform you that your transaction on $(TransactionDateTime) was not successful because you have exceeded the maximum number of transactions for the week. Please try again next week. To speak to an agent, $(CustomerServiceShortCode)  TRANS ID $(TransactionID)' where code = 19;  

update notification set text='Dear $(SenderFirstName) $(SenderLastName), we are sorry to inform you that your transaction on $(TransactionDateTime) was not successful because you have exceeded the maximum number of transactions for the month. Please try again next month. To speak to an agent, Call $(CustomerServiceShortCode)  TRANS ID $(TransactionID)' where code = 20;  

update notification set text='Dear $(SenderFirstName) $(SenderLastName), we are sorry to inform you that your transaction on $(TransactionDateTime) was not successful because you have exceeded the maximum  transaction amount for the day. Please Please try again tomorrow. To speak to an agent, call $(CustomerServiceShortCode)  TRANS ID $(TransactionID)' where code = 21;  

update notification set text='Dear $(SenderFirstName) $(SenderLastName), we are sorry to inform you that your transaction on $(TransactionDateTime) was not successful because you have exceeded the maximum  transaction amount for the week. Please Please try again next week. To speak to an agent, call $(CustomerServiceShortCode)  TRANS ID $(TransactionID)' where code = 22;  

update notification set text='Dear $(SenderFirstName) $(SenderLastName), we are sorry to inform you that your transaction on $(TransactionDateTime) was not successful because you have exceeded the maximum  transaction amount for the month. Please try again next month. To speak to an agent, call $(CustomerServiceShortCode)  TRANS ID $(TransactionID)' where code = 23;  

update notification set text='Dear $(SenderFirstName) $(SenderLastName), Sorry, An error occurred while processing your request. REF: $(TransactionID)' where code = 24;  

update notification set text='Dear $(SenderFirstName) $(SenderLastName), PIN Changed Successfully ' where code = 26;  

update notification set text='Dear $(SenderFirstName) $(SenderLastName), Sorry, your account is Suspended. Please call $(CustomerServiceShortCode) for assistance ' where code = 27;  

update notification set text='Dear $(SenderFirstName) $(SenderLastName), Your request is being processed, Please wait. Thank you.' where code = 28;  

update notification set text='Dear $(SenderFirstName) $(SenderLastName), ERROR: Incorrect PIN, Please try again.You have $(NumberOfTriesLeft) Tries left.' where code = 29;

update notification set text='Dear $(SenderFirstName) $(SenderLastName), we are sorry to inform you that your transaction on $(TransactionDateTime) was not successful because your Dompet service is inactive. To activate,  go to Activate M-Commerce menu. For more info, please call $(CustomerServiceShortCode). TRANS ID $(TransactionID)' where code = 30;  

update notification set text='Dear $(SenderFirstName) $(SenderLastName), we are sorry to inform you that your transaction on $(TransactionDateTime) was not successful because your $(PocketType) service has not yet been activated. To activate, go to Activate M-Commerce menu. For more info, please call $(CustomerServiceShortCode). TRANS ID $(TransactionID)' where code = 32;  

update notification set text='Dear $(SenderFirstName) $(SenderLastName), Your request is being processed, Please wait. Thank you.' where code = 36;  

update notification set text='Dear $(SenderFirstName) $(SenderLastName), please note that your transaction on $(TransactionDateTime) was not successful. This was due to an error that occurred while processing your request. For more info, please call $(CustomerServiceShortCode). TRANS ID $(TransactionID)' where code = 37;  

update notification set text='Dear $(SenderFirstName) $(SenderLastName), please note that the history of your last 3 transactions is being processed. Thank you. TRANS ID $(TransactionID)' where code = 40;  

update notification set text='Dear $(SenderFirstName) $(SenderLastName), Your request is being processed, Please wait. Thank you.' where code = 41;  

update notification set text='Dear $(SenderFirstName) $(SenderLastName), PIN Changed Successfully REF: $(TransactionDateTime).' where code = 47;  

update notification set text='Congratulations, You have successfully activated your account, thank you for choosing Fortis Mobile Money' where code = 52;  

update notification set text='Dear $(SenderFirstName) $(SenderLastName), please note that your Account has already been activated.  For more info, please call $(CustomerServiceShortCode). ' where code = 54;  

update notification set text='Dear $(SenderFirstName) $(SenderLastName), An error occurred while processing your request REF: $(TransactionID)' where code = 55;  

update notification set text='Dear $(SenderFirstName) $(SenderLastName), Your request is being processed, Please wait. Thank you.' where code = 61;  

update notification set text='Dear $(SenderFirstName) $(SenderLastName), An error occurred while processing your request REF: $(TransactionID)' where code = 62;

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

update notification set text='Dear $(SenderFirstName) $(SenderLastName), PIN registration failed. PIN must be $(PINMaximumDigit) digits long. Please try again. For info, call $(CustomerServiceShortCode).' where code = 99;  

update notification set text='Dear $(SenderFirstName) $(SenderLastName), we are sorry to inform you that your transaction on $(TransactionDateTime) was not successful because the amount you entered exceeds the maximum transaction limit $(Currency) $(MAXTxnLimit). Please call $(CustomerServiceShortCode) for more information. TRANS ID $(TransactionID)' where code = 106; 

update notification set text='Dear $(SenderFirstName) $(SenderLastName), we are sorry to inform you that your transaction on $(TransactionDateTime) was not successful because the amount you entered is less than the minimum transaction limit NGN $(MINTxnLimit). Please call $(CustomerServiceShortCode) for more information. TRANS ID $(TransactionID)' where code = 107; 

update notification set text='Dear $(SenderFirstName) $(SenderLastName), Your request is being processed, Please wait. Thank you. ' where code = 110; 

update notification set text='Service unavailable at the moment. Please try again later.' where code = 111; 

update notification set text='Dear $(SenderFirstName) $(SenderLastName), please note that your transaction on $(TransactionDateTime) was not successful. This was due to an error that occurred while processing your request. For more info, call $(CustomerServiceShortCode). TRANS ID $(TransactionID)' where code = 114; 

update notification set text='The $(ReceiverAccountName) account $(DestMDN) is currently inactive. Sorry, Destination mobile number is currently inactive. Advice customer to activate account. ' where code = 128; 

update notification set text='ERROR: Blocked Account. The $(SenderFirstName) $(SenderLastName) account $(DestMDN) is blocked. Request customer to contact Customer Care on $(CustomerServiceShortCode) for assistance' where code = 129; 

update notification set text='Invalid file type. Please retry using the correct file type.' where code = 152; 

update notification set text='Invalid file format. Please retry using the correct file format' where code = 153; 

update notification set text='Source number in file does not exist. Please retry using the correct number' where code = 154; 

update notification set text='Sorry, An error occurred while processing your request Please try again' where code = 155; 

update notification set text='Dear $(SenderFirstName) $(SenderLastName), please note that your transaction on $(TransactionDateTime) was not successful. This was due to an error that occurred while processing your request. For more info, call $(CustomerServiceShortCode). TRANS ID $(TransactionID)' where code = 156; 

update notification set text='Dear $(SenderFirstName) $(SenderLastName), please note that your Bulk upload transaction on $(TransactionDateTime)  was successful.  TRANS ID $(TransactionID)' where code = 157; 

update notification set text='Dear $(SenderFirstName) $(SenderLastName), please note that your transaction on $(TransactionDateTime) was not successful. This was due to an error that occurred while processing your request. For more info, call $(CustomerServiceShortCode). TRANS ID $(TransactionID)' where code = 174; 

update notification set text='Dear $(SenderFirstName) $(SenderLastName), Sorry, You have already uploaded this file. You cannot upload the same file twice.' where code = 176; 

update notification set text='Dear $(SenderFirstName) $(SenderLastName), please note that your request to change your FortisBank MM PIN on $(TransactionDateTime) was not successful. Please try again . For more info, please call $(CustomerServiceShortCode).  TRANS ID $(TransactionID)' where code = 209; 

update notification set text='Dear $(SenderFirstName) $(SenderLastName), Invalid Amount Please re-enter amount in the correct format. REF: $(TransactionID)' where code = 212; 

update notification set text='Dear $(SenderFirstName) $(SenderLastName), we are sorry to inform you that your transaction on $(TransactionDateTime) was not successful due to insufficient funds.  For more Info,  please call $(CustomerServiceShortCode). TRANS ID $(TransactionID)' where code = 216; 

update notification set text='Dear $(SenderFirstName) $(SenderLastName), we are sorry to inform you that your request to check your last 3 transactions on $(TransactionDateTime) was not successful. Please try again . For more info, call $(CustomerServiceShortCode).' where code = 217; 

update notification set text='Dear $(SenderFirstName) $(SenderLastName), Your account is currently suspended. To reactivate, call $(CustomerServiceShortCode) for assistance. ' where code = 225; 

update notification set text='Dear $(SenderFirstName) $(SenderLastName), PIN Reset successful your new account PIN is (subscriber PIN), please change your PIN immediately to continue transactions' where code = 277; 

update notification set text='Dear $(SenderFirstName) $(SenderLastName),you have successfully transferred $(Currency) $(Amount) to $(ReceiverMDN) REF: $(TransactionID). Your balance as on $(TransactionDateTime) is $(Currency) $(CommodityBalanceValue).' where code = 293; 

update notification set text='Dear $(ReceiverFirstName) $(ReceiverLastName), Dear Customer, you have successfully recieved $(Currency) $(Amount) from $(SenderMDN) REF: $(TransferID)
your balance as at $(TransactionDateTime) is ($Amount)' where code = 294; 

update notification set text='Dear $(SenderFirstName) $(SenderLastName), we are sorry to inform you that your transfer on  $(TransactionDateTime) was not successful because you cannot transfer to yourself. Please try again using a valid number. For more info, please call $(CustomerServiceShortCode). TRANS ID $(TransactionID)' where code = 295; 

update notification set text='Dear $(SenderFirstName) $(SenderLastName), please note that you have successfully deposited a cash sum of $(Currency) $(Amount) into $(ReceiverMDN) account and the Service Charge is $(Currency) $(serviceCharge). Your current balance is $(Currency) $(CommodityBalanceValue) on $(TransactionDateTime). TRANS ID $(TransactionID)' where code = 296; 

update notification set text='Dear $(ReceiverFirstName) $(ReceiverLastName), Cash-In from  $(SenderMDN)  with $(Currency) $(Amount) completed successfully. Transaction ID: $(TransferID)  Current Balance: $ (Currency) $(CommodityBalanceValue) at $(TransactionDateTime)' where code = 297; 

update notification set text='Dear $(SenderFirstName) $(SenderLastName), Cash-Out from $(SenderMDN) with $(Currency) $(Amount) completed successfully.Transaction ID: $(TransferID) Fee: $(Currency) $(serviceCharge) Current Balance: $ (Currency) $ (CommodityBalanceValue), $(TransactionDateTime)' where code = 298; 

update notification set text='Dear $(ReceiverFirstName) $(ReceiverLastName), Cash-Out to customer $(SenderMDN) with $(Currency) $(Amount)completed successfully. Transaction ID: $(TransferID)  Current Balance: $ (Currency) $ (CommodityBalanceValue), $(TransactionDateTime)' where code = 299; 

update notification set text='Dear $(SenderFirstName) $(SenderLastName), Sorry, transaction on $(TransactionDateTime) failed. Your e-wallet is temporarily suspended. To unlock, call $(CustomerServiceShortCode). REF: $(TransactionID)' where code = 300; 

update notification set text='Dear $(SenderFirstName) $(SenderLastName), Sorry, transaction on $(TransactionDateTime) failed. Recepients e-wallet is temporarily suspended. REF: $(TransactionID)' where code = 301;

update notification set text='Dear $(SenderFirstName) $(SenderLastName), Thank you for e-money reload $(Amount) on $(TransactionDateTime) to $(ReceiverMDN)   Fee: $(Currency) $(serviceCharge). Transaction ID: $(TransferID).' where code = 305; 

update notification set text='Dear $(ReceiverFirstName) $(ReceiverLastName), You have received $(Currency) $(Amount) on $(TransactionDateTime).Transaction ID: $(TransferID). Current Balance: $ (Currency) $ (Amount) Available Balance: $ (Currency) $ (Amount)  $(TransactionDateTime) ' where code = 306; 

update notification set text='Dear $(SenderFirstName) $(SenderLastName), $(BankName) Thank you for transferring $(Currency) $(Amount) to $(ReceiverBankAccount) Transaction ID: $(TransferID)  Fee: $(Currency) $(serviceCharge) Current Balance: $ ( Currency) $ (CommodityBalanceValue)  $(TransactionDateTime)' where code = 307;

update notification set text='Dear $(SenderFirstName) $(SenderLastName), please note that your transaction on $(TransactionDateTime) was not successful. This is due to an error that occurred while processing your request. For more info, call $(CustomerServiceShortCode). TRANS ID $(TransactionID) ' where code = 531; 

update notification set text='Congratulations, Dear Subscriber, you have been successfully registered for Fortis Mobile money services. Please use your Activation Code: $(OneTimePin) to activate your account. ' where code = 626; 

update notification set text='Congratulations Dear Subscriber, your account has been successfully upgraded to $(KycLevel) status. Contact us on $(CustomerServiceShortCode) or visit the nearest Fortis MM agent to learn more.' where code = 627; 

update notification set text='Dear $(SenderFirstName) $(SenderLastName), Sorry, your request for account upgrade to $(KycLevel) failed. For info, call $(CustomerServiceShortCode) ' where code = 628;


update notification set text='Dear $(SenderFirstName) $(SenderLastName), Your Password has expired Please call $(CustomerServiceShortCode) for assistance' where code = 629; 

update notification set text='Dear $(SenderFirstName) $(SenderLastName), Registration of Subscriber with phone number $(DestMDN) is pending . Confirmation will be sent within 24 hours' where code = 635; 

update notification set text='Dear $(SenderFirstName) $(SenderLastName), ERROR: Account Blocked. You have exceeded the maximum number of PIN attempts. Please contact customer care at $(CustomerServiceShortCode) to reset PIN' where code = 2006;

update notification set text='Dear $(SenderFirstName) $(SenderLastName), please note that you entered an Incorrect Activation Code. Kindly verify your Activation code and try again. Thank you' where code = 2010;

update notification set text='Dear $(SenderFirstName) $(SenderLastName), please note that your your new otp is $(OneTimePin). Thank you' where code = 655; 

update notification set text='Dear $(SenderFirstName) $(SenderLastName), To activate, please click the link $(AppURL)/activation.htm?service=$(Service)&' || 'userid=$(SourceMDN)&' || 'otp=$(OneTimePin)' where code = 657;

update notification set text='Dear $(SenderFirstName) $(SenderLastName), Do you want to top up $(ReceiverMDN) with N$(Amount). Transaction ID: $(TransferID) ' where code = 660; 

update notification set text='Dear $(SenderFirstName) $(SenderLastName), please note that your Topup of N$(Amount) on $(ReceiverMDN) was successful. The Service Charge is N$(ServiceCharge) and your closing Balance is N$(CommodityBalanceValue). Thank you. Transaction ID: $(TransferID) ' where code = 661; 

update notification set text='Dear $(SenderFirstName) $(SenderLastName), please note that your recharge on $(ReceiverMDN) was not sucessful. The amount will be reverted in 24 hours. Thank you. TRANS ID $(TransactionID).' where code = 662; 

update notification set text='Transaction ID: $(TransferID) Dear $(SenderFirstName) $(SenderLastName), your PIN combination is not strong enough, please select a stronger combination' where code = 663; 

update notification set text='Transaction ID: $(TransferID) Dear $(SenderFirstName) $(SenderLastName),  you have been debited of $(Currency) $(Amount) as Reversal of Transaction $(OriginalTransferID).' where code = 666; 

update notification set text='Transaction ID: $(TransferID) Dear $(ReceiverFirstName) $(ReceiverLastName) you have been credited of $(Currency) $(Amount) as Reversal of Transaction $(OriginalTransferID).' where code = 667; 

update notification set text='Dear $(CustomerName), please note that your reversal of TRANS ID $(OriginalTransferID)  has been initiated.' where code = 668; 

update notification set text='Dear $(CustomerName), Reversal of Transaction $(OriginalTransferID) is Rejected. Please contact Customer Care on $(CustomerServiceShortCode) for support.' where code = 669; 

update notification set text='Dear $(CustomerName), Dear Customer, Reversal of Transaction $(OriginalTransferID) could not be completed. Please try again later. ' where code = 670; 

update notification set text='Dear $(SenderFirstName) $(SenderLastName), Sorry, You will not be able to do inter bank transfer to $(BankAccountNumber).  For info, call customer care on $(CustomerServiceShortCode).' where code = 675; 

update notification set text='Dear $(SenderFirstName) $(SenderLastName), we are sorry to inform you that your transfer to $(BankAccountNumber) was not successful.  Please call $(CustomerServiceShortCode)to speak with an agent. Transaction ID: $(TransferID) ' where code = 682; 

update notification set text='Dear $(SenderFirstName) $(SenderLastName), please note that your Transfer to TRANS ID $(TransactionID) could not be completed. The financial Insititution is not availbale, please try again later. TRANS ID $(TransactionID) ' where code = 683; 

update notification set text='Dear $(SenderFirstName) $(SenderLastName), kindly confirm if you requested to transfer $(Currency) $(Amount) to $(BankAccountNumber) with Service Charge $(Currency) $(serviceCharge)  ParentTransactionID $(ParentTransactionID) REF: $(TransferID)' where code = 684; 

update notification set text='Transaction ID: $(TransferID). Dear $(SenderFirstName) $(SenderLastName), you have successfully transferred $(Currency) $(Amount) to $(BankAccountNumber) your account balance is $(CommodityBalanceValue), thank you for choosing Fortis Mobile Money.' where code = 685; 

update notification set text='Dear $(SenderFirstName) $(SenderLastName), Sorry, your request for transfer to $(DestMDN) could not be processed as the reciever status is invalid.' where code = 686; 

update notification set text='Dear $(SenderFirstName) $(SenderLastName), please provide First and Last Name to process your request for a transfer to $(DestMDN).' where code = 687; 

update notification set text='Dear Mobile User, MSISDN used is currently not registered as a Fortis Mobile Money subscriber. Please cal $(CustomerServiceShortCode) for more info. ' where code = 671; 

update notification set text='Dear Mobile User, please note that your phone number $(SourceMDN) used is currently not registered as a Fortis Mobile Money subscriber. Please cal $(CustomerServiceShortCode) for more info.' where code = 672; 

update notification set text='Dear Mobile User, please note that your phone number $(SourceMDN) is currently not registered as a Fortis Mobile Money subscriber To Register,Please cal $(CustomerServiceShortCode) for more info.' where code = 673; 

update notification set text='Dear $(SenderFirstName) $(SenderLastName), kindly confirm that your transfer request of N$(Amount) to $(ReceiverMDN) will attract an extra N$(Servicecharge) charge to complete the transaction. Transaction ID: $(TransferID) ' where code = 676; 

update notification set text='Dear $(SenderFirstName) $(SenderLastName), please note that you have successfully transferred $(Currency) $(Amount) to $(ReceiverMDN) and the Charge is $(Currency) $(serviceCharge). Your Balance as at $(TransactionDateTime) is $(Currency) $(CommodityBalanceValue).Transaction ID: $(TransferID)' where code = 678; 

update notification set text='Dear $(ReceiverFirstName) $(ReceiverLastName), please note that you have received $(Currency) $(Amount) from $(SenderMDN).Fund access code is $(OneTimePin). TRANS ID $(TransactionID) ' where code = 679; 

update notification set text='Dear $(SenderFirstName) $(SenderLastName), please note that you have successfully Cashed-Out from Agent $(PartnerCode) with $(Currency) $(Amount).Transaction ID: $(TransferID)' where code = 680; 

update notification set text='Dear $(ReceiverFirstName) $(ReceiverLastName), please note that you have successfully cashed-out to $(SenderFirstName) $(SenderLastName) $(SenderMDN) with $(Currency) $(Amount). Your balance as at $(TransactionDateTime) is $(Currency) $(DestinationMDNBalance). Transaction ID: $(TransferID)' where code = 681; 

update notification set text='Transaction ID: $(TransferID). Dear $(SenderFirstName) $(SenderLastName),  Your Bulk Transfer Request $(BulkTransferID) has been Processed. Transaction ID: $(TransferID) $(TransactionDateTime)' where code = 691; 

update notification set text='Transaction ID: $(TransferID). Dear $(ReceiverFirstName) $(ReceiverLastName), please note that you have received $(Currency) $(Amount) as part of a Bulk Transfer from $(SenderMDN). Your balance as at $(TransactionDateTime) is $(Currency) $(DestinationMDNBalance).' where code = 693; 

update notification set text='Dear $(CustomerName), please note that your Fund access code is $(OneTimePin)' where code = 695; 

update notification set text='Dear $(CustomerName), please note that your Bulk Transfer Request $(BulkTransferID) was not successful.' where code = 696; 

update notification set text='REF ID: $(TransferID). Dear $(SenderFirstName) $(SenderLastName), please note that your payment to Biller $(BillerCode) $(InvoiceNumber) with $(Currency)$(Amount),ServiceCharge $(Currency)$(serviceCharge) is successful' where code = 697; 

update notification set text='Dear $(SenderFirstName) $(SenderLastName), Your payment to biller $(BillerCode) towards invoice number $(InvoiceNumber) is still pending. 
Confirmation will be sent in 24 hours.' where code = 698; 

update notification set text='Dear $(SenderFirstName) $(SenderLastName), Invalid Amount, Amount should be between $(minAmount) $(Currency) and $(maxAmount) $(Currency).' where code = 699; 

update notification set text='Dear $(SenderFirstName) $(SenderLastName), You requested to cash out amount $(Currency) $(Amount) from ATM and Service Charge $(Currency) $(serviceCharge).' where code = 708; 

commit;