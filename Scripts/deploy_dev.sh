
echo "Stopping tomcat ..."
/etc/init.d/tomcat stop

echo "Waiting 10 sec for tomcat to finish stopping ..."

sleep 10

rm -rf /usr/local/tomcat/webapps/AdminApplication_Dist
cp ../Web/AdminApplication/target/AdminApplication_Dist.war /usr/local/tomcat/webapps/AdminApplication_Dist.war
rm -rf /usr/local/tomcat/webapps/Scheduler
cp ../Web/Scheduler/target/Scheduler.war /usr/local/tomcat/webapps/Scheduler.war

echo "Starting tomcat ..."
/etc/init.d/tomcat start 

echo "Stopping MultiX ..."
/usr/local/mFinoMultiXTpmServer/StopMultiXTpm.sh
echo "Waiting 20 sec for MultiX to finish stopping ..."
sleep 20
cp -P ../mFinoMultiXTpmServer/runtime/* /usr/local/mFinoMultiXTpmServer/runtime/
cd /usr/local/mFinoMultiXTpmServer/
/usr/local/mFinoMultiXTpmServer/StartMultiXTpm.sh
