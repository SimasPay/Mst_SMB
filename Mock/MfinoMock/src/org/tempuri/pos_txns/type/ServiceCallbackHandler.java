
/**
 * ServiceCallbackHandler.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.1  Built on : Aug 31, 2011 (12:22:40 CEST)
 */

    package org.tempuri.pos_txns.type;

    /**
     *  ServiceCallbackHandler Callback class, Users can extend this class and implement
     *  their own receiveResult and receiveError methods.
     */
    public abstract class ServiceCallbackHandler{



    protected Object clientData;

    /**
    * User can pass in any object that needs to be accessed once the NonBlocking
    * Web service call is finished and appropriate method of this CallBack is called.
    * @param clientData Object mechanism by which the user can pass in user data
    * that will be avilable at the time this callback is called.
    */
    public ServiceCallbackHandler(Object clientData){
        this.clientData = clientData;
    }

    /**
    * Please use this constructor if you don't want to set any clientData
    */
    public ServiceCallbackHandler(){
        this.clientData = null;
    }

    /**
     * Get the client data
     */

     public Object getClientData() {
        return clientData;
     }

        
           /**
            * auto generated Axis2 call back method for getPOS_TXNS method
            * override this method for handling normal response from getPOS_TXNS operation
            */
           public void receiveResultgetPOS_TXNS(
                    org.tempuri.pos_txns.type.GetPOS_TXNSResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getPOS_TXNS operation
           */
            public void receiveErrorgetPOS_TXNS(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for savePOS_TXNS method
            * override this method for handling normal response from savePOS_TXNS operation
            */
           public void receiveResultsavePOS_TXNS(
                    org.tempuri.pos_txns.type.SavePOS_TXNSResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from savePOS_TXNS operation
           */
            public void receiveErrorsavePOS_TXNS(java.lang.Exception e) {
            }
                


    }
    