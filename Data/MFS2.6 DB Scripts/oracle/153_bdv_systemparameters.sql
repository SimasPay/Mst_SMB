delete from system_parameters where ParameterName = 'lastbdv.calculation.date';
insert into system_parameters (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, ParameterName, ParameterValue, Description) VALUES (1,sysdate,'System',sysdate,'system','lastbdv.calculation.date',null,'Updated by scheduler, last Booking Dated Calculation date');
