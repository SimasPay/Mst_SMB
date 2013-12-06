package com.mfino.handset.agent.http;

import com.mfino.handset.agent.datacontainers.AgentDataContainer;
import com.mfino.handset.agent.datacontainers.EncryptedResponseDataContainer;
import com.mfino.handset.agent.datacontainers.ResponseDataContainer;
import com.mfino.handset.agent.services.listeners.IResponseListener;
import com.mfino.handset.agent.datacontainers.EncryptedUserDataContainer;
import com.mfino.handset.agent.widgets.CustomDialogs;
import com.sun.lwuit.Dialog;

public class HttpExecutorThread extends Thread {

    private AgentDataContainer container;
    private IResponseListener callBack;

    public HttpExecutorThread(AgentDataContainer container, IResponseListener callBack) {
        this.container = container;
        this.callBack = callBack;
    }

    public void run() {
        WaitingDialogThread thread = new WaitingDialogThread();
        thread.start();
        EncryptedUserDataContainer cont = EncryptedUserDataContainer.createSecureApplicationDataContainer(container);
        EncryptedResponseDataContainer rd=null;
        ResponseDataContainer rdcontainer=null;
        try{
            WebAPIHTTPWrapper wrapper = new WebAPIHTTPWrapper(cont);
            rd = wrapper.getResponseData();
            
            rdcontainer = ResponseDataContainer.createResponseData(rd,container.getAESKey());
        }
        catch(Exception ex){
            rdcontainer=null;
        }
//        EncryptedResponseDataContainer rd = new EncryptedResponseDataContainer();
//        try {
//            Thread.sleep(10000);
//        } catch (InterruptedException ex) {
//            ex.printStackTrace();
//        }
        container.getPresentDialog().dispose();
        this.callBack.responseReceived(rdcontainer);
    }
    
    private class WaitingDialogThread extends Thread{
        private Dialog d;
        public WaitingDialogThread(){
        }
        public void run(){
            d = CustomDialogs.createWaitingForHTTPResponseDialog();
            container.setPresentDialog(d);
            d.show();
        }
    }
}
