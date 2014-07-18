-- Script to include entry in channel_code for SourceApplication_InterSwitch

Delete from channel_code where ChannelSourceApplication = 8;
INSERT INTO channel_code (Version,LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,ChannelCode,ChannelName,Description,ChannelSourceApplication)
VALUES (0,now(),'System',now(),'System',11,'InterSwitchCashIn','InterSwitchCashIn',8);