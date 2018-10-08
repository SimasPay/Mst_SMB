create or replace PROCEDURE CHECK_PWD_PERIOD AS 
BEGIN
    update mfino_user set status=4 where 
    (lastpasswordchangetime is null and createtime < trunc(sysdate)-90)
    or 
    (lastpasswordchangetime < trunc(sysdate)-90);
   -- id=577;
    commit;
END CHECK_PWD_PERIOD;


Execute CHECK_PWD_PERIOD;

BEGIN
  DBMS_SCHEDULER.DROP_JOB ('ENFORCEPWDPERIOD');
END;
/

BEGIN
  DBMS_SCHEDULER.CREATE_JOB (
   job_name          =>  'ENFORCEPWDPERIOD', 
   job_type          =>  'PLSQL_BLOCK',
   job_action        =>  'BEGIN SIMASPAY.CHECK_PWD_PERIOD; END;', 
   repeat_interval   =>  'FREQ=DAILY;BYHOUR=12',
   comments          =>  'Daily at midnight');
END;
/


BEGIN
 DBMS_SCHEDULER.ENABLE('ENFORCEPWDPERIOD'); 
END;
/
