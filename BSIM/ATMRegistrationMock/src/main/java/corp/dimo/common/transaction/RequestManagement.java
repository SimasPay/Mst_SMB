package corp.dimo.common.transaction;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;

import corp.dimo.common.models.MessageDetail;
import iso8583.jPos.common.utils.Utility;
import iso8583.jPos.ism.client.SenderManagement;

public class RequestManagement {
	
	public MessageDetail sendreceiveMessage(ISOMsg send, int timeout, String connectionName){
		MessageDetail messageDetail = new MessageDetail();
		SenderManagement senderManagement = new SenderManagement();
		try {
			messageDetail.setIsoRequest(new String(send.pack()));
			messageDetail.setIsoRequestParse(Utility.parseISOasString(send));
			
			ISOMsg receive=senderManagement.synchronousRequest(send, timeout, connectionName);
			if (receive==null) {
				messageDetail.setIsoResponse("Timeout...");
				messageDetail.setIsoResponseParse(null);
			} else {
				messageDetail.setIsoResponse(new String(receive.pack()));
				messageDetail.setIsoResponseParse(Utility.parseISOasString(receive));
			}
		} catch (ISOException e) {
			e.printStackTrace();
		}
		
		return messageDetail;
	}

}
