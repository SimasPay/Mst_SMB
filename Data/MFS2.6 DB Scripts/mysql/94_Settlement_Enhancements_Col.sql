ALTER TABLE settlement_template DROP column SettlementType;

ALTER table settlement_template drop column CutoffTime;
ALTER TABLE settlement_template ADD CutoffTime bigint(20) DEFAULT 1;

UPDATE schedule_template SET TimerValueMM='0',TimerValueHH='22' where ID in (1,2,3);

ALTER TABLE settlement_template ADD CONSTRAINT FK_setlmnt_tmplt_ctoff  FOREIGN KEY (CutoffTime) REFERENCES schedule_template(`ID`);