
 
CREATE TABLE favorite_category (
  ID number(19,0) NOT NULL PRIMARY KEY,
  Version number(10,0) NOT NULL,
  LastUpdateTime TIMESTAMP NOT NULL,
  UpdatedBy varchar2(255) NOT NULL,
  CreateTime TIMESTAMP NOT NULL,
  CreatedBy varchar2(255) NOT NULL,
  CategoryName varchar2(255) NOT NULL,
  DisplayName varchar2(255) NOT NULL
 );
 
CREATE SEQUENCE  favorite_category_ID_SEQ  
  MINVALUE 1 MAXVALUE 999999999999999999999999 INCREMENT BY 1  NOCYCLE ; 

 
CREATE TABLE subscriber_favorite (
  ID number(19,0) NOT NULL PRIMARY KEY,
  Version number(10,0) NOT NULL,
  LastUpdateTime TIMESTAMP NOT NULL,
  UpdatedBy varchar2(255) NOT NULL,
  CreateTime TIMESTAMP NOT NULL,
  CreatedBy varchar2(255) NOT NULL,
  SubscriberID number(19,0) NOT NULL,
  FavoriteCategoryID number(19,0) NOT NULL,
  FavoriteLabel varchar2(255) NOT NULL,
  FavoriteValue varchar2(255) NOT NULL,
  CONSTRAINT FK_sub_fav_subID FOREIGN KEY (SubscriberID) REFERENCES subscriber(ID),
  CONSTRAINT FK_sub_fav_categoryID FOREIGN KEY (FavoriteCategoryID) REFERENCES favorite_category(ID),
  CONSTRAINT UNIQUE_fav_label UNIQUE (SubscriberID, FavoriteCategoryID, FavoriteLabel),
  CONSTRAINT UNIQUE_fav_value UNIQUE (SubscriberID, FavoriteCategoryID, FavoriteValue)
 );
 
CREATE SEQUENCE  subscriber_favorite_ID_SEQ  
  MINVALUE 1 MAXVALUE 999999999999999999999999 INCREMENT BY 1  NOCYCLE ; 
  
commit; 
 
