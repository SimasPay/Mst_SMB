package com.mfino.sterling;

import com.mfino.sterling.crypto.CryptoService;

public class EncryptionTest {
	
	public static void main(String a[]) throws Exception{
		String xml ="<IBSRequest><ReferenceID>0046266</ReferenceID><RequestType>201</RequestType><Account>1020006636</Account></IBSRequest>";
		byte[] keybyte = {0x01, 0x02, 0x05, 0x05, 0x07, 0x0b, 0x0d, 0x11, 0x12, 0x11, 0x0d, 0x0b, 0x07, 0x02, 0x04, 0x08, 0x01, 0x0c, 0x03, 0x05, 0x07, 0x0b, 0x0d, 0x1c};
		byte[] ivbyte = {0x01,0x02,0x03,0x05,0x07,0x0b,0x0d,0x04};
		CryptoService cryptoService = new CryptoService();
		cryptoService.setInitializationVector(ivbyte);
		cryptoService.setKey(keybyte);		
		String encString = cryptoService.encrypt(xml);
		System.out.println(encString);
		System.out.println(cryptoService.decrypt(encString));		
	}

}
