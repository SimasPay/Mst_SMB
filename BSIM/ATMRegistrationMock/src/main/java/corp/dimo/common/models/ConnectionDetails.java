package corp.dimo.common.models;

public class ConnectionDetails {

	String connName;
	String packagerValue;
	String channelType;
	String isoHeader;
	String host;
	String port;
	String requestListenerClassValue;
	String state;
	
	public String getConnName() {
		return connName;
	}
	public void setConnName(String connName) {
		this.connName = connName;
	}
	public String getPackagerValue() {
		return packagerValue;
	}
	public void setPackagerValue(String packagerValue) {
		this.packagerValue = packagerValue;
	}
	public String getChannelType() {
		return channelType;
	}
	public void setChannelType(String channelType) {
		this.channelType = channelType;
	}
	public String getIsoHeader() {
		return isoHeader;
	}
	public void setIsoHeader(String isoHeader) {
		this.isoHeader = isoHeader;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}
	public String getRequestListenerClassValue() {
		return requestListenerClassValue;
	}
	public void setRequestListenerClassValue(String requestListenerClassValue) {
		this.requestListenerClassValue = requestListenerClassValue;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
}
