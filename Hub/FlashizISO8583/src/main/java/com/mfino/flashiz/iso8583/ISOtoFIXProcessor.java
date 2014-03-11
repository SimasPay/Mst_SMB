package com.mfino.flashiz.iso8583;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import org.jpos.iso.ISOMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.fix.CMultiXBuffer;
import com.mfino.fix.CmFinoFIX.CMBalanceInquiryToBank;
import com.mfino.fix.CmFinoFIX.CMBankResponse;
import com.mfino.fix.CmFinoFIX.CMBase;
import com.mfino.util.DateTimeUtil;
import com.mfino.util.UniqueNumberGen;

public class ISOtoFIXProcessor {

	private static final Logger log	= LoggerFactory.getLogger(ISOtoFIXProcessor.class);

	private static void parseConfigFile() throws IOException {
	}

	public CMultiXBuffer process(byte[] buf, CMBalanceInquiryToBank request) {

		try {

			parseConfigFile();

			CMultiXBuffer buffer = new CMultiXBuffer();
			return buffer;

		}
		catch (UnsupportedEncodingException ex) {
			ex.printStackTrace();
		}
		catch (Exception ex) {
			ex.printStackTrace();
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
								// while(noOfBytesRead<size)
								// {
								// }

								proc.process(buf, null);
							}
						}
						catch (Exception ex) {
							continue;
						}
					}
				}
				catch (UnknownHostException ex) {
					ex.printStackTrace();
				}
				catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		});
		t.start();
	}
	
	public static CMBankResponse getGenericResponse(ISOMsg isoMsg,CMBase request) {
		
		CMBankResponse response = new CMBankResponse();
		response.copy(request);
		if(isoMsg.hasField(38))
		
		if(isoMsg.hasField(39))	
				response.setAIR(isoMsg.getString(39));
		if(isoMsg.hasField(39))
			response.setResponseCode(isoMsg.getString(39));
		//FIXME	response.header().setMsgSeqNum(null);
		response.header().setSendingTime(DateTimeUtil.getLocalTime());
		response.header().setMsgSeqNum(UniqueNumberGen.getNextNum());
		return response;
	}
}
