
/**
 * ServiceSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.1  Built on : Aug 31, 2011 (12:22:40 CEST)
 */
    package org.tempuri.pos_txns.type;

    /**
     *  ServiceSkeleton java skeleton for the axisService
     */
    public class ServiceSkeleton{
    	
        
         
        /**
         * Auto generated method signature
         * 
                                     * @param getPOS_TXNS 
             * @return getPOS_TXNSResponse 
         */
        
                 public org.tempuri.pos_txns.type.GetPOS_TXNSResponse getPOS_TXNS
                  (
                  org.tempuri.pos_txns.type.GetPOS_TXNS getPOS_TXNS
                  )
            {
                //TODO : fill this with the necessary business logic
                throw new  java.lang.UnsupportedOperationException("Please implement " + this.getClass().getName() + "#getPOS_TXNS");
        }
     
         
        /**
         * Auto generated method signature
         * 
                                     * @param savePOS_TXNS 
             * @return savePOS_TXNSResponse 
         */
        
                 public org.tempuri.pos_txns.type.SavePOS_TXNSResponse savePOS_TXNS
                  (
                  org.tempuri.pos_txns.type.SavePOS_TXNS savePOS_TXNS
                  )
            {
                //TODO : fill this with the necessary business logic
//                throw new  java.lang.UnsupportedOperationException("Please implement " + this.getClass().getName() + "#savePOS_TXNS");
                	 System.out.println("DSTV WEB Service :: Amount = "+savePOS_TXNS.getAmount());
                	 SavePOS_TXNSResponse response = new SavePOS_TXNSResponse();
                	 
                	 if(Double.valueOf(savePOS_TXNS.getAmount()) > 0){
                		 response.setSavePOS_TXNSResult("00");
                	 }
                	 else{
                		 response.setSavePOS_TXNSResult("01");
                	 }
                	 
                	 if(savePOS_TXNS.getCustServID().startsWith("54321")){
                		 response.setSavePOS_TXNSResult("01");
                	 }
                	 System.out.println("DSTV WEB Service :: response.setSavePOS_TXNSResult = "+response.getSavePOS_TXNSResult());
                	 return response;
        }
     
    }
    