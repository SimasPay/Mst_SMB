package com.mfino.bsim.iso8583.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class TripleDES
{

	private final String key;

	public static void main(String... args)
			throws Exception
			{
		//TripleDES td = new TripleDES("BCFD57DFB03B25EA0BC1CBA29ED3F1B3");
		//String encrypted td.
		String decrypted = TripleDES.decrypt("BCFD57DFB03B25EA0BC1CBA29ED3F1B3","973A911C1BE4DD00D72B4DFEEA572E0093105B99DAB2D1C4");
		System.out.println("expecting:D3FF18E43663FACDB641C5B12F2147C0D3FF18E43663FACD");
		System.out.println("found: " + decrypted);


		//  TripleDES tde = new TripleDES("D3FF18E43663FACDB641C5B12F2147C0");
		String encrypted = TripleDES.encrypt("D3FF18E43663FACDB641C5B12F2147C0","06224A5B6AFFFFFD");
		System.out.println("Expecting :A2B3137F4BD8845C");
		System.out.println("found: "+encrypted);

		//TripleDES tde2 = new TripleDES("D3FF18E43663FACDB641C5B12F2147C0");
		String descrypted2=TripleDES.decrypt("D3FF18E43663FACDB641C5B12F2147C0","A2B3137F4BD8845C");
		System.out.println("Expecting :06224A5B6AFFFFFD");
		System.out.println("found: "+descrypted2);
			}

	TripleDES(String key)
	{
		this.key = key;
	}

	public static String decrypt(String key,String input)
			throws Exception
			{
				return doEncryptDecrypt(key, input, Cipher.DECRYPT_MODE);
			}


	public static String encrypt(String key,String input)
			throws Exception
			{
				return doEncryptDecrypt(key, input, Cipher.ENCRYPT_MODE);
			}

	private static String doEncryptDecrypt(String key,String input,int mode) throws Exception
	{
		byte[] tmp = h2b(key);
		byte[] byteKey = new byte[24];
		System.arraycopy(tmp, 0, byteKey, 0, 16);
		System.arraycopy(tmp, 0, byteKey, 16, 8);
		Cipher cipher = Cipher.getInstance("DESede/ECB/NoPadding");
		cipher.init(mode, new SecretKeySpec(byteKey, "DESede"));
		byte[] plaintext = cipher.doFinal(h2b(input));
		return b2h(plaintext);
	}

	private static byte[] h2b(String hex)
	{
		if ((hex.length() & 0x01) == 0x01)
			throw new IllegalArgumentException();
		byte[] bytes = new byte[hex.length() / 2];
		for (int idx = 0; idx < bytes.length; ++idx) {
			int hi = Character.digit((int) hex.charAt(idx * 2), 16);
			int lo = Character.digit((int) hex.charAt(idx * 2 + 1), 16);
			if ((hi < 0) || (lo < 0))
				throw new IllegalArgumentException();
			bytes[idx] = (byte) ((hi << 4) | lo);
		}
		return bytes;
	}

	public static String b2h(byte[] bytes)
	{
		char[] hex = new char[bytes.length * 2];
		for (int idx = 0; idx < bytes.length; ++idx) {
			int hi = (bytes[idx] & 0xF0) >>> 4;
			int lo = (bytes[idx] & 0x0F);
			hex[idx * 2] = (char) (hi < 10 ? '0' + hi : 'A' - 10 + hi);
			hex[idx * 2 + 1] = (char) (lo < 10 ? '0' + lo : 'A' - 10 + lo);
		}
		return new String(hex);
	}

}