SET DEFINE OFF;

SET SCAN OFF;
--
-- Dumping data for table address
--

INSERT INTO address VALUES (1,0,sysdate,'user',sysdate,'user',NULL,'temp','temp','temp','temp','temp','temp',NULL);
INSERT INTO address VALUES (2,0,sysdate,'user',sysdate,'user',NULL,'temp','temp','temp','temp','temp','temp',NULL);

--
-- Dumping data for table subscriber
--

INSERT INTO subscriber VALUES (1,3,sysdate,'mfino(System)',sysdate,'user',1,1,0,'mfino',' ',NULL,NULL,NULL,NULL,'temp@temp.com',3,0,'NGN','WAT',0,2,1,sysdate,NULL,sysdate,'Approver','approved','user',sysdate,0,NULL,NULL,NULL,NULL,NULL,3,NULL,2,NULL,NULL,NULL,NULL,NULL,NULL,0,NULL);
INSERT INTO subscriber VALUES (2,3,sysdate,'mfino(System)',sysdate,'user',1,1,0,'dummy','subscriber',NULL,NULL,NULL,NULL,'temp@temp.com',3,0,'NGN','WAT',0,1,1,sysdate,NULL,sysdate,'Approver','approved','user',sysdate,0,NULL,NULL,NULL,NULL,NULL,3,NULL,2,NULL,NULL,NULL,NULL,NULL,NULL,0,NULL);


--
-- Dumping data for table subscriber_mdn
--

INSERT INTO subscriber_mdn VALUES (1,4,sysdate,'mfino(System)',sysdate,'user',1,'2341000',NULL,NULL,NULL,NULL,NULL,1,NULL,0,0,'C22815F5149873A9C024B40C9AF35F40AAE47270450E86771ACDB3A627244282',NULL,NULL,sysdate,NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,0);
INSERT INTO subscriber_mdn VALUES (2,4,sysdate,'system',sysdate,'system',2,'2342000',NULL,NULL,NULL,NULL,NULL,1,NULL,0,0,'C22815F5149873A9C024B40C9AF35F40AAE47270450E86771ACDB3A627244282',NULL,NULL,sysdate,NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,0);

INSERT INTO mfino_user VALUES (4,2,sysdate,'System',sysdate,'System',1,1,'mfino','0c3540b6a792673970f13f4e67d22946438e0055','mfino',' ','temp@temp.com',0,'WAT',0,0,sysdate,0,1,NULL,NULL,22,NULL,NULL,NULL,sysdate,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);

--
-- Dumping data for table partner
--

INSERT INTO partner VALUES (1,3,sysdate,'Approver(System)',sysdate,'user',1,4,1,'mfino',1,'mfino','Cooperative','','','','mfino','','1000',2,1,'',NULL,'temp',2011,'','temp@temp.com',0);

--
-- Dumping data for table pocket
--

INSERT INTO pocket VALUES (1,2,sysdate,'mfino(System)',sysdate,'user',3,1,NULL,'/YST2/P0lVQ=',0.0000,0.0000,0.0000,0,0,0,NULL,NULL,NULL,'TAkyh674z2OrwJ+e2fL0ByaHYjVFFl7v',0,1,1,sysdate,sysdate,NULL,NULL,NULL,NULL,NULL,NULL,NULL,1);
INSERT INTO pocket VALUES (2,1,sysdate,'user',sysdate,'user',5,1,NULL,'/YST2/P0lVQ=',0.0000,0.0000,0.0000,0,0,0,NULL,NULL,NULL,'E5el2XwDuhtDUUFoOgICTs3+D8hAhMJx',0,1,1,sysdate,sysdate,NULL,NULL,NULL,NULL,NULL,NULL,NULL,1);
INSERT INTO pocket VALUES (3,1,sysdate,'user',sysdate,'user',7,1,NULL,'/YST2/P0lVQ=',0.0000,0.0000,0.0000,0,0,0,NULL,NULL,NULL,'Kzex+AvXF/MHrLoCGN3+TTm+iROYg8xP',0,1,1,sysdate,sysdate,NULL,NULL,NULL,NULL,NULL,NULL,NULL,1);
INSERT INTO pocket VALUES (4,1,sysdate,'user',sysdate,'user',7,1,NULL,'/YST2/P0lVQ=',0.0000,0.0000,0.0000,0,0,0,NULL,NULL,NULL,'i5lddOOEfZn6rDispJOyrkhsPY9KyAaT',0,0,1,sysdate,sysdate,NULL,NULL,NULL,NULL,NULL,NULL,NULL,1);
INSERT INTO pocket VALUES (5,1,sysdate,'mfino(System)',sysdate,'user',4,1,NULL,'/YST2/P0lVQ=',0.0000,0.0000,0.0000,0,0,0,NULL,NULL,NULL,'WT2yq+poFsWfOBioyyrtS+atOKnufU3U',0,1,1,sysdate,sysdate,NULL,NULL,NULL,NULL,NULL,NULL,NULL,1);
INSERT INTO pocket VALUES (6,1,sysdate,'user',sysdate,'user',3,1,NULL,'/YST2/P0lVQ=',0.0000,0.0000,0.0000,0,0,0,NULL,NULL,NULL,'74mLcPcbcztdgBAV6AdFoXJ84JARkQ2x',0,0,1,sysdate,sysdate,NULL,NULL,NULL,NULL,NULL,NULL,NULL,1);
INSERT INTO pocket VALUES (7,1,sysdate,'user',sysdate,'user',11,1,NULL,'/YST2/P0lVQ=',0.0000,0.0000,0.0000,0,0,0,NULL,NULL,NULL,'4PtsDkLuncqiIU+uGjV3k1RmPZH6UpdO',0,1,1,sysdate,sysdate,NULL,NULL,NULL,NULL,NULL,NULL,NULL,1);
INSERT INTO pocket VALUES (8,1,sysdate,'system',sysdate,'system',10,2,NULL,'/YST2/P0lVQ=',0.0000,0.0000,0.0000,0,0,0,NULL,NULL,NULL,'vEwAn1V24/45VlTdPdFhWA==',0,1,1,sysdate,sysdate,NULL,NULL,NULL,NULL,NULL,NULL,NULL,1);

--
-- Dumping data for table partner_services
--

INSERT INTO partner_services VALUES (1,1,sysdate,'mfino(System)',sysdate,'user',1,1,1,1,NULL,NULL,NULL,1,2,1,2,NULL);
INSERT INTO partner_services VALUES (2,1,sysdate,'mfino(System)',sysdate,'user',1,1,1,2,NULL,NULL,NULL,1,2,1,2,NULL);
INSERT INTO partner_services VALUES (3,1,sysdate,'mfino(System)',sysdate,'user',1,1,1,3,NULL,NULL,NULL,1,2,1,2,NULL);
INSERT INTO partner_services VALUES (4,1,sysdate,'mfino(System)',sysdate,'user',1,1,1,4,NULL,NULL,NULL,1,2,1,2,NULL);
INSERT INTO partner_services VALUES (5,1,sysdate,'mfino(System)',sysdate,'user',1,1,1,5,NULL,NULL,NULL,1,2,1,2,NULL);
INSERT INTO partner_services VALUES (6,1,sysdate,'mfino(System)',sysdate,'user',1,1,1,6,NULL,NULL,NULL,1,2,1,2,NULL);
INSERT INTO partner_services VALUES (7,1,sysdate,'mfino(System)',sysdate,'user',1,1,1,7,NULL,NULL,NULL,1,2,1,2,NULL);
INSERT INTO partner_services VALUES (8,1,sysdate,'mfino(System)',sysdate,'user',1,1,1,9,NULL,NULL,NULL,1,2,1,2,1);

--
-- Dumping data for table settlement_template
--

INSERT INTO settlement_template VALUES (1,0,sysdate,'user',sysdate,'user',1,'temp_daily',5,1,1);
--
-- Dumping data for table service_settlement_cfg
--

INSERT INTO service_settlement_cfg VALUES (1,0,sysdate,'user',sysdate,'user',1,1,1,NULL,NULL,1,0,NULL,NULL);
INSERT INTO service_settlement_cfg VALUES (2,0,sysdate,'user',sysdate,'user',1,1,2,NULL,NULL,1,0,NULL,NULL);
INSERT INTO service_settlement_cfg VALUES (3,0,sysdate,'user',sysdate,'user',1,1,3,NULL,NULL,1,0,NULL,NULL);
INSERT INTO service_settlement_cfg VALUES (4,0,sysdate,'user',sysdate,'user',1,1,4,NULL,NULL,1,0,NULL,NULL);
INSERT INTO service_settlement_cfg VALUES (5,0,sysdate,'user',sysdate,'user',1,1,5,NULL,NULL,1,0,NULL,NULL);
INSERT INTO service_settlement_cfg VALUES (6,0,sysdate,'user',sysdate,'user',1,1,6,NULL,NULL,1,0,NULL,NULL);
INSERT INTO service_settlement_cfg VALUES (7,0,sysdate,'user',sysdate,'user',1,1,7,NULL,NULL,1,0,NULL,NULL);
INSERT INTO service_settlement_cfg VALUES (8,0,sysdate,'user',sysdate,'user',1,1,8,NULL,NULL,1,0,NULL,NULL);

commit;