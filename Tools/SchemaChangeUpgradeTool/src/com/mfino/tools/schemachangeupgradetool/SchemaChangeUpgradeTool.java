/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.tools.schemachangeupgradetool;

import com.mfino.tools.schemachangeupgradetool.constants.SchemaChangeUpgradeConstants;
import com.mfino.tools.schemachangeupgradetool.database.executors.AbstractExecutor;
import com.mfino.tools.schemachangeupgradetool.database.executors.Executor;
import com.mfino.tools.schemachangeupgradetool.database.executors.HibernateExecutor;
import com.mfino.tools.schemachangeupgradetool.database.executors.JDBCExecutor;
import com.mfino.tools.schemachangeupgradetool.database.executors.MySQLSQLScriptExecutor;
import com.mfino.tools.schemachangeupgradetool.database.executors.factory.ExecutorFactory;
import com.mfino.tools.schemachangeupgradetool.services.DbParamService;
import com.mfino.tools.schemachangeupgradetool.sqltools.SqlParser;
import com.mfino.util.logging.DefaultLogger;
import java.io.File;
import java.util.Properties;

/**
 *
 * @author sandeepjs
 */
public class SchemaChangeUpgradeTool {

    private String updateDirPath;
    private String createDbTablesSQLFilePath;
    private DbParamService dbParamService = new DbParamService();
    private static Executor executor = null;
    private int executorType;
    private int osType;
    private String dbPath;
    private String dbUserName;
    private String dbPassword;

    public SchemaChangeUpgradeTool(String updateDirPath, String createDbTablesSQLFilePath, int executorType) {
        this.updateDirPath = updateDirPath;
        this.createDbTablesSQLFilePath = createDbTablesSQLFilePath;
        this.executor = (Executor) ExecutorFactory.getExecutor(executorType);
        this.executorType = executorType;
    }

    public void setDbParameters(String dbPath, String userName, String password, int osType) {
        this.dbPath = dbPath;
        this.dbUserName = userName;
        this.dbPassword = password;
        this.osType = osType;

    }

    public boolean performSchemaChangeUpgrade(Properties props) {
        boolean retBooleanVal = true;

        try {
            boolean dbCheck = this.performCheck();
            System.out.println(dbCheck);
            runUpdateSqlFiles(updateDirPath);
        } catch (Exception e) {
            retBooleanVal = false;
            e.printStackTrace();
        }

        return retBooleanVal;
    }

    private boolean exceuteFile(String dirPath) {

        if (executor instanceof HibernateExecutor || executor instanceof JDBCExecutor) {

            SqlParser sqlParser = new SqlParser(dirPath);
            String[] sqlstatements = sqlParser.parseSql();
            for (int i = 0; i < sqlstatements.length; i++) {
                System.out.println(sqlstatements[i]);
            }

            ((AbstractExecutor) executor).setData(sqlstatements, 0, sqlstatements.length);

        } else if (executor instanceof MySQLSQLScriptExecutor) {

            ((MySQLSQLScriptExecutor) executor).setParams(dbPath, dbUserName, dbPassword, dirPath, osType);

        }

        return executor.executeUpdate();

    }

    private String getFileVersion(String dirPath) {

        String fileName = new File(dirPath).getName();
        System.out.println(fileName);
        String versionStr = fileName.substring(0, fileName.indexOf("_"));
        System.out.println(versionStr);

        return versionStr;
    }

    private boolean handleFile(String dirPath) throws NumberFormatException {
        boolean done = false;
        String versionStr = getFileVersion(dirPath);
        int fileVersion = Integer.parseInt(versionStr);
        if (fileVersion > dbParamService.getDbVersion()) {
            done = exceuteFile(dirPath);

            // If the Update file is execute correctly then update the Version
            if (done) {
                dbParamService.updateDbVersion(versionStr);
            }
        }
        return done;
    }

    private boolean performCheck() {
        boolean retBooleanVal = true;

        try {
            int code = dbParamService.getDbVersion();
            DefaultLogger.info("code" + code);

            // If there is no db_param table in the database.
            if (code == -1) {
                // Create all the tables.
                DefaultLogger.info("Creating tables.");
                createTables(this.createDbTablesSQLFilePath);
            }

        } catch (Exception e) {
            retBooleanVal = false;
            e.printStackTrace();
        }

        return retBooleanVal;
    }

    private void runUpdateSqlFiles(String dirPath) throws Exception {
        try {

            File directory = new File(dirPath);
            File childFiles[] = directory.listFiles();

            for (int i = 0; i < childFiles.length; i++) {
                if (childFiles[i].isFile() && childFiles[i].getAbsolutePath().contains(SchemaChangeUpgradeConstants.SQL_FILES_SUFFIX)) {
                    System.out.println(childFiles[i]);
                    boolean done = handleFile(childFiles[i].getAbsolutePath());
                    if (!done) {
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createTables(String fileName) {
        if (executorType == 1) {
            DefaultLogger.info("using command line");

            ((MySQLSQLScriptExecutor) executor).setParams(this.dbPath, this.dbUserName, this.dbPassword, this.createDbTablesSQLFilePath, this.osType);

        } else if (executorType == 2) {

            SqlParser sqlParser = new SqlParser(fileName);
            String[] sqlStatements = sqlParser.parseSql();
            for (int i = 0; i < sqlStatements.length; i++) {
                System.out.println(sqlStatements[i]);
            }
            ((HibernateExecutor) executor).setData(sqlStatements, 1, sqlStatements.length);

        } else if (executorType == 3) {
        }

        boolean done = executor.executeUpdate();

        System.out.println("create tables done :" + done);
        dbParamService.createVersionRecord();
    }
}
