

DROP TABLE IF EXISTS `bulk_upload_file_entry`;
CREATE TABLE `bulk_upload_file_entry` (
  ID bigint(20) NOT NULL AUTO_INCREMENT,
  Version int(11) NOT NULL,
  LastUpdateTime datetime NOT NULL,
  UpdatedBy varchar(255) NOT NULL DEFAULT ' ',
  CreateTime datetime NOT NULL,
  CreatedBy varchar(255) NOT NULL,
  BulkUploadFileID bigint(20) NOT NULL,  
  LineData longtext NOT NULL,
  LineNumber int(11) NOT NULL,
  BulkUploadFileEntryStatus bigint(20) NOT NULL,  
  FailureReason varchar(255), 
  PRIMARY KEY (`ID`),
  CONSTRAINT FK_upload_entry_file_ID FOREIGN KEY (BulkUploadFileID) REFERENCES bulk_upload_file(ID)
 )ENGINE=InnoDB DEFAULT CHARSET=utf8;
 
 
 ALTER TABLE bulk_upload_file MODIFY FileData longtext NULL;