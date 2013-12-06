package com.mfino.fidelity;

import java.io.IOException;
import java.util.Random;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOSource;

public class RequestHandler implements Runnable {

	private ISOMsg msg;
	private ISOSource source;
	private static String[] responseCodes = { "111", "114", "115", "116",
			"119", "121", "180", "184", "185", "188", "911" };
	private static Random random = new Random();

	public RequestHandler(ISOMsg msg, ISOSource source) {
		this.msg = msg;
		this.source = source;
	}

	@Override
	public void run() {
		try {
			String mti = msg.getMTI();
			msg.setResponseMTI();
			if ("1804".equals(mti)) {
				msg.set(39, "800");
			} else {
				if (random.nextBoolean()) {
					msg.set(39, "000");
					int pc = Integer.parseInt(msg.getString(3));
					switch (pc) {
					case 310000:
						msg.set(48,
								"+0000000000612196+0000000000612196+0000000000000000+0000000000000000+0000000000612196NGN");
						break;
					case 381000:
						msg.set(125,
								"20130628             6070000208:Int.Pd:01-04-2013 to 30-06-20C           384.8020130628             6070000208:WTax.Pd:01-04-2013to 30-06-20D            38.4820130627             ZENITH-37958-DW-AMADI THEO              C          5400.0020130627             SKYE-96201548-DW-AMADI THEO             C         31500.0020130620             IQTL/10161371734428903/0005932242/TAMADID         45100.0020130619             ATM WD @ 10700167-FBP2,  KOFO ABAYOM    D         15000.0020130618             ATM WD @ 10700166-FBP2,  KOFO ABAYOM    D          5000.0020130617             ATM WD @ 10701731-FB,AIRPORT ROAD BR    D         20000.0020130617             POS Purchase @ 20708F53-EZEKIEL FLORENCED          2910.0020130614             VISA WD @ 10700168-107001660000000&gt;LAD         10000.00");
						break;
					case 401010:
						msg.set(48,
								"+0000000009986365+0000000009567332+0000000000000000+0000000000000000+0000000009567332NGN              +0000098602382589+0000098602382589+0000000000000000+0000000000000000+0000098602382589NGN");
						break;
					default:
						break;
					}
				} else {
					msg.set(39,
							responseCodes[random.nextInt(responseCodes.length)]);
				}
			}
			source.send(msg);
		} catch (ISOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
