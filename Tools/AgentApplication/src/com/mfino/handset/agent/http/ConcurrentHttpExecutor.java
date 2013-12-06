/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.handset.agent.http;

import com.mfino.handset.agent.datacontainers.AgentDataContainer;
import com.mfino.handset.agent.datacontainers.EncryptedResponseDataContainer;
import com.mfino.handset.agent.widgets.CustomizedForm;
import com.sun.lwuit.Form;

/**
 *
 * @author karthik
 */
public class ConcurrentHttpExecutor {

    private static ConcurrentHttpExecutor executor;

    public static ConcurrentHttpExecutor getInstance(AgentDataContainer mfinoConfigData,CustomizedForm form) {
        if (executor == null) 
            executor = new ConcurrentHttpExecutor(mfinoConfigData,form);
        return executor;
    }
    private AgentDataContainer mfinoConfigData;
    private CustomizedForm form;

    private ConcurrentHttpExecutor(AgentDataContainer mfinoConfigData,CustomizedForm form) {
        this.mfinoConfigData = mfinoConfigData;
        this.form = form;
    }
    private EncryptedResponseDataContainer responseData = null;

    public EncryptedResponseDataContainer execute() {

        HttpExecutorThread thread = new HttpExecutorThread(mfinoConfigData);
        thread.start();


        return responseData;
    }

    private class HttpExecutorThread extends Thread {

        private AgentDataContainer mfinoConfigData;

        public HttpExecutorThread(AgentDataContainer mcd) {
            this.mfinoConfigData = mcd;
        }
        public void run() {
//            WebAPIHTTPWrapper wrapper = new WebAPIHTTPWrapper(mfinoConfigData);
//            ConcurrentHttpExecutor.this.responseData = wrapper.getResponseData();
        }
    }
}
