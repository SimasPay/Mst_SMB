DROP SEQUENCE  mfa_authentication_ID_SEQ;
DROP TABLE mfa_authentication CASCADE CONSTRAINTS;
Delete From enum_text where TagID =8105;
DROP SEQUENCE  mfa_transactions_info_ID_SEQ;
DROP TABLE mfa_transactions_info CASCADE CONSTRAINTS;

commit;