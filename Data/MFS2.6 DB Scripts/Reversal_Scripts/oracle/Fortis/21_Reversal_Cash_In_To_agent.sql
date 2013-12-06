Delete from pocket where CARDPAN = 'wl1Hb4D+Yojm6MOu7FCeUQ==';
Delete from system_parameters where ParameterName = 'funding.pocket.for.agent';


DELETE FROM service_transaction where serviceid=(select id from service where servicename='Wallet') and transactiontypeid=(select id from transaction_type where transactionname='CashInToAgent');
DELETE FROM transaction_type where TRANSACTIONNAME = 'CashInToAgent';

DELETE FROM enum_text where TAGID=5636 and ENUMCODE='56';


commit;