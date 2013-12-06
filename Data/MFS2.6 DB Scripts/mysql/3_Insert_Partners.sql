
/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Current Database: `mfino`
--



--
-- Dumping data for table `address`
--

LOCK TABLES `address` WRITE;
/*!40000 ALTER TABLE `address` DISABLE KEYS */;
INSERT INTO `address` VALUES (1,0,now(),'user',now(),'user',NULL,'temp','temp','temp','temp','temp','temp',NULL),(2,0,now(),'user',now(),'user',NULL,'temp','temp','temp','temp','temp','temp',NULL);
/*!40000 ALTER TABLE `address` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `partner`
--

LOCK TABLES `partner` WRITE;
/*!40000 ALTER TABLE `partner` DISABLE KEYS */;
INSERT INTO `partner` VALUES (1,3,now(),'Approver(System)',now(),'user',1,4,1,'mfino',1,'mfino','Cooperative','','','','mfino','','1000',2,1,'',NULL,'temp',2011,'','temp@temp.com',0);
/*!40000 ALTER TABLE `partner` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `partner_services`
--

LOCK TABLES `partner_services` WRITE;
/*!40000 ALTER TABLE `partner_services` DISABLE KEYS */;
INSERT INTO `partner_services` VALUES (1,1,now(),'mfino(System)',now(),'user',1,1,1,1,NULL,NULL,NULL,1,2,1,2,NULL),(2,1,now(),'mfino(System)',now(),'user',1,1,1,2,NULL,NULL,NULL,1,2,1,2,NULL),(3,1,now(),'mfino(System)',now(),'user',1,1,1,3,NULL,NULL,NULL,1,2,1,2,NULL),(4,1,now(),'mfino(System)',now(),'user',1,1,1,4,NULL,NULL,NULL,1,2,1,2,NULL),(5,1,now(),'mfino(System)',now(),'user',1,1,1,5,NULL,NULL,NULL,1,2,1,2,NULL),(7,1,now(),'mfino(System)',now(),'user',1,1,1,9,NULL,NULL,NULL,1,2,1,2,1);
/*!40000 ALTER TABLE `partner_services` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `pocket`
--

LOCK TABLES `pocket` WRITE;
/*!40000 ALTER TABLE `pocket` DISABLE KEYS */;
INSERT INTO `pocket` VALUES (1,2,now(),'mfino(System)',now(),'user',3,1,NULL,'/YST2/P0lVQ=',0.0000,0.0000,0.0000,0,0,0,NULL,NULL,NULL,'TAkyh674z2OrwJ+e2fL0ByaHYjVFFl7v',0,1,1,now(),now(),NULL,NULL,NULL,NULL,NULL,NULL,NULL,1),(2,1,now(),'user',now(),'user',5,1,NULL,'/YST2/P0lVQ=',0.0000,0.0000,0.0000,0,0,0,NULL,NULL,NULL,'E5el2XwDuhtDUUFoOgICTs3+D8hAhMJx',0,1,1,now(),now(),NULL,NULL,NULL,NULL,NULL,NULL,NULL,1),(3,1,now(),'user',now(),'user',7,1,NULL,'/YST2/P0lVQ=',0.0000,0.0000,0.0000,0,0,0,NULL,NULL,NULL,'Kzex+AvXF/MHrLoCGN3+TTm+iROYg8xP',0,1,1,now(),now(),NULL,NULL,NULL,NULL,NULL,NULL,NULL,1),(4,1,now(),'user',now(),'user',7,1,NULL,'/YST2/P0lVQ=',0.0000,0.0000,0.0000,0,0,0,NULL,NULL,NULL,'i5lddOOEfZn6rDispJOyrkhsPY9KyAaT',0,0,1,now(),now(),NULL,NULL,NULL,NULL,NULL,NULL,NULL,1),(5,1,now(),'mfino(System)',now(),'user',4,1,NULL,'/YST2/P0lVQ=',0.0000,0.0000,0.0000,0,0,0,NULL,NULL,NULL,'WT2yq+poFsWfOBioyyrtS+atOKnufU3U',0,1,1,now(),now(),NULL,NULL,NULL,NULL,NULL,NULL,NULL,1),(6,1,now(),'user',now(),'user',3,1,NULL,'/YST2/P0lVQ=',0.0000,0.0000,0.0000,0,0,0,NULL,NULL,NULL,'74mLcPcbcztdgBAV6AdFoXJ84JARkQ2x',0,0,1,now(),now(),NULL,NULL,NULL,NULL,NULL,NULL,NULL,1),(7,1,now(),'user',now(),'user',11,1,NULL,'/YST2/P0lVQ=',0.0000,0.0000,0.0000,0,0,0,NULL,NULL,NULL,'4PtsDkLuncqiIU+uGjV3k1RmPZH6UpdO',0,1,1,now(),now(),NULL,NULL,NULL,NULL,NULL,NULL,NULL,1),(8,1,now(),'system',now(),'system',10,2,NULL,'/YST2/P0lVQ=',0.0000,0.0000,0.0000,0,0,0,NULL,NULL,NULL,'vEwAn1V24/45VlTdPdFhWA==',0,1,1,now(),now(),NULL,NULL,NULL,NULL,NULL,NULL,NULL,1);
/*!40000 ALTER TABLE `pocket` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `service_settlement_cfg`
--

LOCK TABLES `service_settlement_cfg` WRITE;
/*!40000 ALTER TABLE `service_settlement_cfg` DISABLE KEYS */;
INSERT INTO `service_settlement_cfg` VALUES (1,0,now(),'user',now(),'user',1,1,1,NULL,NULL,1,0,NULL,NULL),(2,0,now(),'user',now(),'user',1,1,2,NULL,NULL,1,0,NULL,NULL),(3,0,now(),'user',now(),'user',1,1,3,NULL,NULL,1,0,NULL,NULL),(4,0,now(),'user',now(),'user',1,1,4,NULL,NULL,1,0,NULL,NULL),(5,0,now(),'user',now(),'user',1,1,5,NULL,NULL,1,0,NULL,NULL);
/*!40000 ALTER TABLE `service_settlement_cfg` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `settlement_template`
--

LOCK TABLES `settlement_template` WRITE;
/*!40000 ALTER TABLE `settlement_template` DISABLE KEYS */;
INSERT INTO `settlement_template` VALUES (1,0,now(),'user',now(),'user',1,'temp_daily',5,1,1);
/*!40000 ALTER TABLE `settlement_template` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `subscriber`
--

LOCK TABLES `subscriber` WRITE;
/*!40000 ALTER TABLE `subscriber` DISABLE KEYS */;
INSERT INTO `subscriber` VALUES (1,3,now(),'mfino(System)',now(),'user',1,1,0,'mfino',' ',NULL,NULL,NULL,NULL,'temp@temp.com',3,0,'NGN','WAT',0,2,1,now(),NULL,now(),'Approver','approved','user',now(),0,NULL,NULL,NULL,NULL,NULL,3,NULL,2,NULL,NULL,NULL,NULL,NULL,NULL,0,NULL),(2,3,now(),'mfino(System)',now(),'user',1,1,0,'dummy','subscriber',NULL,NULL,NULL,NULL,'temp@temp.com',3,0,'NGN','WAT',0,1,1,now(),NULL,now(),'Approver','approved','user',now(),0,NULL,NULL,NULL,NULL,NULL,3,NULL,2,NULL,NULL,NULL,NULL,NULL,NULL,0,NULL);
/*!40000 ALTER TABLE `subscriber` ENABLE KEYS */;
UNLOCK TABLES;


--
-- Dumping data for table `subscriber_mdn`
--

LOCK TABLES `subscriber_mdn` WRITE;
/*!40000 ALTER TABLE `subscriber_mdn` DISABLE KEYS */;
INSERT INTO `subscriber_mdn` VALUES (1,4,now(),'mfino(System)',now(),'user',1,'2341000',NULL,NULL,NULL,NULL,NULL,1,NULL,0,0,'C22815F5149873A9C024B40C9AF35F40AAE47270450E86771ACDB3A627244282',NULL,NULL,now(),NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,0),(2,4,now(),'system',now(),'system',2,'2342000',NULL,NULL,NULL,NULL,NULL,1,NULL,0,0,'C22815F5149873A9C024B40C9AF35F40AAE47270450E86771ACDB3A627244282',NULL,NULL,now(),NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,0);
/*!40000 ALTER TABLE `subscriber_mdn` ENABLE KEYS */;
UNLOCK TABLES;

/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
