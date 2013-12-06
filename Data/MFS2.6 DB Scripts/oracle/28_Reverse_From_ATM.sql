alter table bill_payments add(
sourcemdn varchar2(20 char),
originalintxnid varchar2(100 char),
info1 varchar2(255 char),
info2 varchar2(255 char),
info3 varchar2(255 char)
);

alter table bill_payments modify (intxnid varchar2(100 char));
alter table bill_payments modify (sctlid null);

commit;