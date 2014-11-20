 
CREATE TABLE bulk_upload_file_entry (
  ID number(19,0) NOT NULL PRIMARY KEY,
  Version number(10,0) NOT NULL,
  LastUpdateTime TIMESTAMP NOT NULL,
  UpdatedBy varchar2(255) NOT NULL,
  CreateTime TIMESTAMP NOT NULL,
  CreatedBy varchar2(255) NOT NULL,
  BulkUploadFileID number(19,0) NOT NULL,  
  LineData CLOB NOT NULL,
  LineNumber number(10,0) NOT NULL,
  BulkUploadFileEntryStatus number(10,0) NOT NULL,
  FailureReason varchar2(255),
  CONSTRAINT FK_upload_entry_file_ID FOREIGN KEY (BulkUploadFileID) REFERENCES bulk_upload_file(ID)
 );
 
CREATE SEQUENCE  bulk_upload_file_entry_ID_SEQ  
  MINVALUE 1 MAXVALUE 999999999999999999999999 INCREMENT BY 1  NOCYCLE ; 
  

ALTER TABLE bulk_upload_file MODIFY FileData NULL;

commit; 