create or replace procedure subscriber_event_migration
is
 new_status_time Timestamp(0);
 initial_value number(10):= 1;
 DAYS_TO_INACTIVATE_SUBSCRIBER number(10);
 DAYS_TO_INACTIVATE_PARTNER number(10);
 MILLI_SECONDS_PER_DAY number(10);
 TIME_TO_SUSPEND_OF_INACTIVE number(10);
 TIME_TO_RETIRE_OF_SUSPENDED number(10);
 TIME_TO_GRAVE_OF_RETIRED number(10);
 TIME_TO_MOVE_TO_TREASURY number(10);
 status_event_CURRVAL number(10);
 

BEGIN
DECLARE      
   CURSOR subscriber_status_util
   IS
      SELECT parametername,parametervalue
        FROM system_parameters
       WHERE  parametername  like '%days.to%';

BEGIN
 FOR systemparameter 
   IN subscriber_status_util
   LOOP
     IF systemparameter.parametername='days.to.inactivate.of.active.subscriber.when.no.fundmovement' THEN
        DAYS_TO_INACTIVATE_SUBSCRIBER :=systemparameter.parametervalue;
	 ELSIF systemparameter.parametername='days.to.inactivate.of.active.subscriber.when.no.activity' THEN
		DAYS_TO_INACTIVATE_PARTNER :=systemparameter.parametervalue;
     ELSIF  systemparameter.parametername='days.to.suspend.of.inactive' THEN
        TIME_TO_SUSPEND_OF_INACTIVE :=systemparameter.parametervalue;
     ELSIF  systemparameter.parametername='days.to.retire.of.suspended' THEN
        TIME_TO_RETIRE_OF_SUSPENDED :=systemparameter.parametervalue;
     ELSIF  systemparameter.parametername='days.to.grave.of.retired' THEN
        TIME_TO_GRAVE_OF_RETIRED :=systemparameter.parametervalue ;
	ELSIF  systemparameter.parametername='days.to.national.treasury.of.graved' THEN
        TIME_TO_MOVE_TO_TREASURY :=systemparameter.parametervalue ;		
     END IF;            
   END LOOP;  
END;

DECLARE
   CURSOR subscriber_status_till_date
   IS
      SELECT mdn.subscriberid,mdn.statustime,mdn.status,sub.type
        FROM subscriber_mdn mdn,subscriber sub
       WHERE  mdn.status in(1,2,3,5,6)and mdn.subscriberid=sub.id;
BEGIN
   FOR subscriber 
   IN subscriber_status_till_date
   LOOP
     IF subscriber.status=1  and subscriber.type=0 THEN
        new_status_time :=subscriber.statustime+DAYS_TO_INACTIVATE_SUBSCRIBER;
	 ELSIF	subscriber.status=1  and subscriber.type!=0 THEN
		new_status_time :=subscriber.statustime+DAYS_TO_INACTIVATE_PARTNER;
     ELSIF subscriber.status=2 THEN
        new_status_time :=subscriber.statustime+TIME_TO_GRAVE_OF_RETIRED ;
     ELSIF subscriber.status=3 THEN
        new_status_time :=subscriber.statustime+TIME_TO_MOVE_TO_TREASURY;
     ELSIF subscriber.status=5 THEN
        new_status_time :=subscriber.statustime+TIME_TO_RETIRE_OF_SUSPENDED;
     ELSIF subscriber.status=6 THEN
        new_status_time :=subscriber.statustime+TIME_TO_SUSPEND_OF_INACTIVE ;   

     END IF; 
      insert into subscriber_status_event(ID,VERSION,LASTUPDATETIME,UPDATEDBY,CREATETIME,CREATEDBY,SUBSCRIBERID,PICKUPDATETIME,STATUSONPICKUP,SUBSCRIBERTYPE)
      values(initial_value,0,sysdate,'Migration',sysdate,'Migration',subscriber.subscriberid,new_status_time,subscriber.status,subscriber.type);
      initial_value:=initial_value+1;
      SELECT subscriber_status_event_ID_SEQ.NEXTVAL into status_event_CURRVAL FROM DUAL;
      EXECUTE IMMEDIATE 'ALTER SEQUENCE subscriber_status_event_ID_SEQ INCREMENT BY 1';
   END LOOP;
END;   
 EXCEPTION
WHEN OTHERS THEN
   raise_application_error(-20001,'An error was encountered - '||SQLCODE||' -ERROR- '||SQLERRM);  
END;

