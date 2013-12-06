
--
-- Drop columns Charge, MinCharge & MaxCharge in charge_pricing table
--
Alter table charge_pricing drop column Charge;
Alter table charge_pricing drop column MinCharge;
Alter table charge_pricing drop column MaxCharge;

--
-- Drop columns ActualSharePercentage, MinSharePercentage & MaxSharePercentage to share_partner table
--
Alter table share_partner drop column ActualSharePercentage;
Alter table share_partner drop column MinSharePercentage;
Alter table share_partner drop column MaxSharePercentage;

--
-- Delete notification 810 : InvalidChargeDefinitionException
--
Delete from notification where code = 810;
