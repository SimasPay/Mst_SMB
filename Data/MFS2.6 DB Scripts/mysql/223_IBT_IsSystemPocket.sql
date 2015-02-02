ALTER TABLE pocket_template ADD IsSystemPocket   tinyint(4)  DEFAULT 0;

UPDATE pocket_template SET IsSystemPocket=1 where POCKETCODE=10;
