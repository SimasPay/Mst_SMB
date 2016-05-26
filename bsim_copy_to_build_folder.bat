@echo off
if %1.==. goto End1
if Not %1.==. goto Process

:End1
echo Please provide destination Build folder
goto End

:Process
@echo on

mkdir %1\DBscripts\
mkdir %1\tomcat\
mkdir %1\tomcat\mfino_conf\
mkdir %1\tomcat\mfino_conf\RSA\
mkdir %1\tomcat\mfino_conf\CategoryFiles\
mkdir %1\servicemix\
mkdir %1\servicemix\mfino_conf\
mkdir %1\servicemix\jpos
mkdir %1\servicemix\jpos\deploy
mkdir %1\servicemix\jpos\jpos_cfg
mkdir %1\servicemix\jpos\cfg


copy Web\AdminApplication\target\AdminApplication_Dist.war %1\tomcat\
ren %1\tomcat\AdminApplication_Dist.war AdminApplication.war
copy Web\Scheduler\target\Scheduler.war %1\tomcat\
copy Web\webapi\target\webapi.war %1\tomcat\
rem copy Web\ReportScheduler\target\ReportScheduler.war %1\tomcat\
copy Web\TransactionMonitorTool\target\TransactionMonitorTool.war %1\tomcat\
copy Reports\target\Reports.war %1\tomcat

copy Core\settings\bsim\mfino.properties %1\tomcat\mfino_conf\
copy Core\settings\bsim\database_config.properties %1\tomcat\mfino_conf\
copy Core\settings\bsim\languageTranslation.json %1\tomcat\mfino_conf\

copy Core\settings\bsim\RSA*.dat %1\tomcat\mfino_conf\RSA\
copy Core\settings\bsim\category*.txt %1\tomcat\mfino_conf\CategoryFiles\
copy Core\settings\bsim\defaultJson.txt %1\tomcat\mfino_conf\CategoryFiles\
copy Core\settings\bsim\errorJson.txt %1\tomcat\mfino_conf\CategoryFiles\

copy Commons\HierarchyService\target\HierarchyService-0.1-SNAPSHOT.jar %1\servicemix
copy Commons\TransactionApi\target\TransactionApi-0.1-SNAPSHOT.jar %1\servicemix
copy Commons\IntegrationFramework\target\IntegrationFramework-0.1-SNAPSHOT.jar %1\servicemix
copy Core\target\com.mfino.application-Core.jar %1\servicemix
copy MfinoCoreEngine\Backend\target\Backend-0.1-SNAPSHOT.jar %1\servicemix
rem copy MfinoCoreEngine\BankTeller\target\BankTeller-0.1-SNAPSHOT.jar  %1\servicemix
rem copy MfinoCoreEngine\CashinIntegration\target\CashinIntegration-0.1-SNAPSHOT.jar %1\servicemix
rem copy MfinoCoreEngine\CashoutIntegration\target\CashoutIntegration-0.1-SNAPSHOT.jar %1\servicemix
copy MfinoCoreEngine\FIXServer\target\FIXServer-0.1-SNAPSHOT.jar %1\servicemix
copy MfinoCoreEngine\Frontend\target\Frontend-0.1-SNAPSHOT.jar %1\servicemix
copy MfinoCoreEngine\IntegrationInterfaces\target\IntegrationInterfaces-0.1-SNAPSHOT.jar %1\servicemix
copy MfinoCoreEngine\ISOComponent\target\ISOComponent-0.1-SNAPSHOT.jar %1\servicemix
copy MfinoCoreEngine\ISODefinitions\target\ISODefinitions-0.1-SNAPSHOT.jar %1\servicemix
copy MfinoCoreEngine\MCECore\target\MCECore-0.1-SNAPSHOT.jar %1\servicemix
copy MfinoCoreEngine\NotificationService\target\NotificationService-0.1-SNAPSHOT.jar %1\servicemix
copy MfinoCoreEngine\JPOSComponent\target\JPOSComponent-0.1-SNAPSHOT.jar %1\servicemix
copy MfinoCoreEngine\InterBankService\target\InterBankService-0.1-SNAPSHOT.jar %1\servicemix
copy MfinoCoreEngine\HSM\ThalesAdapator\target\ThalesAdaptor-0.1-SNAPSHOT.jar %1\servicemix

copy MfinoCoreEngine\HSM\ThalesConfiguration\src\main\resources\META-INF\Spring\mce_thales_configuration.xml %1\servicemix\
copy MfinoCoreEngine\MCEConfiguration\src\main\resources\META-INF\spring\mce_auto_reversal_configuration.xml %1\servicemix\
copy MfinoCoreEngine\MCEConfiguration\src\main\resources\META-INF\spring\mce_backend_configuration.xml %1\servicemix\
rem copy MfinoCoreEngine\MCEConfiguration\src\main\resources\META-INF\spring\mce_bankteller_configuration.xml %1\servicemix\
copy MfinoCoreEngine\MCEConfiguration\src\main\resources\META-INF\spring\mce_fix_configuration.xml %1\servicemix\
rem copy MfinoCoreEngine\MCEConfiguration\src\main\resources\META-INF\spring\mce_integration_cashin_configuration.xml %1\servicemix\
rem copy MfinoCoreEngine\MCEConfiguration\src\main\resources\META-INF\spring\mce_integration_cashout_configuration.xml %1\servicemix\
copy MfinoCoreEngine\MCEConfiguration\src\main\resources\META-INF\spring\mce_iso_jpos_configuration.xml %1\servicemix\
copy MfinoCoreEngine\MCEConfiguration\src\main\resources\META-INF\spring\mce_newfrontend_configuration.xml %1\servicemix\
copy MfinoCoreEngine\MCEConfiguration\src\main\resources\META-INF\spring\mce_notification_configuration.xml %1\servicemix\
copy Core\settings\bsim\mce_scheduler_configuration.xml %1\servicemix\
rem copy MfinoCoreEngine\MCEConfiguration\src\main\resources\META-INF\spring\pocket_commodity_adjustments.xml %1\servicemix\
rem copy IntegrationTemplates\CashinFrontend\target\CashinFrontend-0.1-SNAPSHOT.jar %1\servicemix
rem copy IntegrationTemplates\CashoutFrontend\target\CashoutFrontend-0.1-SNAPSHOT.jar %1\servicemix
rem copy Tools\CommodityAdjustments\target\CommodityAdjustments-0.1-SNAPSHOT.jar %1\servicemix

copy GTBank\BillPayments\target\BillPayments-0.1-SNAPSHOT.jar %1\servicemix

copy BSIM\BSIMConfiguration\src\main\resources\META-INF\spring\mce_billpay_configuration.xml %1\servicemix\
copy BSIM\BSIMConfiguration\src\main\resources\META-INF\spring\mce_bsim_billpay_configuration.xml %1\servicemix\
copy BSIM\BSIMConfiguration\src\main\resources\META-INF\spring\mce_bsim_iso_configuration.xml %1\servicemix\
rem copy BSIM\BSIMConfiguration\src\main\resources\META-INF\spring\mce_bsim_sms_configuration.xml %1\servicemix\
copy BSIM\BSIMConfiguration\src\main\resources\META-INF\spring\mce_new_bsim_sms_configuration.xml %1\servicemix\
copy BSIM\BSIMConfiguration\src\main\resources\META-INF\spring\mce_bsim_interbanktransfer_configuration.xml %1\servicemix\
copy BSIM\BSIMConfiguration\src\main\resources\META-INF\spring\mce_simaspay_sms_configuration.xml %1\servicemix\
copy BSIM\BSIMBillPayment\target\BSIMBillPayment-0.1-SNAPSHOT.jar %1\servicemix\
copy BSIM\BSIMInterBankTransfer\target\BSIMInterBankTransfer-0.1-SNAPSHOT.jar %1\servicemix\
copy BSIM\BSIMISO8583\target\BSIMISO8583-0.1-SNAPSHOT.jar %1\servicemix\
rem copy BSIM\BSIMSMS\target\BSIMSMS-0.1-SNAPSHOT.jar %1\servicemix\
copy BSIM\NewBSIMSMS\target\NewBSIMSMS-0.1-SNAPSHOT.jar %1\servicemix\
copy Hub\SimaspaySMS\target\SimaspaySMS-0.1-SNAPSHOT.jar %1\servicemix\

copy BSIM\FlashizISO8583\target\BSIMFlashizISO8583-0.1-SNAPSHOT.jar %1\servicemix\
copy BSIM\BSIMConfiguration\src\main\resources\META-INF\spring\mce_bsim_flashiz_iso_configuration.xml %1\servicemix\

copy Core\settings\bsim\database_config.properties %1\servicemix\mfino_conf\
copy Core\settings\bsim\bc2qmap.cfg %1\servicemix\mfino_conf\
copy Core\settings\bsim\mfino.properties %1\servicemix\mfino_conf\
copy MfinoCoreEngine\mfino_conf\externalcodedescriptions.xml %1\servicemix\mfino_conf\
copy Core\settings\bsim\mce.properties %1\servicemix\mfino_conf\


copy MfinoCoreEngine\HSM\ThalesConfiguration\src\main\resources\jpos\thales_logger.xml %1\servicemix\jpos\deploy\
copy MfinoCoreEngine\HSM\ThalesConfiguration\src\main\resources\jpos\thales_config.xml %1\servicemix\jpos\deploy\
copy BSIM\BSIMConfiguration\src\main\jpos\connection_config_bsm.xml %1\servicemix\jpos\deploy\
copy BSIM\BSIMConfiguration\src\main\jpos\jpos_config_bsm.xml %1\servicemix\jpos\deploy\
copy BSIM\BSIMConfiguration\src\main\jpos\mux_config_bsm.xml %1\servicemix\jpos\deploy\
copy BSIM\BSIMConfiguration\src\main\jpos\connection_config_flashiz.xml %1\servicemix\jpos\deploy\
copy BSIM\BSIMConfiguration\src\main\jpos\jpos_config_flashiz.xml %1\servicemix\jpos\deploy\
copy BSIM\BSIMConfiguration\src\main\jpos\mux_config_flashiz.xml %1\servicemix\jpos\deploy\

copy BSIM\BSIMConfiguration\src\main\jpos_cfg\iso87ascii-bsm.xml %1\servicemix\jpos\jpos_cfg\
copy BSIM\BSIMConfiguration\src\main\jpos_cfg\iso87ascii-flashiz.xml %1\servicemix\jpos\jpos_cfg\
copy MfinoCoreEngine\HSM\ThalesConfiguration\src\main\resources\jpos\cfg\*.xml %1\servicemix\jpos\cfg\

rem BSM PPOB integration 
rem copy BSIM\BSMPPOBISO8583\target\BSMPPOBISO8583-0.1-SNAPSHOT.jar %1\servicemix
rem copy BSIM\BSIMConfiguration\src\main\resources\META-INF\spring\mce_bsm_ppob_iso_configuration.xml %1\servicemix\
rem copy BSIM\BSIMConfiguration\src\main\jpos_cfg\iso87ascii-bsm-ppob.xml %1\servicemix\jpos\jpos_cfg\
rem copy BSIM\BSIMConfiguration\src\main\jpos\connection_config_bsm_ppob.xml %1\servicemix\jpos\deploy\
rem copy BSIM\BSIMConfiguration\src\main\jpos\jpos_config_bsm_ppob.xml %1\servicemix\jpos\deploy\
rem copy BSIM\BSIMConfiguration\src\main\jpos\mux_config_bsm_ppob.xml %1\servicemix\jpos\deploy\


:End