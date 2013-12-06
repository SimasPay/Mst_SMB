@echo off
if %1.==. goto End1
if Not %1.==. goto Process

:End1
echo Please provide Profile. Use deployToServiceMix.bat profile
goto End

:Process
@echo on
set SERVICEMIX_HOME=D:\servicemix-4.4.0-fuse-00-43

echo "deleting config files"
del %SERVICEMIX_HOME%\deploy\mce_iso_jpos_configuration.xml
del %SERVICEMIX_HOME%\deploy\mce_backend_configuration.xml 
del %SERVICEMIX_HOME%\deploy\mce_newfrontend_configuration.xml 
del %SERVICEMIX_HOME%\deploy\mce_notification_configuration.xml 
del %SERVICEMIX_HOME%\deploy\mce_fix_configuration.xml 
del %SERVICEMIX_HOME%\deploy\mce_scheduler_configuration.xml
del %SERVICEMIX_HOME%\deploy\mce_zenith_visafone_configuration.xml
del %SERVICEMIX_HOME%\deploy\mce_zenith_dstv_configuration.xml
del %SERVICEMIX_HOME%\deploy\mce_zenith_visafoneairtime_configuration.xml
del %SERVICEMIX_HOME%\deploy\mce_bankteller_configuration.xml
del %SERVICEMIX_HOME%\deploy\mce_zenith_interbanktransfer_configuration.xml
del %SERVICEMIX_HOME%\deploy\mce_zenith_email_configuration.xml
del %SERVICEMIX_HOME%\deploy\mce_zenith_iso_configuration.xml
del %SERVICEMIX_HOME%\deploy\mce_zenith_sms_configuration.xml
del %SERVICEMIX_HOME%\deploy\mce_auto_reversal_configuration.xml  
del %SERVICEMIX_HOME%\deploy\zenith_billpay.xml  
del %SERVICEMIX_HOME%\deploy\zenith_billpay_vtu.xml  
del %SERVICEMIX_HOME%\mce_integration_cashin_configuration.xml
del %SERVICEMIX_HOME%\interswitch_cashin_configuration.xml
del %SERVICEMIX_HOME%\interswitch_cashout_configuration.xml
del %SERVICEMIX_HOME%\mce_integration_cashout_configuration.xml
del %SERVICEMIX_HOME%\mce_billpay_configuration.xml
del %SERVICEMIX_HOME%\mce_quickteller_billpay_configuration.xml

echo "deleting jar files"
del %SERVICEMIX_HOME%\deploy\VisaPhoneSTK-0.1-SNAPSHOT.jar
del %SERVICEMIX_HOME%\deploy\BankTeller-0.1-SNAPSHOT.jar
del %SERVICEMIX_HOME%\deploy\ZenithDSTV-0.1-SNAPSHOT.jar
del %SERVICEMIX_HOME%\deploy\VisafoneAirtime-0.1-SNAPSHOT.jar
del %SERVICEMIX_HOME%\deploy\Frontend-0.1-SNAPSHOT.jar 
del %SERVICEMIX_HOME%\deploy\JPOSComponent-0.1-SNAPSHOT.jar
del %SERVICEMIX_HOME%\deploy\ZenithISO8583-0.1-SNAPSHOT.jar
del %SERVICEMIX_HOME%\deploy\FIXServer-0.1-SNAPSHOT.jar 
del %SERVICEMIX_HOME%\deploy\NotificationService-0.1-SNAPSHOT.jar 
del %SERVICEMIX_HOME%\deploy\Backend-0.1-SNAPSHOT.jar 
del %SERVICEMIX_HOME%\deploy\MCECore-0.1-SNAPSHOT.jar 
del %SERVICEMIX_HOME%\deploy\com.mfino.application-Core.jar
del %SERVICEMIX_HOME%\deploy\TransactionApi-0.1-SNAPSHOT.jar
del %SERVICEMIX_HOME%\deploy\InterBankTransfer-0.1-SNAPSHOT.jar
del %SERVICEMIX_HOME%\deploy\ISODefinitions-0.1-SNAPSHOT.jar
del %SERVICEMIX_HOME%\deploy\HierarchyService-0.1-SNAPSHOT.jar
del %SERVICEMIX_HOME%\deploy\VTUCommunicator-0.1-SNAPSHOT.jar
del %SERVICEMIX_HOME%\deploy\BillPayments-0.1-SNAPSHOT.jar
del %SERVICEMIX_HOME%\deploy\IntegrationInterfaces-0.1-SNAPSHOT.jar
del %SERVICEMIX_HOME%\deploy\CashinFrontend-0.1-SNAPSHOT.jar
del %SERVICEMIX_HOME%\deploy\CashinIntegration-0.1-SNAPSHOT.jar
del %SERVICEMIX_HOME%\deploy\CashoutIntegration-0.1-SNAPSHOT.jar
del %SERVICEMIX_HOME%\deploy\CashoutFrontend-0.1-SNAPSHOT.jar


echo "deleting config files"
del %SERVICEMIX_HOME%\mfino_conf\database_config.properties
del %SERVICEMIX_HOME%\mfino_conf\mce.properties
del %SERVICEMIX_HOME%\mfino_conf\mfino.properties
del %SERVICEMIX_HOME%\mfino_conf\externalcodedescriptions.xml

echo "copying jar files"
mkdir %SERVICEMIX_HOME%\deploy\
mkdir %SERVICEMIX_HOME%\mfino_conf\
copy ..\Core\target\com.mfino.application-Core.jar %SERVICEMIX_HOME%\deploy
copy ..\Commons\TransactionApi\target\TransactionApi-0.1-SNAPSHOT.jar %SERVICEMIX_HOME%\deploy
copy MCECore\target\MCECore-0.1-SNAPSHOT.jar %SERVICEMIX_HOME%\deploy
copy FIXServer\target\FIXServer-0.1-SNAPSHOT.jar %SERVICEMIX_HOME%\deploy
copy Backend\target\Backend-0.1-SNAPSHOT.jar %SERVICEMIX_HOME%\deploy
copy NotificationService\target\NotificationService-0.1-SNAPSHOT.jar %SERVICEMIX_HOME%\deploy
copy JPOSComponent\target\JPOSComponent-0.1-SNAPSHOT.jar %SERVICEMIX_HOME%\deploy
copy ..\Zenith\ZenithISO8583\target\ZenithISO8583-0.1-SNAPSHOT.jar %SERVICEMIX_HOME%\deploy
copy Frontend\target\Frontend-0.1-SNAPSHOT.jar %SERVICEMIX_HOME%\deploy
copy ..\Zenith\VisaPhoneSTK\target\VisaPhoneSTK-0.1-SNAPSHOT.jar %SERVICEMIX_HOME%\deploy
copy ..\Zenith\VTUCommunicator\target\VTUCommunicator-0.1-SNAPSHOT.jar %SERVICEMIX_HOME%\deploy
copy ..\Zenith\ZenithDSTV\target\ZenithDSTV-0.1-SNAPSHOT.jar %SERVICEMIX_HOME%\deploy
copy ..\Zenith\VisafoneAirtime\target\VisafoneAirtime-0.1-SNAPSHOT.jar %SERVICEMIX_HOME%\deploy
copy ..\Zenith\InterBankTransfer\target\InterBankTransfer-0.1-SNAPSHOT.jar %SERVICEMIX_HOME%\deploy
copy BankTeller\target\BankTeller-0.1-SNAPSHOT.jar  %SERVICEMIX_HOME%\deploy
copy ISODefinitions\target\ISODefinitions-0.1-SNAPSHOT.jar %SERVICEMIX_HOME%\deploy
copy ..\Commons\HierarchyService\target\HierarchyService-0.1-SNAPSHOT.jar %SERVICEMIX_HOME%\deploy
copy ..\GTBank\BillPayments\target\BillPayments-0.1-SNAPSHOT.jar %SERVICEMIX_HOME%\deploy
copy IntegrationInterfaces\target\IntegrationInterfaces-0.1-SNAPSHOT.jar %SERVICEMIX_HOME%\deploy
copy ..\IntegrationTemplates\CashinFrontend\target\CashinFrontend-0.1-SNAPSHOT.jar %SERVICEMIX_HOME%\deploy
copy CashinIntegration\target\CashinIntegration-0.1-SNAPSHOT.jar %SERVICEMIX_HOME%\deploy
copy CashoutIntegration\target\CashoutIntegration-0.1-SNAPSHOT.jar %SERVICEMIX_HOME%\deploy
copy ..\IntegrationTemplates\CashoutFrontend\target\CashoutFrontend-0.1-SNAPSHOT.jar %SERVICEMIX_HOME%\deploy


echo "copying database config files"
copy ..\Core\settings\%1\database_config.properties %SERVICEMIX_HOME%\mfino_conf\
copy mfino_conf\mce.properties %SERVICEMIX_HOME%\mfino_conf\
copy ..\Core\settings\%1\mfino.properties %SERVICEMIX_HOME%\mfino_conf\
copy mfino_conf\externalcodedescriptions.xml %SERVICEMIX_HOME%\mfino_conf\

echo "copying jpos config files"
mkdir %SERVICEMIX_HOME%\jpos\deploy
copy ..\Zenith\ZenithConfiguration\src\main\jpos\* %SERVICEMIX_HOME%\jpos\deploy\

echo "copying config files"
copy MCEConfiguration\src\main\resources\META-INF\spring\mce_fix_configuration.xml %SERVICEMIX_HOME%\deploy\
copy MCEConfiguration\src\main\resources\META-INF\spring\mce_newfrontend_configuration.xml %SERVICEMIX_HOME%\deploy\
copy MCEConfiguration\src\main\resources\META-INF\spring\mce_backend_configuration.xml %SERVICEMIX_HOME%\deploy\
copy MCEConfiguration\src\main\resources\META-INF\spring\mce_auto_reversal_configuration.xml %SERVICEMIX_HOME%\deploy\
copy MCEConfiguration\src\main\resources\META-INF\spring\mce_notification_configuration.xml %SERVICEMIX_HOME%\deploy\
copy MCEConfiguration\src\main\resources\META-INF\spring\mce_iso_jpos_configuration.xml %SERVICEMIX_HOME%\deploy\
copy MCEConfiguration\src\main\resources\META-INF\spring\mce_scheduler_configuration.xml %SERVICEMIX_HOME%\deploy\
copy MCEConfiguration\src\main\resources\META-INF\spring\mce_bankteller_configuration.xml %SERVICEMIX_HOME%\deploy\
copy ..\Zenith\ZenithConfiguration\src\main\resources\META-INF\spring\mce_zenith_visafone_configuration.xml %SERVICEMIX_HOME%\deploy\
copy ..\Zenith\ZenithConfiguration\src\main\resources\META-INF\spring\mce_zenith_dstv_configuration.xml %SERVICEMIX_HOME%\deploy\
copy ..\Zenith\ZenithConfiguration\src\main\resources\META-INF\spring\mce_zenith_visafoneairtime_configuration.xml %SERVICEMIX_HOME%\deploy\
copy ..\Zenith\ZenithConfiguration\src\main\resources\META-INF\spring\mce_zenith_interbanktransfer_configuration.xml %SERVICEMIX_HOME%\deploy\
copy ..\Zenith\ZenithConfiguration\src\main\resources\META-INF\spring\mce_zenith_iso_configuration.xml %SERVICEMIX_HOME%\deploy\
copy ..\Zenith\ZenithConfiguration\src\main\resources\META-INF\spring\mce_zenith_sms_configuration.xml %SERVICEMIX_HOME%\deploy\
copy ..\Zenith\ZenithConfiguration\src\main\resources\META-INF\spring\mce_zenith_email_configuration.xml %SERVICEMIX_HOME%\deploy\
copy ..\Zenith\ZenithConfiguration\src\main\resources\META-INF\spring\zenith_billpay.xml %SERVICEMIX_HOME%\deploy\
copy ..\Zenith\ZenithConfiguration\src\main\resources\META-INF\spring\zenith_billpay_vtu.xml %SERVICEMIX_HOME%\deploy\
copy MCEConfiguration\src\main\resources\META-INF\spring\mce_integration_cashin_configuration.xml %SERVICEMIX_HOME%\deploy\
copy ..\GTBank\GTConfiguration\src\main\resources\META-INF\spring\interswitch_cashin_configuration.xml %SERVICEMIX_HOME%\deploy\
copy ..\GTBank\GTConfiguration\src\main\resources\META-INF\spring\interswitch_cashout_configuration.xml %SERVICEMIX_HOME%\deploy\
copy MCEConfiguration\src\main\resources\META-INF\spring\mce_integration_cashout_configuration.xml %SERVICEMIX_HOME%\deploy\
copy ..\GTBank\GTConfiguration\src\main\resources\META-INF\spring\mce_billpay_configuration.xml %SERVICEMIX_HOME%\deploy\
copy ..\GTBank\GTConfiguration\src\main\resources\META-INF\spring\mce_quickteller_billpay_configuration.xml %SERVICE


:End
