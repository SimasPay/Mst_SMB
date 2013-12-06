
ALTER TABLE schedule_template ADD CutoffTime varchar2(255) DEFAULT '00:00';

ALTER TABLE schedule_template ADD TimerValueHH NUMBER(10,0) DEFAULT NULL;

ALTER TABLE schedule_template ADD TimerValueMM NUMBER(10,0) DEFAULT NULL;

commit;
