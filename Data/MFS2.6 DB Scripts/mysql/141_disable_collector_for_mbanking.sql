alter table `partner_services` modify column `partnerid` bigint(20) default null;
alter table `partner_services` drop foreign key  `FK_PartnerServices_PocketByCollectorPocket` ;
alter table `partner_services` drop index  `FK_PartnerServices_PocketByCollectorPocket`;
 