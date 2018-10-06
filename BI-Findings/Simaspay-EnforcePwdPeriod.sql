create or replace PROCEDURE CHECK_PWD_PERIOD AS 
BEGIN
    --set securityLockedSt=4; //SubscriberRestrictions_SecurityLocked
    update mfino_user set status=4 where 
    lastpasswordchangetime < trunc(sysdate)-90 and id =577;
    commit;
END CHECK_PWD_PERIOD;

BEGIN
  DBMS_SCHEDULER.DROP_JOB ('ENFORCEPWDPERIOD');
END;
/

BEGIN
  DBMS_SCHEDULER.CREATE_JOB (
   job_name          =>  'ENFORCEPWDPERIOD',
   program_name      =>  'CHECK_PWD_PERIOD', 
   repeat_interval   =>  'FREQ=DAILY;BYHOUR=12',
   comments          =>  'Daily at midnight');
   
   DBMS_SCHEDULER.ENABLE('ENFORCEPWDPERIOD'); 
END;
/


BEGIN
 DBMS_SCHEDULER.CREATE_CREDENTIAL('SP_CREDENTIAL', 'simaspay', 'simaspay123');
END;
/

GRANT EXECUTE ON SP_CREDENTIAL TO simaspay;

BEGIN
 DBMS_SCHEDULER.ENABLE('ENFORCEPWDPERIOD'); 
END;
/
