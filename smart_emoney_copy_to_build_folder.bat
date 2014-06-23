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
mkdir %1\servicemix\
mkdir %1\servicemix\mfino_conf\
mkdir %1\servicemix\jpos
mkdir %1\servicemix\jpos\deploy
mkdir %1\servicemix\jpos\jpos_cfg

copy Web\AdminApplication\target\AdminApplication_Dist.war %1\tomcat\
ren %1\tomcat\AdminApplication_Dist.war AdminApplication.war
copy Web\Scheduler\target\Scheduler.war %1\tomcat\
copy Web\webapi\target\webapi.war %1\tomcat\
rem copy Web\ReportScheduler\target\ReportScheduler.war %1\tomcat\
rem copy Web\TransactionMonitorTool\target\TransactionMonitorTool.war %1\tomcat\
rem copy reportModule\DbCopyTool\target\DbCopyTool.war %1\tomcat
rem copy reportModule\RTScheduler\target\RTScheduler.war %1\tomcat
copy Reports\target\Reports.war %1\tomcat  

copy Core\settings\smart\mfino.properties %1\tomcat\mfino_conf\
copy Core\settings\smart\languageTranslation.json %1\tomcat\mfino_conf\
copy Core\settings\smart\kyc_txn_list.json %1\tomcat\mfino_conf\

copy Commons\HierarchyService\target\HierarchyService-0.1-SNAPSHOT.jar %1\servicemix
copy Commons\TransactionApi\target\TransactionApi-0.1-SNAPSHOT.jar %1\servicemix
copy Core\target\com.mfino.application-Core.jar %1\servicemix
copy MfinoCoreEngine\Backend\target\Backend-0.1-SNAPSHOT.jar %1\servicemix
copy MfinoCoreEngine\FIXServer\target\FIXServer-0.1-SNAPSHOT.jar %1\servicemix
copy MfinoCoreEngine\Frontend\target\Frontend-0.1-SNAPSHOT.jar %1\servicemix
copy MfinoCoreEngine\ISODefinitions\target\ISODefinitions-0.1-SNAPSHOT.jar %1\servicemix
copy MfinoCoreEngine\MCECore\target\MCECore-0.1-SNAPSHOT.jar %1\servicemix
copy MfinoCoreEngine\NotificationService\target\NotificationService-0.1-SNAPSHOT.jar %1\servicemix
copy MfinoCoreEngine\JPOSComponent\target\JPOSComponent-0.1-SNAPSHOT.jar %1\servicemix
rem copy MfinoCoreEngine\ISOComponent\target\ISOComponent-0.1-SNAPSHOT.jar %1\servicemix
copy MfinoCoreEngine\IntegrationInterfaces\target\IntegrationInterfaces-0.1-SNAPSHOT.jar %1\servicemix
copy ISO8583API\target\ISO8583API-0.1-SNAPSHOT.jar %1\servicemix
copy GTBank\BillPayments\target\BillPayments-0.1-SNAPSHOT.jar %1\servicemix

copy MfinoCoreEngine\MCEConfiguration\src\main\resources\META-INF\spring\mce_auto_reversal_configuration.xml %1\servicemix\
copy MfinoCoreEngine\MCEConfiguration\src\main\resources\META-INF\spring\mce_backend_configuration.xml %1\servicemix\
copy MfinoCoreEngine\MCEConfiguration\src\main\resources\META-INF\spring\mce_fix_configuration.xml %1\servicemix\
copy MfinoCoreEngine\MCEConfiguration\src\main\resources\META-INF\spring\mce_iso_jpos_configuration.xml %1\servicemix\
copy MfinoCoreEngine\MCEConfiguration\src\main\resources\META-INF\spring\mce_newfrontend_configuration.xml %1\servicemix\
copy MfinoCoreEngine\MCEConfiguration\src\main\resources\META-INF\spring\mce_notification_configuration.xml %1\servicemix\
copy MfinoCoreEngine\MCEConfiguration\src\main\resources\META-INF\spring\mce_scheduler_configuration.xml %1\servicemix\

copy Core\settings\smart\database_config.properties %1\servicemix\mfino_conf\
copy MfinoCoreEngine\mfino_conf\bc2qmap.cfg %1\servicemix\mfino_conf\
copy MfinoCoreEngine\mfino_conf\externalcodedescriptions.xml %1\servicemix\mfino_conf\
copy MfinoCoreEngine\mfino_conf\mce.properties %1\servicemix\mfino_conf\
copy Core\settings\smart\mfino.properties %1\servicemix\mfino_conf\

rem SMS service
rem copy Smart\SmartConfiguration\src\main\resources\META-INF\spring\mce_smart_sms_configuration.xml %1\servicemix\
copy BSIM\BSIMSMS\target\BSIMSMS-0.1-SNAPSHOT.jar %1\servicemix\
copy BSIM\BSIMConfiguration\src\main\resources\META-INF\spring\mce_bsim_sms_configuration.xml %1\servicemix\

rem VA cashin build
copy MfinoCoreEngine\CashinIntegration\target\CashinIntegration-0.1-SNAPSHOT.jar %1\servicemix
copy Smart\VAISO8583\target\VAISO8583-0.1-SNAPSHOT.jar %1\servicemix
copy MfinoCoreEngine\MCEConfiguration\src\main\resources\META-INF\spring\mce_integration_cashin_configuration.xml %1\servicemix\
copy Smart\SmartConfiguration\src\main\jpos\vah_isoserver_config.xml %1\servicemix\jpos\deploy\
copy Smart\SmartConfiguration\src\main\jpos\jpos_config_vah.xml %1\servicemix\jpos\deploy\
copy Smart\SmartConfiguration\src\main\jpos_cfg\iso87ascii-vah.xml %1\servicemix\jpos\jpos_cfg\

rem ZTE integration
copy Smart\ZTEBillPayment\target\ZTEBillPayment-0.1-SNAPSHOT.jar %1\servicemix
copy Smart\ZTEISO8583\target\ZTEISO8583-0.1-SNAPSHOT.jar %1\servicemix
rem copy Smart\SmartConfiguration\src\main\resources\META-INF\spring\smart_billpay_router.xml %1\servicemix\
copy Smart\SmartConfiguration\src\main\resources\META-INF\spring\mce_zte_iso_configuration.xml %1\servicemix\
copy Smart\SmartConfiguration\src\main\resources\META-INF\spring\mce_zte_billpay_configuration.xml %1\servicemix\
copy Smart\SmartConfiguration\src\main\jpos\connection_config_zte.xml %1\servicemix\jpos\deploy\
copy Smart\SmartConfiguration\src\main\jpos\jpos_config_zte.xml %1\servicemix\jpos\deploy\
copy Smart\SmartConfiguration\src\main\jpos\mux_config_zte.xml %1\servicemix\jpos\deploy\
copy Smart\SmartConfiguration\src\main\jpos_cfg\iso87ascii-zte.xml %1\servicemix\jpos\jpos_cfg\

rem BSM integration (bank)
copy Smart\BSMISO8583\target\BSMISO8583-0.1-SNAPSHOT.jar %1\servicemix
copy Smart\SmartConfiguration\src\main\resources\META-INF\spring\mce_bsm_iso_configuration.xml %1\servicemix\
copy Smart\SmartConfiguration\src\main\jpos_cfg\iso87ascii-bsm.xml %1\servicemix\jpos\jpos_cfg\
copy Smart\SmartConfiguration\src\main\jpos\connection_config_bsm.xml %1\servicemix\jpos\deploy\
copy Smart\SmartConfiguration\src\main\jpos\jpos_config_bsm.xml %1\servicemix\jpos\deploy\
copy Smart\SmartConfiguration\src\main\jpos\mux_config_bsm.xml %1\servicemix\jpos\deploy\

rem Interbank transfer
copy MfinoCoreEngine\InterBankService\target\InterBankService-0.1-SNAPSHOT.jar %1\servicemix\
copy BSIM\BSIMConfiguration\src\main\resources\META-INF\spring\mce_bsim_interbanktransfer_configuration.xml %1\servicemix\
copy BSIM\BSIMInterBankTransfer\target\BSIMInterBankTransfer-0.1-SNAPSHOT.jar %1\servicemix\

rem Flashiz Related Files
copy Hub\FlashizISO8583\target\FlashizISO8583-0.1-SNAPSHOT.jar %1\servicemix
copy Hub\FlashizBillPayment\target\FlashizBillPayment-0.1-SNAPSHOT.jar %1\servicemix
copy Hub\HubConfiguration\src\main\resources\META-INF\spring\mce_flashiz_iso_configuration.xml %1\servicemix\
copy Hub\HubConfiguration\src\main\resources\META-INF\spring\mce_flashiz_payment_xml_configuration.xml %1\servicemix\
copy Hub\HubConfiguration\src\main\jpos\connection_config_flashiz.xml %1\servicemix\jpos\deploy\
copy Hub\HubConfiguration\src\main\jpos\jpos_config_flashiz.xml %1\servicemix\jpos\deploy\
copy Hub\HubConfiguration\src\main\jpos\mux_config_flashiz.xml %1\servicemix\jpos\deploy\
copy Hub\HubConfiguration\src\main\jpos_cfg\iso87ascii-flashiz.xml %1\servicemix\jpos\jpos_cfg\

rem Bill payment
copy GTBank\BillPayments\target\BillPayments-0.1-SNAPSHOT.jar %1\servicemix\
copy Hub\HubConfiguration\src\main\resources\META-INF\spring\mce_billpay_configuration.xml %1\servicemix
copy Hub\HubConfiguration\src\main\resources\META-INF\spring\mce_bayar_billpay_configuration.xml %1\servicemix
copy Hub\BayarBillPayments\target\BayarBillPayments-0.1-SNAPSHOT.jar %1\servicemix
rem copy Hub\HubConfiguration\src\main\resources\META-INF\spring\mce_hub_xml_configuration.xml %1\servicemix
rem copy Hub\HubXMLRPC\target\HubXMLRPC-0.1-SNAPSHOT.jar %1\servicemix

:End