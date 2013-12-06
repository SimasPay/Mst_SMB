INSERT INTO system_parameters (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, ParameterName, ParameterValue, Description) VALUES (1,sysdate,'System',sysdate,'system','allowed.partners.toregister.throughapi','7,8','allowed.partners.toregister.throughapi');
INSERT INTO system_parameters (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, ParameterName, ParameterValue, Description) VALUES (1,sysdate,'System',sysdate,'system','partner.register.throughapi.approvalrequired','true','partner.register.throughapi.approvalrequired');

INSERT INTO transaction_type (Version,LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,MSPID,TransactionName, DisplayName) VALUES
(1,sysdate,'system',sysdate,'system',1,'PartnerRegistrationThroughAPI','PartnerRegistrationThroughAPI');

INSERT INTO service_transaction (Version,LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,MSPID,ServiceID, TransactionTypeID) VALUES
(1,sysdate,'system',sysdate,'system',1,(select id from service where ServiceName='Account'), (select id from transaction_type where TransactionName='PartnerRegistrationThroughAPI'));


INSERT INTO service_defualt_config (Version,LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,ServiceProviderID,ServiceID, SourcePocketType,DestPocketType) VALUES
(1,now(),'system',now(),'system',(select id from partner where BusinessPartnerType=0),(select id from service where ServiceName='Shopping'),1,1);
INSERT INTO service_defualt_config (Version,LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,ServiceProviderID,ServiceID, SourcePocketType,DestPocketType) VALUES
(1,now(),'system',now(),'system',(select id from partner where BusinessPartnerType=0),(select id from service where ServiceName='Payment'),2,1);

INSERT INTO partner_default_services (Version,LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,BusinessPartnerType,ServiceDefaultConfigurationID) VALUES
(1,now(),'system',now(),'system',7,1);
INSERT INTO partner_default_services (Version,LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,BusinessPartnerType,ServiceDefaultConfigurationID) VALUES
(1,now(),'system',now(),'system',8,2);


