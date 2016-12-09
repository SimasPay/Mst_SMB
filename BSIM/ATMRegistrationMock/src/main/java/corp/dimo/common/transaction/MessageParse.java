package corp.dimo.common.transaction;

import iso8583.jPos.common.utils.PackagerBuilder;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOPackager;

import corp.dimo.common.utils.ConfigConstan;

public class MessageParse {

	public static String getTransactionStatus(String responseMsg){
		
		ISOMsg isoMsg = new ISOMsg();
		ISOPackager packager = PackagerBuilder.customPacakager(ConfigConstan.packager_iso87ascii);
		
		isoMsg.setPackager(packager);
		
		try {
			isoMsg.unpack(responseMsg.getBytes());
		} catch (ISOException e) {
			e.printStackTrace();
		}
		
		return isoMsg.getString(39);
	}
}
