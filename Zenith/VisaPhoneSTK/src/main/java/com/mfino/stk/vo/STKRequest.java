/**
 * 
 */
package com.mfino.stk.vo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

/**
 * @author Bala Sunku
 *
 */
public class STKRequest {
	
	private Byte[] reqes;
	private String requestMsg;
	private String sourceMDN;
	private String decryptedRequestMsg;
	private int[] requestAsInts;
	
	public String getRequestMsg() {
		return requestMsg;
	}
	public void setRequestMsg(String requestMsg) {
		this.requestMsg = requestMsg;
	}
	public String getSourceMDN() {
		return sourceMDN;
	}
	public void setSourceMDN(String sourceMDN) {
		this.sourceMDN = sourceMDN;
	}
	public String getDecryptedRequestMsg() {
		return decryptedRequestMsg;
	}
	public void setDecryptedRequestMsg(String decryptedRequestMsg) {
		this.decryptedRequestMsg = decryptedRequestMsg;
	}
	
	public void setRequestAsInts(int[] requestAsInts) {
	    this.requestAsInts = requestAsInts;
    }
	public int[] getRequestAsInts(){
		return this.requestAsInts;
	}
	
	/**
	 * Mask the secured data like Pin in the given string.
	 * @return
	 */
	public String getSecuredDecryptedRequestMsg() {
		StringBuffer sb = new StringBuffer();
		if (StringUtils.isNotBlank(decryptedRequestMsg)) {
			Pattern p = Pattern.compile("([Pp])(\\d+)");
			Matcher m = p.matcher(decryptedRequestMsg);
			
			while (m.find()) {
				m.appendReplacement(sb, m.group(1) + "xxxx");
			}
			m.appendTail(sb);
		}
		return sb.toString();
	}
}
