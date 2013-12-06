//package com.mfino.handset.subscriber.http;
//
//import com.mfino.handset.subscriber.IResponseReceivedListener;
//import com.mfino.handset.subscriber.datacontainers.ResponseDataContainer;
//import com.mfino.handset.subscriber.datacontainers.SecureResponseDataContainer;
//import com.mfino.handset.subscriber.datacontainers.SecureUserDataContainer;
//import com.mfino.handset.subscriber.datacontainers.UserDataContainer;
//
//public class HttpExecutorThread extends Thread {
//
//    private UserDataContainer container;
//    private IResponseReceivedListener callBack;
//
//    public HttpExecutorThread(UserDataContainer container, IResponseReceivedListener callBack) {
//        this.container = container;
//        this.callBack = callBack;
//    }
//
//    public void run() {
//        SecureUserDataContainer cont = SecureUserDataContainer.createSecureApplicationDataContainer(container);
//        SecureResponseDataContainer rd = null;
//        ResponseDataContainer rdcontainer = null;
//        try {
//            WebAPIHTTPWrapper wrapper = new WebAPIHTTPWrapper(cont);
//            rd = wrapper.getResponseData();
//
//            rdcontainer = ResponseDataContainer.createResponseData(rd, container);
//        } catch (Exception ex) {
//            rdcontainer = null;
//        }
////        SecureResponseDataContainer rd = new SecureResponseDataContainer();
////        try {
////            Thread.sleep(10000);
////        } catch (InterruptedException ex) {
////            ex.printStackTrace();
////        }
//        this.callBack.responseReceived(rdcontainer);
//    }
//}
