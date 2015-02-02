ALTER TABLE pocket_template ADD IsSystemPocket     NUMBER(3)  DEFAULT 0;

UPDATE pocket_template SET IsSystemPocket=1 where POCKETCODE=10;

commit;