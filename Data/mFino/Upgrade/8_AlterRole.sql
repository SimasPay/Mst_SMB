update user set Role='1' where Role='ROLE_ADMIN';
update user set Role='2' where Role='ROLE_SYS_ADMIN';
update user set Role='3' where Role='ROLE_GALLERY_ADMIN';
update user set Role='4' where Role='ROLE_GALLERY_MGR';
update user set Role='5' where Role='ROLE_MERCHANT_SUPPORT';
update user set Role='6' where Role='ROLE_BULK_UPLOAD_SUPPORT';
update user set Role='7' where Role='ROLE_SALES_ADMIN';
update user set Role='8' where Role='ROLE_FINANCE_TREASURY';
update user set Role='9' where Role='ROLE_FINANCE_ADMIN';
update user set Role='10' where Role='ROLE_CUSTOMER_CARE';
update user set Role='11' where Role='ROLE_CUSTOMER_CARE_MGR';
update user set Role='12' where Role='ROLE_REVIEWER';
update user set Role='13' where Role='ROLE_OP_SUPPORT';
update user set Role='14' where Role='ROLE_MERCHANT';

alter table `user` modify column `Role` INT(11) DEFAULT NULL;
alter table `role_permission` modify column `Role` INT(11) DEFAULT NULL;