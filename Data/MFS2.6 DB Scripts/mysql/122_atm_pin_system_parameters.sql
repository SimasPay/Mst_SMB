
DELETE FROM system_parameters WHERE parametername='cashout.atm.fac.as.pin';
INSERT INTO system_parameters (version, lastupdatetime, updatedby, createtime, createdby, parametername, parametervalue, description) VALUES (1,now(),'System',now(),'system','cashout.atm.fac.as.pin','false','Use last digits of FAC as Pin for ATM cashout');