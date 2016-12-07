package corp.dimo.common.models;

import java.io.Serializable;
import java.util.Date;

public class Logs implements Serializable{

	private static final long serialVersionUID = 1L;

	private String uniqueID;
	private String txnNo;
	private Date txnDate;
	private String sourceIP;
	private String sourcePort;
	private String mti;
	private String dateMessage;
	
	private MessageDetail messageDetail;
	private ConnectionDetails connectionDetails;
	
	public String getUniqueID() {
		return uniqueID;
	}
	public void setUniqueID(String uniqueID) {
		this.uniqueID = uniqueID;
	}
	public String getTxnNo() {
		return txnNo;
	}
	public void setTxnNo(String txnNo) {
		this.txnNo = txnNo;
	}
	public Date getTxnDate() {
		return txnDate;
	}
	public void setTxnDate(Date txnDate) {
		this.txnDate = txnDate;
	}
	public String getSourceIP() {
		return sourceIP;
	}
	public void setSourceIP(String sourceIP) {
		this.sourceIP = sourceIP;
	}
	public String getSourcePort() {
		return sourcePort;
	}
	public void setSourcePort(String sourcePort) {
		this.sourcePort = sourcePort;
	}
	public String getMti() {
		return mti;
	}
	public void setMti(String mti) {
		this.mti = mti;
	}
	public String getDateMessage() {
		return dateMessage;
	}
	public void setDateMessage(String dateMessage) {
		this.dateMessage = dateMessage;
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
