USE mfino;

delete from notification where code in (686,687);

INSERT INTO `notification` (`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,`Language`,`Status`,`StatusTime`,`AccessCode`,`SMSNotificationCode`,`CompanyID`) VALUES 
(now(),"System",now(),"System",0,1,686,"UnSupportedStatusForUnRegistered",1,"Dear Customer, could not process your request for transfer to $(DestMDN), as the reciever status is invalid.",null,0,0,now(),null,null,1),
(now(),"System",now(),"System",0,1,686,"UnSupportedStatusForUnRegistered",2,"Dear Customer, could not process your request for transfer to $(DestMDN), as the reciever status is invalid.",null,0,0,now(),null,null,1),
(now(),"System",now(),"System",0,1,686,"UnSupportedStatusForUnRegistered",4,"Dear Customer, could not process your request for transfer to $(DestMDN), as the reciever status is invalid.",null,0,0,now(),null,null,1),
(now(),"System",now(),"System",0,1,686,"UnSupportedStatusForUnRegistered",8,"Dear Customer, could not process your request for transfer to $(DestMDN), as the reciever status is invalid.",null,0,0,now(),null,null,1),
(now(),"System",now(),"System",0,1,686,"UnSupportedStatusForUnRegistered",16,"Dear Customer, could not process your request for transfer to $(DestMDN), as the reciever status is invalid.",null,0,0,now(),null,null,1);

INSERT INTO `notification` (`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,`Language`,`Status`,`StatusTime`,`AccessCode`,`SMSNotificationCode`,`CompanyID`) VALUES
(now(),"System",now(),"System",0,1,687,"InvalidInputsForUnRegistered",1,"Dear Customer, please provide FirstName and LastName to process your request for transfer to $(DestMDN).",null,0,0,now(),null,null,1),
(now(),"System",now(),"System",0,1,687,"InvalidInputsForUnRegistered",2,"Dear Customer, please provide FirstName and LastName to process your request for transfer to $(DestMDN).",null,0,0,now(),null,null,1),
(now(),"System",now(),"System",0,1,687,"InvalidInputsForUnRegistered",4,"Dear Customer, please provide FirstName and LastName to process your request for transfer to $(DestMDN).",null,0,0,now(),null,null,1),
(now(),"System",now(),"System",0,1,687,"InvalidInputsForUnRegistered",8,"Dear Customer, please provide FirstName and LastName to process your request for transfer to $(DestMDN).",null,0,0,now(),null,null,1),
(now(),"System",now(),"System",0,1,687,"InvalidInputsForUnRegistered",16,"Dear Customer, please provide FirstName and LastName to process your request for transfer to $(DestMDN).",null,0,0,now(),null,null,1);

