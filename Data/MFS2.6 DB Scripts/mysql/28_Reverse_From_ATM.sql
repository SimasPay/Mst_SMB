

alter table bill_payments
add column sourcemdn varchar(20),
add column originalintxnid varchar(100),
add column info1 varchar(255),
add column info2 varchar(255),
add column info3 varchar(255);

alter table bill_payments modify intxnid varchar(100);
alter table bill_payments modify sctlid bigint(20);
