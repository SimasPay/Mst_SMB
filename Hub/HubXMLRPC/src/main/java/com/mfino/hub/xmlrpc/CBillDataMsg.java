package com.mfino.hub.xmlrpc;

import java.util.HashMap;

import com.mfino.hibernate.Timestamp;



public class CBillDataMsg {

	private HashMap<String, Object> xmlMsg;
	

	public CBillDataMsg() {
		xmlMsg = new HashMap<String, Object>();
	}


	public Timestamp getRqtime() {
		return (Timestamp)xmlMsg.get("rqtime");
	}


	public void setRqtime(Timestamp rqtime) {
		xmlMsg.put("rqtime", rqtime);
	}


	public String getRqid() {
		return (String) xmlMsg.get("rqid");
	}


	public void setRqid(String rqid) {
		xmlMsg.put("rqid",rqid);
	}


	public String getBillid() {
		return (String) xmlMsg.get("billid");
	}


	public void setBillid(String billid) {
		xmlMsg.put("billid",billid);
	}


	public String getProduct() {
		return (String) xmlMsg.get("product");
	}


	public void setProduct(String product) {
		xmlMsg.put("product",product);
	}


	public String getAmount() {
		return (String) xmlMsg.get("amount");
	}


	public void setAmount(String amount) {
		xmlMsg.put("amount",amount);
	}


	public String getTerminal() {
		return (String) xmlMsg.get("terminal");
	}


	public void setTerminal(String terminal) {
		xmlMsg.put("terminal",terminal);
	}


	public String getAgent() {
		return (String) xmlMsg.get("agent");
	}


	public void setAgent(String agent) {
		xmlMsg.put("agent",agent);
	}


	public String getCaid() {
		return (String) xmlMsg.get("caid");
	}


	public void setCaid(String caid) {
		xmlMsg.put("caid",caid);
	}


	public String getSign() {
		return (String) xmlMsg.get("sign");
	}


	public void setSign(String sign) {
		xmlMsg.put("sign",sign);
	}


	public String getStatus() {
		return (String) xmlMsg.get("status");
	}


	public void setStatus(String status) {
		xmlMsg.put("status",status);
	}


	public String getReffno() {
		return (String) xmlMsg.get("reffno");
	}


	public void setReffno(String reffno) {
		xmlMsg.put("reffno",reffno);
	}


	public String getMessage() {
		return (String) xmlMsg.get("message");
	}


	public void setMessage(String message) {
		xmlMsg.put("message",message);
	}


	public String getTrace() {
		return (String) xmlMsg.get("trace");
	}


	public void setTrace(String trace) {
		xmlMsg.put("trace",trace);
	}
	
	public String getOrgRqId() {
		return (String) xmlMsg.get("orgrqid");
	}


	public void setOrgRqId(String trace) {
		xmlMsg.put("orgrqid",trace);
	}

	public Timestamp getOrgTime() {
		return (Timestamp) xmlMsg.get("orgtime");
	}


	public void setOrgTime(Timestamp rqtime) {
		xmlMsg.put("orgtime",rqtime);
	}
	
	public String getOrgMethod() {
		return (String) xmlMsg.get("orgmethod");
	}

	public void setOrgMethod(String orgmethod) {
		xmlMsg.put("orgmethod",orgmethod);
	}

	public HashMap<String, Object> getXmlMsg() {
		return xmlMsg;
	}


	public void setXmlMsg(HashMap<String, Object> xmlMsg) {
		this.xmlMsg = xmlMsg;
	}
	
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		String seperator = "|";
		for(String key:xmlMsg.keySet()){
			String nameValuePair = key+"="+xmlMsg.get(key)+seperator;
			sb.append(nameValuePair);
		}			
		return sb.toString();
	}
	
	public  CBillDataMsg(String buffer){
		xmlMsg = new HashMap<String, Object>();
		String name = null,value = null;
		String[] keyValuePairs = buffer.split("\\|");
		for(int i=0;i<keyValuePairs.length ; i++){
			String singlePair[] = keyValuePairs[i].split("=");
			name = singlePair[0];
			value = singlePair[1];
			xmlMsg.put(name, value);
			// If the value are other than string check below code
			/*if(name.equalsIgnoreCase("rqtime")){

				TimeZone tz = TimeZone.getTimeZone("GMT");
				Date date = Calendar.getInstance().getTime();

				SimpleDateFormat date_format_gmt_all = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");// Standard format MM/dd/yyyy HH:mm:ss
				DateFormat date_format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
				date_format_gmt_all.setTimeZone(tz);

				Date date_gmt = Calendar.getInstance().getTime();
				try {
					date_gmt = date_format.parse(value);
				} catch (Exception e) {
					e.printStackTrace();
				}


				xmlMsg.put(name, new Timestamp(date_gmt));
			}else{
				xmlMsg.put(name, value);
			}*/	

		}
	}
	
	public static void main(String args[]){
		CBillDataMsg rpc = new CBillDataMsg();
		rpc.setRqtime(new Timestamp());
		rpc.setAmount("190");
		String str = rpc.toString();
		System.out.println(str);
		
		CBillDataMsg rpc2 = new CBillDataMsg(str);
		System.out.println(rpc2.getRqtime());
		System.out.println(rpc2.getAmount());
		
		
	}
	
	
}