
INSERT INTO `notification`
(`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,
`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,
`Language`,`Status`,`StatusTime`,`accesscode`,`smsnotificationcode`,`companyID`)
VALUES
(now(), "System", now(), "System",0,
1, 550, "MultixGenericCCResponse", 4, "Your transaction is having a problem. To check the status of transaction, contact $(CustomerServiceShortCode). CCTransID: $(CCTransID)", null, 
0, 0,now(),null,837,1);


INSERT INTO `notification`
(`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,
`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,
`Language`,`Status`,`StatusTime`,`accesscode`,`smsnotificationcode`,`companyID`)
VALUES
(now(), "System", now(), "System",0,
1, 550, "MultixGenericCCResponse", 4, "Transaksi yang anda lakukan mengalami masalah. Untuk memastikan status transaksi, hub $(CustomerServiceShortCode). CCTransID: $(CCTransID)", null, 
1, 0,now(),null,837,1);



INSERT INTO `notification`
(`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,
`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,
`Language`,`Status`,`StatusTime`,`accesscode`,`smsnotificationcode`,`companyID`)
VALUES
(now(), "System", now(), "System",0,
1, 550, "MultixGenericCCResponse", 4, "Your transaction is having a problem. To check the status of transaction, contact $(CustomerServiceShortCode). CCTransID: $(CCTransID)", null, 
0, 0,now(),null,808,2);


INSERT INTO `notification`
(`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,
`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,
`Language`,`Status`,`StatusTime`,`accesscode`,`smsnotificationcode`,`companyID`)
VALUES
(now(), "System", now(), "System",0,
1, 550, "MultixGenericCCResponse", 4, "Transaksi yang anda lakukan mengalami masalah. Untuk memastikan status transaksi, hub $(CustomerServiceShortCode). CCTransID: $(CCTransID)", null, 1, 0,now(),null,808,2);
