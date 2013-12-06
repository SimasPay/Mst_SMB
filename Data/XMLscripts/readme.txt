##LIQUIBASE supports following databases

Database	 		Type Name	 
MySQL	 	 		mysql	 
PostgreSQL	 		postgresql	 
Oracle	 	 		oracle	 11g driver
MS-SQL	 	 		mssql	
Sybase Anywhere		asany	Since 1.9
DB2	 	 			db2		  
Apache Derby		derby	  
HSQL	 			hsqldb	  
H2		 			h2	  
Informix	 		informix	 
InterSystems 		cache	 
Firebird	 		firebird	 
SAPDB		 		maxdb	 
SQLite		 		sqlite	Since 1.8 

####command to run scripts on fresh db ##
set the db connection properties in liquibase.properties file already present in the folder.

Data\XMLScripts> java -jar liquibase.jar update