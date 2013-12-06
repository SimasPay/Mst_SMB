DELETE FROM enum_text WHERE TagID =8071;

INSERT INTO enum_text (Version,LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Language,TagName,TagID,EnumCode,EnumValue,DisplayText) VALUES (1,sysdate,'System',sysdate,'System',0,'ChargesIncluded',8071,'true','True','True');

INSERT INTO enum_text (Version,LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Language,TagName,TagID,EnumCode,EnumValue,DisplayText) VALUES (1,sysdate,'System',sysdate,'System',0,'ChargesIncluded',8071,'false','False','False');

commit;