/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.fix;

import java.util.Hashtable;

/**
 *
 * @author moshiko
 */
public class CFIXTransportHandler {

	public enum TCompletionCodes {

		MsgReceiveInProgress,
		NewMessageReceived,
		HTTPGetReceived,
		NoATTPAuthentication,
		InvalidMsg,
		SendHTTP100Continue
	}
	protected CMultiXBuffer m_pInBuf;
	int m_ExpectedMsgLength;
	int m_LastSeqSent;
	int m_LastSeqReceived;
	boolean m_bOverHTTP;
	boolean m_bAuthRequired;
	String m_UserName;
	String m_Password;
	String m_FIXVariant;
	String m_CredentialsCookie;
	Hashtable<String,String> m_Cookies;

	public CFIXTransportHandler(boolean bOverHTTP, boolean bAuthRequired) {
		m_pInBuf = null;
		m_ExpectedMsgLength = 0;
		m_LastSeqReceived = 0;
		m_LastSeqSent = 0;
		m_bOverHTTP = bOverHTTP;
		m_bAuthRequired = bAuthRequired;
		if (m_bOverHTTP) {
			m_Cookies = new Hashtable<String,String>();
		}
	}

	public void AppendInBuf(CMultiXBuffer Buf) {
		if (m_pInBuf == null) {
			m_pInBuf = new CMultiXBuffer();
		}
		m_pInBuf.Append(Buf);
	}

	public void AppendInBuf(byte[] pData) {
		if (m_pInBuf == null) {
			m_pInBuf = new CMultiXBuffer();
		}
		m_pInBuf.Append(pData);
	}

	public CMultiXBuffer InBuf() {
		return m_pInBuf;
	}

	public void Initialize() {
		m_LastSeqReceived = 0;
		m_LastSeqSent = 0;
		m_pInBuf = new CMultiXBuffer();
	}

	public boolean ExtractCredetials(String S) {
		if (m_Cookies.get("FIXCredentials") != null) {
			String Auth = (String) m_Cookies.get("FIXCredentials");
			m_UserName = Auth.substring(0, Auth.indexOf(":"));
			m_Password = Auth.substring(Auth.indexOf(":") + 1);
			return true;
		}
		int AuthorizationOffset = S.indexOf("Authorization:");
		if (AuthorizationOffset < 0) {
			return false;
		}
		int Space = S.indexOf(" ", AuthorizationOffset);
		if (Space < 0) {
			return false;
		}
		Space = S.indexOf(" ", Space + 1);
		if (Space < 0) {
			return false;
		}
		int EOL = S.indexOf("\r\n", Space + 1);
		String Auth;
		Auth = S.substring(Space + 1, EOL);

		byte[] B = CBase64.FromBase64(Auth);
		Auth = new String(B);
		m_UserName = Auth.substring(0, Auth.indexOf(":"));
		m_Password = Auth.substring(Auth.indexOf(":") + 1);
		return true;
	}

	void ExtractCookies(String S) {
		int CookieOffset = S.indexOf("Cookie:");
		if (CookieOffset < 0) {
			return;
		}

		int NameStart = CookieOffset + "Cookie:".length() + 1;
		int ValueEnd = 0;
		int EQSign = 0;
		while ((ValueEnd = S.indexOf(";", NameStart)) > 0) {
			EQSign = S.indexOf("=", NameStart);
			String CookieName;
			String CookieValue;
			CookieName = S.substring(NameStart, EQSign);
			CookieValue = S.substring(EQSign + 1, ValueEnd);
			m_Cookies.put(CookieName, CookieValue);
			NameStart = ValueEnd + 2;
		}
		ValueEnd = S.indexOf("\r\n", NameStart);

		EQSign = S.indexOf("=", NameStart);
		String CookieName;
		String CookieValue;
		CookieName = S.substring(NameStart, EQSign);
		CookieValue = S.substring(EQSign + 1, ValueEnd);
		m_Cookies.put(CookieName, CookieValue);
	}

	public TCompletionCodes GetMessage(Object[] Msg) {
		TCompletionCodes RetCode = TCompletionCodes.MsgReceiveInProgress;
		if (m_pInBuf != null) {
			if (m_ExpectedMsgLength == 0) {
				if (m_bOverHTTP) {
					String Ptr = new String(m_pInBuf.DataPtr(), 0, m_pInBuf.Length());

					int HttpHdrLength = Ptr.indexOf("\r\n\r\n");
					if (HttpHdrLength > 0) {
						ExtractCookies(Ptr);
						if (!m_bAuthRequired || ExtractCredetials(Ptr)) //	We Have Authentication data
						{
							if (Ptr.indexOf("GET ") == 0) //	We Got HTTP Get
							{
								Msg[0] = Ptr.substring(HttpHdrLength + 4);
								RetCode = TCompletionCodes.HTTPGetReceived;
								m_pInBuf = null;
							} else {
								int ContentLengthOffset = Ptr.indexOf("Content-Length:");
								if (ContentLengthOffset <= 0) {
									RetCode = TCompletionCodes.InvalidMsg;
								} else {
									int EOL = Ptr.indexOf("\r\n", ContentLengthOffset);
									String ContentLengthStr = Ptr.substring(ContentLengthOffset + "Content-Length:".length(), EOL);
									m_ExpectedMsgLength = Integer.parseInt(ContentLengthStr);
									m_pInBuf.ShiftLeft(HttpHdrLength + 4);
									if (m_pInBuf.Length() == 0) {
										RetCode = TCompletionCodes.SendHTTP100Continue;
									}
								}
							}
						} else {
							RetCode = TCompletionCodes.NoATTPAuthentication;
						}
					}
				} else {
					//	We do not know the expected message length yet, we have to parse the first 2 fields to calculate the expected length
					int FieldSeparatorCount = 0;
					byte[] Ptr = m_pInBuf.DataPtr();
					//	We are looking for 2 SOH chars which indicate to us that we have at least the first 2 fields
					//	in the message, the BeginString and BodyLength
					for (int I = 0; I < m_pInBuf.Length(); I++) {
						if (Ptr[I] == CFIXMsg.FIELD_SEPARATOR) {
							FieldSeparatorCount++;
							if (FieldSeparatorCount == 2) {
								//	we	have 2 SOH chars lets extruct the expected message length
								int[] ExpectedLength = new int[1];

								if (!CFIXMsg.ExpectedMessageLength(m_pInBuf, ExpectedLength)) {
									//	false return value implies that the bytes we received are invalid for a FIX message
									//	So we will return an error indication
									//							m_pInBuf->Empty();
									RetCode = TCompletionCodes.InvalidMsg;
								} else {
									//	we have a valid minimal header, we stop searching for the Message Length
									m_ExpectedMsgLength = ExpectedLength[0];
									break;
								}
							}
						}
					}
				}
			}

			//	We get here once we know the expected Message Length, including all bytes from start to end
			if (m_ExpectedMsgLength > 0 && m_pInBuf.Length() >= m_ExpectedMsgLength) {
				//	We have received at least one full message, so we are going to import the buffer and try and create a new CFIXEcnFEMsg object

				Msg[0] = CFIXMsg.fromFIX(m_pInBuf);
				if (Msg[0] != null) {
					RetCode = TCompletionCodes.NewMessageReceived;
					m_pInBuf.ShiftLeft(m_ExpectedMsgLength);
				} else {
					RetCode = TCompletionCodes.InvalidMsg;
//				m_pInBuf->Empty();
				}
				m_ExpectedMsgLength = 0;	//	We reset the Expected Message Length, for the next message
			}
		}
		return RetCode;
	}
	public CMultiXBuffer FormatBufferToSend(CMultiXBuffer Source) {
		if (m_bOverHTTP) {
			CMultiXBuffer ToSend = new CMultiXBuffer();

			String Http = String.format("HTTP/1.1 200 OK\r\n" +
							"Content-Type: text/*; charset=utf-8\r\n" +
							"Connection: Keep-Alive\r\n" +
							"Set-Cookie: %s\r\n" +
							"Content-Length: %d\r\n\r\n",
							m_CredentialsCookie,
							Source.Length());
			ToSend.Append(Http);
			ToSend.Append(Source);
			return ToSend;
		} else {
			return Source;
		}
	}
}
