-- This scripts need to be executed only in case of fresh DB with out any subscribers / Partners in the Database

use mfino;

INSERT INTO `address` VALUES (1,0,'2011-12-09 02:07:04','user','2011-12-09 02:07:04','user',NULL,'temp','temp','temp','temp','temp','temp',NULL),(2,0,'2011-12-09 02:07:04','user','2011-12-09 02:07:04','user',NULL,'temp','temp','temp','temp','temp','temp',NULL);

INSERT INTO `subscriber` VALUES (1,3,'2011-12-09 02:16:56','mfino(System)','2011-12-09 02:07:04','user',1,1,0,'mfino',' ',NULL,NULL,NULL,NULL,'temp@temp.com',3,0,'NGN','WAT',0,2,1,'2011-12-09 02:16:56',NULL,'2011-12-09 02:15:37','Approver','approved','user','2011-12-09 02:07:04',0,NULL,NULL,NULL,NULL,NULL,3,NULL,2,NULL,NULL,NULL,NULL,NULL,NULL,0,NULL);

INSERT INTO `subscriber_mdn` VALUES (1,4,'2011-12-09 02:16:56','mfino(System)','2011-12-09 02:07:04','user',1,'2341000',NULL,NULL,NULL,NULL,NULL,1,NULL,0,0,'C22815F5149873A9C024B40C9AF35F40AAE47270450E86771ACDB3A627244282',NULL,NULL,'2011-12-09 02:16:56',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL);

INSERT INTO `user` VALUES (4,2,'2011-12-09 02:16:56','user(System)','2011-12-09 02:07:04','user',1,1,'mfino','0c3540b6a792673970f13f4e67d22946438e0055','mfino',' ','temp@temp.com',0,'WAT',0,0,'2011-12-09 02:16:56',0,1,NULL,NULL,22,NULL,NULL,NULL,'2011-12-09 02:16:56',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);

INSERT INTO `partner` VALUES (1,3,'2011-12-09 02:16:56','Approver(System)','2011-12-09 02:07:04','user',1,4,1,'mfino',1,'mfino','Cooperative','','','','mfino','','1000',2,1,'',NULL,'temp',2011,'','temp@temp.com',0);

INSERT INTO `pocket` VALUES (1,2,'2011-12-09 02:16:56','mfino(System)','2011-12-09 02:07:04','user',3,1,NULL,'/YST2/P0lVQ=',0.0000,0.0000,0.0000,0,0,0,NULL,NULL,NULL,'TAkyh674z2OrwJ+e2fL0ByaHYjVFFl7v',0,1,1,'2011-12-09 02:16:56','2011-12-09 02:16:56',NULL,NULL,NULL,NULL,NULL,NULL,NULL,1),(2,1,'2011-12-09 02:07:04','user','2011-12-09 02:07:04','user',5,1,NULL,'/YST2/P0lVQ=',0.0000,0.0000,0.0000,0,0,0,NULL,NULL,NULL,'E5el2XwDuhtDUUFoOgICTs3+D8hAhMJx',0,1,1,'2011-12-09 02:16:56','2011-12-09 02:16:56',NULL,NULL,NULL,NULL,NULL,NULL,NULL,1),(3,1,'2011-12-09 02:17:46','user','2011-12-09 02:08:13','user',7,1,NULL,'/YST2/P0lVQ=',0.0000,0.0000,0.0000,0,0,0,NULL,NULL,NULL,'Kzex+AvXF/MHrLoCGN3+TTm+iROYg8xP',0,1,1,'2011-12-09 02:08:13',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,1),(4,1,'2011-12-09 02:17:25','user','2011-12-09 02:08:19','user',7,1,NULL,'/YST2/P0lVQ=',0.0000,0.0000,0.0000,0,0,0,NULL,NULL,NULL,'i5lddOOEfZn6rDispJOyrkhsPY9KyAaT',0,0,1,'2011-12-09 02:08:19',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,1),(5,1,'2011-12-09 02:16:56','mfino(System)','2011-12-09 02:09:07','user',4,1,NULL,'/YST2/P0lVQ=',0.0000,0.0000,0.0000,0,0,0,NULL,NULL,NULL,'WT2yq+poFsWfOBioyyrtS+atOKnufU3U',0,1,1,'2011-12-09 02:16:56','2011-12-09 02:16:56',NULL,NULL,NULL,NULL,NULL,NULL,NULL,1),(6,1,'2011-12-09 02:17:52','user','2011-12-09 02:09:34','user',3,1,NULL,'/YST2/P0lVQ=',0.0000,0.0000,0.0000,0,0,0,NULL,NULL,NULL,'74mLcPcbcztdgBAV6AdFoXJ84JARkQ2x',0,0,1,'2011-12-09 02:09:34',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,1);

INSERT INTO `partner_services` VALUES (1,1,'2011-12-09 02:16:56','mfino(System)','2011-12-09 02:10:51','user',1,NULL,1,1,1,NULL,NULL,NULL,1,2,1,2, NULL),(2,1,'2011-12-09 02:16:56','mfino(System)','2011-12-09 02:11:15','user',1,NULL,1,1,2,NULL,NULL,NULL,1,2,1,2, NULL),(3,1,'2011-12-09 02:16:56','mfino(System)','2011-12-09 02:11:36','user',1,NULL,1,1,3,NULL,NULL,NULL,1,2,1,2, NULL),(4,1,'2011-12-09 02:16:56','mfino(System)','2011-12-09 02:11:52','user',1,NULL,1,1,4,NULL,NULL,NULL,1,2,1,2, NULL),(5,1,'2011-12-09 02:16:56','mfino(System)','2011-12-09 02:12:11','user',1,NULL,1,1,5,NULL,NULL,NULL,1,2,1,2, NULL);

INSERT INTO `settlement_template` VALUES (1,0,'2011-12-09 02:10:25','user','2011-12-09 02:10:25','user',1,'temp_daily',5,1,1);

INSERT INTO `service_settlement_config` VALUES (1,0,'2011-12-09 02:10:51','user','2011-12-09 02:10:51','user',1,1,1,NULL,NULL,1,0),(2,0,'2011-12-09 02:11:15','user','2011-12-09 02:11:15','user',1,1,2,NULL,NULL,1,0),(3,0,'2011-12-09 02:11:36','user','2011-12-09 02:11:36','user',1,1,3,NULL,NULL,1,0),(4,0,'2011-12-09 02:11:52','user','2011-12-09 02:11:52','user',1,1,4,NULL,NULL,1,0),(5,0,'2011-12-09 02:12:11','user','2011-12-09 02:12:11','user',1,1,5,NULL,NULL,1,0);

update system_parameters set ParameterValue=3 where ParameterName='suspense.pocket.id';
update system_parameters set ParameterValue=4 where ParameterName='charges.pocket.id';
update system_parameters set ParameterValue=2341000 where ParameterName='platform.dummy.mdn';
update system_parameters set ParameterValue=1 where ParameterName='global.sva.pocket.id';
update system_parameters set ParameterValue=5 where ParameterName='global.account.pocket.id';
update system_parameters set ParameterValue=6 where ParameterName='tax.pocket.id';
