
/**
 * NibssFasterPayCallbackHandler.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.1  Built on : Aug 31, 2011 (12:22:40 CEST)
 */

    package com.zenithbank.nfp;

    /**
     *  NibssFasterPayCallbackHandler Callback class, Users can extend this class and implement
     *  their own receiveResult and receiveError methods.
     */
    public abstract class NibssFasterPayCallbackHandler{



    protected Object clientData;

    /**
    * User can pass in any object that needs to be accessed once the NonBlocking
    * Web service call is finished and appropriate method of this CallBack is called.
    * @param clientData Object mechanism by which the user can pass in user data
    * that will be avilable at the time this callback is called.
    */
    public NibssFasterPayCallbackHandler(Object clientData){
        this.clientData = clientData;
    }

    /**
    * Please use this constructor if you don't want to set any clientData
    */
    public NibssFasterPayCallbackHandler(){
        this.clientData = null;
    }

    /**
     * Get the client data
     */

     public Object getClientData() {
        return clientData;
     }

        
           /**
            * auto generated Axis2 call back method for nameenquirybulkitem method
            * override this method for handling normal response from nameenquirybulkitem operation
            */
           public void receiveResultnameenquirybulkitem(
                    com.zenithbank.nfp.NameenquirybulkitemResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from nameenquirybulkitem operation
           */
            public void receiveErrornameenquirybulkitem(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for fundtransfersingleitem2_dc method
            * override this method for handling normal response from fundtransfersingleitem2_dc operation
            */
           public void receiveResultfundtransfersingleitem2_dc(
                    com.zenithbank.nfp.Fundtransfersingleitem2_dcResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from fundtransfersingleitem2_dc operation
           */
            public void receiveErrorfundtransfersingleitem2_dc(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for fundtransfernotification_dc method
            * override this method for handling normal response from fundtransfernotification_dc operation
            */
           public void receiveResultfundtransfernotification_dc(
                    com.zenithbank.nfp.Fundtransfernotification_dcResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from fundtransfernotification_dc operation
           */
            public void receiveErrorfundtransfernotification_dc(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for fundtransferbulkitem method
            * override this method for handling normal response from fundtransferbulkitem operation
            */
           public void receiveResultfundtransferbulkitem(
                    com.zenithbank.nfp.FundtransferbulkitemResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from fundtransferbulkitem operation
           */
            public void receiveErrorfundtransferbulkitem(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for fundtransferbulkitem_dd method
            * override this method for handling normal response from fundtransferbulkitem_dd operation
            */
           public void receiveResultfundtransferbulkitem_dd(
                    com.zenithbank.nfp.Fundtransferbulkitem_ddResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from fundtransferbulkitem_dd operation
           */
            public void receiveErrorfundtransferbulkitem_dd(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for fundtransfersingleitem_dc method
            * override this method for handling normal response from fundtransfersingleitem_dc operation
            */
           public void receiveResultfundtransfersingleitem_dc(
                    com.zenithbank.nfp.Fundtransfersingleitem_dcResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from fundtransfersingleitem_dc operation
           */
            public void receiveErrorfundtransfersingleitem_dc(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for balanceenquiry method
            * override this method for handling normal response from balanceenquiry operation
            */
           public void receiveResultbalanceenquiry(
                    com.zenithbank.nfp.BalanceenquiryResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from balanceenquiry operation
           */
            public void receiveErrorbalanceenquiry(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for fundtransfernotification_dd method
            * override this method for handling normal response from fundtransfernotification_dd operation
            */
           public void receiveResultfundtransfernotification_dd(
                    com.zenithbank.nfp.Fundtransfernotification_ddResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from fundtransfernotification_dd operation
           */
            public void receiveErrorfundtransfernotification_dd(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for txnstatusquerybulkitem method
            * override this method for handling normal response from txnstatusquerybulkitem operation
            */
           public void receiveResulttxnstatusquerybulkitem(
                    com.zenithbank.nfp.TxnstatusquerybulkitemResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from txnstatusquerybulkitem operation
           */
            public void receiveErrortxnstatusquerybulkitem(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for txnstatusquerysingleitem method
            * override this method for handling normal response from txnstatusquerysingleitem operation
            */
           public void receiveResulttxnstatusquerysingleitem(
                    com.zenithbank.nfp.TxnstatusquerysingleitemResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from txnstatusquerysingleitem operation
           */
            public void receiveErrortxnstatusquerysingleitem(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for fundtransferbulkitem_dc method
            * override this method for handling normal response from fundtransferbulkitem_dc operation
            */
           public void receiveResultfundtransferbulkitem_dc(
                    com.zenithbank.nfp.Fundtransferbulkitem_dcResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from fundtransferbulkitem_dc operation
           */
            public void receiveErrorfundtransferbulkitem_dc(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getTransactionStatus method
            * override this method for handling normal response from getTransactionStatus operation
            */
           public void receiveResultgetTransactionStatus(
                    com.zenithbank.nfp.GetTransactionStatusResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getTransactionStatus operation
           */
            public void receiveErrorgetTransactionStatus(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for nameenquirysingleitem method
            * override this method for handling normal response from nameenquirysingleitem operation
            */
           public void receiveResultnameenquirysingleitem(
                    com.zenithbank.nfp.NameenquirysingleitemResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from nameenquirysingleitem operation
           */
            public void receiveErrornameenquirysingleitem(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for nameenquirynotification method
            * override this method for handling normal response from nameenquirynotification operation
            */
           public void receiveResultnameenquirynotification(
                    com.zenithbank.nfp.NameenquirynotificationResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from nameenquirynotification operation
           */
            public void receiveErrornameenquirynotification(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for fundtransfersingleitem_dd method
            * override this method for handling normal response from fundtransfersingleitem_dd operation
            */
           public void receiveResultfundtransfersingleitem_dd(
                    com.zenithbank.nfp.Fundtransfersingleitem_ddResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from fundtransfersingleitem_dd operation
           */
            public void receiveErrorfundtransfersingleitem_dd(java.lang.Exception e) {
            }
                


    }
    