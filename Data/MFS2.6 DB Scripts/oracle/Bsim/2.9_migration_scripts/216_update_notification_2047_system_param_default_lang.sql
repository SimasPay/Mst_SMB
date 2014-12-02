-- Script to chnage the text of notification code 2047
Update notification set Text='Dear $(FirstName), Please reset your PIN using this OTP $(OneTimePin)' where code=2047;

-- Script to chnage the default language to English
update system_parameters set parametervalue = 0 where parametername = 'default.language.of.subscriber';