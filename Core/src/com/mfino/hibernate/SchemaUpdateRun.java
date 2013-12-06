/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.hibernate;

import java.io.File;
import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2ddl.SchemaUpdate;


/**
 * Simple wrapper of schema update from hibernate. It does not work well using
 * ant task for some unknown reason.
 * @author xchen
 */
public class SchemaUpdateRun {

    public static void main(String args[]){
    	boolean generateScriptOnly = false; 
        if(args.length < 1){
          // generateScriptOnly=true;
        }
    	if(args.length > 1){
    		if("g".equalsIgnoreCase(args[0]))
    			generateScriptOnly = true;    			
    	}
//        File f = new File(args[0]);
//        if(f.exists() == false){
//            throw new RuntimeException("The file specified does not exists");
//        }
        Configuration cfg = new Configuration();
        cfg.configure();
        SchemaUpdate su = new SchemaUpdate(cfg);
        if(generateScriptOnly)
        	su.execute(true, false);
        else
        su.execute(false, true);
    }
    
}
