/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.tools.schemachangeupgradetool.database.executors.factory;

import com.mfino.tools.schemachangeupgradetool.constants.SchemaChangeUpgradeConstants;
import com.mfino.tools.schemachangeupgradetool.database.executors.Executor;
import com.mfino.tools.schemachangeupgradetool.database.executors.HibernateExecutor;
import com.mfino.tools.schemachangeupgradetool.database.executors.JDBCExecutor;
import com.mfino.tools.schemachangeupgradetool.database.executors.MySQLSQLScriptExecutor;


/**
 *
 * @author sandeepjs
 */
public class ExecutorFactory {

    private ExecutorFactory()
    {
        
    }

    public static Executor getExecutor(int type)
    {
        Executor exec = null;

        if(type == Integer.parseInt(SchemaChangeUpgradeConstants.MYSQLSCRIPTFILE_EXECUTOR_TYPE))
        {
            exec = (Executor) new MySQLSQLScriptExecutor();
        }
        else if(type == Integer.parseInt(SchemaChangeUpgradeConstants.HIBERNATE_SQL_EXECUTOR_TYPE))
        {
            exec = (Executor) new HibernateExecutor();
        }
        else if(type == Integer.parseInt(SchemaChangeUpgradeConstants.JDBC_SQL_EXECUTOR_TYPE))
        {
               exec = (Executor) new JDBCExecutor();
        }

        return exec;
    }

}
