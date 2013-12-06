update pocket_template_config set isdefault = 1 where id in 
(select ptc.id from pocket_template_config ptc, ptc_group_mapping ptcmap where ptc.id=ptcmap.ptcid and ptcmap.groupid=1);

commit;