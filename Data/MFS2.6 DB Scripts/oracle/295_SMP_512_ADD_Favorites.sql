--ADD Columns Favorite_Category
 
 ALTER TABLE Favorite_Category  ADD SOURCEPOCKETTYPE VARCHAR2(255)  NOT NULL;
 ALTER TABLE Favorite_Category  ADD DESTINATIONPOCKETTYPE VARCHAR2(255)  NOT NULL; 
 ALTER TABLE Favorite_Category  ADD TRANSACTIONTYPE VARCHAR2(255)  NOT NULL; 
 
  --ADD Records Favorite_Category
 
 INSERT INTO favorite_category  VALUES (favorite_category_ID_SEQ.nextval,0,sysdate,'System',sysdate,'System','Transfer B2B','Transfer B2B','Bank','Bank','Transfers');
 INSERT INTO favorite_category  VALUES (favorite_category_ID_SEQ.nextval,0,sysdate,'System',sysdate,'System','Purchase Using Bank','Purchase Using Bank','Bank','Bank','Purchase');
 INSERT INTO favorite_category  VALUES (favorite_category_ID_SEQ.nextval,0,sysdate,'System',sysdate,'System','BillPayments Using Bank','BillPayments Using Bank','Bank','Bank','BillPayments');
 INSERT INTO favorite_category  VALUES (favorite_category_ID_SEQ.nextval,0,sysdate,'System',sysdate,'System','IBT Using B2B','IBT Using B2B','Bank','Bank','IBT');
 INSERT INTO favorite_category  VALUES (favorite_category_ID_SEQ.nextval,0,sysdate,'System',sysdate,'System','Transfer B2E','Transfer B2E','Bank','Emoney','Transfers');
 INSERT INTO favorite_category  VALUES (favorite_category_ID_SEQ.nextval,0,sysdate,'System',sysdate,'System','Transfer E2E','Transfer E2E','Emoney','Emoney','Transfers');
 INSERT INTO favorite_category  VALUES 
                               (favorite_category_ID_SEQ.nextval,0,sysdate,'System',sysdate,'System','Purchase Using Emoney','Purchase Using Emoney','Emoney','Emoney','Purchase');
 INSERT INTO favorite_category  VALUES 
                               (favorite_category_ID_SEQ.nextval,0,sysdate,'System',sysdate,'System','BillPayments  Using Emoney','BillPayments  Using Emoney','Emoney','Emoney','BillPayments');
 INSERT INTO favorite_category  VALUES (favorite_category_ID_SEQ.nextval,0,sysdate,'System',sysdate,'System','IBT Using E2E','IBT Using E2E','Emoney','Emoney','IBT');
 INSERT INTO favorite_category  VALUES (favorite_category_ID_SEQ.nextval,0,sysdate,'System',sysdate,'System','Transfers E2B','Transfers E2B','Emoney','Bank','Transfers');
 INSERT INTO favorite_category  VALUES (favorite_category_ID_SEQ.nextval,0,sysdate,'System',sysdate,'System','CashWithdrawls','CashWithdrawls','Emoney','Emoney','CashWithdrawls');
 
-- Add new notifications for AddFavoriteSuccess
Delete from notification where code = 2087;

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,2087,'AddFavoriteSuccess',1,'Favorite added successfully.',null,0,0,sysdate,null,null,1);
INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,2087,'AddFavoriteSuccess',1,'Favorite added successfully.',null,1,0,sysdate,null,null,1);
INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,2087,'AddFavoriteSuccess',2,'Favorite added successfully.',null,0,0,sysdate,null,null,1);
INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,2087,'AddFavoriteSuccess',2,'Favorite added successfully.',null,1,0,sysdate,null,null,1);
INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,2087,'AddFavoriteSuccess',4,'Favorite added successfully.',null,0,0,sysdate,null,null,1);
INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,2087,'AddFavoriteSuccess',4,'Favorite added successfully.',null,1,0,sysdate,null,null,1);
INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,2087,'AddFavoriteSuccess',8,'Favorite added successfully.',null,0,0,sysdate,null,null,1);
INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,2087,'AddFavoriteSuccess',8,'Favorite added successfully.',null,1,0,sysdate,null,null,1);
INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,2087,'AddFavoriteSuccess',16,'Favorite added successfully.',null,0,0,sysdate,null,null,1);
INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,2087,'AddFavoriteSuccess',16,'Favorite added successfully.',null,1,0,sysdate,null,null,1);


-- Add new notifications for MaxFavoritesPerCategoryCountReached
Delete from notification where code = 2088;
INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,2088,'MaxFavoritesPerCategoryCountReached',1,'Cannot add favorites as max favorites per category count $(MaxFavoriteCount) reached.',null,0,0,sysdate,null,null,1);
INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,2088,'MaxFavoritesPerCategoryCountReached',1,'Cannot add favorites as max favorites per category count $(MaxFavoriteCount) reached.',null,1,0,sysdate,null,null,1);
INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,2088,'MaxFavoritesPerCategoryCountReached',2,'Cannot add favorites as max favorites per category count $(MaxFavoriteCount) reached.',null,0,0,sysdate,null,null,1);
INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,2088,'MaxFavoritesPerCategoryCountReached',2,'Cannot add favorites as max favorites per category count $(MaxFavoriteCount) reached.',null,1,0,sysdate,null,null,1);
INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,2088,'MaxFavoritesPerCategoryCountReached',4,'Cannot add favorites as max favorites per category count $(MaxFavoriteCount) reached.',null,0,0,sysdate,null,null,1);
INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,2088,'MaxFavoritesPerCategoryCountReached',4,'Cannot add favorites as max favorites per category count $(MaxFavoriteCount) reached.',null,1,0,sysdate,null,null,1);
INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,2088,'MaxFavoritesPerCategoryCountReached',8,'Cannot add favorites as max favorites per category count $(MaxFavoriteCount) reached.',null,0,0,sysdate,null,null,1);
INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,2088,'MaxFavoritesPerCategoryCountReached',8,'Cannot add favorites as max favorites per category count $(MaxFavoriteCount) reached.',null,1,0,sysdate,null,null,1);
INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,2088,'MaxFavoritesPerCategoryCountReached',16,'Cannot add favorites as max favorites per category count $(MaxFavoriteCount) reached.',null,0,0,sysdate,null,null,1);
INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,2088,'MaxFavoritesPerCategoryCountReached',16,'Cannot add favorites as max favorites per category count $(MaxFavoriteCount) reached.',null,1,0,sysdate,null,null,1);

-- Add new notifications for DuplicateFavoriteValue
Delete from notification where code = 2089;

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,2089,'DuplicateFavoriteValue',1,'Favorite with the value $(FavoriteValue) is already added under the category.',null,0,0,sysdate,null,null,1);
INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,2089,'DuplicateFavoriteValue',1,'Favorite with the value $(FavoriteValue) is already added under the category.',null,1,0,sysdate,null,null,1);
INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,2089,'DuplicateFavoriteValue',2,'Favorite with the value $(FavoriteValue) is already added under the category.',null,0,0,sysdate,null,null,1);
INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,2089,'DuplicateFavoriteValue',2,'Favorite with the value $(FavoriteValue) is already added under the category.',null,1,0,sysdate,null,null,1);
INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,2089,'DuplicateFavoriteValue',4,'Favorite with the value $(FavoriteValue) is already added under the category.',null,0,0,sysdate,null,null,1);
INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,2089,'DuplicateFavoriteValue',4,'Favorite with the value $(FavoriteValue) is already added under the category.',null,1,0,sysdate,null,null,1);
INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,2089,'DuplicateFavoriteValue',8,'Favorite with the value $(FavoriteValue) is already added under the category.',null,0,0,sysdate,null,null,1);
INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,2089,'DuplicateFavoriteValue',8,'Favorite with the value $(FavoriteValue) is already added under the category.',null,1,0,sysdate,null,null,1);
INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,2089,'DuplicateFavoriteValue',16,'Favorite with the value $(FavoriteValue) is already added under the category.',null,0,0,sysdate,null,null,1);
INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,2089,'DuplicateFavoriteValue',16,'Favorite with the value $(FavoriteValue) is already added under the category.',null,1,0,sysdate,null,null,1);

-- Add new notifications for DuplicateFavoriteLabel
Delete from notification where code = 2090;

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,2090,'DuplicateFavoriteLabel',1,'Favorite with the label $(FavoriteLabel) is already added under the category.',null,0,0,sysdate,null,null,1);
INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,2090,'DuplicateFavoriteLabel',1,'Favorite with the label $(FavoriteLabel) is already added under the category.',null,1,0,sysdate,null,null,1);
INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,2090,'DuplicateFavoriteLabel',2,'Favorite with the label $(FavoriteLabel) is already added under the category.',null,0,0,sysdate,null,null,1);
INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,2090,'DuplicateFavoriteLabel',2,'Favorite with the label $(FavoriteLabel) is already added under the category.',null,1,0,sysdate,null,null,1);
INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,2090,'DuplicateFavoriteLabel',4,'Favorite with the label $(FavoriteLabel) is already added under the category.',null,0,0,sysdate,null,null,1);
INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,2090,'DuplicateFavoriteLabel',4,'Favorite with the label $(FavoriteLabel) is already added under the category.',null,1,0,sysdate,null,null,1);
INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,2090,'DuplicateFavoriteLabel',8,'Favorite with the label $(FavoriteLabel) is already added under the category.',null,0,0,sysdate,null,null,1);
INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,2090,'DuplicateFavoriteLabel',8,'Favorite with the label $(FavoriteLabel) is already added under the category.',null,1,0,sysdate,null,null,1);
INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,2090,'DuplicateFavoriteLabel',16,'Favorite with the label $(FavoriteLabel) is already added under the category.',null,0,0,sysdate,null,null,1);
INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,2090,'DuplicateFavoriteLabel',16,'Favorite with the label $(FavoriteLabel) is already added under the category.',null,1,0,sysdate,null,null,1);





-- Add system param : "max.favorites.per.category"
Delete from system_parameters where ParameterName = 'max.favorites.per.category';
INSERT INTO system_parameters (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, ParameterName, ParameterValue, Description) VALUES (1,sysdate,'System',sysdate,'system','max.favorites.per.category','10','Max no of favorites allowed per category');

-- Add "AddFavorite" Transaction name
INSERT INTO transaction_type VALUES (transaction_type_id_seq.nextval,1,sysdate,'System',sysdate,'System',1,'AddFavorite','AddFavorite');

INSERT INTO SERVICE_TRANSACTION(VERSION,LASTUPDATETIME,UPDATEDBY,CREATETIME,CREATEDBY,MSPID,SERVICEID,TRANSACTIONTYPEID)  VALUES 
(1,SYSDATE,'System',SYSDATE,'System',1, (SELECT ID FROM SERVICE WHERE SERVICENAME = 'Account'),
 (SELECT ID FROM TRANSACTION_TYPE WHERE TRANSACTIONNAME = 'AddFavorite'));
 
 
 


							   
 
