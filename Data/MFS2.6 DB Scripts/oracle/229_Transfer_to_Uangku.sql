


INSERT INTO SYSTEM_PARAMETERS (ID, VERSION, LASTUPDATETIME, UPDATEDBY, CREATETIME, CREATEDBY, PARAMETERNAME, PARAMETERVALUE, DESCRIPTION) 
 VALUES (system_parameters_ID_SEQ.NEXTVAL,1,SYSDATE,'System',SYSDATE,'system','transfer.to.uangku.prefix.number','8881','Prefix that should be added during the Transfer to Uangku Transaction.');
 
INSERT INTO TRANSACTION_TYPE VALUES (TRANSACTION_TYPE_ID_SEQ.NEXTVAL,1,SYSDATE,'System',SYSDATE,'System',1,'TransferToUangkuInquiry','Transfer To Uangku Inquiry');

INSERT INTO TRANSACTION_TYPE VALUES (TRANSACTION_TYPE_ID_SEQ.NEXTVAL,1,SYSDATE,'System',SYSDATE,'System',1,'TransferToUangku','Transfer To Uangku');

INSERT INTO SERVICE_TRANSACTION(VERSION,LASTUPDATETIME,UPDATEDBY,CREATETIME,CREATEDBY,MSPID,SERVICEID,TRANSACTIONTYPEID)  VALUES (1,SYSDATE,'System',SYSDATE,'System',1, (SELECT ID FROM SERVICE WHERE SERVICENAME = 'Bank'), (SELECT ID FROM TRANSACTION_TYPE WHERE TRANSACTIONNAME = 'TransferToUangku'));

INSERT INTO MFA_TRANSACTIONS_INFO VALUES (MFA_TRANSACTIONS_INFO_ID_SEQ.NEXTVAL,1,SYSDATE,'System',SYSDATE,'System',1,(SELECT ID FROM SERVICE WHERE SERVICENAME = 'Bank' AND MSPID=1),(SELECT ID FROM TRANSACTION_TYPE WHERE TRANSACTIONNAME = 'TransferToUangku' AND MSPID=1),(SELECT ID FROM CHANNEL_CODE WHERE CHANNELNAME = 'WebAPI'),1);

DELETE FROM NOTIFICATION WHERE CODE = 2126;

INSERT INTO NOTIFICATION (VERSION, LASTUPDATETIME, UPDATEDBY, CREATETIME, CREATEDBY, MSPID, CODE, CODENAME, NOTIFICATIONMETHOD, TEXT, LANGUAGE, STATUS, STATUSTIME, COMPANYID, ISACTIVE) VALUES ('0', SYSDATE, 'System', SYSDATE, 'System', '1', '2126', 'InvalidMFAOTP', '1', 'Kode OTP salah. Pastikan kode yang Anda masukkan sudah sesuai dengan SMS OTP terbaru dari kami.', '0', '0', SYSDATE, '1', '1');

INSERT INTO NOTIFICATION (VERSION, LASTUPDATETIME, UPDATEDBY, CREATETIME, CREATEDBY, MSPID, CODE, CODENAME, NOTIFICATIONMETHOD, TEXT, LANGUAGE, STATUS, STATUSTIME, COMPANYID, ISACTIVE) VALUES ('0', SYSDATE, 'System', SYSDATE, 'System', '1', '2126', 'InvalidMFAOTP', '2', 'Kode OTP salah. Pastikan kode yang Anda masukkan sudah sesuai dengan SMS OTP terbaru dari kami.', '0', '0', SYSDATE, '1', '1');

INSERT INTO NOTIFICATION (VERSION, LASTUPDATETIME, UPDATEDBY, CREATETIME, CREATEDBY, MSPID, CODE, CODENAME, NOTIFICATIONMETHOD, TEXT, LANGUAGE, STATUS, STATUSTIME, COMPANYID, ISACTIVE) VALUES ('0', SYSDATE, 'System', SYSDATE, 'System', '1', '2126', 'InvalidMFAOTP', '4', 'Kode OTP salah. Pastikan kode yang Anda masukkan sudah sesuai dengan SMS OTP terbaru dari kami.', '0', '0', SYSDATE, '1', '1');

INSERT INTO NOTIFICATION (VERSION, LASTUPDATETIME, UPDATEDBY, CREATETIME, CREATEDBY, MSPID, CODE, CODENAME, NOTIFICATIONMETHOD, TEXT, LANGUAGE, STATUS, STATUSTIME, COMPANYID, ISACTIVE) VALUES ('0', SYSDATE, 'System', SYSDATE, 'System', '1', '2126', 'InvalidMFAOTP', '8', 'Kode OTP salah. Pastikan kode yang Anda masukkan sudah sesuai dengan SMS OTP terbaru dari kami.', '0', '0', SYSDATE, '1', '1');

INSERT INTO NOTIFICATION (VERSION, LASTUPDATETIME, UPDATEDBY, CREATETIME, CREATEDBY, MSPID, CODE, CODENAME, NOTIFICATIONMETHOD, TEXT, LANGUAGE, STATUS, STATUSTIME, COMPANYID, ISACTIVE) VALUES ('0', SYSDATE, 'System', SYSDATE, 'System', '1', '2126', 'InvalidMFAOTP', '16', 'Kode OTP salah. Pastikan kode yang Anda masukkan sudah sesuai dengan SMS OTP terbaru dari kami.', '0', '0', SYSDATE, '1', '1');

INSERT INTO NOTIFICATION (VERSION, LASTUPDATETIME, UPDATEDBY, CREATETIME, CREATEDBY, MSPID, CODE, CODENAME, NOTIFICATIONMETHOD, TEXT, LANGUAGE, STATUS, STATUSTIME, COMPANYID, ISACTIVE) VALUES ('0', SYSDATE, 'System', SYSDATE, 'System', '1', '2126', 'InvalidMFAOTP', '1', 'Kode OTP salah. Pastikan kode yang Anda masukkan sudah sesuai dengan SMS OTP terbaru dari kami.', '1', '0', SYSDATE, '1', '1');

INSERT INTO NOTIFICATION (VERSION, LASTUPDATETIME, UPDATEDBY, CREATETIME, CREATEDBY, MSPID, CODE, CODENAME, NOTIFICATIONMETHOD, TEXT, LANGUAGE, STATUS, STATUSTIME, COMPANYID, ISACTIVE) VALUES ('0', SYSDATE, 'System', SYSDATE, 'System', '1', '2126', 'InvalidMFAOTP', '2', 'Kode OTP salah. Pastikan kode yang Anda masukkan sudah sesuai dengan SMS OTP terbaru dari kami.', '1', '0', SYSDATE, '1', '1');

INSERT INTO NOTIFICATION (VERSION, LASTUPDATETIME, UPDATEDBY, CREATETIME, CREATEDBY, MSPID, CODE, CODENAME, NOTIFICATIONMETHOD, TEXT, LANGUAGE, STATUS, STATUSTIME, COMPANYID, ISACTIVE) VALUES ('0', SYSDATE, 'System', SYSDATE, 'System', '1', '2126', 'InvalidMFAOTP', '4', 'Kode OTP salah. Pastikan kode yang Anda masukkan sudah sesuai dengan SMS OTP terbaru dari kami.', '1', '0', SYSDATE, '1', '1');

INSERT INTO NOTIFICATION (VERSION, LASTUPDATETIME, UPDATEDBY, CREATETIME, CREATEDBY, MSPID, CODE, CODENAME, NOTIFICATIONMETHOD, TEXT, LANGUAGE, STATUS, STATUSTIME, COMPANYID, ISACTIVE) VALUES ('0', SYSDATE, 'System', SYSDATE, 'System', '1', '2126', 'InvalidMFAOTP', '8', 'Kode OTP salah. Pastikan kode yang Anda masukkan sudah sesuai dengan SMS OTP terbaru dari kami.', '1', '0', SYSDATE, '1', '1');

INSERT INTO NOTIFICATION (VERSION, LASTUPDATETIME, UPDATEDBY, CREATETIME, CREATEDBY, MSPID, CODE, CODENAME, NOTIFICATIONMETHOD, TEXT, LANGUAGE, STATUS, STATUSTIME, COMPANYID, ISACTIVE) VALUES ('0', SYSDATE, 'System', SYSDATE, 'System', '1', '2126', 'InvalidMFAOTP', '16', 'Kode OTP salah. Pastikan kode yang Anda masukkan sudah sesuai dengan SMS OTP terbaru dari kami.', '1', '0', SYSDATE, '1', '1');

DELETE FROM NOTIFICATION WHERE CODE = 2127;

INSERT INTO NOTIFICATION (VERSION, LASTUPDATETIME, UPDATEDBY, CREATETIME, CREATEDBY, MSPID, CODE, CODENAME, NOTIFICATIONMETHOD, TEXT, LANGUAGE, STATUS, STATUSTIME, COMPANYID, ISACTIVE) VALUES ('0', SYSDATE, 'System', SYSDATE, 'System', '1', '2127', 'TransferToUangkuAccountCompletedToSender', '1', 'Transaction ID: $(TransferID). Dear Customer, you have successfully transferred $(Currency) $(Amount) to $(OnBehalfOfMDN) and  Service Charge $(Currency) $(serviceCharge).', '0', '0', SYSDATE, '1', '1');

INSERT INTO NOTIFICATION (VERSION, LASTUPDATETIME, UPDATEDBY, CREATETIME, CREATEDBY, MSPID, CODE, CODENAME, NOTIFICATIONMETHOD, TEXT, LANGUAGE, STATUS, STATUSTIME, COMPANYID, ISACTIVE) VALUES ('0', SYSDATE, 'System', SYSDATE, 'System', '1', '2127', 'TransferToUangkuAccountCompletedToSender', '2', 'Transaction ID: $(TransferID). Dear Customer, you have successfully transferred $(Currency) $(Amount) to $(OnBehalfOfMDN) and  Service Charge $(Currency) $(serviceCharge).', '0', '0', SYSDATE, '1', '1');

INSERT INTO NOTIFICATION (VERSION, LASTUPDATETIME, UPDATEDBY, CREATETIME, CREATEDBY, MSPID, CODE, CODENAME, NOTIFICATIONMETHOD, TEXT, LANGUAGE, STATUS, STATUSTIME, COMPANYID, ISACTIVE) VALUES ('0', SYSDATE, 'System', SYSDATE, 'System', '1', '2127', 'TransferToUangkuAccountCompletedToSender', '4', 'Transaction ID: $(TransferID). Dear Customer, you have successfully transferred $(Currency) $(Amount) to $(OnBehalfOfMDN) and  Service Charge $(Currency) $(serviceCharge).', '0', '0', SYSDATE, '1', '1');

INSERT INTO NOTIFICATION (VERSION, LASTUPDATETIME, UPDATEDBY, CREATETIME, CREATEDBY, MSPID, CODE, CODENAME, NOTIFICATIONMETHOD, TEXT, LANGUAGE, STATUS, STATUSTIME, COMPANYID, ISACTIVE) VALUES ('0', SYSDATE, 'System', SYSDATE, 'System', '1', '2127', 'TransferToUangkuAccountCompletedToSender', '8', 'Transaction ID: $(TransferID). Dear Customer, you have successfully transferred $(Currency) $(Amount) to $(OnBehalfOfMDN) and  Service Charge $(Currency) $(serviceCharge).', '0', '0', SYSDATE, '1', '1');

INSERT INTO NOTIFICATION (VERSION, LASTUPDATETIME, UPDATEDBY, CREATETIME, CREATEDBY, MSPID, CODE, CODENAME, NOTIFICATIONMETHOD, TEXT, LANGUAGE, STATUS, STATUSTIME, COMPANYID, ISACTIVE) VALUES ('0', SYSDATE, 'System', SYSDATE, 'System', '1', '2127', 'TransferToUangkuAccountCompletedToSender', '16', 'Transaction ID: $(TransferID). Dear Customer, you have successfully transferred $(Currency) $(Amount) to $(OnBehalfOfMDN) and  Service Charge $(Currency) $(serviceCharge).', '0', '0', SYSDATE, '1', '1');

INSERT INTO NOTIFICATION (VERSION, LASTUPDATETIME, UPDATEDBY, CREATETIME, CREATEDBY, MSPID, CODE, CODENAME, NOTIFICATIONMETHOD, TEXT, LANGUAGE, STATUS, STATUSTIME, COMPANYID, ISACTIVE) VALUES ('0', SYSDATE, 'System', SYSDATE, 'System', '1', '2127', 'TransferToUangkuAccountCompletedToSender', '1', 'Transaction ID: $(TransferID). Dear Customer, you have successfully transferred $(Currency) $(Amount) to $(OnBehalfOfMDN) and  Service Charge $(Currency) $(serviceCharge).', '1', '0', SYSDATE, '1', '1');

INSERT INTO NOTIFICATION (VERSION, LASTUPDATETIME, UPDATEDBY, CREATETIME, CREATEDBY, MSPID, CODE, CODENAME, NOTIFICATIONMETHOD, TEXT, LANGUAGE, STATUS, STATUSTIME, COMPANYID, ISACTIVE) VALUES ('0', SYSDATE, 'System', SYSDATE, 'System', '1', '2127', 'TransferToUangkuAccountCompletedToSender', '2', 'Transaction ID: $(TransferID). Dear Customer, you have successfully transferred $(Currency) $(Amount) to $(OnBehalfOfMDN) and  Service Charge $(Currency) $(serviceCharge).', '1', '0', SYSDATE, '1', '1');

INSERT INTO NOTIFICATION (VERSION, LASTUPDATETIME, UPDATEDBY, CREATETIME, CREATEDBY, MSPID, CODE, CODENAME, NOTIFICATIONMETHOD, TEXT, LANGUAGE, STATUS, STATUSTIME, COMPANYID, ISACTIVE) VALUES ('0', SYSDATE, 'System', SYSDATE, 'System', '1', '2127', 'TransferToUangkuAccountCompletedToSender', '4', 'Transaction ID: $(TransferID). Dear Customer, you have successfully transferred $(Currency) $(Amount) to $(OnBehalfOfMDN) and  Service Charge $(Currency) $(serviceCharge).', '1', '0', SYSDATE, '1', '1');

INSERT INTO NOTIFICATION (VERSION, LASTUPDATETIME, UPDATEDBY, CREATETIME, CREATEDBY, MSPID, CODE, CODENAME, NOTIFICATIONMETHOD, TEXT, LANGUAGE, STATUS, STATUSTIME, COMPANYID, ISACTIVE) VALUES ('0', SYSDATE, 'System', SYSDATE, 'System', '1', '2127', 'TransferToUangkuAccountCompletedToSender', '8', 'Transaction ID: $(TransferID). Dear Customer, you have successfully transferred $(Currency) $(Amount) to $(OnBehalfOfMDN) and  Service Charge $(Currency) $(serviceCharge).', '1', '0', SYSDATE, '1', '1');

INSERT INTO NOTIFICATION (VERSION, LASTUPDATETIME, UPDATEDBY, CREATETIME, CREATEDBY, MSPID, CODE, CODENAME, NOTIFICATIONMETHOD, TEXT, LANGUAGE, STATUS, STATUSTIME, COMPANYID, ISACTIVE) VALUES ('0', SYSDATE, 'System', SYSDATE, 'System', '1', '2127', 'TransferToUangkuAccountCompletedToSender', '16', 'Transaction ID: $(TransferID). Dear Customer, you have successfully transferred $(Currency) $(Amount) to $(OnBehalfOfMDN) and  Service Charge $(Currency) $(serviceCharge).', '1', '0', SYSDATE, '1', '1');

COMMIT;