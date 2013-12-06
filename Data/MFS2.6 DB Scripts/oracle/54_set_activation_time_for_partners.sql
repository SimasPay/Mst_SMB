update subscriber  set activationtime=lastupdatetime where type=2 and status=1 and activationtime is null;

update subscriber_mdn set activationtime=lastupdatetime where subscriberid in (select id from subscriber where type=2) and status=1 and activationtime is null;

commit;