#!/bin/bash
createDir(){

	if [ -d "all_targets" ]
	then
		rm -rf all_targets
	fi

	/bin/mkdir -p "all_targets" >/dev/null 2>&1 && echo "all_targets directory created." ||  ( echo "Error: Failed to create all_targets directory." && return 0 )
	#cd ./all_targets
	/bin/mkdir -p ./all_targets/"serviceMixDeploy" >/dev/null 2>&1 && echo "serviceMixDeploy directory created." ||  ( echo "Error: Failed to create servicMixDeploy directory." && return 0 )
	
	/bin/mkdir -p ./all_targets/"tomcatWebapps" >/dev/null 2>&1 && echo "tomcatWebapps directory created." || ( echo "Error: Failed to create tomcatWebapps." && return 0 )
	return 1
}

deployToServiceMix(){
	SERVICEMIX_HOME="../all_targets/serviceMixDeploy"
	
	echo "cping jar files to serviceMixDeploy"
	cd ./MfinoCoreEngine
	cp ../Core/target/com.mfino.application-Core.jar $SERVICEMIX_HOME
	cp ../Commons/TransactionApi/target/TransactionApi-0.1-SNAPSHOT.jar $SERVICEMIX_HOME
	cp ./MCECore/target/MCECore-0.1-SNAPSHOT.jar $SERVICEMIX_HOME
	cp ./FIXServer/target/FIXServer-0.1-SNAPSHOT.jar $SERVICEMIX_HOME
	cp ./Backend/target/Backend-0.1-SNAPSHOT.jar $SERVICEMIX_HOME
	cp ./NotificationService/target/NotificationService-0.1-SNAPSHOT.jar $SERVICEMIX_HOME
	cp ./JPOSComponent/target/JPOSComponent-0.1-SNAPSHOT.jar $SERVICEMIX_HOME
	cp ../ZenithISO8583/target/ZenithISO8583-0.1-SNAPSHOT.jar $SERVICEMIX_HOME
	cp ./Frontend/target/Frontend-0.1-SNAPSHOT.jar $SERVICEMIX_HOME
	cp ../Zenith/VisaPhoneSTK/target/VisaPhoneSTK-0.1-SNAPSHOT.jar $SERVICEMIX_HOME
	cp ../Zenith/ZenithDSTV/target/ZenithDSTV-0.1-SNAPSHOT.jar $SERVICEMIX_HOME


	echo "cping jpos config files"
	mkdir -p $SERVICEMIX_HOME/jpos/deploy
	cp ./JPOSComponent/config/* $SERVICEMIX_HOME/jpos/deploy/

	echo "cping config files"
	cp ./MCEConfiguration/src/main/resources/META-INF/spring/mce_fix_configuration.xml $SERVICEMIX_HOME
	cp ./MCEConfiguration/src/main/resources/META-INF/spring/mce_backend_configuration.xml $SERVICEMIX_HOME
	cp ./MCEConfiguration/src/main/resources/META-INF/spring/mce_notification_configuration.xml $SERVICEMIX_HOME
	cp ./MCEConfiguration/src/main/resources/META-INF/spring/mce_iso_jpos_configuration.xml $SERVICEMIX_HOME
	cp ./MCEConfiguration/src/main/resources/META-INF/spring/mce_frontend_configuration.xml $SERVICEMIX_HOME	
	cp ./MCEConfiguration/src/main/resources/META-INF/spring/mce_scheduler_configuration.xml $SERVICEMIX_HOME
	cp ../Zenith/ZenithConfiguration/src/main/resources/META-INF/spring/mce_zenith_visafone_configuration.xml $SERVICEMIX_HOME
	cp ../Zenith/ZenithConfiguration/src/main/resources/META-INF/spring/mce_zenith_dstv_configuration.xml $SERVICEMIX_HOME
   
    	cd ..  
}

deployToWebapps(){
	WEBAPPS_HOME="./all_targets/tomcatWebapps"
	echo "cping war files to tomcatWebapps"
	cp ./Web/AdminApplication/target/AdminApplication_Dist.war $WEBAPPS_HOME/AdminApplication.war
	cp ./Web/Scheduler/target/Scheduler.war $WEBAPPS_HOME
	cp ./Web/ReportScheduler/target/ReportScheduler.war $WEBAPPS_HOME 
	cp ./Web/webapi/target/webapi.war $WEBAPPS_HOME

}

createDir
deployToServiceMix
deployToWebapps


