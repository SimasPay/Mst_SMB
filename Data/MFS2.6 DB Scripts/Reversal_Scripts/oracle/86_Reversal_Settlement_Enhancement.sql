
ALTER TABLE settlement_template DROP COLUMN ScheduleTemplateID;
ALTER TABLE settlement_txn_log DROP COLUMN Amount;
ALTER TABLE settlement_txn_log DROP COLUMN Description;


DROP TABLE schedule_template CASCADE CONSTRAINTS;
Drop SEQUENCE schedule_template_ID_SEQ;

DROP TABLE sctl_settlement_map CASCADE CONSTRAINTS;
Drop SEQUENCE sctl_settlement_map_ID_SEQ;

DROP TABLE settlement_txn_sctl_map CASCADE CONSTRAINTS;
Drop SEQUENCE settlement_txn_sctl_map_ID_SEQ;

commit;