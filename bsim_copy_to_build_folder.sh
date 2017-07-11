#@echo off

DEPLOY_FOLDER="/Users/dimoyog/Simaspay/source/jboss/deploy"
#echo $DEPLOY_FOLDER
#:Process
#@echo on



rm -r $DEPLOY_FOLDER/DBscripts/; mkdir $DEPLOY_FOLDER/DBscripts/
rm -r $DEPLOY_FOLDER/tomcat/; mkdir $DEPLOY_FOLDER/tomcat/
rm -r $DEPLOY_FOLDER/tomcat/mfino_conf/; mkdir $DEPLOY_FOLDER/tomcat/mfino_conf/
rm -r $DEPLOY_FOLDER/tomcat/mfino_conf/RSA/; mkdir $DEPLOY_FOLDER/tomcat/mfino_conf/RSA/
rm -r $DEPLOY_FOLDER/tomcat/mfino_conf/CategoryFiles/; mkdir $DEPLOY_FOLDER/tomcat/mfino_conf/CategoryFiles/
rm -r $DEPLOY_FOLDER/servicemix/; mkdir $DEPLOY_FOLDER/servicemix/
rm -r $DEPLOY_FOLDER/servicemix/mfino_conf/; mkdir $DEPLOY_FOLDER/servicemix/mfino_conf/
rm -r $DEPLOY_FOLDER/servicemix/jpos; mkdir $DEPLOY_FOLDER/servicemix/jpos
rm -r $DEPLOY_FOLDER/servicemix/jpos/deploy; mkdir $DEPLOY_FOLDER/servicemix/jpos/deploy
rm -r $DEPLOY_FOLDER/servicemix/jpos/jpos_cfg; mkdir $DEPLOY_FOLDER/servicemix/jpos/jpos_cfg
rm -r $DEPLOY_FOLDER/servicemix/jpos/cfg; mkdir $DEPLOY_FOLDER/servicemix/jpos/cfg


cp Web/AdminApplication/target/AdminApplication_Dist.war $DEPLOY_FOLDER/tomcat/
mv $DEPLOY_FOLDER/tomcat/AdminApplication_Dist.war $DEPLOY_FOLDER/tomcat/AdminApplication.war
cp Web/Scheduler/target/Scheduler.war $DEPLOY_FOLDER/tomcat/
cp Web/webapi/target/webapi.war $DEPLOY_FOLDER/tomcat/
# cp Web/ReportScheduler/target/ReportScheduler.war $DEPLOY_FOLDER/tomcat/
cp Web/TransactionMonitorTool/target/TransactionMonitorTool.war $DEPLOY_FOLDER/tomcat/
cp Reports/target/Reports.war $DEPLOY_FOLDER/tomcat

cp Core/settings/bsim/mfino.properties $DEPLOY_FOLDER/tomcat/mfino_conf/
cp Core/settings/bsim/database_config.properties $DEPLOY_FOLDER/tomcat/mfino_conf/
cp Core/settings/bsim/languageTranslation.json $DEPLOY_FOLDER/tomcat/mfino_conf/

cp Core/settings/bsim/RSA*.dat $DEPLOY_FOLDER/tomcat/mfino_conf/RSA/
cp Core/settings/bsim/category*.txt $DEPLOY_FOLDER/tomcat/mfino_conf/CategoryFiles/
cp Core/settings/bsim/defaultJson.txt $DEPLOY_FOLDER/tomcat/mfino_conf/CategoryFiles/
cp Core/settings/bsim/errorJson.txt $DEPLOY_FOLDER/tomcat/mfino_conf/CategoryFiles/

cp Commons/HierarchyService/target/HierarchyService-0.1-SNAPSHOT.jar $DEPLOY_FOLDER/servicemix
cp Commons/TransactionApi/target/TransactionApi-0.1-SNAPSHOT.jar $DEPLOY_FOLDER/servicemix
cp Commons/IntegrationFramework/target/IntegrationFramework-0.1-SNAPSHOT.jar $DEPLOY_FOLDER/servicemix
cp Core/target/com.mfino.application-Core.jar $DEPLOY_FOLDER/servicemix
cp MfinoCoreEngine/Backend/target/Backend-0.1-SNAPSHOT.jar $DEPLOY_FOLDER/servicemix
# cp MfinoCoreEngine/BankTeller/target/BankTeller-0.1-SNAPSHOT.jar  $DEPLOY_FOLDER/servicemix
# cp MfinoCoreEngine/CashinIntegration/target/CashinIntegration-0.1-SNAPSHOT.jar $DEPLOY_FOLDER/servicemix
# cp MfinoCoreEngine/CashoutIntegration/target/CashoutIntegration-0.1-SNAPSHOT.jar $DEPLOY_FOLDER/servicemix
cp MfinoCoreEngine/FIXServer/target/FIXServer-0.1-SNAPSHOT.jar $DEPLOY_FOLDER/servicemix
cp MfinoCoreEngine/Frontend/target/Frontend-0.1-SNAPSHOT.jar $DEPLOY_FOLDER/servicemix
cp MfinoCoreEngine/IntegrationInterfaces/target/IntegrationInterfaces-0.1-SNAPSHOT.jar $DEPLOY_FOLDER/servicemix
cp MfinoCoreEngine/ISOComponent/target/ISOComponent-0.1-SNAPSHOT.jar $DEPLOY_FOLDER/servicemix
cp MfinoCoreEngine/ISODefinitions/target/ISODefinitions-0.1-SNAPSHOT.jar $DEPLOY_FOLDER/servicemix
cp MfinoCoreEngine/MCECore/target/MCECore-0.1-SNAPSHOT.jar $DEPLOY_FOLDER/servicemix
cp MfinoCoreEngine/NotificationService/target/NotificationService-0.1-SNAPSHOT.jar $DEPLOY_FOLDER/servicemix
cp MfinoCoreEngine/JPOSComponent/target/JPOSComponent-0.1-SNAPSHOT.jar $DEPLOY_FOLDER/servicemix
cp MfinoCoreEngine/InterBankService/target/InterBankService-0.1-SNAPSHOT.jar $DEPLOY_FOLDER/servicemix
cp MfinoCoreEngine/HSM/ThalesAdapator/target/ThalesAdaptor-0.1-SNAPSHOT.jar $DEPLOY_FOLDER/servicemix

cp MfinoCoreEngine/HSM/ThalesConfiguration/src/main/resources/META-INF/Spring/mce_thales_configuration.xml $DEPLOY_FOLDER/servicemix/
cp MfinoCoreEngine/MCEConfiguration/src/main/resources/META-INF/spring/mce_auto_reversal_configuration.xml $DEPLOY_FOLDER/servicemix/
cp MfinoCoreEngine/MCEConfiguration/src/main/resources/META-INF/spring/mce_backend_configuration.xml $DEPLOY_FOLDER/servicemix/
# cp MfinoCoreEngine/MCEConfiguration/src/main/resources/META-INF/spring/mce_bankteller_configuration.xml $DEPLOY_FOLDER/servicemix/
cp MfinoCoreEngine/MCEConfiguration/src/main/resources/META-INF/spring/mce_fix_configuration.xml $DEPLOY_FOLDER/servicemix/
# cp MfinoCoreEngine/MCEConfiguration/src/main/resources/META-INF/spring/mce_integration_cashin_configuration.xml $DEPLOY_FOLDER/servicemix/
# cp MfinoCoreEngine/MCEConfiguration/src/main/resources/META-INF/spring/mce_integration_cashout_configuration.xml $DEPLOY_FOLDER/servicemix/
cp MfinoCoreEngine/MCEConfiguration/src/main/resources/META-INF/spring/mce_iso_jpos_configuration.xml $DEPLOY_FOLDER/servicemix/
cp MfinoCoreEngine/MCEConfiguration/src/main/resources/META-INF/spring/mce_newfrontend_configuration.xml $DEPLOY_FOLDER/servicemix/
cp MfinoCoreEngine/MCEConfiguration/src/main/resources/META-INF/spring/mce_notification_configuration.xml $DEPLOY_FOLDER/servicemix/
cp Core/settings/bsim/mce_scheduler_configuration.xml $DEPLOY_FOLDER/servicemix/
# cp MfinoCoreEngine/MCEConfiguration/src/main/resources/META-INF/spring/pocket_commodity_adjustments.xml $DEPLOY_FOLDER/servicemix/
# cp IntegrationTemplates/CashinFrontend/target/CashinFrontend-0.1-SNAPSHOT.jar $DEPLOY_FOLDER/servicemix
# cp IntegrationTemplates/CashoutFrontend/target/CashoutFrontend-0.1-SNAPSHOT.jar $DEPLOY_FOLDER/servicemix
# cp Tools/CommodityAdjustments/target/CommodityAdjustments-0.1-SNAPSHOT.jar $DEPLOY_FOLDER/servicemix

cp GTBank/BillPayments/target/BillPayments-0.1-SNAPSHOT.jar $DEPLOY_FOLDER/servicemix

cp BSIM/BSIMConfiguration/src/main/resources/META-INF/spring/mce_billpay_configuration.xml $DEPLOY_FOLDER/servicemix/
cp BSIM/BSIMConfiguration/src/main/resources/META-INF/spring/mce_bsim_billpay_configuration.xml $DEPLOY_FOLDER/servicemix/
cp BSIM/BSIMConfiguration/src/main/resources/META-INF/spring/mce_bsim_iso_configuration.xml $DEPLOY_FOLDER/servicemix/
# cp BSIM/BSIMConfiguration/src/main/resources/META-INF/spring/mce_bsim_sms_configuration.xml $DEPLOY_FOLDER/servicemix/
cp BSIM/BSIMConfiguration/src/main/resources/META-INF/spring/mce_new_bsim_sms_configuration.xml $DEPLOY_FOLDER/servicemix/
cp BSIM/BSIMConfiguration/src/main/resources/META-INF/spring/mce_bsim_interbanktransfer_configuration.xml $DEPLOY_FOLDER/servicemix/
cp BSIM/BSIMConfiguration/src/main/resources/META-INF/spring/mce_simaspay_sms_configuration.xml $DEPLOY_FOLDER/servicemix/
cp BSIM/BSIMBillPayment/target/BSIMBillPayment-0.1-SNAPSHOT.jar $DEPLOY_FOLDER/servicemix/
cp BSIM/BSIMInterBankTransfer/target/BSIMInterBankTransfer-0.1-SNAPSHOT.jar $DEPLOY_FOLDER/servicemix/
cp BSIM/BSIMISO8583/target/BSIMISO8583-0.1-SNAPSHOT.jar $DEPLOY_FOLDER/servicemix/
# cp BSIM/BSIMSMS/target/BSIMSMS-0.1-SNAPSHOT.jar $DEPLOY_FOLDER/servicemix/
cp BSIM/NewBSIMSMS/target/NewBSIMSMS-0.1-SNAPSHOT.jar $DEPLOY_FOLDER/servicemix/
cp Hub/SimaspaySMS/target/SimaspaySMS-0.1-SNAPSHOT.jar $DEPLOY_FOLDER/servicemix/

cp BSIM/FlashizISO8583/target/BSIMFlashizISO8583-0.1-SNAPSHOT.jar $DEPLOY_FOLDER/servicemix/
cp BSIM/BSIMConfiguration/src/main/resources/META-INF/spring/mce_bsim_flashiz_iso_configuration.xml $DEPLOY_FOLDER/servicemix/

cp Core/settings/bsim/database_config.properties $DEPLOY_FOLDER/servicemix/mfino_conf/
cp Core/settings/bsim/bc2qmap.cfg $DEPLOY_FOLDER/servicemix/mfino_conf/
cp Core/settings/bsim/mfino.properties $DEPLOY_FOLDER/servicemix/mfino_conf/
cp MfinoCoreEngine/mfino_conf/externalcodedescriptions.xml $DEPLOY_FOLDER/servicemix/mfino_conf/
cp Core/settings/bsim/mce.properties $DEPLOY_FOLDER/servicemix/mfino_conf/


cp MfinoCoreEngine/HSM/ThalesConfiguration/src/main/resources/jpos/thales_logger.xml $DEPLOY_FOLDER/servicemix/jpos/deploy/
cp MfinoCoreEngine/HSM/ThalesConfiguration/src/main/resources/jpos/thales_config.xml $DEPLOY_FOLDER/servicemix/jpos/deploy/
cp BSIM/BSIMConfiguration/src/main/jpos/connection_config_bsm.xml $DEPLOY_FOLDER/servicemix/jpos/deploy/
cp BSIM/BSIMConfiguration/src/main/jpos/jpos_config_bsm.xml $DEPLOY_FOLDER/servicemix/jpos/deploy/
cp BSIM/BSIMConfiguration/src/main/jpos/mux_config_bsm.xml $DEPLOY_FOLDER/servicemix/jpos/deploy/
cp BSIM/BSIMConfiguration/src/main/jpos/connection_config_flashiz.xml $DEPLOY_FOLDER/servicemix/jpos/deploy/
cp BSIM/BSIMConfiguration/src/main/jpos/jpos_config_flashiz.xml $DEPLOY_FOLDER/servicemix/jpos/deploy/
cp BSIM/BSIMConfiguration/src/main/jpos/mux_config_flashiz.xml $DEPLOY_FOLDER/servicemix/jpos/deploy/

cp BSIM/BSIMConfiguration/src/main/jpos_cfg/iso87ascii-bsm.xml $DEPLOY_FOLDER/servicemix/jpos/jpos_cfg/
cp BSIM/BSIMConfiguration/src/main/jpos_cfg/iso87ascii-flashiz.xml $DEPLOY_FOLDER/servicemix/jpos/jpos_cfg/
cp MfinoCoreEngine/HSM/ThalesConfiguration/src/main/resources/jpos/cfg/*.xml $DEPLOY_FOLDER/servicemix/jpos/cfg/

# BSM PPOB integration 
# cp BSIM/BSMPPOBISO8583/target/BSMPPOBISO8583-0.1-SNAPSHOT.jar $DEPLOY_FOLDER/servicemix
# cp BSIM/BSIMConfiguration/src/main/resources/META-INF/spring/mce_bsm_ppob_iso_configuration.xml $DEPLOY_FOLDER/servicemix/
# cp BSIM/BSIMConfiguration/src/main/jpos_cfg/iso87ascii-bsm-ppob.xml $DEPLOY_FOLDER/servicemix/jpos/jpos_cfg/
# cp BSIM/BSIMConfiguration/src/main/jpos/connection_config_bsm_ppob.xml $DEPLOY_FOLDER/servicemix/jpos/deploy/
#rm cp BSIM/BSIMConfiguration/src/main/jpos/jpos_config_bsm_ppob.xml $DEPLOY_FOLDER/servicemix/jpos/deploy/
#rm cp BSIM/BSIMConfiguration/src/main/jpos/mux_config_bsm_ppob.xml $DEPLOY_FOLDER/servicemix/jpos/deploy/
