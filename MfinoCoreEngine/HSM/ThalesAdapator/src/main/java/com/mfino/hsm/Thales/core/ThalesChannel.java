package com.mfino.hsm.thales.core;

import org.jpos.iso.FSDISOMsg;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.channel.FSDChannel;
import org.jpos.util.FSDMsg;


/**
 * Jpos Channel using FSDChannel for sending the message to HSM
 * @author POCHADRI
 *
 */
public class ThalesChannel extends FSDChannel {
     String basePath;
     String schema;

    String baseResponsePath;

    public String getBaseResponsePath() {
        return baseResponsePath;
    }

    public void setBaseResponsePath(String baseResponsePath) {
        this.baseResponsePath = baseResponsePath;
    }



     public String getBasePath() {
         return basePath;
     }

     public void setBasePath(String basePath) {
         this.basePath = basePath;
     }

     public String getSchema() {
         return schema;
     }

     public void setSchema(String schema) {
         this.schema = schema;
     }

     @Override
     public ISOMsg createMsg() {
         if (basePath != null && schema != null)
             return new ThalesISOMsg(new ThalesMsg(basePath, schema));

         if (basePath != null)
             return new ThalesISOMsg(new ThalesMsg(basePath));

         return new ThalesISOMsg();
     }
}