
DROP TABLE IF EXISTS `permission_group`;
CREATE TABLE `permission_group` (
  ID bigint(20) NOT NULL,
  Version int(11) NOT NULL,
  LastUpdateTime datetime NOT NULL,
  UpdatedBy varchar(255) NOT NULL DEFAULT ' ',
  CreateTime datetime NOT NULL,
  CreatedBy varchar(255) NOT NULL,
  PermissionGroupName varchar(255) NOT NULL,
  PRIMARY KEY (`ID`)
 )ENGINE=InnoDB DEFAULT CHARSET=utf8;