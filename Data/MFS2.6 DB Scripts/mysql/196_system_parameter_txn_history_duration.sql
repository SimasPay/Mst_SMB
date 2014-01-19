
-- Insert New System Parameter
Delete from system_parameters where ParameterName = 'max.duration.to.fetch.txn.history';
INSERT INTO system_parameters (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, ParameterName, ParameterValue, Description) VALUES (1,now(),'System',now(),'system','max.duration.to.fetch.txn.history','90','max duration in days to fetch txn history');

