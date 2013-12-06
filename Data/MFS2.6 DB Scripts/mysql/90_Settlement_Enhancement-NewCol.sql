
ALTER TABLE schedule_template ADD CutoffTime VARCHAR(255) DEFAULT '00:00';

ALTER TABLE schedule_template ADD TimerValueHH INT(11) DEFAULT NULL;

ALTER TABLE schedule_template ADD TimerValueMM INT(11) DEFAULT NULL;