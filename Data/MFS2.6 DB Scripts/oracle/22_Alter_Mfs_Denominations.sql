ALTER TABLE mfs_denominations rename column Denomination to DenominationAmount;
ALTER TABLE mfs_denominations modify DenominationAmount number(25,4);

commit;