use mfino;
-- Removes the unnecessary values from language drop-down.
Delete from enum_text where tagid = 5134 and  enumcode = 1;

-- Removes the unnecessary values from Currency drop-down.
Delete from enum_text where tagid = 5135 and  enumcode = 'USD';
Delete from enum_text where tagid = 5135 and  enumcode = 'IDR';
Delete from enum_text where tagid = 5135 and  enumcode = 'UNK';

-- Removes the unnecessary values from Time zone drop-down.
Delete from enum_text where tagid = 5136 and  enumcode = 'Asia/Jakarta';
Delete from enum_text where tagid = 5136 and  enumcode = 'Asia/Makassar';
Delete from enum_text where tagid = 5136 and  enumcode = 'Asia/Jayapura';

-- Removes the unnecessary values from Subscriber Type drop-down.
Delete from enum_text where tagid = 5053 and  enumcode = '1';

-- Removes the unnecessary values from Settlement Type drop-down.
Delete from enum_text where tagid = 6024 and  enumcode = '5';
Delete from enum_text where tagid = 6024 and  enumcode = '4';

-- Removes the unnecessary values from Bank Account Card type drop-down.
Delete from enum_text where tagid = 5202 and  enumcode = '0';
Delete from enum_text where tagid = 5202 and  enumcode = '1';
Delete from enum_text where tagid = 5202 and  enumcode = '2';

-- Removes the unnecessary values from Pocket type drop-down.
Delete from enum_text where tagid = 5049 and  enumcode = '2';

-- Removes the unnecessary values from commodity type drop-down.
Delete from enum_text where tagid = 5059 and  enumcode = '0';
Delete from enum_text where tagid = 5059 and  enumcode = '1';
Delete from enum_text where tagid = 5059 and  enumcode = '2';

-- Removes the unnecessary values from Transaction UI category.
Delete from enum_text where tagid = 5636 and  enumcode = '0';
Delete from enum_text where tagid = 5636 and  enumcode = '1';
Delete from enum_text where tagid = 5636 and  enumcode = '2';
Delete from enum_text where tagid = 5636 and  enumcode = '3';
Delete from enum_text where tagid = 5636 and  enumcode = '4';
Delete from enum_text where tagid = 5636 and  enumcode = '5';
Delete from enum_text where tagid = 5636 and  enumcode = '7';
Delete from enum_text where tagid = 5636 and  enumcode = '8';
Delete from enum_text where tagid = 5636 and  enumcode = '9';
Delete from enum_text where tagid = 5636 and  enumcode = '10';
Delete from enum_text where tagid = 5636 and  enumcode = '11';
Delete from enum_text where tagid = 5636 and  enumcode = '18';
Delete from enum_text where tagid = 5636 and  enumcode = '19';
Delete from enum_text where tagid = 5636 and  enumcode = '20';
Delete from enum_text where tagid = 5636 and  enumcode = '21';
Delete from enum_text where tagid = 5636 and  enumcode = '22';
Delete from enum_text where tagid = 5636 and  enumcode = '23';
Delete from enum_text where tagid = 5636 and  enumcode = '24';
Delete from enum_text where tagid = 5636 and  enumcode = '25';
Delete from enum_text where tagid = 5636 and  enumcode = '26';
Delete from enum_text where tagid = 5636 and  enumcode = '27';
Delete from enum_text where tagid = 5636 and  enumcode = '28';
Delete from enum_text where tagid = 5636 and  enumcode = '29';
Delete from enum_text where tagid = 5636 and  enumcode = '30';

update enum_text set DisplayText='Bank_To_Bank_Transfer' where tagid = 5636 and  enumcode = '6';
update enum_text set DisplayText='EMoney_To_Bank_Transfer' where tagid = 5636 and  enumcode = '16';
update enum_text set DisplayText='Bank_To_EMoney_Transfer' where tagid = 5636 and  enumcode = '17';


-- Removes the unnecessary values from commodity type drop-down.
Delete from enum_text where tagid = 5352 and  enumcode in (3,4,5,6,7,9,11,12,13,14,16,17,18,19,21,24);
Update enum_text set DisplayText ="Partner" where tagid=5352 and enumcode=22;
Update enum_text set DisplayText ="Agent" where tagid=5352 and enumcode=23;
Update enum_text set DisplayText ="Audit" where tagid=5352 and enumcode=8;
Update enum_text set DisplayText ="Finance" where tagid=5352 and enumcode=15;

Update kyc_fields set KYCFieldsLevelID=2 where KYCFieldsName='subsrefaccount';

-- Removes the unnecessary values from user Status and restrictions.
Delete from enum_text where tagid =5268 and  enumcode = '3';
Delete from enum_text where tagid =5268 and  enumcode = '4';

Delete from enum_text where tagid =5264 and  enumcode = '1';
Delete from enum_text where tagid =5264 and  enumcode = '8';

-- Removes the unnecessary values from status for subscribers and partners.
Delete from enum_text where tagid =5024 and  enumcode = '4';
Delete from enum_text where tagid =5155 and  enumcode = '4';
Delete from enum_text where tagid =6017 and  enumcode = '4';