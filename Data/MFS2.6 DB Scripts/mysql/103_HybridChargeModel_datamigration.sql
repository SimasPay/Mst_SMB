
--
-- Migrating data from old column sharepercentage to new columns in "share_partner" table
--
Update share_partner set actualsharepercentage = concat(truncate(sharepercentage, 2), '%'), minsharepercentage = actualsharepercentage, maxsharepercentage = actualsharepercentage where sharepercentage is not null;

--
-- Migrating data from old columns chargeinfixed, chargeinpercentage to new columns in "charge_pricing" table
--
Update charge_pricing set charge = chargeinfixed, mincharge = chargeinfixed, maxcharge = chargeinfixed where chargeinfixed is not null;
Update charge_pricing set charge = concat('amount*',truncate(chargeinpercentage,2), '%'), mincharge = charge, maxcharge = charge where chargeinpercentage is not null;
