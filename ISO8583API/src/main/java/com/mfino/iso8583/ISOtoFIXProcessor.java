package com.mfino.iso8583;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.ParseException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mfino.fix.CMultiXBuffer;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBalanceInquiryFromBank.CGEntries;
import com.mfino.fix.CmFinoFIX.CMBalanceInquiryToBank;
import com.mfino.fix.CmFinoFIX.CMBankResponse;
import com.mfino.fix.CmFinoFIX.CMBase;
import com.mfino.hibernate.Timestamp;
import com.mfino.iso8583.processor.bank.AdditionalAmounts;
import com.mfino.iso8583.processor.bank.SinarmasISOMessage;
import com.mfino.iso8583.processor.zenithbank.isotofix.networkmanagement.MacKeyExchangeResponse;
import com.mfino.util.DateTimeUtil;
import com.solab.iso8583.IsoMessage;
import com.solab.iso8583.MessageFactory;
import com.solab.iso8583.parse.ConfigParser;

public class ISOtoFIXProcessor {

	public static MessageFactory	msgFactory;
	private static Log	log	= LogFactory.getLog(MacKeyExchangeResponse.class);

	private static void parseConfigFile() throws IOException {
		msgFactory = ConfigParser.createFromClasspathConfig("config.xml");
	}

	public CMultiXBuffer process(byte[] buf, CMBalanceInquiryToBank request) {

		try {

			parseConfigFile();

			String str = new String(buf);
			IsoMessage isoMsgMsg = msgFactory.parseMessage(buf, 0);
			SinarmasISOMessage isoMsg = new SinarmasISOMessage(isoMsgMsg);

			String rc = isoMsg.getResponseCode();
			if (!rc.equals(CmFinoFIX.ISO8583_ResponseCode_Success)) {
				return null;
			}
			String str1 = null;
			str1 = isoMsg.getPAN();//2
			str1 = isoMsg.getProcessingCode().toString();//3
			str1 = isoMsg.getTransactionAmount();// 4
			str1 = isoMsg.getTransmissionTime().toString();// 7
			str1 = isoMsg.getSTAN().toString();// 11
			str1 = isoMsg.getLocalTransactionDate().toString();// 13
			str1 = isoMsg.getSettlementDate().toString();// 15
			str1 = isoMsg.getMerchantType().toString();//18
			str1 = isoMsg.getAuthorizingIdentificationResponseLength().toString();// 27
			str1 = isoMsg.getAcquiringInstitutionIdentificationCode();// 32
			str1 = isoMsg.getForwardInstitutionIdentificationCode();// 33
			str1 = isoMsg.getTrack2Data();// 35
			str1 = isoMsg.getRRN();//37
			str1 = isoMsg.getCardAcceptorIdentificationCode();//42
			str1 = isoMsg.getCardAcceptorNameLocation();// 43
			str1 = isoMsg.getPrivateTransactionID();
			str1 = isoMsg.getEncryptedPin();// 52
			CmFinoFIX.CMBalanceInquiryFromBank response = new CmFinoFIX.CMBalanceInquiryFromBank();
			/*
			 * response.setSourceApplication(request.getSourceApplication());
			 * response.setServletPath(request.getServletPath());
			 * response.setMSPID(request.getMSPID());
			 * response.setSourceMDN(request.getSourceMDN());
			 * response.setServiceName(request.getServiceName());
			 * response.setServiceNumber(request.getServiceNumber());
			 * response.setTransactionID(request.getTransactionID());
			 * response.setParentTransactionID
			 * (request.getParentTransactionID());
			 * response.setLoginName(request.getLoginName());
			 * response.setPassword(request.getPassword());
			 * response.setSourceIP(request.getSourceIP());
			 * response.setReceiveTime(request.getReceiveTime());
			 * response.setOperatorName(request.getOperatorName());
			 * response.setWebClientIP(request.getWebClientIP());
			 * response.setChannelCode(request.getChannelCode());
			 * response.setBillPaymentReferenceID
			 * (request.getBillPaymentReferenceID());
			 * response.setPaymentInquiryDetails
			 * (request.getPaymentInquiryDetails());
			 */
			response.setAIR(isoMsg.getAuthorizationIdentificationResponse());
			response.setResponseCode(isoMsg.getResponseCode());
			CGEntries[] entries = response.allocateEntries(10);
			String amounts = isoMsg.getAdditionalAmounts();
			int index = 0;
			int entrySize = 20;
			while (amounts.length() > index * entrySize && index < response.getEntries().length) {

				AdditionalAmounts aa = AdditionalAmounts.parseAdditionalAmounts(amounts);

				if (aa.getAmountSign() == 'D')
					aa.setAmount(aa.getAmount().multiply(new BigDecimal("-1")));
				entries[index] = new CmFinoFIX.CMBalanceInquiryFromBank.CGEntries();
				entries[index].setAmount(aa.getAmount());
				entries[index].setBankAccountType(aa.getAccountType());
				entries[index].setBankAmountType(aa.getAmountType());

				if (aa.getCurrencyCode() == CmFinoFIX.ISO8583_Sinarmas_CurrencyCode_IDR)
					entries[index].setCurrency(String.valueOf(aa.getCurrencyCode()));
				else if (aa.getCurrencyCode() == CmFinoFIX.ISO8583_Sinarmas_CurrencyCode_USD)
					entries[index].setCurrency(String.valueOf(aa.getCurrencyCode()));
				else
					entries[index].setCurrency(String.valueOf(CmFinoFIX.Currency_UnKnown));

				index++;
			}

			response.m_pHeader.setMsgSeqNum(131);
			response.m_pHeader.setSendingTime(new Timestamp());
			CMultiXBuffer buffer = new CMultiXBuffer();
			response.toFIX(buffer);
			return buffer;

		}
		catch (UnsupportedEncodingException ex) {
			log.error("Error processing ISO msg to Fix:", ex);
		}
		catch (ParseException ex) {
			log.error("Error processing ISO msg to Fix:", ex);
		}
		catch (Exception ex) {
			log.error("Error processing ISO msg to Fix:", ex);
		}

		return null;
	}

	public static void main(String[] args) {
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				ISOtoFIXProcessor proc = new ISOtoFIXProcessor();
				try {
					ServerSocket server = new ServerSocket(9992);
					byte[] lenBytes = new byte[4];
					while (true) {
						System.out.print(" Ready to read from the stream:");
						try {
							Socket sock = server.accept();
							if (sock.getInputStream().read(lenBytes, 0, 4) == 4) {
								System.out.print("Length successfully read");
								String str = new String(lenBytes);
								int size = Integer.parseInt(str);
								byte[] buf = new byte[size];
								int noOfBytesRead = 0;
								// while(noOfBytesRead<size)
								// {
								noOfBytesRead = sock.getInputStream().read(buf, 0, size);
								// }

								proc.process(buf, null);
							}
						}
						catch (Exception ex) {
							log.error("Error processing ISO msg to Fix:", ex);
							continue;
						}
					}
				}
				catch (UnknownHostException ex) {
					log.error("Error processing ISO msg to Fix:", ex);
				}
				catch (IOException ex) {
					log.error("Error processing ISO msg to Fix:", ex);				}
			}
		});
		t.start();
	}
	
	public static CMBankResponse getGenericResponse(WrapperISOMessage isoMsg,CMBase request) {
		
		CMBankResponse response = new CMBankResponse();
		response.copy(request);
		response.setAIR(isoMsg.getAuthorizationIdentificationResponse());
		response.setResponseCode(isoMsg.getResponseCode());
//FIXME		response.header().setMsgSeqNum(null);
		response.header().setSendingTime(DateTimeUtil.getLocalTime());
		return response;
	}
}
