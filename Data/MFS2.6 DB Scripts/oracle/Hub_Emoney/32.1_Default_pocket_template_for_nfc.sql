INSERT into pocket_template values (POCKET_TEMPLATE_ID_SEQ.nextval,0,sysdate,'System',sysdate,'System',1,4,NULL,'NFC Offline Pocket',4,NULL,NULL,0,100000.0000,0.0000,10000.0000,10.0000,50000.0000,10000000.0000,1000000000.0000,50,100,1000,0,153,NULL,NULL,0.0000,0,0,0,0,0,NULL,NULL,POCKET_TEMPLATE_ID_SEQ.nextval,0,'',0,1,0);


INSERT INTO pocket_template_config (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, SubscriberType, BusinessPartnerType, KYCLevel ,Commodity, PocketType, IsSuspencePocket, IsCollectorPocket, PocketTemplateID, IsDefault) VALUES (1,sysdate,'System',sysdate,'System',0,NULL,1,4,4,0,0,(select max(id) from pocket_template p),1);

Insert into ptc_group_mapping (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, groupID, ptcID) values (1, sysdate, 'System', sysdate, 'System', 1, (select max(id) from pocket_template_config p));


INSERT INTO pocket_template_config (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, SubscriberType, BusinessPartnerType, KYCLevel ,Commodity, PocketType, IsSuspencePocket, IsCollectorPocket, PocketTemplateID, IsDefault) VALUES (1,sysdate,'System',sysdate,'System',0,NULL,2,4,4,0,0,(select max(id) from pocket_template p),1);

Insert into ptc_group_mapping (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, groupID, ptcID) values (1, sysdate, 'System', sysdate, 'System', 1, (select max(id) from pocket_template_config p));

INSERT INTO pocket_template_config (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, SubscriberType, BusinessPartnerType, KYCLevel ,Commodity, PocketType, IsSuspencePocket, IsCollectorPocket, PocketTemplateID, IsDefault) VALUES (1,sysdate,'System',sysdate,'System',0,NULL,3,4,4,0,0,(select max(id) from pocket_template p),1);

Insert into ptc_group_mapping (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, groupID, ptcID) values (1, sysdate, 'System', sysdate, 'System', 1, (select max(id) from pocket_template_config p));

INSERT INTO pocket_template_config (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, SubscriberType, BusinessPartnerType, KYCLevel ,Commodity, PocketType, IsSuspencePocket, IsCollectorPocket, PocketTemplateID, IsDefault) VALUES (1,sysdate,'System',sysdate,'System',0,NULL,4,4,4,0,0,(select max(id) from pocket_template p),1);

Insert into ptc_group_mapping (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, groupID, ptcID) values (1, sysdate, 'System', sysdate, 'System', 1, (select max(id) from pocket_template_config p));

commit;