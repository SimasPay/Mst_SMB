@echo off
if %1.==. goto End1
if Not %1.==. goto Process

:End1
echo Please provide Profile. Use deployToServiceMix.bat profile
goto End

:Process
@echo on
set SERVICEMIX_HOME=D:\apache-servicemix-4.4.1-fuse-01-13

echo "deleting config files"
del %SERVICEMIX_HOME%\deploy\mce_iso_jpos_configuration.xml
del %SERVICEMIX_HOME%\deploy\mce_backend_configuration.xml 
del %SERVICEMIX_HOME%\deploy\mce_newfrontend_configuration.xml 
del %SERVICEMIX_HOME%\deploy\mce_notification_configuration.xml 
del %SERVICEMIX_HOME%\deploy\mce_fix_configuration.xml 
del %SERVICEMIX_HOME%\deploy\mce_scheduler_configuration.xml
del %SERVICEMIX_HOME%\deploy\mce_auto_reversal_configuration.xml  

del %SERVICEMIX_HOME%\deploy\mce_bsm_iso_configuration.xml
del %SERVICEMIX_HOME%\deploy\mce_zte_iso_configuration.xml
del %SERVICEMIX_HOME%\deploy\mce_billpay_configuration.xml
del %SERVICEMIX_HOME%\deploy\mce_zte_billpay_configuration.xml

del %SERVICEMIX_HOME%\deploy\billpay_event_processing.xml
del %SERVICEMIX_HOME%\deploy\billpay_integration_calls.xml
del %SERVICEMIX_HOME%\deploy\billpay_reversals.xml
del %SERVICEMIX_HOME%\deploy\billpay_src_to_suspense.xml
del %SERVICEMIX_HOME%\deploy\billpay_susp_to_dest.xml

echo "deleting jar files"
del %SERVICEMIX_HOME%\deploy\Frontend-0.1-SNAPSHOT.jar 
del %SERVICEMIX_HOME%\deploy\JPOSComponent-0.1-SNAPSHOT.jar
del %SERVICEMIX_HOME%\deploy\BSMISO8583-0.1-SNAPSHOT.jar
del %SERVICEMIX_HOME%\deploy\FIXServer-0.1-SNAPSHOT.jar 
del %SERVICEMIX_HOME%\deploy\NotificationService-0.1-SNAPSHOT.jar 
del %SERVICEMIX_HOME%\deploy\Backend-0.1-SNAPSHOT.jar 
del %SERVICEMIX_HOME%\deploy\MCECore-0.1-SNAPSHOT.jar 
del %SERVICEMIX_HOME%\deploy\com.mfino.application-Core.jar
del %SERVICEMIX_HOME%\deploy\TransactionApi-0.1-SNAPSHOT.jar
del %SERVICEMIX_HOME%\deploy\ISODefinitions-0.1-SNAPSHOT.jar
del %SERVICEMIX_HOME%\deploy\HierarchyService-0.1-SNAPSHOT.jar
del %SERVICEMIX_HOME%\deploy\IntegrationInterfaces-0.1-SNAPSHOT.jar
del %SERVICEMIX_HOME%\deploy\ZTEISO8583-0.1-SNAPSHOT.jar
del %SERVICEMIX_HOME%\deploy\BillPayments-0.1-SNAPSHOT.jar
del %SERVICEMIX_HOME%\deploy\ZTEBillPayment-0.1-SNAPSHOT.jar
del %SERVICEMIX_HOME%\deploy\IntegrationFramework-0.1-SNAPSHOT.jar
del %SERVICEMIX_HOME%\deploy\BillerGatewayTestServer-0.1-SNAPSHOT.jar
del %SERVICEMIX_HOME%\deploy\SmartBillerGatewayService-0.1-SNAPSHOT.jar

echo "deleting config files"
del %SERVICEMIX_HOME%\mfino_conf\database_config.properties
del %SERVICEMIX_HOME%\mfino_conf\mce.properties
del %SERVICEMIX_HOME%\mfino_conf\mfino.properties
del %SERVICEMIX_HOME%\mfino_conf\bc2qmap
del %SERVICEMIX_HOME%\mfino_conf\
del %SERVICEMIX_HOME%\mfino_conf\Variant.xml
del %SERVICEMIX_HOME%\mfino_conf\PaymentRequest.xml
del %SERVICEMIX_HOME%\mfino_conf\PaymentResponse.xml
del %SERVICEMIX_HOME%\mfino_conf\InquiryRequest.xml
del %SERVICEMIX_HOME%\mfino_conf\InquiryResponse.xml

echo "copying jar files"
mkdir %SERVICEMIX_HOME%\deploy\
mkdir %SERVICEMIX_HOME%\mfino_conf\

copy ..\Core\target\com.mfino.application-Core.jar %SERVICEMIX_HOME%\deploy
copy ..\Commons\TransactionApi\target\TransactionApi-0.1-SNAPSHOT.jar %SERVICEMIX_HOME%\deploy
copy ..\Commons\HierarchyService\target\HierarchyService-0.1-SNAPSHOT.jar %SERVICEMIX_HOME%\deploy
copy MCECore\target\MCECore-0.1-SNAPSHOT.jar %SERVICEMIX_HOME%\deploy
copy FIXServer\target\FIXServer-0.1-SNAPSHOT.jar %SERVICEMIX_HOME%\deploy
copy Backend\target\Backend-0.1-SNAPSHOT.jar %SERVICEMIX_HOME%\deploy
copy NotificationService\target\NotificationService-0.1-SNAPSHOT.jar %SERVICEMIX_HOME%\deploy
copy JPOSComponent\target\JPOSComponent-0.1-SNAPSHOT.jar %SERVICEMIX_HOME%\deploy

copy ..\Smart\ZTEISO8583\target\ZTEISO8583-0.1-SNAPSHOT.jar %SERVICEMIX_HOME%\deploy
copy ..\Smart\BSMISO8583\target\BSMISO8583-0.1-SNAPSHOT.jar %SERVICEMIX_HOME%\deploy
copy ..\Smart\ZTEBillPayment\target\ZTEBillPayment-0.1-SNAPSHOT.jar %SERVICEMIX_HOME%\deploy

copy Frontend\target\Frontend-0.1-SNAPSHOT.jar %SERVICEMIX_HOME%\deploy
copy ISODefinitions\target\ISODefinitions-0.1-SNAPSHOT.jar %SERVICEMIX_HOME%\deploy
copy ..\GTBank\BillPayments\target\BillPayments-0.1-SNAPSHOT.jar %SERVICEMIX_HOME%\deploy

echo "copying database config files"
copy ..\Core\settings\%1\database_config.properties %SERVICEMIX_HOME%\mfino_conf\
copy mfino_conf\* %SERVICEMIX_HOME%\mfino_conf\
copy ..\GTBank\gt.properties %SERVICEMIX_HOME%\mfino_conf\
copy ..\Core\settings\%1\mfino.properties %SERVICEMIX_HOME%\mfino_conf\

echo "copying jpos config files"
mkdir %SERVICEMIX_HOME%\jpos\deploy
copy ..\Smart\SmartConfiguration\src\main\jpos\* %SERVICEMIX_HOME%\jpos\deploy\

mkdir %SERVICEMIX_HOME%\jpos\jpos_cfg
copy ..\Smart\SmartConfiguration\src\main\jpos_cfg\* %SERVICEMIX_HOME%\jpos\jpos_cfg\

echo "copying config files"
copy MCEConfiguration\src\main\resources\META-INF\spring\mce_fix_configuration.xml %SERVICEMIX_HOME%\deploy\
copy MCEConfiguration\src\main\resources\META-INF\spring\mce_newfrontend_configuration.xml %SERVICEMIX_HOME%\deploy\
copy MCEConfiguration\src\main\resources\META-INF\spring\mce_backend_configuration.xml %SERVICEMIX_HOME%\deploy\
copy MCEConfiguration\src\main\resources\META-INF\spring\mce_notification_configuration.xml %SERVICEMIX_HOME%\deploy\
copy MCEConfiguration\src\main\resources\META-INF\spring\mce_iso_jpos_configuration.xml %SERVICEMIX_HOME%\deploy\
copy MCEConfiguration\src\main\resources\META-INF\spring\mce_scheduler_configuration.xml %SERVICEMIX_HOME%\deploy\
copy MCEConfiguration\src\main\resources\META-INF\spring\mce_auto_reversal_configuration.xml %SERVICEMIX_HOME%\deploy\

copy ..\Smart\SmartConfiguration\src\main\resources\META-INF\spring\mce_bsm_iso_configuration.xml %SERVICEMIX_HOME%\deploy\
copy ..\Smart\SmartConfiguration\src\main\resources\META-INF\spring\mce_zte_iso_configuration.xml %SERVICEMIX_HOME%\deploy\
copy ..\Smart\SmartConfiguration\src\main\resources\META-INF\spring\mce_billpay_configuration.xml %SERVICEMIX_HOME%\deploy\
copy ..\Smart\SmartConfiguration\src\main\resources\META-INF\spring\mce_zte_billpay_configuration.xml %SERVICEMIX_HOME%\deploy\

echo "copying integration framework jar files"
copy ..\Commons\IntegrationFramework\target\IntegrationFramework-0.1-SNAPSHOT.jar %SERVICEMIX_HOME%\deploy
copy ..\Smart\SmartBillerGatewayService\target\SmartBillerGatewayService-0.1-SNAPSHOT.jar %SERVICEMIX_HOME%\deploy
copy ..\Smart\BillerGatewayTestServer\target\BillerGatewayTestServer-0.1-SNAPSHOT.jar %SERVICEMIX_HOME%\deploy

echo "copying smart gateway variant files"
copy ..\Smart\GatewayVariantFiles\* %SERVICEMIX_HOME%\mfino_conf\

echo "copying smart gateway xml files"
copy ..\Smart\SmartConfiguration\src\main\resources\META-INF\spring\billpay_event_processing.xml %SERVICEMIX_HOME%\deploy\
copy ..\Smart\SmartConfiguration\src\main\resources\META-INF\spring\billpay_integration_calls.xml %SERVICEMIX_HOME%\deploy\
copy ..\Smart\SmartConfiguration\src\main\resources\META-INF\spring\billpay_reversals.xml %SERVICEMIX_HOME%\deploy\
copy ..\Smart\SmartConfiguration\src\main\resources\META-INF\spring\billpay_src_to_suspense.xml %SERVICEMIX_HOME%\deploy\
copy ..\Smart\SmartConfiguration\src\main\resources\META-INF\spring\billpay_susp_to_dest.xml %SERVICEMIX_HOME%\deploy\

:End
