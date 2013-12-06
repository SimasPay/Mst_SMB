DELETE FROM enum_text WHERE TagID =6174 and enumCode='3';

INSERT INTO enum_text (Version,LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Language,TagName,TagID,EnumCode,EnumValue,DisplayText) VALUES (1,sysdate,'System',sysdate,'System',0,'RegistrationMedium',6174,'3','Self','Self');

commit;