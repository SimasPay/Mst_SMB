drop procedure if exists insert_subscriber_groups;
delimiter //
create procedure insert_subscriber_groups()
BEGIN
	declare no_more_records INT Default 0;
	declare sub_id, groupId bigint(20);
	declare s_cursor cursor for select id from subscriber s where not exists(select 1 from subscriber_groups sg where sg.subscriberid=s.id);
	declare Continue Handler for NOT FOUND Set no_more_records = 1;	
		
	select id into groupId from groups where groupname = 'ANY';
	open s_cursor;
	Loop1:loop
		fetch s_cursor into sub_id;
		
		if (no_more_records = 1) then 
			close s_cursor;
			leave Loop1;
		end if;
		
		Insert into subscriber_groups (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, subscriberId, groupID) values (1, now(), 'System', now(), 'System', sub_id, groupId);
		
	end loop Loop1;
End;
//
delimiter ;
call insert_subscriber_groups();
drop procedure if exists insert_subscriber_groups;