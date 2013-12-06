Create Table current_balance_info 
(ID int NOT NULL AUTO_INCREMENT,
`Version` int(11) NOT NULL,
`LastUpdateTime` datetime NOT NULL,
`UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
`CreateTime` datetime NOT NULL,
`CreatedBy` varchar(255) NOT NULL,
CurrentBalance VARCHAR(255),
SubscriberID BIGINT(20),
KYCLevel BIGINT(20),
PRIMARY KEY (ID)
)