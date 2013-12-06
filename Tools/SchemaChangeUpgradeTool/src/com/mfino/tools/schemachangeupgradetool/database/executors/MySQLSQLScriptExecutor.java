/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.tools.schemachangeupgradetool.database.executors;

import java.util.HashMap;
import java.util.List;

/**
 *
 * @author sandeepjs
 */
public class MySQLSQLScriptExecutor extends AbstractSQLScriptExecutor {

    private static final String EXE = "bin/mysql";
    private static final String param1 = "--user=";
    private static final String param2 = "--password=";

    public MySQLSQLScriptExecutor() {
    }

    @Override
    public boolean executeUpdate() {

        HashMap params = new HashMap();
        params.put("param1", param1 + this.userName);
        params.put("param2", param2 + this.password);
        params.put("param3", "<");


        String cmd = "";

        if (osType == 1) {
            params.put("param4", "\"" + fileName + "\"");
            cmd = "\"" + dbHome + "/" + EXE + "\"";
        } else if (osType == 2) {
            params.put("param4", "\'" + fileName + "\'");
            cmd = "\'" + dbHome + "/" + EXE + "\'";
        }


        return this.executeCommand(cmd, params, osType);
    }

    public static void main(String[] args) {
        MySQLSQLScriptExecutor ms = new MySQLSQLScriptExecutor();
        ms.setParams("/usr", "root", "mFino260", "/home/xchen/dev/mfino/trunk/Data/mFino/Table/Tables.sql", 1);
        ms.executeUpdate();
    }

    public List execute() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
