update notification set text="Your request to pay $(BillerCode) with $(Currency) $(NominalAmount) failed, Please try again later. $(OperatorMessage) REF: $(TransferID)" where code=714;

update notification set text="Your request to pay $(BillerCode) with $(Currency) $(NominalAmount), ServiceCharge $(Currency) $(serviceCharge) is successful. $(VoucherToken) REF: $(TransferID)" where code=715;