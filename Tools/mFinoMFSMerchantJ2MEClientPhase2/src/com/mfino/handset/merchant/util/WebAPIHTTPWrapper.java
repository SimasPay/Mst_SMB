package com.mfino.handset.merchant.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

public class WebAPIHTTPWrapper {
	
	private MfinoConfigData mFinoConfigData;
	
	public WebAPIHTTPWrapper(MfinoConfigData mFinoConfigData){
		this.mFinoConfigData = mFinoConfigData;
	}
	
	public String getUrl(){
		String requestUrl = "";
		if("Activation".equals(mFinoConfigData.getServiceName())){
			
			mFinoConfigData.setMode("1");
			
			requestUrl = MfinoConfigData.webAPIUrl 
						+ "?channelId="+mFinoConfigData.getChannelId() 
						+ "&contactNumber=" + mFinoConfigData.getSourceMdn()
						+ "&mode=" + mFinoConfigData.getMode()
						+ "&secretAnswer=" + mFinoConfigData.getSecretAnswer()
						+ "&serviceName=" + mFinoConfigData.getServiceName()
						+ "&sourceMDN=" + mFinoConfigData.getSourceMdn()
						+ "&sourcePIN=" + mFinoConfigData.getSourcePin();
				
		}else if("resetPin".equals(mFinoConfigData.getServiceName())){
			
			mFinoConfigData.setMode("1");
			
			requestUrl = MfinoConfigData.webAPIUrl 
						+ "?channelId="+mFinoConfigData.getChannelId() 
						+ "&mode=" + mFinoConfigData.getMode()
						+ "&secretAnswer=" + mFinoConfigData.getSecretAnswer()
						+ "&serviceName=" + mFinoConfigData.getServiceName()
						+ "&sourceMDN=" + mFinoConfigData.getSourceMdn()
						+ "&newPIN=" + mFinoConfigData.getNewPin();
			
		}else if("changePin".equals(mFinoConfigData.getServiceName())){
			
			mFinoConfigData.setMode("1");
			
			requestUrl = MfinoConfigData.webAPIUrl 
						+ "?channelId="+mFinoConfigData.getChannelId() 
						+ "&mode=" + mFinoConfigData.getMode()
						+ "&oldPIN=" + mFinoConfigData.getOldPin()
						+ "&serviceName=" + mFinoConfigData.getServiceName()
						+ "&sourceMDN=" + mFinoConfigData.getSourceMdn()
						+ "&newPIN=" + mFinoConfigData.getNewPin();
			
		}else if("recharge".equals(mFinoConfigData.getServiceName())){
			
			mFinoConfigData.setMode("3");
			
			requestUrl = MfinoConfigData.webAPIUrl 
						+ "?channelId="+mFinoConfigData.getChannelId() 
						+ "&mode=" + mFinoConfigData.getMode()
						+ "&serviceName=" + mFinoConfigData.getServiceName()
						+ "&sourceMDN=" + mFinoConfigData.getSourceMdn()
						+ "&sourcePIN=" + mFinoConfigData.getSourcePin()
						+ "&amount=" + mFinoConfigData.getAmount()
						+ "&bankID=" + mFinoConfigData.getBankId()
						+ "&destMDN=" + mFinoConfigData.getDestinationMdn()
						+ "&sourcePocketCode=" + mFinoConfigData.getSourcePocketCode()
						+ "&bucketType=" + mFinoConfigData.getBucketType();
			
		} else if("svaemoneycheckBalance".equals(mFinoConfigData.getServiceName())){
			
			mFinoConfigData.setMode("3");
			
			requestUrl = MfinoConfigData.webAPIUrl 
						+ "?channelId="+mFinoConfigData.getChannelId() 
						+ "&mode=" + mFinoConfigData.getMode()
						+ "&serviceName=" + mFinoConfigData.getServiceName()
						+ "&sourceMDN=" + mFinoConfigData.getSourceMdn()
						+ "&sourcePIN=" + mFinoConfigData.getSourcePin()
						+ "&bankID=" + mFinoConfigData.getBankId()
						+ "&sourcePocketCode=" + mFinoConfigData.getSourcePocketCode();
			
		} else if("bankCheckBalance".equals(mFinoConfigData.getServiceName())){
			
			mFinoConfigData.setMode("3");
			
			requestUrl = MfinoConfigData.webAPIUrl 
						+ "?channelId="+mFinoConfigData.getChannelId() 
						+ "&mode=" + mFinoConfigData.getMode()
						+ "&serviceName=" + mFinoConfigData.getServiceName()
						+ "&sourceMDN=" + mFinoConfigData.getSourceMdn()
						+ "&sourcePIN=" + mFinoConfigData.getSourcePin()
						+ "&bankID=" + mFinoConfigData.getBankId()
						+ "&sourcePocketCode=" + mFinoConfigData.getSourcePocketCode();
			
		} else if("svaemoneyHistory".equals(mFinoConfigData.getServiceName())){
			
			mFinoConfigData.setMode("3");
			
			requestUrl = MfinoConfigData.webAPIUrl 
						+ "?channelId="+mFinoConfigData.getChannelId() 
						+ "&mode=" + mFinoConfigData.getMode()
						+ "&serviceName=" + mFinoConfigData.getServiceName()
						+ "&sourceMDN=" + mFinoConfigData.getSourceMdn()
						+ "&sourcePIN=" + mFinoConfigData.getSourcePin()
						+ "&sourcePocketCode=" + mFinoConfigData.getSourcePocketCode();
			
		} else if("bankHistory".equals(mFinoConfigData.getServiceName())){
			
			mFinoConfigData.setMode("3");
			
			requestUrl = MfinoConfigData.webAPIUrl 
						+ "?channelId="+mFinoConfigData.getChannelId() 
						+ "&mode=" + mFinoConfigData.getMode()
						+ "&serviceName=" + mFinoConfigData.getServiceName()
						+ "&sourceMDN=" + mFinoConfigData.getSourceMdn()
						+ "&sourcePIN=" + mFinoConfigData.getSourcePin()
						+ "&sourcePocketCode=" + mFinoConfigData.getSourcePocketCode();
			
		} else if("transferInquiry".equals(mFinoConfigData.getServiceName())){
			
			mFinoConfigData.setMode("3");
			
			requestUrl = MfinoConfigData.webAPIUrl 
						+ "?channelId="+mFinoConfigData.getChannelId() 
						+ "&mode=" + mFinoConfigData.getMode()
						+ "&serviceName=" + mFinoConfigData.getServiceName()
						+ "&sourceMDN=" + mFinoConfigData.getSourceMdn()
						+ "&sourcePIN=" + mFinoConfigData.getSourcePin()
						+ "&sourcePocketCode=" + mFinoConfigData.getSourcePocketCode()
						+ "&destMDN=" + mFinoConfigData.getDestinationMdn()
						+ "&bankID=" + mFinoConfigData.getBankId()
						+ "&amount=" + mFinoConfigData.getAmount()
						+ "&destPocketCode="+mFinoConfigData.getDestinationPocketCode();
			
		} else if("transfer".equals(mFinoConfigData.getServiceName())){
			
			mFinoConfigData.setMode("3");
			mFinoConfigData.setConfirmed("true");
			
			requestUrl = MfinoConfigData.webAPIUrl 
						+ "?channelId="+mFinoConfigData.getChannelId() 
						+ "&mode=" + mFinoConfigData.getMode()
						+ "&serviceName=" + mFinoConfigData.getServiceName()
						+ "&sourceMDN=" + mFinoConfigData.getSourceMdn()
						//+ "&sourcePIN=" + mFinoConfigData.getSourcePin()
						+ "&sourcePocketCode=" + mFinoConfigData.getSourcePocketCode()
						+ "&destMDN=" + mFinoConfigData.getDestinationMdn()
						+ "&bankID=" + mFinoConfigData.getBankId()
						//+ "&amount=" + mFinoConfigData.getAmount()
						+ "&confirmed=" + mFinoConfigData.getConfirmed()
						+ "&parentTxnID=" + mFinoConfigData.getParentTxnId()
						+ "&transferId=" + mFinoConfigData.getTransferId()
						+ "&destPocketCode="+mFinoConfigData.getDestinationPocketCode();
			
		} else if("transfer".equals(mFinoConfigData.getServiceName())){
			
			mFinoConfigData.setMode("3");
			mFinoConfigData.setConfirmed("true");
			
			requestUrl = MfinoConfigData.webAPIUrl 
						+ "?channelId="+mFinoConfigData.getChannelId() 
						+ "&mode=" + mFinoConfigData.getMode()
						+ "&serviceName=" + mFinoConfigData.getServiceName()
						+ "&sourceMDN=" + mFinoConfigData.getSourceMdn()
						//+ "&sourcePIN=" + mFinoConfigData.getSourcePin()
						+ "&sourcePocketCode=" + mFinoConfigData.getSourcePocketCode()
						+ "&destMDN=" + mFinoConfigData.getDestinationMdn()
						+ "&bankID=" + mFinoConfigData.getBankId()
						//+ "&amount=" + mFinoConfigData.getAmount()
						+ "&confirmed=" + mFinoConfigData.getConfirmed()
						+ "&parentTxnID=" + mFinoConfigData.getParentTxnId()
						+ "&transferId=" + mFinoConfigData.getTransferId()
						+ "&destPocketCode="+mFinoConfigData.getDestinationPocketCode();
			
		} else if("billPaymentInquiry".equals(mFinoConfigData.getServiceName())){
			
			mFinoConfigData.setMode("4");
		
			requestUrl = MfinoConfigData.webAPIUrl 
						+ "?channelId="+mFinoConfigData.getChannelId() 
						+ "&mode=" + mFinoConfigData.getMode()
						+ "&serviceName=" + mFinoConfigData.getServiceName()
						+ "&sourceMDN=" + mFinoConfigData.getSourceMdn()
						+ "&sourcePIN=" + mFinoConfigData.getSourcePin()
						+ "&sourcePocketCode=" + mFinoConfigData.getSourcePocketCode()
						+ "&billerName=" + mFinoConfigData.getBillerName()
						+ "&customerID=" + mFinoConfigData.getCustomerId();
			
		} else if("billPayment".equals(mFinoConfigData.getServiceName())){
			
			mFinoConfigData.setMode("4");
			mFinoConfigData.setConfirmed("true");
			
			requestUrl = MfinoConfigData.webAPIUrl 
						+ "?channelId="+mFinoConfigData.getChannelId() 
						+ "&mode=" + mFinoConfigData.getMode()
						+ "&serviceName=" + mFinoConfigData.getServiceName()
						+ "&sourceMDN=" + mFinoConfigData.getSourceMdn()
						+ "&sourcePIN=" + mFinoConfigData.getSourcePin()
						+ "&sourcePocketCode=" + mFinoConfigData.getSourcePocketCode()
						+ "&amount=" + mFinoConfigData.getAmount()
						+ "&confirmed=" + mFinoConfigData.getConfirmed()
						+ "&parentTrxID=" + mFinoConfigData.getParentTxnId()
						+ "&transferID=" + mFinoConfigData.getTransferId()
						+ "&customerID=" + mFinoConfigData.getCustomerId()
						+ "&billerName=" + mFinoConfigData.getBillerName()
						+ "&billDetails=" + mFinoConfigData.getBillerName();
			
		} else if("shareLoad".equals(mFinoConfigData.getServiceName())){
			
			mFinoConfigData.setMode("1");
			
			requestUrl = MfinoConfigData.webAPIUrl 
						+ "?channelId="+mFinoConfigData.getChannelId() 
						+ "&mode=" + mFinoConfigData.getMode()
						+ "&serviceName=" + mFinoConfigData.getServiceName()
						+ "&sourceMDN=" + mFinoConfigData.getSourceMdn()
						+ "&sourcePIN=" + mFinoConfigData.getSourcePin()
						+ "&amount=" + mFinoConfigData.getAmount()
						+ "&destMDN=" + mFinoConfigData.getDestinationMdn();
			
		} else if("checkBalance".equals(mFinoConfigData.getServiceName())){
			
			mFinoConfigData.setMode("2");
			
			requestUrl = MfinoConfigData.webAPIUrl 
						+ "?channelId="+mFinoConfigData.getChannelId() 
						+ "&mode=" + mFinoConfigData.getMode()
						+ "&serviceName=" + mFinoConfigData.getServiceName()
						+ "&sourceMDN=" + mFinoConfigData.getSourceMdn()
						+ "&sourcePIN=" + mFinoConfigData.getSourcePin();
			
		}

		return requestUrl;
	}
	
	public ResponseData getResponseData()
	{
		System.out.println("WebAPIHTTPWrapper getResponseData() for URL "+getUrl());
		
		ResponseData responseData = new ResponseData();
		
        HttpConnection http = null;
        InputStream iStrm = null;
		
        try {
			http = (HttpConnection) Connector.open(getUrl());
	        http.setRequestMethod(HttpConnection.GET);
	        http.setRequestProperty("User-Agent", "Profile/MIDP-1.0 Configuration/CLDC-1.0");
	        
	        if (http.getResponseCode() == HttpConnection.HTTP_OK) {
                iStrm = http.openInputStream();  // open and return an input stream for connection
                int length = (int) http.getLength();
                System.out.println("WebAPIHTTPWrapper length="+length);
                
                String responseString = "";
                
                if (length != -1) {
                    byte serverData[] = new byte[length];
                    iStrm.read(serverData);
                    responseString = new String(serverData);
                } else 
                {
                    ByteArrayOutputStream bStrm = new ByteArrayOutputStream();

                    int ch;
                    while ((ch = iStrm.read()) != -1) {
                        bStrm.write(ch);
                    }

                    responseString = new String(bStrm.toByteArray());
                    bStrm.close();
                }
                
                System.out.println("File Contents: " + responseString);
                if (responseString.indexOf("<body>") != -1 && responseString.indexOf("</body>") != -1) {
                	responseString = responseString.substring(responseString.indexOf("<body>") + "<body>".length(), responseString.indexOf("</body>"));
                }
                
                XMLParser parser = new XMLParser();
                responseData = parser.parse(responseString);
	        }
	        else{
	        	responseData.setMsgCode("101010");
	        	responseData.setMsg("HTTP Error: Error connecting to webapi, Please check your connectivity");
	        }
	        
		} catch (IOException e) {
        	responseData.setMsgCode("101010");
        	responseData.setMsg("HTTP Error: Error connecting to webapi, Please check your connectivity");

        	e.printStackTrace();
		} catch (Exception e) {
        	responseData.setMsgCode("202020");
        	responseData.setMsg("Generic Error: Please contact mFino support team");
			
			e.printStackTrace();
		} finally {
			try{
	            if (iStrm != null) {
	            	iStrm.close();
	            }
	            if (http != null) {
	                http.close();
	            }
			} catch(IOException ioe){
				ioe.printStackTrace();
			}
        }
        
        return responseData;
	}
}
