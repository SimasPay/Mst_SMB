Delete from system_parameters where ParameterName = 'nfc.card.topup.partner.code';
INSERT INTO system_parameters (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, ParameterName, ParameterValue, Description) VALUES (1,NOW(),'System',NOW(),'system','nfc.card.topup.partner.code','NFC','NFC Partner Code for Card Topup');


