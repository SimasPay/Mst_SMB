/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.tools.schemachangeupgradetool;

/**
 *
 * @author sandeepjs
 */
public class Main {

    public static void main(String args[])
    {
        SchemaChangeUpgradeTool sm = new SchemaChangeUpgradeTool(args[0],args[1],Integer.parseInt(args[2]));
        sm.setDbParameters(args[3],args[4],args[5], Integer.parseInt(args[6]));
        sm.performSchemaChangeUpgrade(null);
    }


}
