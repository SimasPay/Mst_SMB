ALTER TABLE subscriber_mdn ADD OtpRetryCount NUMBER(10,0);
ALTER TABLE mdn_otp ADD OtpRetryCount NUMBER(10,0);

commit;