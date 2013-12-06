package com.mfino.fep;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;

import org.apache.commons.lang.math.RandomUtils;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.channel.PostChannel;
import org.jpos.iso.packager.GenericPackager;

public class FepClient {
	private PostChannel channel;
	private BufferedReader reader;
	private String host;
	private int port;

	public FepClient() {
		reader = new BufferedReader(new InputStreamReader(System.in));
	}

	public static void main(String args[]) {
		FepClient fepClient = new FepClient();
		fepClient.start();
	}

	private void start() {
		getHostnPort();
		channel = new PostChannel();
		try {
			channel.setPackager(new GenericPackager("fep.xml"));
			channel.setPort(port);
			channel.setHost(host);
			channel.connect();
		} catch (Exception e) {
			System.out.println("Unable to connect to server ..." + e);
			System.exit(0);
		}
		System.out.println("connected to server ...");
		for (;;) {
			try {
				String choice = selectChoice();
				String[] data;
				if ("1".equals(choice)) {
					data = getInputData();
					sendRequest("0200", data);
				} else if ("2".equals(choice)) {
					data = getInputData();
					sendRequest("0420", data);
				} else
					break;
			} catch (Exception e) {
				System.out.println("Exception occured " + e);
			}
		}
	}

	private void getHostnPort() {
		boolean done = false;
		do {
			try {
				System.out.println("Enter Server Host ip");
				host = reader.readLine();
				System.out.println("Enter Server port");
				port = Integer.parseInt(reader.readLine());
				done = true;
			} catch (Exception e) {
				System.out.println("Please enter host and port correctly");
			}
		} while (!done);
	}

	private void sendRequest(String mti, String[] data) throws ISOException,
			IOException {
		Date d = new Date();
		ISOMsg m = new ISOMsg();
		m.setMTI(mti);
		m.set(3, "010000");
		m.set(4, data[2] + "00");
		m.set(7, String.format("%Tm%<Td%<TH%<TM%<TS", d));
		m.set(11, String.valueOf(RandomUtils.nextInt() % 1000000));
		m.set(12, String.format("%TH%<TM%<TS", d));
		m.set(13, String.format("%Tm%<Td", d));
		m.set(18, "6011");
		m.set(22, "901");
		m.set(25, "00");
		m.set(26, "12");
		m.set(35, "9389292029230");
		m.set(41, "10582142");
		m.set(42, "GTB000000000054");
		m.set(43, "GTBank A/ Ademola       V/I         LANG");
		m.set(49, "566");
		m.set(52, "93029200".getBytes());
		m.set(100, "62805161");
		m.set(123, "511201213344002");
		m.set("127.2", "105821420027115121");
		m.set("127.12", "SWTGTBsn");
		m.set("127.13", "00234000000   566");
		m.set("127.14", "GTB");
		m.set("127.033", "9010");
		m.set("127.022", set127(data));
		channel.send(m);
		System.out.println("Sending request....");
		ISOMsg response = channel.receive();
		System.out.println("Recieved response field 39: "+ response.getString(39));
		System.out.println();
	}

	private String[] getInputData() throws IOException {
		String[] data = new String[3];
		System.out.println("\nEnter MDN");
		data[0] = reader.readLine();
		System.out.println("Enter FAC");
		data[1] = reader.readLine();
		System.out.println("Enter Amount");
		data[2] = reader.readLine();
		return data;
	}

	private String selectChoice() throws IOException {
		System.out.println("\nEnter \n 1 for Cashout\n 2 for CashoutReversal\n AnyOtherKey to quit");
		return reader.readLine();
	}

	private static String set127(String[] data) {
		return "211MediaTotals3121<MediaTotals><Total><Amount>369800000</Amount><Currency>566</Currency><MediaClass>Cash</MediaClass></Total></MediaTotals>212MediaBatchNr175131889214AdditionalInfo3326<AdditionalInfo><Transaction><BufferB>"
				+ data[0]
				+ "</BufferB><BufferC>"
				+ data[1]
				+ "</BufferC><CfgExtendedTrxType>9010</CfgExtendedTrxType><CfgReceivingInstitutionIDCode>62805161</CfgReceivingInstitutionIDCode></Transaction><Download><ATMConfigID>5006</ATMConfigID><AtmAppConfigID>5006</AtmAppConfigID></Download></AdditionalInfo>";
	}

}
