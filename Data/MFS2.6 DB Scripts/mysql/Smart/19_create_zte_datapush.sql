
CREATE TABLE zte_datapush (
         ID BIGINT(20) unsigned NOT NULL AUTO_INCREMENT,
         Version INT(11) NOT NULL,
		 LastUpdateTime DATETIME NOT NULL,
         UpdatedBy VARCHAR(255) NOT NULL,
		 CreateTime DATETIME NOT NULL,
         CreatedBy VARCHAR(255) NOT NULL,
		 Msisdn VARCHAR(255), 
		 FirstName VARCHAR(255),
		 LastName VARCHAR(255), 
		 Email VARCHAR(255),
         Language INT(11), 
		 Currency VARCHAR(255),
         PaidFlag VARCHAR(255), 
		 BirthDate DATETIME,  
		 IDType VARCHAR(255),
         IDNumber VARCHAR(255), 
		 Gender VARCHAR(255), 
		 Address VARCHAR(255), 
		 City VARCHAR(255), 
		 BirthPlace VARCHAR(255), 
		 IMSI VARCHAR(255),
         MarketingCatg VARCHAR(255),  
		 Product VARCHAR(255),
		 PRIMARY KEY (`ID`)
       )ENGINE=InnoDB DEFAULT CHARSET=utf8;

