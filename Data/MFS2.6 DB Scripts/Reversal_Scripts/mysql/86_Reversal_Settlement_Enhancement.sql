USE mfino;

ALTER TABLE settlement_template DROP FOREIGN KEY FK_setlmnt_tmplt_schdl;

ALTER TABLE settlement_template DROP COLUMN ScheduleTemplateID ;

ALTER TABLE settlement_txn_log DROP COLUMN Description;
ALTER TABLE settlement_txn_log DROP COLUMN Amount;

        
        
DROP TABLE IF EXISTS schedule_template;

DROP TABLE IF EXISTS sctl_settlement_map;

DROP TABLE IF EXISTS settlement_txn_sctl_map;