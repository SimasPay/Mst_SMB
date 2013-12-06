UPDATE notification SET Text="Thank you for topping up $(Amount) on $(TransactionDateTime) to $(ReceiverMDN) using $(IssuerName) Credit Card $(F6L3). REF:$(CreditCardTransactionID)" where code=336;

UPDATE notification SET Text="You have received Airtime Topup of $(Amount) on $(TransactionDateTime). REF:$(CreditCardTransactionID)" where code=337;

UPDATE notification SET Text="Thank you for paying $(Currency) $(Amount) to $(ReceiverMDN) on $(TransactionDateTime) using $(IssuerName) Credit Card $(F6L3). REF: $(CreditCardTransactionID)" where code=338;

UPDATE notification SET Text="Your SMARTFREN billing of $(Amount) already paid on $(TransactionDateTime). REF: $(CreditCardTransactionID)" where code=339;