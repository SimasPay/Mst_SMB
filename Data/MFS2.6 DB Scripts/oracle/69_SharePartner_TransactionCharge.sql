
alter table share_partner add ShareType varchar2(255 char);
alter table share_partner add ShareHolderType varchar2(255 char);
alter table share_partner modify (PartnerID NULL);

commit;