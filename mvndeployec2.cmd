pscp -pw WhiteZebra10 web\AdminApplication\target\AdminApplication_Dist.war administrator@staging.mfino.com:C:\tmp\AdminApplication.war

pscp -pw WhiteZebra10 web\Scheduler\target\Scheduler.war administrator@staging.mfino.com:C:\tmp

plink -pw WhiteZebra10 administrator@staging.mfino.com "cd C:\Programs\apache-tomcat-6.0.20\bin&&cd&&.\shutdown.bat"

ping -n 20 localhost

plink -pw WhiteZebra10 administrator@staging.mfino.com rd /Q /S C:\Programs\apache-tomcat-6.0.20\webapps\AdminApplication
plink -pw WhiteZebra10 administrator@staging.mfino.com rd /Q /S C:\Programs\apache-tomcat-6.0.20\webapps\Scheduler

plink -pw WhiteZebra10 administrator@staging.mfino.com move /Y C:\tmp\AdminApplication.war C:\Programs\apache-tomcat-6.0.20\webapps
plink -pw WhiteZebra10 administrator@staging.mfino.com move /Y C:\tmp\Scheduler.war C:\Programs\apache-tomcat-6.0.20\webapps

rem this seems to take forever and 100% cpu
rem plink -pw WhiteZebra10 administrator@staging.mfino.com "cd C:\Programs\apache-tomcat-6.0.20\bin&&cd&&.\startup.bat"

pause
