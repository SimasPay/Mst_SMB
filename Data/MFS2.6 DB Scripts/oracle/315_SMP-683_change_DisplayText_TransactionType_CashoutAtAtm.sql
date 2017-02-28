update TRANSACTION_TYPE set DISPLAYNAME = 'e-Money Cash Withdrawal' where TRANSACTIONNAME = 'CashOutAtATM';
update ENUM_TEXT set DISPLAYTEXT = 'e-Money Cash Withdrawal' where ENUMVALUE = 'Cashout_At_ATM';
commit;