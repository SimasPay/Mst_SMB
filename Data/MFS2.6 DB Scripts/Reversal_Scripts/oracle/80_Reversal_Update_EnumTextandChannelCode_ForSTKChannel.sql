update enum_text set EnumValue='SMS' , DisplayText='SMS' where TagID='5069' and EnumCode=6;

update enum_text set EnumValue='SMS', DisplayText='SMS' where TagID='5744' and EnumCode=6;

DELETE FROM enum_text where TagID=5069 and EnumCode=10;

DELETE FROM enum_text where TagID=5744 and EnumCode=10;

update channel_code set channelname='SMS', description='SMS' where id = 6 and channelcode = 6;

update channel_code set channelname='Reserved10', description='Reserved10' where id =10 and channelcode = 10;

commit;