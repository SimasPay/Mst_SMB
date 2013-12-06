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
mkdir %1\servicemix\
mkdir %1\servicemix\mfino_conf\
mkdir %1\servicemix\mfino_conf\xslt

copy Web\AdminApplication\target\AdminApplication_Dist.war %1\tomcat\
ren %1\tomcat\AdminApplication_Dist.war AdminApplication.war
copy Web\Scheduler\target\Scheduler.war %1\tomcat\
copy Web\webapi\target\webapi.war %1\tomcat\
copy Web\ReportScheduler\target\ReportScheduler.war %1\tomcat\

copy Commons\HierarchyService\target\HierarchyService-0.1-SNAPSHOT.jar %1\servicemix
copy Commons\TransactionApi\target\TransactionApi-0.1-SNAPSHOT.jar %1\servicemix
copy Commons\TransactionAwareHandlers\target\TransactionAwareHandlers-0.1-SNAPSHOT.jar
copy Core\target\com.mfino.application-Core.jar %1\servicemix
copy MfinoCoreEngine\Backend\target\Backend-0.1-SNAPSHOT.jar %1\servicemix
copy MfinoCoreEngine\BankTeller\target\BankTeller-0.1-SNAPSHOT.jar  %1\servicemix
copy MfinoCoreEngine\CashinIntegration\target\CashinIntegration-0.1-SNAPSHOT.jar %1\servicemix
copy MfinoCoreEngine\CashoutIntegration\target\CashoutIntegration-0.1-SNAPSHOT.jar %1\servicemix
copy MfinoCoreEngine\FIXServer\target\FIXServer-0.1-SNAPSHOT.jar %1\servicemix
copy MfinoCoreEngine\Frontend\target\Frontend-0.1-SNAPSHOT.jar %1\servicemix
copy MfinoCoreEngine\IntegrationInterfaces\target\IntegrationInterfaces-0.1-SNAPSHOT.jar %1\servicemix
copy MfinoCoreEngine\ISODefinitions\target\ISODefinitions-0.1-SNAPSHOT.jar %1\servicemix
copy MfinoCoreEngine\MCECore\target\MCECore-0.1-SNAPSHOT.jar %1\servicemix
copy MfinoCoreEngine\NotificationService\target\NotificationService-0.1-SNAPSHOT.jar %1\servicemix
copy MfinoCoreEngine\MCEConfiguration\src\main\resources\META-INF\spring\mce_auto_reversal_configuration.xml %1\servicemix\
copy MfinoCoreEngine\MCEConfiguration\src\main\resources\META-INF\spring\mce_backend_configuration.xml %1\servicemix\
copy MfinoCoreEngine\MCEConfiguration\src\main\resources\META-INF\spring\mce_bankteller_configuration.xml %1\servicemix\
copy MfinoCoreEngine\MCEConfiguration\src\main\resources\META-INF\spring\mce_fix_configuration.xml %1\servicemix\
copy MfinoCoreEngine\MCEConfiguration\src\main\resources\META-INF\spring\mce_integration_cashin_configuration.xml %1\servicemix\
copy MfinoCoreEngine\MCEConfiguration\src\main\resources\META-INF\spring\mce_integration_cashout_configuration.xml %1\servicemix\
copy MfinoCoreEngine\MCEConfiguration\src\main\resources\META-INF\spring\mce_newfrontend_configuration.xml %1\servicemix\
copy MfinoCoreEngine\MCEConfiguration\src\main\resources\META-INF\spring\mce_notification_configuration.xml %1\servicemix\
copy MfinoCoreEngine\MCEConfiguration\src\main\resources\META-INF\spring\mce_scheduler_configuration.xml %1\servicemix\
copy MfinoCoreEngine\MCEConfiguration\src\main\resources\META-INF\spring\pocket_commodity_adjustments.xml %1\servicemix\
copy IntegrationTemplates\CashinFrontend\target\CashinFrontend-0.1-SNAPSHOT.jar %1\servicemix
copy IntegrationTemplates\CashoutFrontend\target\CashoutFrontend-0.1-SNAPSHOT.jar %1\servicemix
copy Tools\CommodityAdjustments\target\CommodityAdjustments-0.1-SNAPSHOT.jar %1\servicemix

copy GTBank\BillPayments\target\BillPayments-0.1-SNAPSHOT.jar %1\servicemix
copy Fortis\FortisConfiguration\src\main\resources\META-INF\spring\mce_email_configuration.xml %1\servicemix\
copy GTBank\GTConfiguration\src\main\resources\META-INF\spring\interswitch_cashin_configuration.xml %1\servicemix\
copy GTBank\GTConfiguration\src\main\resources\META-INF\spring\mce_billpay_configuration.xml %1\servicemix\
copy GTBank\GTConfiguration\src\main\resources\META-INF\spring\mce_billpayment_timers_configuration.xml %1\servicemix\
copy GTBank\GTConfiguration\src\main\resources\META-INF\spring\interswitch_cashout_configuration.xml %1\servicemix\
copy GTBank\GTConfiguration\src\main\resources\META-INF\spring\mce_quickteller_billpay_configuration.xml %1\servicemix\
copy GTBank\GTConfiguration\src\main\resources\META-INF\spring\nibs_cashin_configuration.xml %1\servicemix\

copy Sterling\SterlingBankIntegration\target\SterlingBankIntegration-0.1-SNAPSHOT.jar %1\servicemix\
copy Sterling\SterlingConfiguration\src\main\resources\META-INF\spring\mce_sterling_bank_configuration.xml %1\servicemix\
copy Core\settings\sterling\externalcodedescriptions.xml %1\servicemix\mfino_conf\

copy Core\settings\fortis\database_config.properties %1\servicemix\mfino_conf\
copy MfinoCoreEngine\mfino_conf\bc2qmap.cfg %1\servicemix\mfino_conf\
copy MfinoCoreEngine\mfino_conf\cashinfrontendmap.cfg %1\servicemix\mfino_conf\
copy MfinoCoreEngine\mfino_conf\cashoutfrontendmap.cfg %1\servicemix\mfino_conf\
copy MfinoCoreEngine\mfino_conf\mce.properties %1\servicemix\mfino_conf\

copy GTBank\xslt\interswitchDetailsRequest.xsl %1\servicemix\mfino_conf\xslt
copy GTBank\xslt\interswitchDetailsResponse.xsl %1\servicemix\mfino_conf\xslt
copy GTBank\xslt\interswitchRequest.xsl %1\servicemix\mfino_conf\xslt
copy GTBank\xslt\interswitchResponse.xsl %1\servicemix\mfino_conf\xslt
copy GTBank\xslt\nibsDetailsRequest.xsl %1\servicemix\mfino_conf\xslt
copy GTBank\xslt\nibsDetailsResponse.xsl %1\servicemix\mfino_conf\xslt
copy GTBank\xslt\nibsRequest.xsl %1\servicemix\mfino_conf\xslt
copy GTBank\xslt\nibsResponse.xsl %1\servicemix\mfino_conf\xslt

:End