DROP TABLE IF EXISTS subscriber_status_event;
CREATE TABLE subscriber_status_event (
  ID bigint(20) NOT NULL AUTO_INCREMENT,
  Version int(11) NOT NULL,
  LastUpdateTime datetime NOT NULL,
  UpdatedBy varchar(255) NOT NULL DEFAULT ' ',
  CreateTime datetime NOT NULL,
  CreatedBy varchar(255) NOT NULL,
  subscriberid bigint(20) NOT NULL,
  pickupdatetime datetime NOT NULL,
  processingstatus bigint(3) NOT NULL DEFAULT 0,
  statusonpickup bigint(10) NOT NULL,
  subscribertype bigint(10) NOT NULL,
  PRIMARY KEY (ID)
 )ENGINE=InnoDB DEFAULT CHARSET=utf8;