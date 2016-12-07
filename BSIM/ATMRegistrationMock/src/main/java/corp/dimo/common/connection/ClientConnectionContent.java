package corp.dimo.common.connection;

import corp.dimo.common.models.ConnectionDetails;

public class ClientConnectionContent {

	public String generateContent(ConnectionDetails connectionDetails){
		return connectionDetails.getConnName()+"#"+
				connectionDetails.getPackagerValue()+""+
				connectionDetails.getChannelType()+"#"+
				connectionDetails.getIsoHeader()+"#"+
				connectionDetails.getHost()+"#"+
				connectionDetails.getPort()+"#"+
				connectionDetails.getRequestListenerClassValue()+"";
	}
}
