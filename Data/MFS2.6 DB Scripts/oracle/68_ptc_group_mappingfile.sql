PROMPT Creating table ptc_group_mapping
CREATE TABLE ptc_group_mapping (
ID NUMBER(19,0) NOT NULL ,
Version NUMBER(10,0) NOT NULL,
LastUpdateTime TIMESTAMP(0) NOT NULL,
UpdatedBy VARCHAR2(255 CHAR) NOT NULL,
CreateTime TIMESTAMP(0) NOT NULL,
CreatedBy VARCHAR2(255 CHAR) NOT NULL,
groupID NUMBER(19,0) REFERENCES groups (ID),
ptcID NUMBER(19,0) REFERENCES pocket_template_config (ID),
PRIMARY KEY (ID),
CONSTRAINT ptc_group_unique UNIQUE (groupID, ptcID)
);

PROMPT Creating Sequence ptc_group_mapping_ID_SEQ ...
CREATE SEQUENCE  ptc_group_mapping_ID_SEQ  
  MINVALUE 1 MAXVALUE 999999999999999999999999 INCREMENT BY 1  NOCYCLE ;

PROMPT Creating Trigger ptc_group_mapping_ID_TRG ...  
CREATE OR REPLACE TRIGGER ptc_group_mapping_ID_TRG BEFORE INSERT ON ptc_group_mapping
FOR EACH ROW
DECLARE 
v_newVal NUMBER(12) := 0;
v_incval NUMBER(12) := 0;
BEGIN
  IF INSERTING AND :new.ID IS NULL THEN
    SELECT  ptc_group_mapping_ID_SEQ.NEXTVAL INTO v_newVal FROM DUAL;
    -- If this is the first time this table have been inserted into (sequence == 1)
    IF v_newVal = 1 THEN 
      --get the max indentity value from the table
      SELECT NVL(max(ID),0) INTO v_newVal FROM ptc_group_mapping;
      v_newVal := v_newVal + 1;
      --set the sequence to that value
      LOOP
           EXIT WHEN v_incval>=v_newVal;
           SELECT ptc_group_mapping_ID_SEQ.nextval INTO v_incval FROM dual;
      END LOOP;
    END IF;
    --used to emulate LAST_INSERT_ID()
    --mysql_utilities.identity := v_newVal; 
   -- assign the value from the sequence to emulate the identity column
   :new.ID := v_newVal;
  END IF;
END;

/

PROMPT Creating PTC_GROUP_MAPPING entries with default group 'ANY' ...
DECLARE 
  command1 varchar(2000);
  cursor ptc_cursor is select * from pocket_template_config;
  ptc_id number(19);
  groupId number(19);
  part1 varchar(255) := 'Insert into ptc_group_mapping (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, groupID, ptcID) values (1, sysdate, ''System'', sysdate, ''System'', ';
BEGIN
  delete from ptc_group_mapping;
  select id into groupId from groups where groupname = 'ANY';
	For ptc in ptc_cursor loop
    command1 := part1 || groupId || ',' || ptc.id || ')';
    execute immediate command1; 
  End loop;

	exception
		when no_data_found then
			dbms_output.put_line('No record avialable');
		when too_many_rows then
			dbms_output.put_line('Too many rows');
	
END;

/

commit;