DELETE FROM `system_parameters` WHERE ParameterName='uangku.ibt.biller.code';

INSERT INTO `system_parameters` (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, ParameterName, ParameterValue, Description) VALUES (1,now(),'System',now(),'system','uangku.ibt.biller.code','UANGKUIBT','Biller Code For Uangkut IBT in MFS System');

INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',NOW(),'system',NOW(),'system','0','IntegrationCode','7039','UANGKUIBT','UANGKUIBT','UANGKUIBT');