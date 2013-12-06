/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.tools.schemachangeupgradetool.sqltools;

import com.mfino.tools.schemachangeupgradetool.constants.SchemaChangeUpgradeConstants;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * This class provides us with the method to parse the sql statements
 * from a sql script file. (Eg: Tables.sql)
 * @author sandeepjs
 */
public class SqlParser {

    private String sqlFileAbsolutePath;

    public SqlParser(String filePath)
    {
        this.sqlFileAbsolutePath = filePath;
    }


    public String[] parseSql()
    {
        String[] retStringArrayVal = null;

        try
        {

            FileInputStream fis = new FileInputStream(new File(this.sqlFileAbsolutePath));
            DataInputStream dis = new DataInputStream(fis);

            String line = dis.readLine();

            StringBuffer sqlStatementBuffer = new StringBuffer();

            ArrayList sqlStatementList = new ArrayList();

            while(line != null)
            {
                line = checkAndRemoveTODOs(line);
                sqlStatementBuffer.append(line.trim());

                if(line.equals(""))
                {
                sqlStatementList.add(sqlStatementBuffer.toString());
                sqlStatementBuffer = new StringBuffer();
                }

                line = dis.readLine();
            }

            retStringArrayVal = pruneStatements(sqlStatementList);

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return retStringArrayVal;
    }

    private String[] pruneStatements(ArrayList sqlStatementsList) throws Exception
    {
        String[] retStringArrayVal = null;

        try
        {
                Iterator sqlStatmentsListItrerator = sqlStatementsList.iterator();

                while(sqlStatmentsListItrerator.hasNext())
                {

                String sqlStatement = (String) sqlStatmentsListItrerator.next();


                if(sqlStatement.indexOf(SchemaChangeUpgradeConstants.SQL_COMMENT_START) >= 0 || sqlStatement.indexOf(SchemaChangeUpgradeConstants.SQL_COMMENT_END) >= 0)
                {
                    sqlStatmentsListItrerator.remove();
                }

                retStringArrayVal = convertListToArray(sqlStatementsList);

                }
        }
        catch(Exception e)
        {
            //e.printStackTrace();
            throw(e);
        }

        return retStringArrayVal;
    }

    private String[] convertListToArray(ArrayList sqlStatementsList) throws Exception
    {
        String retStringArrayVal[] = new String[sqlStatementsList.size()];

        Iterator sqlStatmentsListItrerator = sqlStatementsList.iterator();

        int index = 0;

        while(sqlStatmentsListItrerator.hasNext())
        {
            retStringArrayVal[index++] = (String) sqlStatmentsListItrerator.next();
        }
        
        return retStringArrayVal;
    }

    private String checkAndRemoveTODOs(String line)
    {

        int index = line.indexOf(SchemaChangeUpgradeConstants.TODO_START);

        if( index > 0)
        {
            line = line.substring(0, index-1);
        }

        return line;
    }

}
