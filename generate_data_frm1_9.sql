use mfino;

SELECT s.firstName, s.lastName, sm.MDN,'4', p.cardPan,'###','###','###','','###','###','11052016','12041981','ANY','0000','###','###','8790562227','###','###','###','12041981'
INTO OUTFILE '/var/log/mfino/mig.csv'
FIELDS TERMINATED BY '|'
ESCAPED BY '\\'
LINES TERMINATED BY '\n'
FROM subscriber s, subscriber_mdn sm, pocket_template pt, pocket p
where s.id = sm.subscriberid and
p.mdnid = sm.id and
p.pocketTemplateId = pt.id and
pt.type=3 and
p.isDefault=1 and
s.status=1 and
sm.status=1 and
p.status=1;
