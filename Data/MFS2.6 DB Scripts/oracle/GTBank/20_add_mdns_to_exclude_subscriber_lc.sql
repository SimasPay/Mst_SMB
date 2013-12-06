INSERT INTO exclude_subscriber_lc (id,version, lastupdatetime, updatedby, createtime, createdby, mdnid) VALUES (1,1,sysdate,'System',sysdate,'system',(select id from subscriber_mdn where mdn like '2341000'));
INSERT INTO exclude_subscriber_lc (id,version, lastupdatetime, updatedby, createtime, createdby, mdnid) VALUES (2,1,sysdate,'System',sysdate,'system',(select id from subscriber_mdn where mdn like '2342000'));
INSERT INTO exclude_subscriber_lc (id,version, lastupdatetime, updatedby, createtime, createdby, mdnid) VALUES (3,1,sysdate,'System',sysdate,'system',(select id from subscriber_mdn where mdn like '2343000'));

commit;