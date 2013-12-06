alter table partner_services modify (CollectorPocket null);

alter table partner_services drop constraint FK_PARTNERSERVICES_POCKETBYCOL;

drop index FK_PARTNERSERVICES_POCKETBYCOL;