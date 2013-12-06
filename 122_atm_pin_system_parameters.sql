DELETE FROM system_parameters WHERE ParameterName='cashout.atm.fac.as.pin';

INSERT INTO system_parameters (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, ParameterName, ParameterValue, Description) VALUES (1,sysdate,'System',sysdate,'system','cashout.atm.fac.as.pin','false','Use last digits of FAC as Pin for ATM cashout');