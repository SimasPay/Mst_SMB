set TOMCAT_HOME=D:\apache-tomcat-6.0.33\

del %TOMCAT_HOME%\webapps\webapps\AdminApplication.war
del %TOMCAT_HOME%\webapps\webapps\Scheduler.war
del %TOMCAT_HOME%\webapps\webapps\webapi.war
del %TOMCAT_HOME%\webapps\webapps\ReportScheduler.war
RMDIR /S /Q %TOMCAT_HOME%\webapps\AdminApplication
RMDIR /S /Q %TOMCAT_HOME%\webapps\Scheduler
RMDIR /S /Q %TOMCAT_HOME%\webapps\webapi
RMDIR /S /Q %TOMCAT_HOME%\webapps\ReportScheduler
RMDIR /S /Q %TOMCAT_HOME%\work

echo "copying war files for tomcat"
copy Web\AdminApplication\target\AdminApplication.war %TOMCAT_HOME%\webapps\
copy Web\Scheduler\target\Scheduler.war %TOMCAT_HOME%\webapps\
copy Web\webapi\target\webapi.war  %TOMCAT_HOME%\webapps\ 
copy Web\ReportScheduler\target\ReportScheduler.war  %TOMCAT_HOME%\webapps\
