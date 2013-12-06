
/**
 * ExtensionMapper.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.2  Built on : Apr 17, 2012 (05:34:40 IST)
 */

        
            package com.ucitech.neptune;
        
            /**
            *  ExtensionMapper class
            */
            @SuppressWarnings({"unchecked","unused"})
        
        public  class ExtensionMapper{

          public static java.lang.Object getTypeObject(java.lang.String namespaceURI,
                                                       java.lang.String typeName,
                                                       javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception{

              
                  if (
                  "http://neptune.ucitech.com/".equals(namespaceURI) &&
                  "queryMerchantProductsResponse".equals(typeName)){
                   
                            return  com.ucitech.neptune.QueryMerchantProductsResponse.Factory.parse(reader);
                        

                  }

              
                  if (
                  "http://neptune.ucitech.com/".equals(namespaceURI) &&
                  "addCustomerProductByMerchant".equals(typeName)){
                   
                            return  com.ucitech.neptune.AddCustomerProductByMerchant.Factory.parse(reader);
                        

                  }

              
                  if (
                  "http://neptune.ucitech.com/".equals(namespaceURI) &&
                  "delCustomerProductsByMerchantResponse".equals(typeName)){
                   
                            return  com.ucitech.neptune.DelCustomerProductsByMerchantResponse.Factory.parse(reader);
                        

                  }

              
                  if (
                  "http://neptune.ucitech.com/".equals(namespaceURI) &&
                  "queryMerchantTransactionResponse".equals(typeName)){
                   
                            return  com.ucitech.neptune.QueryMerchantTransactionResponse.Factory.parse(reader);
                        

                  }

              
                  if (
                  "http://neptune.ucitech.com/".equals(namespaceURI) &&
                  "queryMerchantsResponse".equals(typeName)){
                   
                            return  com.ucitech.neptune.QueryMerchantsResponse.Factory.parse(reader);
                        

                  }

              
                  if (
                  "http://neptune.ucitech.com/".equals(namespaceURI) &&
                  "login".equals(typeName)){
                   
                            return  com.ucitech.neptune.Login.Factory.parse(reader);
                        

                  }

              
                  if (
                  "http://neptune.ucitech.com/".equals(namespaceURI) &&
                  "queryCustomerByMerchant".equals(typeName)){
                   
                            return  com.ucitech.neptune.QueryCustomerByMerchant.Factory.parse(reader);
                        

                  }

              
                  if (
                  "http://neptune.ucitech.com/".equals(namespaceURI) &&
                  "queryCustomerByMerchantResponse".equals(typeName)){
                   
                            return  com.ucitech.neptune.QueryCustomerByMerchantResponse.Factory.parse(reader);
                        

                  }

              
                  if (
                  "http://neptune.ucitech.com/".equals(namespaceURI) &&
                  "queryMerchantProducts".equals(typeName)){
                   
                            return  com.ucitech.neptune.QueryMerchantProducts.Factory.parse(reader);
                        

                  }

              
                  if (
                  "http://neptune.ucitech.com/".equals(namespaceURI) &&
                  "item".equals(typeName)){
                   
                            return  com.ucitech.neptune.Item.Factory.parse(reader);
                        

                  }

              
                  if (
                  "http://neptune.ucitech.com/".equals(namespaceURI) &&
                  "order".equals(typeName)){
                   
                            return  com.ucitech.neptune.Order.Factory.parse(reader);
                        

                  }

              
                  if (
                  "http://neptune.ucitech.com/".equals(namespaceURI) &&
                  "addCustomerProductsByMerchant".equals(typeName)){
                   
                            return  com.ucitech.neptune.AddCustomerProductsByMerchant.Factory.parse(reader);
                        

                  }

              
                  if (
                  "http://neptune.ucitech.com/".equals(namespaceURI) &&
                  "delCustomerProductsByMerchant".equals(typeName)){
                   
                            return  com.ucitech.neptune.DelCustomerProductsByMerchant.Factory.parse(reader);
                        

                  }

              
                  if (
                  "http://neptune.ucitech.com/".equals(namespaceURI) &&
                  "delCustomerProductByMerchantResponse".equals(typeName)){
                   
                            return  com.ucitech.neptune.DelCustomerProductByMerchantResponse.Factory.parse(reader);
                        

                  }

              
                  if (
                  "http://neptune.ucitech.com/".equals(namespaceURI) &&
                  "delCustomerProductByMerchant".equals(typeName)){
                   
                            return  com.ucitech.neptune.DelCustomerProductByMerchant.Factory.parse(reader);
                        

                  }

              
                  if (
                  "http://neptune.ucitech.com/".equals(namespaceURI) &&
                  "proxyResponse".equals(typeName)){
                   
                            return  com.ucitech.neptune.ProxyResponse.Factory.parse(reader);
                        

                  }

              
                  if (
                  "http://neptune.ucitech.com/".equals(namespaceURI) &&
                  "loginResponse".equals(typeName)){
                   
                            return  com.ucitech.neptune.LoginResponse.Factory.parse(reader);
                        

                  }

              
                  if (
                  "http://neptune.ucitech.com/".equals(namespaceURI) &&
                  "queryMerchants".equals(typeName)){
                   
                            return  com.ucitech.neptune.QueryMerchants.Factory.parse(reader);
                        

                  }

              
                  if (
                  "http://neptune.ucitech.com/".equals(namespaceURI) &&
                  "queryMerchantTransaction".equals(typeName)){
                   
                            return  com.ucitech.neptune.QueryMerchantTransaction.Factory.parse(reader);
                        

                  }

              
                  if (
                  "http://neptune.ucitech.com/".equals(namespaceURI) &&
                  "addCustomerProductsByMerchantResponse".equals(typeName)){
                   
                            return  com.ucitech.neptune.AddCustomerProductsByMerchantResponse.Factory.parse(reader);
                        

                  }

              
                  if (
                  "http://neptune.ucitech.com/".equals(namespaceURI) &&
                  "addCustomerProductByMerchantResponse".equals(typeName)){
                   
                            return  com.ucitech.neptune.AddCustomerProductByMerchantResponse.Factory.parse(reader);
                        

                  }

              
             throw new org.apache.axis2.databinding.ADBException("Unsupported type " + namespaceURI + " " + typeName);
          }

        }
    