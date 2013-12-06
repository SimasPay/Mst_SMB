@echo off

set RELEASE_FOLDER_PATH=D:\programs\BUILDS\BSIM_BUILD_MAIN_11398

copy MfinoCoreEngine\Backend\target\Backend-0.1-SNAPSHOT.jar %RELEASE_FOLDER_PATH%\smx
copy GTBank\BillPayments\target\BillPayments-0.1-SNAPSHOT.jar %RELEASE_FOLDER_PATH%\smx

copy BSIM\BSIMConfiguration\src\main\resources\META-INF\spring\mce_billpay_configuration.xml %RELEASE_FOLDER_PATH%\smx
copy BSIM\BSIMConfiguration\src\main\resources\META-INF\spring\mce_bsim_billpay_configuration.xml %RELEASE_FOLDER_PATH%\smx
copy BSIM\BSIMConfiguration\src\main\resources\META-INF\spring\mce_bsim_iso_configuration.xml %RELEASE_FOLDER_PATH%\smx
copy BSIM\BSIMConfiguration\src\main\resources\META-INF\spring\mce_bsim_sms_configuration.xml %RELEASE_FOLDER_PATH%\smx
copy BSIM\BSIMConfiguration\src\main\resources\META-INF\spring\mce_bsim_interbanktransfer_configuration.xml %RELEASE_FOLDER_PATH%\smx
copy BSIM\BSIMBillPayment\target\BSIMBillPayment-0.1-SNAPSHOT.jar %RELEASE_FOLDER_PATH%\smx
copy BSIM\BSIMISO8583\target\BSIMISO8583-0.1-SNAPSHOT.jar %RELEASE_FOLDER_PATH%\smx
copy BSIM\BSIMSMS\target\BSIMSMS-0.1-SNAPSHOT.jar %RELEASE_FOLDER_PATH%\smx
copy BSIM\BSIMInterBankTransfer\target\BSIMInterBankTransfer-0.1-SNAPSHOT.jar %RELEASE_FOLDER_PATH%\smx

copy Core\target\com.mfino.application-Core.jar %RELEASE_FOLDER_PATH%\smx
copy MfinoCoreEngine\FIXServer\target\FIXServer-0.1-SNAPSHOT.jar %RELEASE_FOLDER_PATH%\smx
copy MfinoCoreEngine\Frontend\target\Frontend-0.1-SNAPSHOT.jar %RELEASE_FOLDER_PATH%\smx
copy Commons\HierarchyService\target\HierarchyService-0.1-SNAPSHOT.jar %RELEASE_FOLDER_PATH%\smx
copy Commons\IntegrationFramework\target\IntegrationFramework-0.1-SNAPSHOT.jar %RELEASE_FOLDER_PATH%\smx
copy MfinoCoreEngine\IntegrationInterfaces\target\IntegrationInterfaces-0.1-SNAPSHOT.jar %RELEASE_FOLDER_PATH%\smx
copy MfinoCoreEngine\ISOComponent\target\ISOComponent-0.1-SNAPSHOT.jar %RELEASE_FOLDER_PATH%\smx
copy MfinoCoreEngine\ISODefinitions\target\ISODefinitions-0.1-SNAPSHOT.jar %RELEASE_FOLDER_PATH%\smx
copy MfinoCoreEngine\JPOSComponent\target\JPOSComponent-0.1-SNAPSHOT.jar %RELEASE_FOLDER_PATH%\smx
copy MfinoCoreEngine\MCECore\target\MCECore-0.1-SNAPSHOT.jar %RELEASE_FOLDER_PATH%\smx
copy MfinoCoreEngine\InterBankService\target\InterBankService-0.1-SNAPSHOT.jar %RELEASE_FOLDER_PATH%\smx
copy MfinoCoreEngine\MCEConfiguration\src\main\resources\META-INF\spring\mce_auto_reversal_configuration.xml %RELEASE_FOLDER_PATH%\smx
copy MfinoCoreEngine\MCEConfiguration\src\main\resources\META-INF\spring\mce_backend_configuration.xml %RELEASE_FOLDER_PATH%\smx

copy MfinoCoreEngine\MCEConfiguration\src\main\resources\META-INF\spring\mce_fix_configuration.xml %RELEASE_FOLDER_PATH%\smx
copy MfinoCoreEngine\MCEConfiguration\src\main\resources\META-INF\spring\mce_integration_cashin_configuration.xml %RELEASE_FOLDER_PATH%\smx
copy MfinoCoreEngine\MCEConfiguration\src\main\resources\META-INF\spring\mce_iso_jpos_configuration.xml %RELEASE_FOLDER_PATH%\smx
copy MfinoCoreEngine\MCEConfiguration\src\main\resources\META-INF\spring\mce_newfrontend_configuration.xml %RELEASE_FOLDER_PATH%\smx
copy MfinoCoreEngine\MCEConfiguration\src\main\resources\META-INF\spring\mce_notification_configuration.xml %RELEASE_FOLDER_PATH%\smx
copy MfinoCoreEngine\MCEConfiguration\src\main\resources\META-INF\spring\mce_scheduler_configuration.xml %RELEASE_FOLDER_PATH%\smx
copy MfinoCoreEngine\NotificationService\target\NotificationService-0.1-SNAPSHOT.jar %RELEASE_FOLDER_PATH%\smx
copy Commons\TransactionApi\target\TransactionApi-0.1-SNAPSHOT.jar %RELEASE_FOLDER_PATH%\smx

copy Web\AdminApplication\target\AdminApplication_Dist.war %RELEASE_FOLDER_PATH%\web
copy Web\webapi\target\webapi.war %RELEASE_FOLDER_PATH%\web
copy Web\Scheduler\target\Scheduler.war %RELEASE_FOLDER_PATH%\web