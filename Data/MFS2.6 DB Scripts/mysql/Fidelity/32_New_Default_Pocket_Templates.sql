Update pocket_template set Description = 'Subscriber Emoney-UnBanked' where ID=1;

Update pocket_template set Description = 'Subscriber Emoney-SemiBanked' where ID=2;

Update pocket_template set Description = 'Subscriber Emoney-FullyBanked' where ID=3;

Update pocket_template set Description = 'Partner Bank' where ID=4;

Update pocket_template set Description = 'Collector Pocket' where ID=5;

Update pocket_template set Description = 'Agent Bank' where ID=6;

Update pocket_template set Description = 'Suspence Transit Pocket' where ID=7;

Update pocket_template set Description = 'Subscriber Bank',BankCode=152 where ID=8;

Update pocket_template set Description = 'MFS Pool A/c' where ID=11;

INSERT into pocket_template values (12,0,sysdate,'System',sysdate,'System',1,1,NULL,'Agent Emoney',4,NULL,NULL,0,100000.0000,0.0000,10000.0000,10.0000,50000.0000,10000000.0000,1000000000.0000,50,100,1000,0,9999,NULL,NULL,0.0000,0,0,0,0,0,NULL,NULL,'12',0,'',0,1,0);

INSERT into pocket_template values (13,0,sysdate,'System',sysdate,'System',1,1,NULL,'Partner Emoney',4,NULL,NULL,0,100000.0000,0.0000,10000.0000,10.0000,50000.0000,10000000.0000,1000000000.0000,50,100,1000,0,9999,NULL,NULL,0.0000,0,0,0,0,0,NULL,NULL,'13',0,'',0,1,0);

INSERT into pocket_template values (14,0,sysdate,'System',sysdate,'System',1,1,NULL,'Tax Pocket',4,NULL,NULL,0,100000.0000,0.0000,10000.0000,10.0000,50000.0000,10000000.0000,1000000000.0000,50,100,1000,0,9999,NULL,NULL,0.0000,0,0,0,0,0,NULL,NULL,'14',0,'',0,1,0);

INSERT into pocket_template values (15,0,sysdate,'System',sysdate,'System',1,1,NULL,'Reversal Transit Pocket',4,NULL,NULL,0,10000000.0000,0.0000,10000.0000,0.0000,10000000.0000,100000000.0000,10000000000.0000,100000,10000000,100000000,0,9999,NULL,NULL,0.0000,0,0,0,0,0,NULL,NULL,'15',0,'',0,1,1);

INSERT into pocket_template values (16,0,sysdate,'System',sysdate,'System',1,1,NULL,'Suspence Charges Pocket',4,NULL,NULL,0,10000000.0000,0.0000,10000.0000,0.0000,10000000.0000,100000000.0000,10000000000.0000,100000,10000000,100000000,0,9999,NULL,NULL,0.0000,0,0,0,0,0,NULL,NULL,'16',0,'',0,1,1)

Update pocket set PocketTemplateID=13 where ID in (1,9);
Update pocket set PocketTemplateID=16 where ID=4;
Update pocket set PocketTemplateID=14 where ID=6;
Update pocket set PocketTemplateID=15 where ID=13;

Delete from pocket_template_config;

DROP SEQUENCE pocket_template_config_ID_SEQ;

CREATE SEQUENCE  pocket_template_config_ID_SEQ MINVALUE 1 MAXVALUE 999999999999999999999999 INCREMENT BY 1  NOCYCLE ;

INSERT INTO pocket_template_config (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, SubscriberType, BusinessPartnerType, KYCLevel ,Commodity, PocketType, IsSuspencePocket, IsCollectorPocket, PocketTemplateID, IsDefault) VALUES (0,sysdate,'System',sysdate,'System',0,NULL,1,4,1,0,0,1,1);
INSERT INTO pocket_template_config (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, SubscriberType, BusinessPartnerType, KYCLevel ,Commodity, PocketType, IsSuspencePocket, IsCollectorPocket, PocketTemplateID, IsDefault) VALUES (0,sysdate,'System',sysdate,'System',0,NULL,2,4,1,0,0,2,1);
INSERT INTO pocket_template_config (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, SubscriberType, BusinessPartnerType, KYCLevel ,Commodity, PocketType, IsSuspencePocket, IsCollectorPocket, PocketTemplateID, IsDefault) VALUES (0,sysdate,'System',sysdate,'System',0,NULL,3,4,1,0,0,3,1);
INSERT INTO pocket_template_config (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, SubscriberType, BusinessPartnerType, KYCLevel ,Commodity, PocketType, IsSuspencePocket, IsCollectorPocket, PocketTemplateID, IsDefault) VALUES (0,sysdate,'System',sysdate,'System',0,NULL,3,4,3,0,0,8,1);
INSERT INTO pocket_template_config (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, SubscriberType, BusinessPartnerType, KYCLevel ,Commodity, PocketType, IsSuspencePocket, IsCollectorPocket, PocketTemplateID, IsDefault) VALUES (0,sysdate,'System',sysdate,'System',2,4,3,4,1,0,0,12,1);
INSERT INTO pocket_template_config (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, SubscriberType, BusinessPartnerType, KYCLevel ,Commodity, PocketType, IsSuspencePocket, IsCollectorPocket, PocketTemplateID, IsDefault) VALUES (0,sysdate,'System',sysdate,'System',2,4,3,4,3,0,0,6,1);
INSERT INTO pocket_template_config (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, SubscriberType, BusinessPartnerType, KYCLevel ,Commodity, PocketType, IsSuspencePocket, IsCollectorPocket, PocketTemplateID, IsDefault) VALUES (0,sysdate,'System',sysdate,'System',2,4,3,4,1,0,1,5,1);
INSERT INTO pocket_template_config (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, SubscriberType, BusinessPartnerType, KYCLevel ,Commodity, PocketType, IsSuspencePocket, IsCollectorPocket, PocketTemplateID, IsDefault) VALUES (0,sysdate,'System',sysdate,'System',2,5,3,4,1,0,0,12,1);
INSERT INTO pocket_template_config (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, SubscriberType, BusinessPartnerType, KYCLevel ,Commodity, PocketType, IsSuspencePocket, IsCollectorPocket, PocketTemplateID, IsDefault) VALUES (0,sysdate,'System',sysdate,'System',2,5,3,4,3,0,0,6,1);
INSERT INTO pocket_template_config (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, SubscriberType, BusinessPartnerType, KYCLevel ,Commodity, PocketType, IsSuspencePocket, IsCollectorPocket, PocketTemplateID, IsDefault) VALUES (0,sysdate,'System',sysdate,'System',2,5,3,4,1,0,1,5,1);
INSERT INTO pocket_template_config (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, SubscriberType, BusinessPartnerType, KYCLevel ,Commodity, PocketType, IsSuspencePocket, IsCollectorPocket, PocketTemplateID, IsDefault) VALUES (0,sysdate,'System',sysdate,'System',2,6,3,4,1,0,0,12,1);
INSERT INTO pocket_template_config (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, SubscriberType, BusinessPartnerType, KYCLevel ,Commodity, PocketType, IsSuspencePocket, IsCollectorPocket, PocketTemplateID, IsDefault) VALUES (0,sysdate,'System',sysdate,'System',2,6,3,4,3,0,0,6,1);
INSERT INTO pocket_template_config (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, SubscriberType, BusinessPartnerType, KYCLevel ,Commodity, PocketType, IsSuspencePocket, IsCollectorPocket, PocketTemplateID, IsDefault) VALUES (0,sysdate,'System',sysdate,'System',2,6,3,4,1,0,1,5,1);
INSERT INTO pocket_template_config (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, SubscriberType, BusinessPartnerType, KYCLevel ,Commodity, PocketType, IsSuspencePocket, IsCollectorPocket, PocketTemplateID, IsDefault) VALUES (1,sysdate,'System',sysdate,'System',2,0,3,4,1,0,0,13,0);
INSERT INTO pocket_template_config (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, SubscriberType, BusinessPartnerType, KYCLevel ,Commodity, PocketType, IsSuspencePocket, IsCollectorPocket, PocketTemplateID, IsDefault) VALUES (0,sysdate,'System',sysdate,'System',2,0,3,4,3,0,0,4,1);
INSERT INTO pocket_template_config (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, SubscriberType, BusinessPartnerType, KYCLevel ,Commodity, PocketType, IsSuspencePocket, IsCollectorPocket, PocketTemplateID, IsDefault) VALUES (1,sysdate,'System',sysdate,'System',2,0,3,4,1,0,1,5,0);
INSERT INTO pocket_template_config (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, SubscriberType, BusinessPartnerType, KYCLevel ,Commodity, PocketType, IsSuspencePocket, IsCollectorPocket, PocketTemplateID, IsDefault) VALUES (2,sysdate,'System',sysdate,'System',2,0,3,4,1,1,0,7,0);
INSERT INTO pocket_template_config (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, SubscriberType, BusinessPartnerType, KYCLevel ,Commodity, PocketType, IsSuspencePocket, IsCollectorPocket, PocketTemplateID, IsDefault) VALUES (1,sysdate,'System',sysdate,'System',2,0,3,4,1,1,0,16,0);
INSERT INTO pocket_template_config (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, SubscriberType, BusinessPartnerType, KYCLevel ,Commodity, PocketType, IsSuspencePocket, IsCollectorPocket, PocketTemplateID, IsDefault) VALUES (0,sysdate,'System',sysdate,'System',2,0,3,4,1,0,1,11,1);
INSERT INTO pocket_template_config (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, SubscriberType, BusinessPartnerType, KYCLevel ,Commodity, PocketType, IsSuspencePocket, IsCollectorPocket, PocketTemplateID, IsDefault) VALUES (0,sysdate,'System',sysdate,'System',2,0,3,4,1,1,0,15,1);
INSERT INTO pocket_template_config (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, SubscriberType, BusinessPartnerType, KYCLevel ,Commodity, PocketType, IsSuspencePocket, IsCollectorPocket, PocketTemplateID, IsDefault) VALUES (0,sysdate,'System',sysdate,'System',2,0,3,4,1,0,0,14,1);
INSERT INTO pocket_template_config (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, SubscriberType, BusinessPartnerType, KYCLevel ,Commodity, PocketType, IsSuspencePocket, IsCollectorPocket, PocketTemplateID, IsDefault) VALUES (0,sysdate,'System',sysdate,'System',2,1,3,4,1,0,0,13,1);
INSERT INTO pocket_template_config (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, SubscriberType, BusinessPartnerType, KYCLevel ,Commodity, PocketType, IsSuspencePocket, IsCollectorPocket, PocketTemplateID, IsDefault) VALUES (0,sysdate,'System',sysdate,'System',2,1,3,4,3,0,0,4,1);
INSERT INTO pocket_template_config (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, SubscriberType, BusinessPartnerType, KYCLevel ,Commodity, PocketType, IsSuspencePocket, IsCollectorPocket, PocketTemplateID, IsDefault) VALUES (0,sysdate,'System',sysdate,'System',2,1,3,4,1,0,1,5,1);
INSERT INTO pocket_template_config (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, SubscriberType, BusinessPartnerType, KYCLevel ,Commodity, PocketType, IsSuspencePocket, IsCollectorPocket, PocketTemplateID, IsDefault) VALUES (0,sysdate,'System',sysdate,'System',2,1,3,4,1,1,0,7,1);
INSERT INTO pocket_template_config (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, SubscriberType, BusinessPartnerType, KYCLevel ,Commodity, PocketType, IsSuspencePocket, IsCollectorPocket, PocketTemplateID, IsDefault) VALUES (0,sysdate,'System',sysdate,'System',2,2,3,4,1,0,0,13,1);
INSERT INTO pocket_template_config (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, SubscriberType, BusinessPartnerType, KYCLevel ,Commodity, PocketType, IsSuspencePocket, IsCollectorPocket, PocketTemplateID, IsDefault) VALUES (0,sysdate,'System',sysdate,'System',2,2,3,4,3,0,0,4,1);
INSERT INTO pocket_template_config (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, SubscriberType, BusinessPartnerType, KYCLevel ,Commodity, PocketType, IsSuspencePocket, IsCollectorPocket, PocketTemplateID, IsDefault) VALUES (0,sysdate,'System',sysdate,'System',2,2,3,4,1,0,1,5,1);
INSERT INTO pocket_template_config (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, SubscriberType, BusinessPartnerType, KYCLevel ,Commodity, PocketType, IsSuspencePocket, IsCollectorPocket, PocketTemplateID, IsDefault) VALUES (0,sysdate,'System',sysdate,'System',2,2,3,4,1,1,0,7,1);
INSERT INTO pocket_template_config (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, SubscriberType, BusinessPartnerType, KYCLevel ,Commodity, PocketType, IsSuspencePocket, IsCollectorPocket, PocketTemplateID, IsDefault) VALUES (0,sysdate,'System',sysdate,'System',2,3,3,4,1,0,0,13,1);
INSERT INTO pocket_template_config (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, SubscriberType, BusinessPartnerType, KYCLevel ,Commodity, PocketType, IsSuspencePocket, IsCollectorPocket, PocketTemplateID, IsDefault) VALUES (0,sysdate,'System',sysdate,'System',2,3,3,4,3,0,0,4,1);
INSERT INTO pocket_template_config (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, SubscriberType, BusinessPartnerType, KYCLevel ,Commodity, PocketType, IsSuspencePocket, IsCollectorPocket, PocketTemplateID, IsDefault) VALUES (0,sysdate,'System',sysdate,'System',2,3,3,4,1,0,1,5,1);
INSERT INTO pocket_template_config (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, SubscriberType, BusinessPartnerType, KYCLevel ,Commodity, PocketType, IsSuspencePocket, IsCollectorPocket, PocketTemplateID, IsDefault) VALUES (0,sysdate,'System',sysdate,'System',2,3,3,4,1,1,0,7,1);
INSERT INTO pocket_template_config (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, SubscriberType, BusinessPartnerType, KYCLevel ,Commodity, PocketType, IsSuspencePocket, IsCollectorPocket, PocketTemplateID, IsDefault) VALUES (0,sysdate,'System',sysdate,'System',2,7,3,4,1,0,0,13,1);
INSERT INTO pocket_template_config (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, SubscriberType, BusinessPartnerType, KYCLevel ,Commodity, PocketType, IsSuspencePocket, IsCollectorPocket, PocketTemplateID, IsDefault) VALUES (0,sysdate,'System',sysdate,'System',2,7,3,4,3,0,0,4,1);
INSERT INTO pocket_template_config (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, SubscriberType, BusinessPartnerType, KYCLevel ,Commodity, PocketType, IsSuspencePocket, IsCollectorPocket, PocketTemplateID, IsDefault) VALUES (0,sysdate,'System',sysdate,'System',2,7,3,4,1,0,1,5,1);
INSERT INTO pocket_template_config (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, SubscriberType, BusinessPartnerType, KYCLevel ,Commodity, PocketType, IsSuspencePocket, IsCollectorPocket, PocketTemplateID, IsDefault) VALUES (0,sysdate,'System',sysdate,'System',2,7,3,4,1,1,0,7,1);
INSERT INTO pocket_template_config (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, SubscriberType, BusinessPartnerType, KYCLevel ,Commodity, PocketType, IsSuspencePocket, IsCollectorPocket, PocketTemplateID, IsDefault) VALUES (0,sysdate,'System',sysdate,'System',2,8,3,4,1,0,0,13,1);
INSERT INTO pocket_template_config (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, SubscriberType, BusinessPartnerType, KYCLevel ,Commodity, PocketType, IsSuspencePocket, IsCollectorPocket, PocketTemplateID, IsDefault) VALUES (0,sysdate,'System',sysdate,'System',2,8,3,4,3,0,0,4,1);
INSERT INTO pocket_template_config (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, SubscriberType, BusinessPartnerType, KYCLevel ,Commodity, PocketType, IsSuspencePocket, IsCollectorPocket, PocketTemplateID, IsDefault) VALUES (0,sysdate,'System',sysdate,'System',2,8,3,4,1,0,1,5,1);
INSERT INTO pocket_template_config (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, SubscriberType, BusinessPartnerType, KYCLevel ,Commodity, PocketType, IsSuspencePocket, IsCollectorPocket, PocketTemplateID, IsDefault) VALUES (0,sysdate,'System',sysdate,'System',2,8,3,4,1,1,0,7,1);
INSERT INTO pocket_template_config (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, SubscriberType, BusinessPartnerType, KYCLevel ,Commodity, PocketType, IsSuspencePocket, IsCollectorPocket, PocketTemplateID, IsDefault) VALUES (0,sysdate,'System',sysdate,'System',2,9,3,4,1,0,0,13,1);
INSERT INTO pocket_template_config (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, SubscriberType, BusinessPartnerType, KYCLevel ,Commodity, PocketType, IsSuspencePocket, IsCollectorPocket, PocketTemplateID, IsDefault) VALUES (0,sysdate,'System',sysdate,'System',2,9,3,4,3,0,0,4,1);
INSERT INTO pocket_template_config (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, SubscriberType, BusinessPartnerType, KYCLevel ,Commodity, PocketType, IsSuspencePocket, IsCollectorPocket, PocketTemplateID, IsDefault) VALUES (0,sysdate,'System',sysdate,'System',2,9,3,4,1,0,1,5,1);
INSERT INTO pocket_template_config (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, SubscriberType, BusinessPartnerType, KYCLevel ,Commodity, PocketType, IsSuspencePocket, IsCollectorPocket, PocketTemplateID, IsDefault) VALUES (0,sysdate,'System',sysdate,'System',2,9,3,4,1,1,0,7,1);


Delete from ptc_group_mapping;

DROP SEQUENCE ptc_group_mapping_ID_SEQ;

CREATE SEQUENCE  ptc_group_ID_SEQ MINVALUE 1 MAXVALUE 999999999999999999999999 INCREMENT BY 1  NOCYCLE ;

INSERT INTO ptc_group_mapping (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, groupID, ptcID ) VALUES (1,sysdate,'System',sysdate,'System',1,1);
INSERT INTO ptc_group_mapping (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, groupID, ptcID ) VALUES (1,sysdate,'System',sysdate,'System',1,2);
INSERT INTO ptc_group_mapping (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, groupID, ptcID ) VALUES (1,sysdate,'System',sysdate,'System',1,3);
INSERT INTO ptc_group_mapping (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, groupID, ptcID ) VALUES (1,sysdate,'System',sysdate,'System',1,4);
INSERT INTO ptc_group_mapping (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, groupID, ptcID ) VALUES (1,sysdate,'System',sysdate,'System',1,5);
INSERT INTO ptc_group_mapping (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, groupID, ptcID ) VALUES (1,sysdate,'System',sysdate,'System',1,6);
INSERT INTO ptc_group_mapping (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, groupID, ptcID ) VALUES (1,sysdate,'System',sysdate,'System',1,7);
INSERT INTO ptc_group_mapping (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, groupID, ptcID ) VALUES (1,sysdate,'System',sysdate,'System',1,8);
INSERT INTO ptc_group_mapping (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, groupID, ptcID ) VALUES (1,sysdate,'System',sysdate,'System',1,9);
INSERT INTO ptc_group_mapping (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, groupID, ptcID ) VALUES (1,sysdate,'System',sysdate,'System',1,10);
INSERT INTO ptc_group_mapping (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, groupID, ptcID ) VALUES (1,sysdate,'System',sysdate,'System',1,11);
INSERT INTO ptc_group_mapping (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, groupID, ptcID ) VALUES (1,sysdate,'System',sysdate,'System',1,12);
INSERT INTO ptc_group_mapping (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, groupID, ptcID ) VALUES (1,sysdate,'System',sysdate,'System',1,13);
INSERT INTO ptc_group_mapping (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, groupID, ptcID ) VALUES (1,sysdate,'System',sysdate,'System',1,14);
INSERT INTO ptc_group_mapping (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, groupID, ptcID ) VALUES (1,sysdate,'System',sysdate,'System',1,15);
INSERT INTO ptc_group_mapping (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, groupID, ptcID ) VALUES (1,sysdate,'System',sysdate,'System',1,16);
INSERT INTO ptc_group_mapping (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, groupID, ptcID ) VALUES (1,sysdate,'System',sysdate,'System',1,17);
INSERT INTO ptc_group_mapping (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, groupID, ptcID ) VALUES (1,sysdate,'System',sysdate,'System',1,18);
INSERT INTO ptc_group_mapping (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, groupID, ptcID ) VALUES (1,sysdate,'System',sysdate,'System',1,19);
INSERT INTO ptc_group_mapping (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, groupID, ptcID ) VALUES (1,sysdate,'System',sysdate,'System',1,20);
INSERT INTO ptc_group_mapping (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, groupID, ptcID ) VALUES (1,sysdate,'System',sysdate,'System',1,21);
INSERT INTO ptc_group_mapping (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, groupID, ptcID ) VALUES (1,sysdate,'System',sysdate,'System',1,22);
INSERT INTO ptc_group_mapping (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, groupID, ptcID ) VALUES (1,sysdate,'System',sysdate,'System',1,23);
INSERT INTO ptc_group_mapping (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, groupID, ptcID ) VALUES (1,sysdate,'System',sysdate,'System',1,24);
INSERT INTO ptc_group_mapping (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, groupID, ptcID ) VALUES (1,sysdate,'System',sysdate,'System',1,25);
INSERT INTO ptc_group_mapping (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, groupID, ptcID ) VALUES (1,sysdate,'System',sysdate,'System',1,26);
INSERT INTO ptc_group_mapping (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, groupID, ptcID ) VALUES (1,sysdate,'System',sysdate,'System',1,27);
INSERT INTO ptc_group_mapping (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, groupID, ptcID ) VALUES (1,sysdate,'System',sysdate,'System',1,28);
INSERT INTO ptc_group_mapping (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, groupID, ptcID ) VALUES (1,sysdate,'System',sysdate,'System',1,29);
INSERT INTO ptc_group_mapping (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, groupID, ptcID ) VALUES (1,sysdate,'System',sysdate,'System',1,30);
INSERT INTO ptc_group_mapping (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, groupID, ptcID ) VALUES (1,sysdate,'System',sysdate,'System',1,31);
INSERT INTO ptc_group_mapping (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, groupID, ptcID ) VALUES (1,sysdate,'System',sysdate,'System',1,32);
INSERT INTO ptc_group_mapping (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, groupID, ptcID ) VALUES (1,sysdate,'System',sysdate,'System',1,33);
INSERT INTO ptc_group_mapping (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, groupID, ptcID ) VALUES (1,sysdate,'System',sysdate,'System',1,34);
INSERT INTO ptc_group_mapping (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, groupID, ptcID ) VALUES (1,sysdate,'System',sysdate,'System',1,35);
INSERT INTO ptc_group_mapping (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, groupID, ptcID ) VALUES (1,sysdate,'System',sysdate,'System',1,36);
INSERT INTO ptc_group_mapping (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, groupID, ptcID ) VALUES (1,sysdate,'System',sysdate,'System',1,37);
INSERT INTO ptc_group_mapping (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, groupID, ptcID ) VALUES (1,sysdate,'System',sysdate,'System',1,38);
INSERT INTO ptc_group_mapping (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, groupID, ptcID ) VALUES (1,sysdate,'System',sysdate,'System',1,39);
INSERT INTO ptc_group_mapping (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, groupID, ptcID ) VALUES (1,sysdate,'System',sysdate,'System',1,40);
INSERT INTO ptc_group_mapping (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, groupID, ptcID ) VALUES (1,sysdate,'System',sysdate,'System',1,41);
INSERT INTO ptc_group_mapping (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, groupID, ptcID ) VALUES (1,sysdate,'System',sysdate,'System',1,42);
INSERT INTO ptc_group_mapping (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, groupID, ptcID ) VALUES (1,sysdate,'System',sysdate,'System',1,43);
INSERT INTO ptc_group_mapping (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, groupID, ptcID ) VALUES (1,sysdate,'System',sysdate,'System',1,44);
INSERT INTO ptc_group_mapping (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, groupID, ptcID ) VALUES (1,sysdate,'System',sysdate,'System',1,45);

commit;