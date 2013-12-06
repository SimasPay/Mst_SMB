use mfino;

ALTER TABLE sms_code add column ShortCodes varchar(255) null;

INSERT INTO `notification`
(`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,
`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,
`Language`,`Status`,`StatusTime`,`accesscode`,`smsnotificationcode`,`companyID`)
VALUES
(now(), "System", now(), "System",0,
1, 547, "CCActivated", 1, "Your account has been activated. Now you can use your credit card to top up or pay SmartFren postpaid bills in SmartFren website. Info call $(CustomerServiceShortCode).", null, 
0, 0,now(),null,837,1);


INSERT INTO `notification`
(`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,
`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,
`Language`,`Status`,`StatusTime`,`accesscode`,`smsnotificationcode`,`companyID`)
VALUES
(now(), "System", now(), "System",0,
1, 547, "CCActivated", 1, "Rekening anda sudah diaktifkan. Anda dapat menggunakan kartu kredit anda untuk top up dan melakukan pembayaran tagihan SmartFren melalui SmartFren website. Info hubungi $(CustomerServiceShortCode).", null, 
1, 0,now(),null,837,1);



INSERT INTO `notification`
(`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,
`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,
`Language`,`Status`,`StatusTime`,`accesscode`,`smsnotificationcode`,`companyID`)
VALUES
(now(), "System", now(), "System",0,
1, 547, "CCActivated", 1, "Your account has been activated. Now you can use your credit card to top up or pay SmartFren postpaid bills in SmartFren website. Info call $(CustomerServiceShortCode).", null, 
0, 0,now(),null,808,2);


INSERT INTO `notification`
(`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,
`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,
`Language`,`Status`,`StatusTime`,`accesscode`,`smsnotificationcode`,`companyID`)
VALUES
(now(), "System", now(), "System",0,
1, 547, "CCActivated", 1, "Rekening anda sudah diaktifkan. Anda dapat menggunakan kartu kredit anda untuk top up dan melakukan pembayaran tagihan SmartFren melalui SmartFren website. Info hubungi $(CustomerServiceShortCode).", null, 1, 0,now(),null,808,2);


INSERT INTO `notification`
(`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,
`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,
`Language`,`Status`,`StatusTime`,`accesscode`,`smsnotificationcode`,`companyID`)
VALUES
(now(), "System", now(), "System",0,
1, 548, "CCRejected", 1, " Sorry, your credit card registration can’t be processed. The registered information does not match with bank information. Info call $(CustomerServiceShortCode).", null, 
0, 0,now(),null,837,1);

INSERT INTO `notification`
(`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,
`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,
`Language`,`Status`,`StatusTime`,`accesscode`,`smsnotificationcode`,`companyID`)
VALUES
(now(), "System", now(), "System",0,
1, 548, "CCRejected", 1, "Maaf, registrasi kartu kredit Anda tidak dapat diproses. Informasi yang diregistrasikan berbeda dengan informasi bank. Info hub $(CustomerServiceShortCode).", null, 
1, 0,now(),null,837,1);


INSERT INTO `notification`
(`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,
`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,
`Language`,`Status`,`StatusTime`,`accesscode`,`smsnotificationcode`,`companyID`)
VALUES
(now(), "System", now(), "System",0,
1, 548, "CCRejected", 1, "Sorry, your credit card registration can’t be processed. The registered information does not match with bank information. Info call $(CustomerServiceShortCode).", null, 
0, 0,now(),null,808,2);
INSERT INTO `notification`
(`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,
`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,
`Language`,`Status`,`StatusTime`,`accesscode`,`smsnotificationcode`,`companyID`)
VALUES
(now(), "System", now(), "System",0,
1, 548, "CCRejected", 1, "Maaf, registrasi kartu kredit Anda tidak dapat diproses. Informasi yang diregistrasikan berbeda dengan informasi bank. Info hub $(CustomerServiceShortCode).", null, 
1, 0,now(),null,808,2);








