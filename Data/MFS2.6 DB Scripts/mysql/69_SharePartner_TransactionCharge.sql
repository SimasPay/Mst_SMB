
alter table `share_partner` add column `ShareType` varchar(255);
alter table `share_partner` add column `ShareHolderType` varchar(255);
alter table `share_partner` modify column `PartnerID` bigint(20) default NULL;



