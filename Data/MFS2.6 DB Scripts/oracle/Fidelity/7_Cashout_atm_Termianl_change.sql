Delete from system_parameters where ParameterName = 'atm.terminal.prefix.code';
INSERT INTO system_parameters (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, ParameterName, ParameterValue, Description) VALUES (1,sysdate,'System',sysdate,'system','atm.terminal.prefix.code','1058','ATM Terminal Prefix Code');

Delete from system_parameters where ParameterName = 'atm.terminal.default.account.number';
INSERT INTO system_parameters (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, ParameterName, ParameterValue, Description) VALUES (1,sysdate,'System',sysdate,'system','atm.terminal.default.account.number','205000021401000500','ATM Terminal Default Account number');

commit;