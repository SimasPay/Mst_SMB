package corp.dimo.test;

import corp.dimo.common.utils.ConfigConstan;
import iso8583.jPos.ism.server.ConnectionManagement;

public class ISOServerSimulator {

	public static void main(String[] args){
		ConnectionManagement connectionManagement = new ConnectionManagement();
		connectionManagement.startListenerAutoSuccessMock("localhost", 12345, ConfigConstan.packager_iso87ascii, "ASCIIChannel", "ISO123456789", "100", "10", null);
	}
}
