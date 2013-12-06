
DELETE FROM `system_parameters` WHERE ParameterName='default.language.of.subscriber';

INSERT INTO `system_parameters` (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, ParameterName, ParameterValue, Description) VALUES (1,now(),'System',now(),'system','default.language.of.subscriber','1','Default Language of Subscriber. 0-English, 1-Bahasa');
