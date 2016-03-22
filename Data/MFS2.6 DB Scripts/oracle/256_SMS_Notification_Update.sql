-- changed as per SMP-93
UPDATE NOTIFICATION SET TEXT = 'To activate your OTP is $(OneTimePin)' WHERE CODE =657;

UPDATE NOTIFICATION SET TEXT = 'Dear Customer, welcome to Simobi services.' WHERE CODE =2032;

COMMIT;