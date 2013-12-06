UPDATE share_partner SET ShareType=1;
UPDATE share_partner SET ShareHolderType=4 where PartnerID IS NOT NULL;

PROMPT Creating Trigger share_partner_ID_TRG ...  
CREATE OR REPLACE TRIGGER share_partner_ID_TRG BEFORE INSERT ON share_partner
FOR EACH ROW
DECLARE 
v_newVal NUMBER(12) := 0;
v_incval NUMBER(12) := 0;
BEGIN
  IF INSERTING AND :new.ID IS NULL THEN
    SELECT  share_partner_ID_SEQ.NEXTVAL INTO v_newVal FROM DUAL;
    -- If this is the first time this table have been inserted into (sequence == 1)
    IF v_newVal = 1 THEN 
      --get the max indentity value from the table
      SELECT NVL(max(ID),0) INTO v_newVal FROM share_partner;
      v_newVal := v_newVal + 1;
      --set the sequence to that value
      LOOP
           EXIT WHEN v_incval>=v_newVal;
           SELECT share_partner_ID_SEQ.nextval INTO v_incval FROM dual;
      END LOOP;
    END IF;
    --used to emulate LAST_INSERT_ID()
    --mysql_utilities.identity := v_newVal; 
   -- assign the value from the sequence to emulate the identity column
   :new.ID := v_newVal;
  END IF;
END;

/

-- Procedure for creating the TransactionCharge changes
-- Its a one-time activity

PROMPT Migration of TransactionCharge data to SharePartner ...
DECLARE 
  cursor tc_cursor is select * from transaction_charge;
  command1 varchar(1000);
  part1 varchar(500) := 'Insert into share_partner (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, 
                                    TransactionChargeID, PartnerID, SharePercentage, ShareType, ShareHolderType) values 
                                    (0,sysdate,''System'',sysdate,''System'',1, ';
BEGIN

	For tc in tc_cursor loop
	
		IF tc.sourcecommision IS NOT NULL THEN     	
			command1 := part1 || tc.id || ', null, ' || tc.sourcecommision || ', 1, 1)';
			execute immediate command1; 			
		END IF;

		IF tc.destcommision IS NOT NULL THEN     	
			command1 := part1 || tc.id || ', null, ' || tc.destcommision || ', 1, 2)';
			execute immediate command1;
		END IF;

		IF tc.registeringpartnercommision IS NOT NULL THEN     	
			command1 := part1 || tc.id || ', null, ' || tc.registeringpartnercommision || ', 1, 3)';
			execute immediate command1;
		END IF;
		  
	End loop;

	exception
		when no_data_found then
			dbms_output.put_line('No record avialable');
		when too_many_rows then
			dbms_output.put_line('Too many rows');
	
END;

/

alter table transaction_charge DROP COLUMN RegisteringPartnerCommision;
alter table transaction_charge DROP COLUMN DestCommision;
alter table transaction_charge DROP COLUMN SourceCommision;

commit;