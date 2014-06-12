update notification set text="$(NotificationCode) You requested to recharge $(InvoiceNumber) from $(BillerName) with $(Currency) $(NominalAmount), ServiceCharge $(Currency) $(serviceCharge). REF: $(TransferID)" where code=660;

update notification set text="$(NotificationCode) Recharge Successful for $(InvoiceNumber) from $(BillerName) with $(Currency) $(NominalAmount), ServiceCharge $(Currency) $(serviceCharge). REF: $(TransferID)" where code=661;

update notification set text="$(NotificationCode) Your request to recharge $(InvoiceNumber) from $(BillerName) with $(Currency) $(NominalAmount) failed. $(OperatorMessage) Your amount will be reverted in 24 hours. REF: $(TransferID)" where code=662;