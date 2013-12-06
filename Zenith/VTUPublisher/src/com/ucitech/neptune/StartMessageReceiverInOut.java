
/**
 * StartMessageReceiverInOut.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.2  Built on : Apr 17, 2012 (05:33:49 IST)
 */
        package com.ucitech.neptune;

        /**
        *  StartMessageReceiverInOut message receiver
        */

        public class StartMessageReceiverInOut extends org.apache.axis2.receivers.AbstractInOutMessageReceiver{


        public void invokeBusinessLogic(org.apache.axis2.context.MessageContext msgContext, org.apache.axis2.context.MessageContext newMsgContext)
        throws org.apache.axis2.AxisFault{

        try {

        // get the implementation class for the Web Service
        Object obj = getTheImplementationObject(msgContext);

        StartSkeleton skel = (StartSkeleton)obj;
        //Out Envelop
        org.apache.axiom.soap.SOAPEnvelope envelope = null;
        //Find the axisOperation that has been set by the Dispatch phase.
        org.apache.axis2.description.AxisOperation op = msgContext.getOperationContext().getAxisOperation();
        if (op == null) {
        throw new org.apache.axis2.AxisFault("Operation is not located, if this is doclit style the SOAP-ACTION should specified via the SOAP Action to use the RawXMLProvider");
        }

        java.lang.String methodName;
        if((op.getName() != null) && ((methodName = org.apache.axis2.util.JavaUtils.xmlNameToJavaIdentifier(op.getName().getLocalPart())) != null)){


        

            if("addCustomerProductsByMerchant".equals(methodName)){
                
                com.ucitech.neptune.AddCustomerProductsByMerchantResponseE addCustomerProductsByMerchantResponse1 = null;
	                        com.ucitech.neptune.AddCustomerProductsByMerchantE wrappedParam =
                                                             (com.ucitech.neptune.AddCustomerProductsByMerchantE)fromOM(
                                    msgContext.getEnvelope().getBody().getFirstElement(),
                                    com.ucitech.neptune.AddCustomerProductsByMerchantE.class,
                                    getEnvelopeNamespaces(msgContext.getEnvelope()));
                                                
                                               addCustomerProductsByMerchantResponse1 =
                                                   
                                                   
                                                         skel.addCustomerProductsByMerchant(wrappedParam)
                                                    ;
                                            
                                        envelope = toEnvelope(getSOAPFactory(msgContext), addCustomerProductsByMerchantResponse1, false, new javax.xml.namespace.QName("http://neptune.ucitech.com/",
                                                    "addCustomerProductsByMerchant"));
                                    } else 

            if("delCustomerProductByMerchant".equals(methodName)){
                
                com.ucitech.neptune.DelCustomerProductByMerchantResponseE delCustomerProductByMerchantResponse3 = null;
	                        com.ucitech.neptune.DelCustomerProductByMerchantE wrappedParam =
                                                             (com.ucitech.neptune.DelCustomerProductByMerchantE)fromOM(
                                    msgContext.getEnvelope().getBody().getFirstElement(),
                                    com.ucitech.neptune.DelCustomerProductByMerchantE.class,
                                    getEnvelopeNamespaces(msgContext.getEnvelope()));
                                                
                                               delCustomerProductByMerchantResponse3 =
                                                   
                                                   
                                                         skel.delCustomerProductByMerchant(wrappedParam)
                                                    ;
                                            
                                        envelope = toEnvelope(getSOAPFactory(msgContext), delCustomerProductByMerchantResponse3, false, new javax.xml.namespace.QName("http://neptune.ucitech.com/",
                                                    "delCustomerProductByMerchant"));
                                    } else 

            if("queryCustomerByMerchant".equals(methodName)){
                
                com.ucitech.neptune.QueryCustomerByMerchantResponseE queryCustomerByMerchantResponse5 = null;
	                        com.ucitech.neptune.QueryCustomerByMerchantE wrappedParam =
                                                             (com.ucitech.neptune.QueryCustomerByMerchantE)fromOM(
                                    msgContext.getEnvelope().getBody().getFirstElement(),
                                    com.ucitech.neptune.QueryCustomerByMerchantE.class,
                                    getEnvelopeNamespaces(msgContext.getEnvelope()));
                                                
                                               queryCustomerByMerchantResponse5 =
                                                   
                                                   
                                                         skel.queryCustomerByMerchant(wrappedParam)
                                                    ;
                                            
                                        envelope = toEnvelope(getSOAPFactory(msgContext), queryCustomerByMerchantResponse5, false, new javax.xml.namespace.QName("http://neptune.ucitech.com/",
                                                    "queryCustomerByMerchant"));
                                    } else 

            if("delCustomerProductsByMerchant".equals(methodName)){
                
                com.ucitech.neptune.DelCustomerProductsByMerchantResponseE delCustomerProductsByMerchantResponse7 = null;
	                        com.ucitech.neptune.DelCustomerProductsByMerchantE wrappedParam =
                                                             (com.ucitech.neptune.DelCustomerProductsByMerchantE)fromOM(
                                    msgContext.getEnvelope().getBody().getFirstElement(),
                                    com.ucitech.neptune.DelCustomerProductsByMerchantE.class,
                                    getEnvelopeNamespaces(msgContext.getEnvelope()));
                                                
                                               delCustomerProductsByMerchantResponse7 =
                                                   
                                                   
                                                         skel.delCustomerProductsByMerchant(wrappedParam)
                                                    ;
                                            
                                        envelope = toEnvelope(getSOAPFactory(msgContext), delCustomerProductsByMerchantResponse7, false, new javax.xml.namespace.QName("http://neptune.ucitech.com/",
                                                    "delCustomerProductsByMerchant"));
                                    } else 

            if("login".equals(methodName)){
                
                com.ucitech.neptune.LoginResponseE loginResponse9 = null;
	                        com.ucitech.neptune.LoginE wrappedParam =
                                                             (com.ucitech.neptune.LoginE)fromOM(
                                    msgContext.getEnvelope().getBody().getFirstElement(),
                                    com.ucitech.neptune.LoginE.class,
                                    getEnvelopeNamespaces(msgContext.getEnvelope()));
                                                
                                               loginResponse9 =
                                                   
                                                   
                                                         skel.login(wrappedParam)
                                                    ;
                                            
                                        envelope = toEnvelope(getSOAPFactory(msgContext), loginResponse9, false, new javax.xml.namespace.QName("http://neptune.ucitech.com/",
                                                    "login"));
                                    } else 

            if("addCustomerProductByMerchant".equals(methodName)){
                
                com.ucitech.neptune.AddCustomerProductByMerchantResponseE addCustomerProductByMerchantResponse11 = null;
	                        com.ucitech.neptune.AddCustomerProductByMerchantE wrappedParam =
                                                             (com.ucitech.neptune.AddCustomerProductByMerchantE)fromOM(
                                    msgContext.getEnvelope().getBody().getFirstElement(),
                                    com.ucitech.neptune.AddCustomerProductByMerchantE.class,
                                    getEnvelopeNamespaces(msgContext.getEnvelope()));
                                                
                                               addCustomerProductByMerchantResponse11 =
                                                   
                                                   
                                                         skel.addCustomerProductByMerchant(wrappedParam)
                                                    ;
                                            
                                        envelope = toEnvelope(getSOAPFactory(msgContext), addCustomerProductByMerchantResponse11, false, new javax.xml.namespace.QName("http://neptune.ucitech.com/",
                                                    "addCustomerProductByMerchant"));
                                    } else 

            if("queryMerchants".equals(methodName)){
                
                com.ucitech.neptune.QueryMerchantsResponseE queryMerchantsResponse13 = null;
	                        com.ucitech.neptune.QueryMerchantsE wrappedParam =
                                                             (com.ucitech.neptune.QueryMerchantsE)fromOM(
                                    msgContext.getEnvelope().getBody().getFirstElement(),
                                    com.ucitech.neptune.QueryMerchantsE.class,
                                    getEnvelopeNamespaces(msgContext.getEnvelope()));
                                                
                                               queryMerchantsResponse13 =
                                                   
                                                   
                                                         skel.queryMerchants(wrappedParam)
                                                    ;
                                            
                                        envelope = toEnvelope(getSOAPFactory(msgContext), queryMerchantsResponse13, false, new javax.xml.namespace.QName("http://neptune.ucitech.com/",
                                                    "queryMerchants"));
                                    } else 

            if("queryMerchantProducts".equals(methodName)){
                
                com.ucitech.neptune.QueryMerchantProductsResponseE queryMerchantProductsResponse15 = null;
	                        com.ucitech.neptune.QueryMerchantProductsE wrappedParam =
                                                             (com.ucitech.neptune.QueryMerchantProductsE)fromOM(
                                    msgContext.getEnvelope().getBody().getFirstElement(),
                                    com.ucitech.neptune.QueryMerchantProductsE.class,
                                    getEnvelopeNamespaces(msgContext.getEnvelope()));
                                                
                                               queryMerchantProductsResponse15 =
                                                   
                                                   
                                                         skel.queryMerchantProducts(wrappedParam)
                                                    ;
                                            
                                        envelope = toEnvelope(getSOAPFactory(msgContext), queryMerchantProductsResponse15, false, new javax.xml.namespace.QName("http://neptune.ucitech.com/",
                                                    "queryMerchantProducts"));
                                    } else 

            if("queryMerchantTransaction".equals(methodName)){
                
                com.ucitech.neptune.QueryMerchantTransactionResponseE queryMerchantTransactionResponse17 = null;
	                        com.ucitech.neptune.QueryMerchantTransactionE wrappedParam =
                                                             (com.ucitech.neptune.QueryMerchantTransactionE)fromOM(
                                    msgContext.getEnvelope().getBody().getFirstElement(),
                                    com.ucitech.neptune.QueryMerchantTransactionE.class,
                                    getEnvelopeNamespaces(msgContext.getEnvelope()));
                                                
                                               queryMerchantTransactionResponse17 =
                                                   
                                                   
                                                         skel.queryMerchantTransaction(wrappedParam)
                                                    ;
                                            
                                        envelope = toEnvelope(getSOAPFactory(msgContext), queryMerchantTransactionResponse17, false, new javax.xml.namespace.QName("http://neptune.ucitech.com/",
                                                    "queryMerchantTransaction"));
                                    
            } else {
              throw new java.lang.RuntimeException("method not found");
            }
        

        newMsgContext.setEnvelope(envelope);
        }
        }
        catch (java.lang.Exception e) {
        throw org.apache.axis2.AxisFault.makeFault(e);
        }
        }
        
        //
            private  org.apache.axiom.om.OMElement  toOM(com.ucitech.neptune.AddCustomerProductsByMerchantE param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(com.ucitech.neptune.AddCustomerProductsByMerchantE.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(com.ucitech.neptune.AddCustomerProductsByMerchantResponseE param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(com.ucitech.neptune.AddCustomerProductsByMerchantResponseE.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(com.ucitech.neptune.DelCustomerProductByMerchantE param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(com.ucitech.neptune.DelCustomerProductByMerchantE.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(com.ucitech.neptune.DelCustomerProductByMerchantResponseE param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(com.ucitech.neptune.DelCustomerProductByMerchantResponseE.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(com.ucitech.neptune.QueryCustomerByMerchantE param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(com.ucitech.neptune.QueryCustomerByMerchantE.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(com.ucitech.neptune.QueryCustomerByMerchantResponseE param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(com.ucitech.neptune.QueryCustomerByMerchantResponseE.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(com.ucitech.neptune.DelCustomerProductsByMerchantE param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(com.ucitech.neptune.DelCustomerProductsByMerchantE.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(com.ucitech.neptune.DelCustomerProductsByMerchantResponseE param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(com.ucitech.neptune.DelCustomerProductsByMerchantResponseE.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(com.ucitech.neptune.LoginE param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(com.ucitech.neptune.LoginE.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(com.ucitech.neptune.LoginResponseE param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(com.ucitech.neptune.LoginResponseE.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(com.ucitech.neptune.AddCustomerProductByMerchantE param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(com.ucitech.neptune.AddCustomerProductByMerchantE.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(com.ucitech.neptune.AddCustomerProductByMerchantResponseE param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(com.ucitech.neptune.AddCustomerProductByMerchantResponseE.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(com.ucitech.neptune.QueryMerchantsE param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(com.ucitech.neptune.QueryMerchantsE.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(com.ucitech.neptune.QueryMerchantsResponseE param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(com.ucitech.neptune.QueryMerchantsResponseE.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(com.ucitech.neptune.QueryMerchantProductsE param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(com.ucitech.neptune.QueryMerchantProductsE.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(com.ucitech.neptune.QueryMerchantProductsResponseE param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(com.ucitech.neptune.QueryMerchantProductsResponseE.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(com.ucitech.neptune.QueryMerchantTransactionE param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(com.ucitech.neptune.QueryMerchantTransactionE.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(com.ucitech.neptune.QueryMerchantTransactionResponseE param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(com.ucitech.neptune.QueryMerchantTransactionResponseE.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
                    private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, com.ucitech.neptune.AddCustomerProductsByMerchantResponseE param, boolean optimizeContent, javax.xml.namespace.QName methodQName)
                        throws org.apache.axis2.AxisFault{
                      try{
                          org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
                           
                                    emptyEnvelope.getBody().addChild(param.getOMElement(com.ucitech.neptune.AddCustomerProductsByMerchantResponseE.MY_QNAME,factory));
                                

                         return emptyEnvelope;
                    } catch(org.apache.axis2.databinding.ADBException e){
                        throw org.apache.axis2.AxisFault.makeFault(e);
                    }
                    }
                    
                         private com.ucitech.neptune.AddCustomerProductsByMerchantResponseE wrapaddCustomerProductsByMerchant(){
                                com.ucitech.neptune.AddCustomerProductsByMerchantResponseE wrappedElement = new com.ucitech.neptune.AddCustomerProductsByMerchantResponseE();
                                return wrappedElement;
                         }
                    
                    private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, com.ucitech.neptune.DelCustomerProductByMerchantResponseE param, boolean optimizeContent, javax.xml.namespace.QName methodQName)
                        throws org.apache.axis2.AxisFault{
                      try{
                          org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
                           
                                    emptyEnvelope.getBody().addChild(param.getOMElement(com.ucitech.neptune.DelCustomerProductByMerchantResponseE.MY_QNAME,factory));
                                

                         return emptyEnvelope;
                    } catch(org.apache.axis2.databinding.ADBException e){
                        throw org.apache.axis2.AxisFault.makeFault(e);
                    }
                    }
                    
                         private com.ucitech.neptune.DelCustomerProductByMerchantResponseE wrapdelCustomerProductByMerchant(){
                                com.ucitech.neptune.DelCustomerProductByMerchantResponseE wrappedElement = new com.ucitech.neptune.DelCustomerProductByMerchantResponseE();
                                return wrappedElement;
                         }
                    
                    private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, com.ucitech.neptune.QueryCustomerByMerchantResponseE param, boolean optimizeContent, javax.xml.namespace.QName methodQName)
                        throws org.apache.axis2.AxisFault{
                      try{
                          org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
                           
                                    emptyEnvelope.getBody().addChild(param.getOMElement(com.ucitech.neptune.QueryCustomerByMerchantResponseE.MY_QNAME,factory));
                                

                         return emptyEnvelope;
                    } catch(org.apache.axis2.databinding.ADBException e){
                        throw org.apache.axis2.AxisFault.makeFault(e);
                    }
                    }
                    
                         private com.ucitech.neptune.QueryCustomerByMerchantResponseE wrapqueryCustomerByMerchant(){
                                com.ucitech.neptune.QueryCustomerByMerchantResponseE wrappedElement = new com.ucitech.neptune.QueryCustomerByMerchantResponseE();
                                return wrappedElement;
                         }
                    
                    private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, com.ucitech.neptune.DelCustomerProductsByMerchantResponseE param, boolean optimizeContent, javax.xml.namespace.QName methodQName)
                        throws org.apache.axis2.AxisFault{
                      try{
                          org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
                           
                                    emptyEnvelope.getBody().addChild(param.getOMElement(com.ucitech.neptune.DelCustomerProductsByMerchantResponseE.MY_QNAME,factory));
                                

                         return emptyEnvelope;
                    } catch(org.apache.axis2.databinding.ADBException e){
                        throw org.apache.axis2.AxisFault.makeFault(e);
                    }
                    }
                    
                         private com.ucitech.neptune.DelCustomerProductsByMerchantResponseE wrapdelCustomerProductsByMerchant(){
                                com.ucitech.neptune.DelCustomerProductsByMerchantResponseE wrappedElement = new com.ucitech.neptune.DelCustomerProductsByMerchantResponseE();
                                return wrappedElement;
                         }
                    
                    private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, com.ucitech.neptune.LoginResponseE param, boolean optimizeContent, javax.xml.namespace.QName methodQName)
                        throws org.apache.axis2.AxisFault{
                      try{
                          org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
                           
                                    emptyEnvelope.getBody().addChild(param.getOMElement(com.ucitech.neptune.LoginResponseE.MY_QNAME,factory));
                                

                         return emptyEnvelope;
                    } catch(org.apache.axis2.databinding.ADBException e){
                        throw org.apache.axis2.AxisFault.makeFault(e);
                    }
                    }
                    
                         private com.ucitech.neptune.LoginResponseE wraplogin(){
                                com.ucitech.neptune.LoginResponseE wrappedElement = new com.ucitech.neptune.LoginResponseE();
                                return wrappedElement;
                         }
                    
                    private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, com.ucitech.neptune.AddCustomerProductByMerchantResponseE param, boolean optimizeContent, javax.xml.namespace.QName methodQName)
                        throws org.apache.axis2.AxisFault{
                      try{
                          org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
                           
                                    emptyEnvelope.getBody().addChild(param.getOMElement(com.ucitech.neptune.AddCustomerProductByMerchantResponseE.MY_QNAME,factory));
                                

                         return emptyEnvelope;
                    } catch(org.apache.axis2.databinding.ADBException e){
                        throw org.apache.axis2.AxisFault.makeFault(e);
                    }
                    }
                    
                         private com.ucitech.neptune.AddCustomerProductByMerchantResponseE wrapaddCustomerProductByMerchant(){
                                com.ucitech.neptune.AddCustomerProductByMerchantResponseE wrappedElement = new com.ucitech.neptune.AddCustomerProductByMerchantResponseE();
                                return wrappedElement;
                         }
                    
                    private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, com.ucitech.neptune.QueryMerchantsResponseE param, boolean optimizeContent, javax.xml.namespace.QName methodQName)
                        throws org.apache.axis2.AxisFault{
                      try{
                          org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
                           
                                    emptyEnvelope.getBody().addChild(param.getOMElement(com.ucitech.neptune.QueryMerchantsResponseE.MY_QNAME,factory));
                                

                         return emptyEnvelope;
                    } catch(org.apache.axis2.databinding.ADBException e){
                        throw org.apache.axis2.AxisFault.makeFault(e);
                    }
                    }
                    
                         private com.ucitech.neptune.QueryMerchantsResponseE wrapqueryMerchants(){
                                com.ucitech.neptune.QueryMerchantsResponseE wrappedElement = new com.ucitech.neptune.QueryMerchantsResponseE();
                                return wrappedElement;
                         }
                    
                    private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, com.ucitech.neptune.QueryMerchantProductsResponseE param, boolean optimizeContent, javax.xml.namespace.QName methodQName)
                        throws org.apache.axis2.AxisFault{
                      try{
                          org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
                           
                                    emptyEnvelope.getBody().addChild(param.getOMElement(com.ucitech.neptune.QueryMerchantProductsResponseE.MY_QNAME,factory));
                                

                         return emptyEnvelope;
                    } catch(org.apache.axis2.databinding.ADBException e){
                        throw org.apache.axis2.AxisFault.makeFault(e);
                    }
                    }
                    
                         private com.ucitech.neptune.QueryMerchantProductsResponseE wrapqueryMerchantProducts(){
                                com.ucitech.neptune.QueryMerchantProductsResponseE wrappedElement = new com.ucitech.neptune.QueryMerchantProductsResponseE();
                                return wrappedElement;
                         }
                    
                    private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, com.ucitech.neptune.QueryMerchantTransactionResponseE param, boolean optimizeContent, javax.xml.namespace.QName methodQName)
                        throws org.apache.axis2.AxisFault{
                      try{
                          org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
                           
                                    emptyEnvelope.getBody().addChild(param.getOMElement(com.ucitech.neptune.QueryMerchantTransactionResponseE.MY_QNAME,factory));
                                

                         return emptyEnvelope;
                    } catch(org.apache.axis2.databinding.ADBException e){
                        throw org.apache.axis2.AxisFault.makeFault(e);
                    }
                    }
                    
                         private com.ucitech.neptune.QueryMerchantTransactionResponseE wrapqueryMerchantTransaction(){
                                com.ucitech.neptune.QueryMerchantTransactionResponseE wrappedElement = new com.ucitech.neptune.QueryMerchantTransactionResponseE();
                                return wrappedElement;
                         }
                    


        /**
        *  get the default envelope
        */
        private org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory){
        return factory.getDefaultEnvelope();
        }


        private  java.lang.Object fromOM(
        org.apache.axiom.om.OMElement param,
        java.lang.Class type,
        java.util.Map extraNamespaces) throws org.apache.axis2.AxisFault{

        try {
        
                if (com.ucitech.neptune.AddCustomerProductsByMerchantE.class.equals(type)){
                
                           return com.ucitech.neptune.AddCustomerProductsByMerchantE.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (com.ucitech.neptune.AddCustomerProductsByMerchantResponseE.class.equals(type)){
                
                           return com.ucitech.neptune.AddCustomerProductsByMerchantResponseE.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (com.ucitech.neptune.DelCustomerProductByMerchantE.class.equals(type)){
                
                           return com.ucitech.neptune.DelCustomerProductByMerchantE.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (com.ucitech.neptune.DelCustomerProductByMerchantResponseE.class.equals(type)){
                
                           return com.ucitech.neptune.DelCustomerProductByMerchantResponseE.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (com.ucitech.neptune.QueryCustomerByMerchantE.class.equals(type)){
                
                           return com.ucitech.neptune.QueryCustomerByMerchantE.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (com.ucitech.neptune.QueryCustomerByMerchantResponseE.class.equals(type)){
                
                           return com.ucitech.neptune.QueryCustomerByMerchantResponseE.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (com.ucitech.neptune.DelCustomerProductsByMerchantE.class.equals(type)){
                
                           return com.ucitech.neptune.DelCustomerProductsByMerchantE.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (com.ucitech.neptune.DelCustomerProductsByMerchantResponseE.class.equals(type)){
                
                           return com.ucitech.neptune.DelCustomerProductsByMerchantResponseE.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (com.ucitech.neptune.LoginE.class.equals(type)){
                
                           return com.ucitech.neptune.LoginE.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (com.ucitech.neptune.LoginResponseE.class.equals(type)){
                
                           return com.ucitech.neptune.LoginResponseE.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (com.ucitech.neptune.AddCustomerProductByMerchantE.class.equals(type)){
                
                           return com.ucitech.neptune.AddCustomerProductByMerchantE.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (com.ucitech.neptune.AddCustomerProductByMerchantResponseE.class.equals(type)){
                
                           return com.ucitech.neptune.AddCustomerProductByMerchantResponseE.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (com.ucitech.neptune.QueryMerchantsE.class.equals(type)){
                
                           return com.ucitech.neptune.QueryMerchantsE.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (com.ucitech.neptune.QueryMerchantsResponseE.class.equals(type)){
                
                           return com.ucitech.neptune.QueryMerchantsResponseE.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (com.ucitech.neptune.QueryMerchantProductsE.class.equals(type)){
                
                           return com.ucitech.neptune.QueryMerchantProductsE.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (com.ucitech.neptune.QueryMerchantProductsResponseE.class.equals(type)){
                
                           return com.ucitech.neptune.QueryMerchantProductsResponseE.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (com.ucitech.neptune.QueryMerchantTransactionE.class.equals(type)){
                
                           return com.ucitech.neptune.QueryMerchantTransactionE.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (com.ucitech.neptune.QueryMerchantTransactionResponseE.class.equals(type)){
                
                           return com.ucitech.neptune.QueryMerchantTransactionResponseE.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
        } catch (java.lang.Exception e) {
        throw org.apache.axis2.AxisFault.makeFault(e);
        }
           return null;
        }



    

        /**
        *  A utility method that copies the namepaces from the SOAPEnvelope
        */
        private java.util.Map getEnvelopeNamespaces(org.apache.axiom.soap.SOAPEnvelope env){
        java.util.Map returnMap = new java.util.HashMap();
        java.util.Iterator namespaceIterator = env.getAllDeclaredNamespaces();
        while (namespaceIterator.hasNext()) {
        org.apache.axiom.om.OMNamespace ns = (org.apache.axiom.om.OMNamespace) namespaceIterator.next();
        returnMap.put(ns.getPrefix(),ns.getNamespaceURI());
        }
        return returnMap;
        }

        private org.apache.axis2.AxisFault createAxisFault(java.lang.Exception e) {
        org.apache.axis2.AxisFault f;
        Throwable cause = e.getCause();
        if (cause != null) {
            f = new org.apache.axis2.AxisFault(e.getMessage(), cause);
        } else {
            f = new org.apache.axis2.AxisFault(e.getMessage());
        }

        return f;
    }

        }//end of class
    