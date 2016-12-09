package corp.dimo.simulate.handler;

import iso8583.jPos.common.utils.Utility;
import iso8583.jPos.ism.server.ConnectionManagement;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

import org.primefaces.context.RequestContext;

import corp.dimo.common.models.ConnectionDetails;
import corp.dimo.common.models.Logs;
import corp.dimo.common.models.MessageDetail;
import corp.dimo.common.utils.ConfigConstan;
import corp.dimo.simulate.service.LogsService;
import file.processor.framework.file.FileManagement;

@ManagedBean
public class MFSServerSimulator {
	
	private List<Logs> logs;
	
	@ManagedProperty("#{logsService}")
	private LogsService logService;
	
	private MessageDetail messageDetail;
	private ConnectionDetails connectionDetails;

	@PostConstruct
    public void init() {
		logs = logService.retriveLogs();
	}

	public void mfsConnectionInfo(){
		FileManagement fileManagement = new FileManagement();
		
		Boolean existConfig = fileManagement.checkSpecificFile("");
		
		if (existConfig==true) {
			String contentOfConnection = fileManagement.readFileAsString("path", "fileName");
			String[] valueOfConnection = contentOfConnection.split("#");
			
			connectionDetails.setConnName(valueOfConnection[0]);
			connectionDetails.setPackagerValue(valueOfConnection[1]);
			connectionDetails.setChannelType(valueOfConnection[2]);
			connectionDetails.setIsoHeader(valueOfConnection[3]);
			connectionDetails.setHost(valueOfConnection[4]);
			connectionDetails.setPort(valueOfConnection[5]);
			connectionDetails.setRequestListenerClassValue(valueOfConnection[5]);
			
		}else {
			RequestContext.getCurrentInstance().openDialog("createConnection");
		}
	}
	
	public void startMFSConnectionInfo(){
		ConnectionManagement connectionManagement = new ConnectionManagement();
		connectionManagement.startListenerAutoSuccessMock("localhost", Integer.parseInt(connectionDetails.getPort()), connectionDetails.getPackagerValue(), connectionDetails.getChannelType(), connectionDetails.getIsoHeader(), "100", "10", null);
	}
	
	public void stopMFSConnectionInfo(){
		ConnectionManagement connectionManagement = new ConnectionManagement();
		connectionManagement.stopListener(connectionDetails.getPort());
	}
	
	public void restartMFSConnectionInfo(){
		stopMFSConnectionInfo();
		Utility.sleep(5);
		startMFSConnectionInfo();
	}
	
	public List<Logs> getLogs() {
		return logs;
	}

	public void setLogs(List<Logs> logs) {
		this.logs = logs;
	}

	public MessageDetail getMessageDetail() {
		return messageDetail;
	}

	public void setMessageDetail(MessageDetail messageDetail) {
		this.messageDetail = messageDetail;
	}

	public ConnectionDetails getConnectionDetails() {
		return connectionDetails;
	}

	public void setConnectionDetails(ConnectionDetails connectionDetails) {
		this.connectionDetails = connectionDetails;
	}
	
}
