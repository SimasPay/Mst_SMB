-- inserts into System Parametrs table
use mfino;
DELETE FROM `system_parameters`;
ALTER TABLE `system_parameters` AUTO_INCREMENT = 1;

insert into system_parameters values(1, 1, now(), 'System', now(), 'system', 'otp.length', '4', 'One Time Password Length');
insert into system_parameters values(2, 1, now(), 'System', now(), 'system', 'default.currency.code', 'NGN', 'Default Currecny Code');
insert into system_parameters values(3, 1, now(), 'System', now(), 'system', 'sctl.timeout', '600000', 'SCTL Timeout (ms)');
insert into system_parameters values(4, 1, now(), 'System', now(), 'system', 'timezone', 'WAT', 'Time zone');
insert into system_parameters values(5, 1, now(), 'System', now(), 'system', 'wrong.pin.count', '3', 'Wrong Pin Count');
insert into system_parameters values(6, 1, now(), 'System', now(), 'system', 'suspense.pocket.id', '', 'Suspense Pocket Id');
insert into system_parameters values(7, 1, now(), 'System', now(), 'system', 'charges.pocket.id', '', 'Charges Pocket Id');
insert into system_parameters values(8, 1, now(), 'System', now(), 'system', 'platform.dummy.mdn', '', 'Platform MDN');
insert into system_parameters values(9, 1, now(), 'System', now(), 'system', 'global.sva.pocket.id', '', 'Global SVA Pocket Id');
insert into system_parameters values(10, 1, now(), 'System', now(), 'system', 'global.account.pocket.id', '', 'Global Account Pocket Number');
insert into system_parameters values(11, 1, now(), 'System', now(), 'system', 'tax.pocket.id', '', 'Tax Pocket Id');
insert into system_parameters values(12, 1, now(), 'System', now(), 'system', 'tax.percentage', '5', 'Tax Percentage');
insert into system_parameters values(13, 1, now(), 'System', now(), 'system', 'collector.pocket.template.id', '5', 'Collector Pocket Template Id');
