PROMPT Set the Default Group 'Any' for all subscribers with out group ...
DECLARE 
  command1 varchar(2000);
  cursor s_cursor is select id from subscriber s where not exists(select 1 from subscriber_groups sg where sg.subscriberid=s.id);
  groupId number(19);
  part1 varchar(255) := 'Insert into subscriber_groups (Id, Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, subscriberId, groupID) values (subscriber_groups_id_seq.nextval, 1, sysdate, ''System'', sysdate, ''System'', ';
BEGIN
  select id into groupId from groups where groupname = 'ANY';
  For s in s_cursor loop
    command1 := part1 || s.id || ',' || groupId || ')';
    execute immediate command1; 
  End loop;

	exception
		when no_data_found then
			dbms_output.put_line('No record avialable');
		when too_many_rows then
			dbms_output.put_line('Too many rows');
	
END;

/
