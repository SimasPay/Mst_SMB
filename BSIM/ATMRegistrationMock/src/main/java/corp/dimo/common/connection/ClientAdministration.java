package corp.dimo.common.connection;

import corp.dimo.common.utils.ConfigConstan;

public class ClientAdministration {
	
	public String startConnection(String connName, String packagerValue, String channelType, String isoHeader, String host, String port, String requestListenerClassValue){
		System.out.println("startConnection");
		String state = ClientConnectionManagement.getInstance().decisionOfConnection(connName);
		
		if (state.equals(ConfigConstan.connConnected)) {
			System.out.println("startConnection connConnected");
			return state;
		} else {
			ClientConnectionManagement.getInstance().createConnection(connName, packagerValue, channelType, isoHeader, host, port, requestListenerClassValue);
			
			try {
				Thread.sleep(ConfigConstan.sleep);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("startConnection "+ClientConnectionManagement.getInstance().decisionOfConnection(connName));
			return ClientConnectionManagement.getInstance().decisionOfConnection(connName);
		}
		
		
	}
	
	//dnfdnfdsk
	public void stopConnection(String connName){
		ClientConnectionManagement.getInstance().stopConnection(connName);
	}
	
	public void removeConnection(String connName){
		ClientConnectionManagement.getInstance().destroyConnection(connName);
	}
	
	public String checkConnection(String connName){
		return ClientConnectionManagement.getInstance().decisionOfConnection(connName);
	}

}
