
/**
 * NibssFasterPayMessageReceiverInOut.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.1  Built on : Aug 31, 2011 (12:22:40 CEST)
 */
        package com.zenithbank.nfp;

        /**
        *  NibssFasterPayMessageReceiverInOut message receiver
        */

        public class NibssFasterPayMessageReceiverInOut extends org.apache.axis2.receivers.AbstractInOutMessageReceiver{


        public void invokeBusinessLogic(org.apache.axis2.context.MessageContext msgContext, org.apache.axis2.context.MessageContext newMsgContext)
        throws org.apache.axis2.AxisFault{

        try {

        // get the implementation class for the Web Service
        Object obj = getTheImplementationObject(msgContext);

        NibssFasterPaySkeletonInterface skel = (NibssFasterPaySkeletonInterface)obj;
        //Out Envelop
        org.apache.axiom.soap.SOAPEnvelope envelope = null;
        //Find the axisOperation that has been set by the Dispatch phase.
        org.apache.axis2.description.AxisOperation op = msgContext.getOperationContext().getAxisOperation();
        if (op == null) {
        throw new org.apache.axis2.AxisFault("Operation is not located, if this is doclit style the SOAP-ACTION should specified via the SOAP Action to use the RawXMLProvider");
        }

        java.lang.String methodName;
        if((op.getName() != null) && ((methodName = org.apache.axis2.util.JavaUtils.xmlNameToJavaIdentifier(op.getName().getLocalPart())) != null)){


        

            if("nameenquirybulkitem".equals(methodName)){
                
                com.zenithbank.nfp.NameenquirybulkitemResponse nameenquirybulkitemResponse31 = null;
	                        com.zenithbank.nfp.Nameenquirybulkitem wrappedParam =
                                                             (com.zenithbank.nfp.Nameenquirybulkitem)fromOM(
                                    msgContext.getEnvelope().getBody().getFirstElement(),
                                    com.zenithbank.nfp.Nameenquirybulkitem.class,
                                    getEnvelopeNamespaces(msgContext.getEnvelope()));
                                                
                                               nameenquirybulkitemResponse31 =
                                                   
                                                   
                                                         skel.nameenquirybulkitem(wrappedParam)
                                                    ;
                                            
                                        envelope = toEnvelope(getSOAPFactory(msgContext), nameenquirybulkitemResponse31, false, new javax.xml.namespace.QName("http://nfp.zenithbank.com",
                                                    "nameenquirybulkitem"));
                                    } else 

            if("fundtransfersingleitem2_dc".equals(methodName)){
                
                com.zenithbank.nfp.Fundtransfersingleitem2_dcResponse fundtransfersingleitem2_dcResponse33 = null;
	                        com.zenithbank.nfp.Fundtransfersingleitem2_dc wrappedParam =
                                                             (com.zenithbank.nfp.Fundtransfersingleitem2_dc)fromOM(
                                    msgContext.getEnvelope().getBody().getFirstElement(),
                                    com.zenithbank.nfp.Fundtransfersingleitem2_dc.class,
                                    getEnvelopeNamespaces(msgContext.getEnvelope()));
                                                
                                               fundtransfersingleitem2_dcResponse33 =
                                                   
                                                   
                                                         skel.fundtransfersingleitem2_dc(wrappedParam)
                                                    ;
                                            
                                        envelope = toEnvelope(getSOAPFactory(msgContext), fundtransfersingleitem2_dcResponse33, false, new javax.xml.namespace.QName("http://nfp.zenithbank.com",
                                                    "fundtransfersingleitem2_dc"));
                                    } else 

            if("fundtransfernotification_dc".equals(methodName)){
                
                com.zenithbank.nfp.Fundtransfernotification_dcResponse fundtransfernotification_dcResponse35 = null;
	                        com.zenithbank.nfp.Fundtransfernotification_dc wrappedParam =
                                                             (com.zenithbank.nfp.Fundtransfernotification_dc)fromOM(
                                    msgContext.getEnvelope().getBody().getFirstElement(),
                                    com.zenithbank.nfp.Fundtransfernotification_dc.class,
                                    getEnvelopeNamespaces(msgContext.getEnvelope()));
                                                
                                               fundtransfernotification_dcResponse35 =
                                                   
                                                   
                                                         skel.fundtransfernotification_dc(wrappedParam)
                                                    ;
                                            
                                        envelope = toEnvelope(getSOAPFactory(msgContext), fundtransfernotification_dcResponse35, false, new javax.xml.namespace.QName("http://nfp.zenithbank.com",
                                                    "fundtransfernotification_dc"));
                                    } else 

            if("fundtransferbulkitem".equals(methodName)){
                
                com.zenithbank.nfp.FundtransferbulkitemResponse fundtransferbulkitemResponse37 = null;
	                        com.zenithbank.nfp.Fundtransferbulkitem wrappedParam =
                                                             (com.zenithbank.nfp.Fundtransferbulkitem)fromOM(
                                    msgContext.getEnvelope().getBody().getFirstElement(),
                                    com.zenithbank.nfp.Fundtransferbulkitem.class,
                                    getEnvelopeNamespaces(msgContext.getEnvelope()));
                                                
                                               fundtransferbulkitemResponse37 =
                                                   
                                                   
                                                         skel.fundtransferbulkitem(wrappedParam)
                                                    ;
                                            
                                        envelope = toEnvelope(getSOAPFactory(msgContext), fundtransferbulkitemResponse37, false, new javax.xml.namespace.QName("http://nfp.zenithbank.com",
                                                    "fundtransferbulkitem"));
                                    } else 

            if("fundtransferbulkitem_dd".equals(methodName)){
                
                com.zenithbank.nfp.Fundtransferbulkitem_ddResponse fundtransferbulkitem_ddResponse39 = null;
	                        com.zenithbank.nfp.Fundtransferbulkitem_dd wrappedParam =
                                                             (com.zenithbank.nfp.Fundtransferbulkitem_dd)fromOM(
                                    msgContext.getEnvelope().getBody().getFirstElement(),
                                    com.zenithbank.nfp.Fundtransferbulkitem_dd.class,
                                    getEnvelopeNamespaces(msgContext.getEnvelope()));
                                                
                                               fundtransferbulkitem_ddResponse39 =
                                                   
                                                   
                                                         skel.fundtransferbulkitem_dd(wrappedParam)
                                                    ;
                                            
                                        envelope = toEnvelope(getSOAPFactory(msgContext), fundtransferbulkitem_ddResponse39, false, new javax.xml.namespace.QName("http://nfp.zenithbank.com",
                                                    "fundtransferbulkitem_dd"));
                                    } else 

            if("fundtransfersingleitem_dc".equals(methodName)){
                
                com.zenithbank.nfp.Fundtransfersingleitem_dcResponse fundtransfersingleitem_dcResponse41 = null;
	                        com.zenithbank.nfp.Fundtransfersingleitem_dc wrappedParam =
                                                             (com.zenithbank.nfp.Fundtransfersingleitem_dc)fromOM(
                                    msgContext.getEnvelope().getBody().getFirstElement(),
                                    com.zenithbank.nfp.Fundtransfersingleitem_dc.class,
                                    getEnvelopeNamespaces(msgContext.getEnvelope()));
                                                
                                               fundtransfersingleitem_dcResponse41 =
                                                   
                                                   
                                                         skel.fundtransfersingleitem_dc(wrappedParam)
                                                    ;
                                            
                                        envelope = toEnvelope(getSOAPFactory(msgContext), fundtransfersingleitem_dcResponse41, false, new javax.xml.namespace.QName("http://nfp.zenithbank.com",
                                                    "fundtransfersingleitem_dc"));
                                    } else 

            if("balanceenquiry".equals(methodName)){
                
                com.zenithbank.nfp.BalanceenquiryResponse balanceenquiryResponse43 = null;
	                        com.zenithbank.nfp.Balanceenquiry wrappedParam =
                                                             (com.zenithbank.nfp.Balanceenquiry)fromOM(
                                    msgContext.getEnvelope().getBody().getFirstElement(),
                                    com.zenithbank.nfp.Balanceenquiry.class,
                                    getEnvelopeNamespaces(msgContext.getEnvelope()));
                                                
                                               balanceenquiryResponse43 =
                                                   
                                                   
                                                         skel.balanceenquiry(wrappedParam)
                                                    ;
                                            
                                        envelope = toEnvelope(getSOAPFactory(msgContext), balanceenquiryResponse43, false, new javax.xml.namespace.QName("http://nfp.zenithbank.com",
                                                    "balanceenquiry"));
                                    } else 

            if("fundtransfernotification_dd".equals(methodName)){
                
                com.zenithbank.nfp.Fundtransfernotification_ddResponse fundtransfernotification_ddResponse45 = null;
	                        com.zenithbank.nfp.Fundtransfernotification_dd wrappedParam =
                                                             (com.zenithbank.nfp.Fundtransfernotification_dd)fromOM(
                                    msgContext.getEnvelope().getBody().getFirstElement(),
                                    com.zenithbank.nfp.Fundtransfernotification_dd.class,
                                    getEnvelopeNamespaces(msgContext.getEnvelope()));
                                                
                                               fundtransfernotification_ddResponse45 =
                                                   
                                                   
                                                         skel.fundtransfernotification_dd(wrappedParam)
                                                    ;
                                            
                                        envelope = toEnvelope(getSOAPFactory(msgContext), fundtransfernotification_ddResponse45, false, new javax.xml.namespace.QName("http://nfp.zenithbank.com",
                                                    "fundtransfernotification_dd"));
                                    } else 

            if("txnstatusquerybulkitem".equals(methodName)){
                
                com.zenithbank.nfp.TxnstatusquerybulkitemResponse txnstatusquerybulkitemResponse47 = null;
	                        com.zenithbank.nfp.Txnstatusquerybulkitem wrappedParam =
                                                             (com.zenithbank.nfp.Txnstatusquerybulkitem)fromOM(
                                    msgContext.getEnvelope().getBody().getFirstElement(),
                                    com.zenithbank.nfp.Txnstatusquerybulkitem.class,
                                    getEnvelopeNamespaces(msgContext.getEnvelope()));
                                                
                                               txnstatusquerybulkitemResponse47 =
                                                   
                                                   
                                                         skel.txnstatusquerybulkitem(wrappedParam)
                                                    ;
                                            
                                        envelope = toEnvelope(getSOAPFactory(msgContext), txnstatusquerybulkitemResponse47, false, new javax.xml.namespace.QName("http://nfp.zenithbank.com",
                                                    "txnstatusquerybulkitem"));
                                    } else 

            if("txnstatusquerysingleitem".equals(methodName)){
                
                com.zenithbank.nfp.TxnstatusquerysingleitemResponse txnstatusquerysingleitemResponse49 = null;
	                        com.zenithbank.nfp.Txnstatusquerysingleitem wrappedParam =
                                                             (com.zenithbank.nfp.Txnstatusquerysingleitem)fromOM(
                                    msgContext.getEnvelope().getBody().getFirstElement(),
                                    com.zenithbank.nfp.Txnstatusquerysingleitem.class,
                                    getEnvelopeNamespaces(msgContext.getEnvelope()));
                                                
                                               txnstatusquerysingleitemResponse49 =
                                                   
                                                   
                                                         skel.txnstatusquerysingleitem(wrappedParam)
                                                    ;
                                            
                                        envelope = toEnvelope(getSOAPFactory(msgContext), txnstatusquerysingleitemResponse49, false, new javax.xml.namespace.QName("http://nfp.zenithbank.com",
                                                    "txnstatusquerysingleitem"));
                                    } else 

            if("fundtransferbulkitem_dc".equals(methodName)){
                
                com.zenithbank.nfp.Fundtransferbulkitem_dcResponse fundtransferbulkitem_dcResponse51 = null;
	                        com.zenithbank.nfp.Fundtransferbulkitem_dc wrappedParam =
                                                             (com.zenithbank.nfp.Fundtransferbulkitem_dc)fromOM(
                                    msgContext.getEnvelope().getBody().getFirstElement(),
                                    com.zenithbank.nfp.Fundtransferbulkitem_dc.class,
                                    getEnvelopeNamespaces(msgContext.getEnvelope()));
                                                
                                               fundtransferbulkitem_dcResponse51 =
                                                   
                                                   
                                                         skel.fundtransferbulkitem_dc(wrappedParam)
                                                    ;
                                            
                                        envelope = toEnvelope(getSOAPFactory(msgContext), fundtransferbulkitem_dcResponse51, false, new javax.xml.namespace.QName("http://nfp.zenithbank.com",
                                                    "fundtransferbulkitem_dc"));
                                    } else 

            if("getTransactionStatus".equals(methodName)){
                
                com.zenithbank.nfp.GetTransactionStatusResponse getTransactionStatusResponse53 = null;
	                        com.zenithbank.nfp.GetTransactionStatus wrappedParam =
                                                             (com.zenithbank.nfp.GetTransactionStatus)fromOM(
                                    msgContext.getEnvelope().getBody().getFirstElement(),
                                    com.zenithbank.nfp.GetTransactionStatus.class,
                                    getEnvelopeNamespaces(msgContext.getEnvelope()));
                                                
                                               getTransactionStatusResponse53 =
                                                   
                                                   
                                                         skel.getTransactionStatus(wrappedParam)
                                                    ;
                                            
                                        envelope = toEnvelope(getSOAPFactory(msgContext), getTransactionStatusResponse53, false, new javax.xml.namespace.QName("http://nfp.zenithbank.com",
                                                    "getTransactionStatus"));
                                    } else 

            if("nameenquirysingleitem".equals(methodName)){
                
                com.zenithbank.nfp.NameenquirysingleitemResponse nameenquirysingleitemResponse55 = null;
	                        com.zenithbank.nfp.Nameenquirysingleitem wrappedParam =
                                                             (com.zenithbank.nfp.Nameenquirysingleitem)fromOM(
                                    msgContext.getEnvelope().getBody().getFirstElement(),
                                    com.zenithbank.nfp.Nameenquirysingleitem.class,
                                    getEnvelopeNamespaces(msgContext.getEnvelope()));
                                                
                                               nameenquirysingleitemResponse55 =
                                                   
                                                   
                                                         skel.nameenquirysingleitem(wrappedParam)
                                                    ;
                                            
                                        envelope = toEnvelope(getSOAPFactory(msgContext), nameenquirysingleitemResponse55, false, new javax.xml.namespace.QName("http://nfp.zenithbank.com",
                                                    "nameenquirysingleitem"));
                                    } else 

            if("nameenquirynotification".equals(methodName)){
                
                com.zenithbank.nfp.NameenquirynotificationResponse nameenquirynotificationResponse57 = null;
	                        com.zenithbank.nfp.Nameenquirynotification wrappedParam =
                                                             (com.zenithbank.nfp.Nameenquirynotification)fromOM(
                                    msgContext.getEnvelope().getBody().getFirstElement(),
                                    com.zenithbank.nfp.Nameenquirynotification.class,
                                    getEnvelopeNamespaces(msgContext.getEnvelope()));
                                                
                                               nameenquirynotificationResponse57 =
                                                   
                                                   
                                                         skel.nameenquirynotification(wrappedParam)
                                                    ;
                                            
                                        envelope = toEnvelope(getSOAPFactory(msgContext), nameenquirynotificationResponse57, false, new javax.xml.namespace.QName("http://nfp.zenithbank.com",
                                                    "nameenquirynotification"));
                                    } else 

            if("fundtransfersingleitem_dd".equals(methodName)){
                
                com.zenithbank.nfp.Fundtransfersingleitem_ddResponse fundtransfersingleitem_ddResponse59 = null;
	                        com.zenithbank.nfp.Fundtransfersingleitem_dd wrappedParam =
                                                             (com.zenithbank.nfp.Fundtransfersingleitem_dd)fromOM(
                                    msgContext.getEnvelope().getBody().getFirstElement(),
                                    com.zenithbank.nfp.Fundtransfersingleitem_dd.class,
                                    getEnvelopeNamespaces(msgContext.getEnvelope()));
                                                
                                               fundtransfersingleitem_ddResponse59 =
                                                   
                                                   
                                                         skel.fundtransfersingleitem_dd(wrappedParam)
                                                    ;
                                            
                                        envelope = toEnvelope(getSOAPFactory(msgContext), fundtransfersingleitem_ddResponse59, false, new javax.xml.namespace.QName("http://nfp.zenithbank.com",
                                                    "fundtransfersingleitem_dd"));
                                    
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
            private  org.apache.axiom.om.OMElement  toOM(com.zenithbank.nfp.Nameenquirybulkitem param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(com.zenithbank.nfp.Nameenquirybulkitem.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(com.zenithbank.nfp.NameenquirybulkitemResponse param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(com.zenithbank.nfp.NameenquirybulkitemResponse.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(com.zenithbank.nfp.Fundtransfersingleitem2_dc param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(com.zenithbank.nfp.Fundtransfersingleitem2_dc.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(com.zenithbank.nfp.Fundtransfersingleitem2_dcResponse param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(com.zenithbank.nfp.Fundtransfersingleitem2_dcResponse.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(com.zenithbank.nfp.Fundtransfernotification_dc param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(com.zenithbank.nfp.Fundtransfernotification_dc.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(com.zenithbank.nfp.Fundtransfernotification_dcResponse param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(com.zenithbank.nfp.Fundtransfernotification_dcResponse.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(com.zenithbank.nfp.Fundtransferbulkitem param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(com.zenithbank.nfp.Fundtransferbulkitem.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(com.zenithbank.nfp.FundtransferbulkitemResponse param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(com.zenithbank.nfp.FundtransferbulkitemResponse.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(com.zenithbank.nfp.Fundtransferbulkitem_dd param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(com.zenithbank.nfp.Fundtransferbulkitem_dd.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(com.zenithbank.nfp.Fundtransferbulkitem_ddResponse param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(com.zenithbank.nfp.Fundtransferbulkitem_ddResponse.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(com.zenithbank.nfp.Fundtransfersingleitem_dc param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(com.zenithbank.nfp.Fundtransfersingleitem_dc.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(com.zenithbank.nfp.Fundtransfersingleitem_dcResponse param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(com.zenithbank.nfp.Fundtransfersingleitem_dcResponse.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(com.zenithbank.nfp.Balanceenquiry param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(com.zenithbank.nfp.Balanceenquiry.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(com.zenithbank.nfp.BalanceenquiryResponse param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(com.zenithbank.nfp.BalanceenquiryResponse.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(com.zenithbank.nfp.Fundtransfernotification_dd param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(com.zenithbank.nfp.Fundtransfernotification_dd.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(com.zenithbank.nfp.Fundtransfernotification_ddResponse param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(com.zenithbank.nfp.Fundtransfernotification_ddResponse.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(com.zenithbank.nfp.Txnstatusquerybulkitem param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(com.zenithbank.nfp.Txnstatusquerybulkitem.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(com.zenithbank.nfp.TxnstatusquerybulkitemResponse param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(com.zenithbank.nfp.TxnstatusquerybulkitemResponse.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(com.zenithbank.nfp.Txnstatusquerysingleitem param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(com.zenithbank.nfp.Txnstatusquerysingleitem.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(com.zenithbank.nfp.TxnstatusquerysingleitemResponse param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(com.zenithbank.nfp.TxnstatusquerysingleitemResponse.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(com.zenithbank.nfp.Fundtransferbulkitem_dc param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(com.zenithbank.nfp.Fundtransferbulkitem_dc.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(com.zenithbank.nfp.Fundtransferbulkitem_dcResponse param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(com.zenithbank.nfp.Fundtransferbulkitem_dcResponse.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(com.zenithbank.nfp.GetTransactionStatus param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(com.zenithbank.nfp.GetTransactionStatus.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(com.zenithbank.nfp.GetTransactionStatusResponse param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(com.zenithbank.nfp.GetTransactionStatusResponse.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(com.zenithbank.nfp.Nameenquirysingleitem param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(com.zenithbank.nfp.Nameenquirysingleitem.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(com.zenithbank.nfp.NameenquirysingleitemResponse param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(com.zenithbank.nfp.NameenquirysingleitemResponse.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(com.zenithbank.nfp.Nameenquirynotification param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(com.zenithbank.nfp.Nameenquirynotification.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(com.zenithbank.nfp.NameenquirynotificationResponse param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(com.zenithbank.nfp.NameenquirynotificationResponse.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(com.zenithbank.nfp.Fundtransfersingleitem_dd param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(com.zenithbank.nfp.Fundtransfersingleitem_dd.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
            private  org.apache.axiom.om.OMElement  toOM(com.zenithbank.nfp.Fundtransfersingleitem_ddResponse param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

            
                        try{
                             return param.getOMElement(com.zenithbank.nfp.Fundtransfersingleitem_ddResponse.MY_QNAME,
                                          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
                        } catch(org.apache.axis2.databinding.ADBException e){
                            throw org.apache.axis2.AxisFault.makeFault(e);
                        }
                    

            }
        
                    private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, com.zenithbank.nfp.NameenquirybulkitemResponse param, boolean optimizeContent, javax.xml.namespace.QName methodQName)
                        throws org.apache.axis2.AxisFault{
                      try{
                          org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
                           
                                    emptyEnvelope.getBody().addChild(param.getOMElement(com.zenithbank.nfp.NameenquirybulkitemResponse.MY_QNAME,factory));
                                

                         return emptyEnvelope;
                    } catch(org.apache.axis2.databinding.ADBException e){
                        throw org.apache.axis2.AxisFault.makeFault(e);
                    }
                    }
                    
                         private com.zenithbank.nfp.NameenquirybulkitemResponse wrapnameenquirybulkitem(){
                                com.zenithbank.nfp.NameenquirybulkitemResponse wrappedElement = new com.zenithbank.nfp.NameenquirybulkitemResponse();
                                return wrappedElement;
                         }
                    
                    private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, com.zenithbank.nfp.Fundtransfersingleitem2_dcResponse param, boolean optimizeContent, javax.xml.namespace.QName methodQName)
                        throws org.apache.axis2.AxisFault{
                      try{
                          org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
                           
                                    emptyEnvelope.getBody().addChild(param.getOMElement(com.zenithbank.nfp.Fundtransfersingleitem2_dcResponse.MY_QNAME,factory));
                                

                         return emptyEnvelope;
                    } catch(org.apache.axis2.databinding.ADBException e){
                        throw org.apache.axis2.AxisFault.makeFault(e);
                    }
                    }
                    
                         private com.zenithbank.nfp.Fundtransfersingleitem2_dcResponse wrapfundtransfersingleitem2_dc(){
                                com.zenithbank.nfp.Fundtransfersingleitem2_dcResponse wrappedElement = new com.zenithbank.nfp.Fundtransfersingleitem2_dcResponse();
                                return wrappedElement;
                         }
                    
                    private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, com.zenithbank.nfp.Fundtransfernotification_dcResponse param, boolean optimizeContent, javax.xml.namespace.QName methodQName)
                        throws org.apache.axis2.AxisFault{
                      try{
                          org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
                           
                                    emptyEnvelope.getBody().addChild(param.getOMElement(com.zenithbank.nfp.Fundtransfernotification_dcResponse.MY_QNAME,factory));
                                

                         return emptyEnvelope;
                    } catch(org.apache.axis2.databinding.ADBException e){
                        throw org.apache.axis2.AxisFault.makeFault(e);
                    }
                    }
                    
                         private com.zenithbank.nfp.Fundtransfernotification_dcResponse wrapfundtransfernotification_dc(){
                                com.zenithbank.nfp.Fundtransfernotification_dcResponse wrappedElement = new com.zenithbank.nfp.Fundtransfernotification_dcResponse();
                                return wrappedElement;
                         }
                    
                    private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, com.zenithbank.nfp.FundtransferbulkitemResponse param, boolean optimizeContent, javax.xml.namespace.QName methodQName)
                        throws org.apache.axis2.AxisFault{
                      try{
                          org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
                           
                                    emptyEnvelope.getBody().addChild(param.getOMElement(com.zenithbank.nfp.FundtransferbulkitemResponse.MY_QNAME,factory));
                                

                         return emptyEnvelope;
                    } catch(org.apache.axis2.databinding.ADBException e){
                        throw org.apache.axis2.AxisFault.makeFault(e);
                    }
                    }
                    
                         private com.zenithbank.nfp.FundtransferbulkitemResponse wrapfundtransferbulkitem(){
                                com.zenithbank.nfp.FundtransferbulkitemResponse wrappedElement = new com.zenithbank.nfp.FundtransferbulkitemResponse();
                                return wrappedElement;
                         }
                    
                    private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, com.zenithbank.nfp.Fundtransferbulkitem_ddResponse param, boolean optimizeContent, javax.xml.namespace.QName methodQName)
                        throws org.apache.axis2.AxisFault{
                      try{
                          org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
                           
                                    emptyEnvelope.getBody().addChild(param.getOMElement(com.zenithbank.nfp.Fundtransferbulkitem_ddResponse.MY_QNAME,factory));
                                

                         return emptyEnvelope;
                    } catch(org.apache.axis2.databinding.ADBException e){
                        throw org.apache.axis2.AxisFault.makeFault(e);
                    }
                    }
                    
                         private com.zenithbank.nfp.Fundtransferbulkitem_ddResponse wrapfundtransferbulkitem_dd(){
                                com.zenithbank.nfp.Fundtransferbulkitem_ddResponse wrappedElement = new com.zenithbank.nfp.Fundtransferbulkitem_ddResponse();
                                return wrappedElement;
                         }
                    
                    private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, com.zenithbank.nfp.Fundtransfersingleitem_dcResponse param, boolean optimizeContent, javax.xml.namespace.QName methodQName)
                        throws org.apache.axis2.AxisFault{
                      try{
                          org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
                           
                                    emptyEnvelope.getBody().addChild(param.getOMElement(com.zenithbank.nfp.Fundtransfersingleitem_dcResponse.MY_QNAME,factory));
                                

                         return emptyEnvelope;
                    } catch(org.apache.axis2.databinding.ADBException e){
                        throw org.apache.axis2.AxisFault.makeFault(e);
                    }
                    }
                    
                         private com.zenithbank.nfp.Fundtransfersingleitem_dcResponse wrapfundtransfersingleitem_dc(){
                                com.zenithbank.nfp.Fundtransfersingleitem_dcResponse wrappedElement = new com.zenithbank.nfp.Fundtransfersingleitem_dcResponse();
                                return wrappedElement;
                         }
                    
                    private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, com.zenithbank.nfp.BalanceenquiryResponse param, boolean optimizeContent, javax.xml.namespace.QName methodQName)
                        throws org.apache.axis2.AxisFault{
                      try{
                          org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
                           
                                    emptyEnvelope.getBody().addChild(param.getOMElement(com.zenithbank.nfp.BalanceenquiryResponse.MY_QNAME,factory));
                                

                         return emptyEnvelope;
                    } catch(org.apache.axis2.databinding.ADBException e){
                        throw org.apache.axis2.AxisFault.makeFault(e);
                    }
                    }
                    
                         private com.zenithbank.nfp.BalanceenquiryResponse wrapbalanceenquiry(){
                                com.zenithbank.nfp.BalanceenquiryResponse wrappedElement = new com.zenithbank.nfp.BalanceenquiryResponse();
                                return wrappedElement;
                         }
                    
                    private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, com.zenithbank.nfp.Fundtransfernotification_ddResponse param, boolean optimizeContent, javax.xml.namespace.QName methodQName)
                        throws org.apache.axis2.AxisFault{
                      try{
                          org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
                           
                                    emptyEnvelope.getBody().addChild(param.getOMElement(com.zenithbank.nfp.Fundtransfernotification_ddResponse.MY_QNAME,factory));
                                

                         return emptyEnvelope;
                    } catch(org.apache.axis2.databinding.ADBException e){
                        throw org.apache.axis2.AxisFault.makeFault(e);
                    }
                    }
                    
                         private com.zenithbank.nfp.Fundtransfernotification_ddResponse wrapfundtransfernotification_dd(){
                                com.zenithbank.nfp.Fundtransfernotification_ddResponse wrappedElement = new com.zenithbank.nfp.Fundtransfernotification_ddResponse();
                                return wrappedElement;
                         }
                    
                    private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, com.zenithbank.nfp.TxnstatusquerybulkitemResponse param, boolean optimizeContent, javax.xml.namespace.QName methodQName)
                        throws org.apache.axis2.AxisFault{
                      try{
                          org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
                           
                                    emptyEnvelope.getBody().addChild(param.getOMElement(com.zenithbank.nfp.TxnstatusquerybulkitemResponse.MY_QNAME,factory));
                                

                         return emptyEnvelope;
                    } catch(org.apache.axis2.databinding.ADBException e){
                        throw org.apache.axis2.AxisFault.makeFault(e);
                    }
                    }
                    
                         private com.zenithbank.nfp.TxnstatusquerybulkitemResponse wraptxnstatusquerybulkitem(){
                                com.zenithbank.nfp.TxnstatusquerybulkitemResponse wrappedElement = new com.zenithbank.nfp.TxnstatusquerybulkitemResponse();
                                return wrappedElement;
                         }
                    
                    private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, com.zenithbank.nfp.TxnstatusquerysingleitemResponse param, boolean optimizeContent, javax.xml.namespace.QName methodQName)
                        throws org.apache.axis2.AxisFault{
                      try{
                          org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
                           
                                    emptyEnvelope.getBody().addChild(param.getOMElement(com.zenithbank.nfp.TxnstatusquerysingleitemResponse.MY_QNAME,factory));
                                

                         return emptyEnvelope;
                    } catch(org.apache.axis2.databinding.ADBException e){
                        throw org.apache.axis2.AxisFault.makeFault(e);
                    }
                    }
                    
                         private com.zenithbank.nfp.TxnstatusquerysingleitemResponse wraptxnstatusquerysingleitem(){
                                com.zenithbank.nfp.TxnstatusquerysingleitemResponse wrappedElement = new com.zenithbank.nfp.TxnstatusquerysingleitemResponse();
                                return wrappedElement;
                         }
                    
                    private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, com.zenithbank.nfp.Fundtransferbulkitem_dcResponse param, boolean optimizeContent, javax.xml.namespace.QName methodQName)
                        throws org.apache.axis2.AxisFault{
                      try{
                          org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
                           
                                    emptyEnvelope.getBody().addChild(param.getOMElement(com.zenithbank.nfp.Fundtransferbulkitem_dcResponse.MY_QNAME,factory));
                                

                         return emptyEnvelope;
                    } catch(org.apache.axis2.databinding.ADBException e){
                        throw org.apache.axis2.AxisFault.makeFault(e);
                    }
                    }
                    
                         private com.zenithbank.nfp.Fundtransferbulkitem_dcResponse wrapfundtransferbulkitem_dc(){
                                com.zenithbank.nfp.Fundtransferbulkitem_dcResponse wrappedElement = new com.zenithbank.nfp.Fundtransferbulkitem_dcResponse();
                                return wrappedElement;
                         }
                    
                    private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, com.zenithbank.nfp.GetTransactionStatusResponse param, boolean optimizeContent, javax.xml.namespace.QName methodQName)
                        throws org.apache.axis2.AxisFault{
                      try{
                          org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
                           
                                    emptyEnvelope.getBody().addChild(param.getOMElement(com.zenithbank.nfp.GetTransactionStatusResponse.MY_QNAME,factory));
                                

                         return emptyEnvelope;
                    } catch(org.apache.axis2.databinding.ADBException e){
                        throw org.apache.axis2.AxisFault.makeFault(e);
                    }
                    }
                    
                         private com.zenithbank.nfp.GetTransactionStatusResponse wrapgetTransactionStatus(){
                                com.zenithbank.nfp.GetTransactionStatusResponse wrappedElement = new com.zenithbank.nfp.GetTransactionStatusResponse();
                                return wrappedElement;
                         }
                    
                    private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, com.zenithbank.nfp.NameenquirysingleitemResponse param, boolean optimizeContent, javax.xml.namespace.QName methodQName)
                        throws org.apache.axis2.AxisFault{
                      try{
                          org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
                           
                                    emptyEnvelope.getBody().addChild(param.getOMElement(com.zenithbank.nfp.NameenquirysingleitemResponse.MY_QNAME,factory));
                                

                         return emptyEnvelope;
                    } catch(org.apache.axis2.databinding.ADBException e){
                        throw org.apache.axis2.AxisFault.makeFault(e);
                    }
                    }
                    
                         private com.zenithbank.nfp.NameenquirysingleitemResponse wrapnameenquirysingleitem(){
                                com.zenithbank.nfp.NameenquirysingleitemResponse wrappedElement = new com.zenithbank.nfp.NameenquirysingleitemResponse();
                                return wrappedElement;
                         }
                    
                    private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, com.zenithbank.nfp.NameenquirynotificationResponse param, boolean optimizeContent, javax.xml.namespace.QName methodQName)
                        throws org.apache.axis2.AxisFault{
                      try{
                          org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
                           
                                    emptyEnvelope.getBody().addChild(param.getOMElement(com.zenithbank.nfp.NameenquirynotificationResponse.MY_QNAME,factory));
                                

                         return emptyEnvelope;
                    } catch(org.apache.axis2.databinding.ADBException e){
                        throw org.apache.axis2.AxisFault.makeFault(e);
                    }
                    }
                    
                         private com.zenithbank.nfp.NameenquirynotificationResponse wrapnameenquirynotification(){
                                com.zenithbank.nfp.NameenquirynotificationResponse wrappedElement = new com.zenithbank.nfp.NameenquirynotificationResponse();
                                return wrappedElement;
                         }
                    
                    private  org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, com.zenithbank.nfp.Fundtransfersingleitem_ddResponse param, boolean optimizeContent, javax.xml.namespace.QName methodQName)
                        throws org.apache.axis2.AxisFault{
                      try{
                          org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
                           
                                    emptyEnvelope.getBody().addChild(param.getOMElement(com.zenithbank.nfp.Fundtransfersingleitem_ddResponse.MY_QNAME,factory));
                                

                         return emptyEnvelope;
                    } catch(org.apache.axis2.databinding.ADBException e){
                        throw org.apache.axis2.AxisFault.makeFault(e);
                    }
                    }
                    
                         private com.zenithbank.nfp.Fundtransfersingleitem_ddResponse wrapfundtransfersingleitem_dd(){
                                com.zenithbank.nfp.Fundtransfersingleitem_ddResponse wrappedElement = new com.zenithbank.nfp.Fundtransfersingleitem_ddResponse();
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
        
                if (com.zenithbank.nfp.Nameenquirybulkitem.class.equals(type)){
                
                           return com.zenithbank.nfp.Nameenquirybulkitem.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (com.zenithbank.nfp.NameenquirybulkitemResponse.class.equals(type)){
                
                           return com.zenithbank.nfp.NameenquirybulkitemResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (com.zenithbank.nfp.Fundtransfersingleitem2_dc.class.equals(type)){
                
                           return com.zenithbank.nfp.Fundtransfersingleitem2_dc.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (com.zenithbank.nfp.Fundtransfersingleitem2_dcResponse.class.equals(type)){
                
                           return com.zenithbank.nfp.Fundtransfersingleitem2_dcResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (com.zenithbank.nfp.Fundtransfernotification_dc.class.equals(type)){
                
                           return com.zenithbank.nfp.Fundtransfernotification_dc.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (com.zenithbank.nfp.Fundtransfernotification_dcResponse.class.equals(type)){
                
                           return com.zenithbank.nfp.Fundtransfernotification_dcResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (com.zenithbank.nfp.Fundtransferbulkitem.class.equals(type)){
                
                           return com.zenithbank.nfp.Fundtransferbulkitem.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (com.zenithbank.nfp.FundtransferbulkitemResponse.class.equals(type)){
                
                           return com.zenithbank.nfp.FundtransferbulkitemResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (com.zenithbank.nfp.Fundtransferbulkitem_dd.class.equals(type)){
                
                           return com.zenithbank.nfp.Fundtransferbulkitem_dd.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (com.zenithbank.nfp.Fundtransferbulkitem_ddResponse.class.equals(type)){
                
                           return com.zenithbank.nfp.Fundtransferbulkitem_ddResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (com.zenithbank.nfp.Fundtransfersingleitem_dc.class.equals(type)){
                
                           return com.zenithbank.nfp.Fundtransfersingleitem_dc.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (com.zenithbank.nfp.Fundtransfersingleitem_dcResponse.class.equals(type)){
                
                           return com.zenithbank.nfp.Fundtransfersingleitem_dcResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (com.zenithbank.nfp.Balanceenquiry.class.equals(type)){
                
                           return com.zenithbank.nfp.Balanceenquiry.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (com.zenithbank.nfp.BalanceenquiryResponse.class.equals(type)){
                
                           return com.zenithbank.nfp.BalanceenquiryResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (com.zenithbank.nfp.Fundtransfernotification_dd.class.equals(type)){
                
                           return com.zenithbank.nfp.Fundtransfernotification_dd.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (com.zenithbank.nfp.Fundtransfernotification_ddResponse.class.equals(type)){
                
                           return com.zenithbank.nfp.Fundtransfernotification_ddResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (com.zenithbank.nfp.Txnstatusquerybulkitem.class.equals(type)){
                
                           return com.zenithbank.nfp.Txnstatusquerybulkitem.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (com.zenithbank.nfp.TxnstatusquerybulkitemResponse.class.equals(type)){
                
                           return com.zenithbank.nfp.TxnstatusquerybulkitemResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (com.zenithbank.nfp.Txnstatusquerysingleitem.class.equals(type)){
                
                           return com.zenithbank.nfp.Txnstatusquerysingleitem.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (com.zenithbank.nfp.TxnstatusquerysingleitemResponse.class.equals(type)){
                
                           return com.zenithbank.nfp.TxnstatusquerysingleitemResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (com.zenithbank.nfp.Fundtransferbulkitem_dc.class.equals(type)){
                
                           return com.zenithbank.nfp.Fundtransferbulkitem_dc.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (com.zenithbank.nfp.Fundtransferbulkitem_dcResponse.class.equals(type)){
                
                           return com.zenithbank.nfp.Fundtransferbulkitem_dcResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (com.zenithbank.nfp.GetTransactionStatus.class.equals(type)){
                
                           return com.zenithbank.nfp.GetTransactionStatus.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (com.zenithbank.nfp.GetTransactionStatusResponse.class.equals(type)){
                
                           return com.zenithbank.nfp.GetTransactionStatusResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (com.zenithbank.nfp.Nameenquirysingleitem.class.equals(type)){
                
                           return com.zenithbank.nfp.Nameenquirysingleitem.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (com.zenithbank.nfp.NameenquirysingleitemResponse.class.equals(type)){
                
                           return com.zenithbank.nfp.NameenquirysingleitemResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (com.zenithbank.nfp.Nameenquirynotification.class.equals(type)){
                
                           return com.zenithbank.nfp.Nameenquirynotification.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (com.zenithbank.nfp.NameenquirynotificationResponse.class.equals(type)){
                
                           return com.zenithbank.nfp.NameenquirynotificationResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (com.zenithbank.nfp.Fundtransfersingleitem_dd.class.equals(type)){
                
                           return com.zenithbank.nfp.Fundtransfersingleitem_dd.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

                }
           
                if (com.zenithbank.nfp.Fundtransfersingleitem_ddResponse.class.equals(type)){
                
                           return com.zenithbank.nfp.Fundtransfersingleitem_ddResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
                    

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
    