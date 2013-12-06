To Run DbCopyTool.war
1) Execute MFS _V2_6\Data\MFS2.6 DB Scripts\mysql\DbCopyTool.sql
2) Deploy DbCopyTool.war in tomcat (This instance should be different from the ones used for regular applications AdminApplication, WebAPI)

To Change configurations, use the following files
1)mfino db configuration -- \apache-tomcat-6.0.33\webapps\DbCopyTool\WEB-INF\classes\database_config.properties
2)report db configuration -- \apache-tomcat-6.0.33\webapps\DbCopyTool\WEB-INF\classes\reportdb.cfg.xml

To change the schedule to run DbCopyTool.war, use the following file
apache-tomcat-6.0.33\webapps\DbCopyTool\WEB-INF\applicationContext.xml and then use the following bean
   <bean id="dbCopyTrigger" class="org.springframework.scheduling.quartz.SimpleTriggerBean">
        <property name="jobDetail" ref="dbCopyJob" />
        <property name="startDelay" value="20000" />
        <property name="repeatInterval" value="300000" />
    </bean>
