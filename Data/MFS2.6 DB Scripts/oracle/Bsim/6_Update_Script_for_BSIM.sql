
Delete from enum_text where tagid = 5134 and  enumcode = '1';
INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','Language','5134','1','Bahasa','Bahasa');

Delete from enum_text where tagid = 5135 and  enumcode = 'IDR';
INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','Currency','5135','IDR','IDR','IDR');

Delete from enum_text where tagid = 5135 and  enumcode = 'NGN';

Delete from enum_text where tagid = 5136 and  enumcode = 'Asia/Jakarta';
INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','Timezone','5136','Asia/Jakarta','West_Indonesia_Time','West_Indonesia_Time');

Delete from enum_text where tagid = 5136 and  enumcode = 'Asia/Makassar';
INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','Timezone','5136','Asia/Makassar','Central_Indonesia_Time','Central_Indonesia_Time');

Delete from enum_text where tagid = 5136 and  enumcode = 'Asia/Jayapura';
INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','Timezone','5136','Asia/Jayapura','East_Indonesia_Time','East_Indonesia_Time');

Delete from enum_text where tagid = 5136 and  enumcode = 'WAT';
Delete from enum_text where tagid = 5136 and  enumcode = 'UTC+1';

update brand set internationalcountrycode='62';

update subscriber set currency='IDR', timezone='West_Indonesia_Time';

update subscriber_mdn set mdn='621000' where mdn='2341000';
update subscriber_mdn set mdn='622000' where mdn='2342000';
update subscriber_mdn set mdn='623000' where mdn='2343000';

update system_parameters set ParameterValue='62' where ParameterName='country.code';
update system_parameters set ParameterValue='623000' where ParameterName='thirdparty.partner.mdn';
update system_parameters set ParameterValue='Smart' where ParameterName='profile';
update system_parameters set ParameterValue='622000' where ParameterName='platform.dummy.subscriber.mdn';
update system_parameters set ParameterValue='621000' where ParameterName='platform.dummy.mdn';
update system_parameters set ParameterValue='http://localhost:8080/mobileapp/agentapp' where ParameterName='agentapp.url';
update system_parameters set ParameterValue='http://localhost:8080/mobileapp/subapp' where ParameterName='subapp.url';

commit;