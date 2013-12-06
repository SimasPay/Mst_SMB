/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.tools.schemachangeupgradetool.database.executors;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;

/**
 *
 * @author sandeepjs
 */
public abstract class AbstractSQLScriptExecutor implements SQLScriptExecutor {

    private static final String[] dosShellCommands = {"cmd.exe", "/c"};
    private static final String[] linuxShellCommands = {"/bin/bash", "-c"};
    protected String dbHome;
    protected String password;
    protected String userName;
    protected String fileName;
    protected int osType;

    protected boolean executeCommand(String command, HashMap params, int osType) {
        boolean retBooleanVal = true;

        Process process = null;
        try {

            String aCmd = command;

            if (params != null) {

                Iterator paramsKeysIt = params.keySet().iterator();
                while (paramsKeysIt.hasNext()) {
                    aCmd += " " + (String) params.get(paramsKeysIt.next());
                }
            }

          if(osType == 1)
          aCmd = "\"" + aCmd + "\"";

            String commands[] = null;

            if (osType == 1) {
                commands = new String[]{dosShellCommands[0], dosShellCommands[1], aCmd};

            } else if (osType == 2) {
                commands = new String[]{linuxShellCommands[0], linuxShellCommands[1], aCmd};
            }


            process = Runtime.getRuntime().exec(commands);

            System.out.println("running command: ");
            System.out.println(commands[0]);
            System.out.println(commands[1]);
            System.out.println(commands[2]);

            BufferedReader in =
                    new BufferedReader(new InputStreamReader(process.getInputStream()));

            // Read and print the output
            String line = null;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }

            in.close();

            // clean up if any output in stderr
            BufferedReader brCleanUp =
                    new BufferedReader(new InputStreamReader(process.getErrorStream()));
            while ((line = brCleanUp.readLine()) != null) {
                System.out.println("[Stderr] " + line);
                retBooleanVal = false;
            }
            brCleanUp.close();

        } catch (Exception e) {
            retBooleanVal = false;
            e.printStackTrace();
        }

        return retBooleanVal;
    }

    public void setParams(String dbHome, String userName, String password, String fileName, int osType) {
        this.dbHome = dbHome;
        this.userName = userName;
        this.password = password;
        this.fileName = fileName;
        this.osType = osType;
    }
}
