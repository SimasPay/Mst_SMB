

update notification set text='REF: $(TransferID). Received N $(Amount) from $(SenderFirstName). BAL: N $(DestinationMDNBalance) as at $(TransactionDateTime).' where code = 294;

update notification set text='REF:$(TransferID). N $(Amount) cash-out to  $(SenderFirstName) is successful.BAL: N $(DestinationMDNBalance) as at $(TransactionDateTime).' where code = 299;

update notification set text='Transaction ID: $(TransferID). $(BankName) You have received $(Currency) $(Amount) from $(SenderFirstName) on $(TransactionDateTime).' where code = 306;

update notification set text='Transaction ID: $(TransferID). You have received $(Currency) $(Amount) from $(SenderFirstName). Visit any eazymoney agent/Zenith bank branch for cashout.' where code = 679;

update notification set text='Transaction ID: $(TransferID). Dear Customer, your Account Number $(BankAccountNumber) has been credited $(Currency) $(Amount) by $(SenderFirstName) on $(TransactionDateTime)' where code = 704;

update notification set text='REF: $(TransferID). Received N $(Amount) from $(SenderFirstName) on $(TransactionDateTime).' where code = 645;

update notification set text='REF: $(TransferID). Successfully paid to Biller $(PartnerCode), $(InvoiceNumber) with $(Currency)$(Amount),ServiceCharge $(Currency)$(serviceCharge).Current balance is $(Currency) $(CommodityBalanceValue) on $(TransactionDateTime).' where code = 653;

update notification set text='REF ID: $(TransferID). Received Payment from customer $(SenderFirstName), $(InvoiceNumber) with $(Currency) $(Amount).' where code = 654;