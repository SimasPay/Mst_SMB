
DROP TABLE IF EXISTS `subscriber_favorite`;
DROP TABLE IF EXISTS `favorite_category`;

CREATE TABLE `favorite_category` (
  ID bigint(20) NOT NULL AUTO_INCREMENT,
  Version int(11) NOT NULL,
  LastUpdateTime datetime NOT NULL,
  UpdatedBy varchar(255) NOT NULL DEFAULT ' ',
  CreateTime datetime NOT NULL,
  CreatedBy varchar(255) NOT NULL,
  CategoryName varchar(255) NOT NULL,
  DisplayName varchar(255) NOT NULL,
  PRIMARY KEY (`ID`)
 )ENGINE=InnoDB DEFAULT CHARSET=utf8;
 

CREATE TABLE `subscriber_favorite` (
  ID bigint(20) NOT NULL AUTO_INCREMENT,
  Version int(11) NOT NULL,
  LastUpdateTime datetime NOT NULL,
  UpdatedBy varchar(255) NOT NULL DEFAULT ' ',
  CreateTime datetime NOT NULL,
  CreatedBy varchar(255) NOT NULL,
  SubscriberID bigint(20) NOT NULL,
  FavoriteCategoryID bigint(20) NOT NULL,
  FavoriteLabel varchar(255) NOT NULL,
  FavoriteValue varchar(255) NOT NULL,
  PRIMARY KEY (`ID`),
  CONSTRAINT FK_sub_fav_subID FOREIGN KEY(SubscriberID) references subscriber(ID),
  CONSTRAINT FK_sub_fav_categoryID FOREIGN KEY(FavoriteCategoryID) references favorite_category(ID),
  CONSTRAINT UNIQUE_fav_label UNIQUE (SubscriberID, FavoriteCategoryID, FavoriteLabel),
  CONSTRAINT UNIQUE_fav_value UNIQUE (SubscriberID, FavoriteCategoryID, FavoriteValue)
 )ENGINE=InnoDB DEFAULT CHARSET=utf8;