INSERT INTO exclude_subscriber_lc (id,version, lastupdatetime, updatedby, createtime, createdby, mdnid) VALUES (1,1,sysdate,'System',sysdate,'system',(select id from subscriber_mdn where mdn like '621000'));
INSERT INTO exclude_subscriber_lc (id,version, lastupdatetime, updatedby, createtime, createdby, mdnid) VALUES (2,1,sysdate,'System',sysdate,'system',(select id from subscriber_mdn where mdn like '622000'));
INSERT INTO exclude_subscriber_lc (id,version, lastupdatetime, updatedby, createtime, createdby, mdnid) VALUES (3,1,sysdate,'System',sysdate,'system',(select id from subscriber_mdn where mdn like '623000'));

commit;