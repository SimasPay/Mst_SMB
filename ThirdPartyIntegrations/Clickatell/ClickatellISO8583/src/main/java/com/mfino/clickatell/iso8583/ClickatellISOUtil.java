package com.mfino.clickatell.iso8583;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;

import com.mfino.mce.iso.jpos.util.BaseISOUtil;
import com.mfino.mce.iso.jpos.util.ISOUtil;

public class ClickatellISOUtil extends BaseISOUtil implements ISOUtil {
	public ISOMsg getEchoMessage() throws ISOException 
	{
		 ISOMsg isoMsg = new ISOMsg();
		 try
		 {
          isoMsg.setMTI("0800");
          isoMsg.set(11,"999999"); //System Audit Number.
		  isoMsg.set(18,"Echo"); //Client TransactionID.
          isoMsg.set(70,"301"); //Network Management Information Code.
         }catch (ISOException ex) {
				log.error("BillPaymentToClickatellProcessor :Error",ex);
			}
		return isoMsg;
		 
	
  }
	@Override
	public ISOMsg getSignOffMessage() {
		// TODO Auto-generated method stub
				return null;
	}

	@Override
	public ISOMsg getSignOnMessage() throws ISOException {
		 ISOMsg isoMsg = new ISOMsg();
		 try
		 {
          isoMsg.setMTI("0800");
          isoMsg.set(11,"999998"); //System Audit Number.
		  isoMsg.set(18,"EchoasSignOn"); //Client TransactionID.
          isoMsg.set(70,"301"); //Network Management Information Code.
         }catch (ISOException ex) {
				log.error("BillPaymentToClickatellProcessor :Error",ex);
			}
		return isoMsg;
	}
	
	public String getSignOnSuccessResponseCode() {
		return "0000";
	}
	
	public String getEchoSuccessResponseCode() {
		return "0000";
	}
	
	public String getSuccessResponseCode() {
		return "0000";
	}

}
