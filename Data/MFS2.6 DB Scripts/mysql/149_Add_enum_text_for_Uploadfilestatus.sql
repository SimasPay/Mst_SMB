
DELETE FROM enum_text WHERE TagID = 5395 and EnumCode = 1;

INSERT INTO enum_text (Version,LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Language,TagName,TagID,EnumCode,EnumValue,DisplayText) VALUES (1,now(),'System',now(),'System',0,'UploadFileStatus',5395,'1','Uploading','Uploading');



DELETE FROM enum_text WHERE TagID = 8125;

INSERT INTO enum_text (Version,LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Language,TagName,TagID,EnumCode,EnumValue,DisplayText) VALUES (1,now(),'System',now(),'System',0,'BulkUploadFileEntryStatus',8125,'1','Initialized','Initialized');
INSERT INTO enum_text (Version,LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Language,TagName,TagID,EnumCode,EnumValue,DisplayText) VALUES (1,now(),'System',now(),'System',0,'BulkUploadFileEntryStatus',8125,'2','Processing','Processing');
INSERT INTO enum_text (Version,LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Language,TagName,TagID,EnumCode,EnumValue,DisplayText) VALUES (1,now(),'System',now(),'System',0,'BulkUploadFileEntryStatus',8125,'3','Completed','Completed');
INSERT INTO enum_text (Version,LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Language,TagName,TagID,EnumCode,EnumValue,DisplayText) VALUES (1,now(),'System',now(),'System',0,'BulkUploadFileEntryStatus',8125,'4','Falied','Falied');


-- To set all file upload statuses to Processed if in processing state earlier 

UPDATE bulk_upload_file set UploadFileStatus = 30 where UploadFileStatus = 20;