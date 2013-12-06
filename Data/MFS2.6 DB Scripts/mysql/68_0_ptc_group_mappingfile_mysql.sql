drop table if exists ptc_group_mapping;
CREATE TABLE ptc_group_mapping (
  ID bigint(20) NOT NULL AUTO_INCREMENT,
  Version int(11) NOT NULL,
  LastUpdateTime datetime NOT NULL,
  UpdatedBy varchar(255) NOT NULL DEFAULT ' ',
  CreateTime datetime NOT NULL,
  CreatedBy varchar(255) NOT NULL,
  groupID BIGINT(20) references groups (ID),
  ptcID BIGINT(20) references pocket_template_config (ID),
  PRIMARY KEY (ID),
  UNIQUE KEY ptc_group (groupID, ptcID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

drop procedure if exists insert_ptc_group_map;
delimiter //
create procedure insert_ptc_group_map()
BEGIN
	declare no_more_records INT Default 0;
	declare ptc_id, groupId bigint(20);
	declare ptc_cursor cursor for select id from pocket_template_config;
	declare Continue Handler for NOT FOUND Set no_more_records = 1;	
		
	select id into groupId from groups where groupname = 'ANY';
	open ptc_cursor;
	Loop1:loop
		fetch ptc_cursor into ptc_id;
		
		if (no_more_records = 1) then 
			close ptc_cursor;
			leave Loop1;
		end if;
		
		Insert into ptc_group_mapping (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, groupID, ptcID) values (1, now(), 'System', now(), 'System', groupId, ptc_id);
		
	end loop Loop1;
End;
//
delimiter ;
call insert_ptc_group_map();
drop procedure if exists insert_ptc_group_map;
