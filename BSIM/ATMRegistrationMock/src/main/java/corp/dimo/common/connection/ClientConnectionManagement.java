package corp.dimo.common.connection;

import corp.dimo.common.utils.ConfigConstan;
import iso8583.jPos.ism.client.ConnectionManagement;

public class ClientConnectionManagement {

	private static ClientConnectionManagement instance;
	
	public static ClientConnectionManagement getInstance(){
		if (instance!=null) {
			return instance;
		} else {
			synchronized (ClientConnectionManagement.class) {
				if (instance!=null) {
					return instance;
				}
				
				try {
					instance = new ClientConnectionManagement();
					return instance;
				} catch (Exception e) {}
			}
		}
		return null;
	}
	
	protected void createConnection(String connName, String packagerValue, String channelType, String isoHeader, String host, String port, String requestListenerClassValue){
		new ConnectionManagement().createConnection(connName, host, Integer.parseInt(port), packagerValue, channelType, isoHeader, requestListenerClassValue);
	}
	
	protected void stopConnection(String connName){
		new ConnectionManagement().stopConnection(connName);
	}
	
	protected void destroyConnection(String connName){
		new ConnectionManagement().destroyConnection(connName);
	}
	
	protected String decisionOfConnection(String connName){
		System.out.println("decisionOfConnection");
		ConnectionManagement connectionManagement = new ConnectionManagement();
		Boolean decision = connectionManagement.checkConnection(connName);
		if (decision==null) {
			System.out.println("decision null");
			return ConfigConstan.connNotFound;
		} else if (decision==false){
			connectionManagement.stopConnection(connName);
			connectionManagement.destroyConnection(connName);
			System.out.println("decision connNotConnected");
			return ConfigConstan.connNotConnected;
		} else {
			System.out.println("decision connConnected");
			return ConfigConstan.connConnected;
		}
	}
	
}
