-- Script to update the favorite category names as per the mobile apps.

delete from favorite_category where id not in (1,4,5,6,7,8);

update favorite_category set CategoryName='TransferToPhone', DisplayName='Transfer To Phone' where id=1;
update favorite_category set CategoryName='TransferToBank', DisplayName='Transfer To Bank' where id=4;
update favorite_category set CategoryName='PrepaidPLN', DisplayName='Prepaid PLN' where id=5;
update favorite_category set CategoryName='PrePaidPhone', DisplayName='PrePaid Phone' where id=6;
update favorite_category set CategoryName='PostpaidPLN', DisplayName='Postpaid PLN' where id=7;
update favorite_category set CategoryName='PostpaidPhone', DisplayName='Postpaid Phone' where id=8;
