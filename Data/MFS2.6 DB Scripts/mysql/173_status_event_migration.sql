DROP PROCEDURE IF EXISTS subscriber_event_migration ;

DELIMITER //

CREATE PROCEDURE subscriber_event_migration () 
BEGIN
  DECLARE new_status_time DATETIME ;
  DECLARE initial_value BIGINT ;
  DECLARE done INT DEFAULT 0;
  DECLARE DAYS_TO_INACTIVATE_SUBSCRIBER INTEGER ;
  DECLARE DAYS_TO_INACTIVATE_PARTNER INT ;
  DECLARE MILLI_SECONDS_PER_DAY INT ;
  DECLARE TIME_TO_SUSPEND_OF_INACTIVE INT ;
  DECLARE TIME_TO_RETIRE_OF_SUSPENDED INT ;
  DECLARE TIME_TO_GRAVE_OF_RETIRED INT ;
  DECLARE TIME_TO_MOVE_TO_TREASURY INT ;
  DECLARE parametername VARCHAR (255) ;
  DECLARE parametervalue BIGINT ;
  DECLARE subscriberid BIGINT ;
  DECLARE statustime DATETIME ;
  DECLARE STATUS BIGINT ;
  DECLARE TYPE BIGINT ;
  DECLARE subscriber_status_util CURSOR FOR 
  SELECT 
    x.parametername,
    x.parametervalue 
  FROM
    system_parameters x
  WHERE x.parametername LIKE '%days.to%' ;
  DECLARE subscriber_status_till_date CURSOR FOR 
  SELECT 
    mdn.subscriberid,
    mdn.statustime,
    mdn.status,
    sub.type 
  FROM
    subscriber_mdn mdn,
    subscriber sub 
  WHERE mdn.status IN (1, 2, 3, 5, 6) 
    AND mdn.subscriberid = sub.id ;
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = 1;
  OPEN subscriber_status_util ;
  LOOP1 :
  LOOP
    FETCH subscriber_status_util INTO parametername,
    parametervalue ;
    IF done = 1 THEN
            LEAVE LOOP1;
        END IF;
    IF parametername = 'days.to.inactivate.of.active.subscriber.when.no.fundmovement' 
    THEN SET DAYS_TO_INACTIVATE_SUBSCRIBER = parametervalue ;
    ELSEIF parametername = 'days.to.inactivate.of.active.subscriber.when.no.activity' 
    THEN SET DAYS_TO_INACTIVATE_PARTNER = parametervalue ;
    ELSEIF parametername = 'days.to.suspend.of.inactive' 
    THEN SET TIME_TO_SUSPEND_OF_INACTIVE = parametervalue ;
    ELSEIF parametername = 'days.to.retire.of.suspended' 
    THEN SET TIME_TO_RETIRE_OF_SUSPENDED = parametervalue ;
    ELSEIF parametername = 'days.to.grave.of.retired' 
    THEN SET TIME_TO_GRAVE_OF_RETIRED = parametervalue ;
    ELSEIF parametername = 'days.to.national.treasury.of.graved' 
    THEN SET TIME_TO_MOVE_TO_TREASURY = parametervalue ;
    END IF ;
  END LOOP LOOP1 ;
 CLOSE  subscriber_status_util;
  SET initial_value = 1 ;
  SET done=0;
  OPEN subscriber_status_till_date ;
  LOOP2 :
  LOOP
    FETCH subscriber_status_till_date INTO subscriberid,
    statustime,
    STATUS,
    TYPE;
     IF done = 1 THEN
            LEAVE LOOP2;
        END IF;
     IF STATUS = 1 AND TYPE = 0 
    THEN SET new_status_time = DATE_ADD(statustime ,INTERVAL  DAYS_TO_INACTIVATE_SUBSCRIBER DAY);
    ELSEIF STATUS = 1 
    AND TYPE <>0 
    THEN  SET new_status_time = DATE_ADD(statustime , INTERVAL DAYS_TO_INACTIVATE_PARTNER DAY);
    ELSEIF STATUS = 2 
    THEN SET new_status_time = DATE_ADD(statustime , INTERVAL TIME_TO_GRAVE_OF_RETIRED DAY);
    ELSEIF STATUS = 3 
    THEN SET new_status_time = DATE_ADD(statustime , INTERVAL  TIME_TO_MOVE_TO_TREASURY DAY);
    ELSEIF STATUS = 5 
    THEN SET new_status_time = DATE_ADD(statustime , INTERVAL TIME_TO_RETIRE_OF_SUSPENDED DAY);
    ELSEIF STATUS = 6 
    THEN SET new_status_time = DATE_ADD(statustime , INTERVAL TIME_TO_SUSPEND_OF_INACTIVE DAY);
    END IF ;
    INSERT INTO subscriber_status_event (
      ID,
      VERSION,
      LASTUPDATETIME,
      UPDATEDBY,
      CREATETIME,
      CREATEDBY,
      SUBSCRIBERID,
      PICKUPDATETIME,
      STATUSONPICKUP,
      SUBSCRIBERTYPE
    ) 
    VALUES
      (
        initial_value,
        0,
        NOW(),
        'Migration',
        NOW(),
        'Migration',
        subscriberid,
        new_status_time,
        STATUS,
        TYPE
      ) ;
    SET initial_value = initial_value + 1 ;
  END LOOP LOOP2;
CLOSE subscriber_status_till_date;  
END ;
//
DELIMITER ;
 CALL subscriber_event_migration();

DROP PROCEDURE subscriber_event_migration;
