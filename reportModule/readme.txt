Report Module has 2 Main Projects
1)DbCopyTool
2)saiku

1)DbCopyTool
  --The Goal of this project is to copy the required data from the production database replica(mfino schema) to the report schema in order to generate the reports.
  --To build this project, use eclipse and export the project as runnable jar.
  
2)saiku
 --The Goal of this project is to generate analytical resports. 
 --It is consists of 3 sub projects
		i)	saiku-service
	   ii)	saiku-web
	  iii)	saiku-webapp
	  
 -- To build saiku, build the following sub projects in order
	    i) 	saiku-service - use pom.xml to build the project
	   ii) 	saiku-web  - use pom.xml to build the project
	  iii)	saiku-webapp - use pom.xml to build the project
	   and finally saiku.war is built, which can be found in the target folder of saiku-webapp project.
	   



**************Setup Guide****************************
1)create a new schema by name 'report'
2)Run the DbCopyTool runnable jar by using the follwoing command 
		java -jar <runnable Jar Name>
3)Deploy saiku.war in tomcat server.
	When you deploy saiku.war in tomcat server, make sure mfino.properties of AdminApplication has the following property set to correct location
mfino.olap.url=<URL of the saiku.war> for example  mfino.olap.url=http://olapserver:8080/saiku
	 
  
  