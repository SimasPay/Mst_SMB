/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.hibernate;

import java.io.File;
import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2ddl.SchemaUpdate;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author xchen
 */
@Ignore //This is run during master build. The test here is for debugging only.
public class SchemaUpdateRunTest {

    @Test
    public void testSchemaUpdate(){
        Configuration cfg = new Configuration();
        cfg.configure(new File("C:/Users/xchen/Projects/mf/devguard/mfino/trunk/Core/build/classes/hibernate.cfg.xml"));
        SchemaUpdate su = new SchemaUpdate(cfg);
        su.execute(false, true);
    }
}
