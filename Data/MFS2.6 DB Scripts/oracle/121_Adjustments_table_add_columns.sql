
ALTER TABLE adjustments ADD AdjustmentType NUMBER(10,0);
ALTER TABLE adjustments ADD Description VARCHAR2(255 CHAR);

commit;