update enum_text set EnumValue='STK' , DisplayText='STK' where TagID='5069' and EnumCode=6;

update enum_text set EnumValue='STK' , DisplayText='STK' where TagID='5744' and EnumCode=6;

DELETE FROM enum_text where TagID=5069 and EnumCode=10;

DELETE FROM enum_text where TagID=5744 and EnumCode=10;

INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','SourceApplication','5069','10','SMS','SMS');

INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','ChannelSourceApplication','5744','10','SMS','SMS');

update channel_code set channelname='STK', description='STK' where id = 6 and channelcode = 6;

update channel_code set channelname='SMS', description='SMS' where id = 10 and channelcode = 10;

commit;