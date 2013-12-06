delete from system_parameters where parametername='app.url';

insert into system_parameters (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, ParameterName, ParameterValue, Description)
VALUES
(1,now(),'System',now(),'system','subapp.url','https://212.100.66.24:8443/eazyapp/subapp','mobile subapps url'),
(1,now(),'System',now(),'system','agentapp.url','https://212.100.66.24:8443/eazyapp/agentapp','mobile agentapps url');