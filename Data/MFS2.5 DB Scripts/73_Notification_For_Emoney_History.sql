use mfino;

update notification set text ='$(TransactionID) $(TransactionDateTime) $(TransactionType) $(SenderMDN) to $(ReceiverMDN) $(Currency) $(Amount) $(TransferStatus)' where code = 39;
