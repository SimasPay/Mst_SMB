DELETE FROM system_parameters WHERE parametername='national.treasury.partner.code';
DELETE FROM system_parameters WHERE parametername='national.treasury.pocket';
INSERT INTO system_parameters (version, lastupdatetime, updatedby, createtime, createdby, parametername, parametervalue, description) VALUES (1,sysdate,'System',sysdate,'system','national.treasury.pocket','-1','Pocket Id of Partner associated with National Treasury for graved account money moment');

commit;