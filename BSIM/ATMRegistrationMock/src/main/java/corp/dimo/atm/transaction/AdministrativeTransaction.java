package corp.dimo.atm.transaction;

import iso8583.jpos.ism.Client_ASCIIChannelAdapter;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;

import org.jpos.iso.ISOException;
import org.primefaces.context.RequestContext;

import corp.dimo.common.models.ConnectionDetails;
import corp.dimo.common.models.MessageDetail;
import corp.dimo.common.transaction.MessageBuilder;
import corp.dimo.common.transaction.MessageParse;
import corp.dimo.common.utils.ConfigConstan;

@ManagedBean
public class AdministrativeTransaction {
	
	private String mdn;
	private String acctNo;
	private String mPin;
	
	private MessageDetail messageDetail;
	private ConnectionDetails connectionDetails;
	
	String host;
	String port;
	
	public void mBankingRegistration(String sourceAcctNo, String encryptPin, String mdn){
		Client_ASCIIChannelAdapter asciiChannelAdapter = new Client_ASCIIChannelAdapter();
		String stateConn=asciiChannelAdapter.stateConnection(ConfigConstan.connName_atm);
		if (stateConn.equals("Connected")) {
			try {
				String response = asciiChannelAdapter.sendreceiveRequest(ConfigConstan.connName_atm, MessageBuilder.atmMBankingRegistration(sourceAcctNo, encryptPin, mdn), 30);
			
				
				String messageRequest = "[Rquest]["+new String(MessageBuilder.atmMBankingRegistration(sourceAcctNo, encryptPin, mdn).pack())+"]";
				String messageRespone = "[Response]["+response+"]";
				String messageData = messageRequest+
						"\n"+messageRespone;
				String messageHeader = "Transaction status ["+MessageParse.getTransactionStatus(response)+"]";
				
				showMessage(messageHeader, messageData);
			} catch (ISOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void atmConnectionInfo(){
		
	}
	
	public void atmCreateConnection(){
		try {
			String stateConnection = null;
			Client_ASCIIChannelAdapter asciiChannelAdapter = new Client_ASCIIChannelAdapter();
			
			asciiChannelAdapter.createConnection(ConfigConstan.connName_atm, getHost(), Integer.parseInt(getPort()), ConfigConstan.packager_iso87ascii);
			try {
				Thread.sleep(5*1000);
				stateConnection = asciiChannelAdapter.stateConnection(ConfigConstan.connName_atm);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			showMessage("Connection Info",stateConnection+" to "+getHost()+":"+getPort());
		} catch (NumberFormatException | ISOException e) {
			e.printStackTrace();
		}
	}
	
	public void atmReCreateConnection(){
		
	}
	
	public void showMessage(String messageType, String messageDetail) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, messageType, messageDetail);
         
        RequestContext.getCurrentInstance().showMessageInDialog(message);
    }
     
    public void addMessage(String summary, String detail) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, summary, detail);
        FacesContext.getCurrentInstance().addMessage(null, message);
    }
	
	public String getMdn() {
		return mdn;
	}
	public void setMdn(String mdn) {
		this.mdn = mdn;
	}
	public String getAcctNo() {
		return acctNo;
	}
	public void setAcctNo(String acctNo) {
		this.acctNo = acctNo;
	}
	public String getmPin() {
		return mPin;
	}
	public void setmPin(String mPin) {
		this.mPin = mPin;
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

}
