/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.fix;

/**
 *
 * @author moshiko
 */
public class CBase64 {

	static String Base64Chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/0=";
	static byte[] Base64Inverted = new byte[128];

	public static String ToBase64(byte[] pData) {
		int I = 0;
		String Str = "";
		for (I = 0; I < pData.length -2; I += 3) {
			int I32 = (pData[I] << 16) + (pData[I + 1] << 8) + pData[I + 2];
			Str += Base64Chars.charAt((I32 >> 18) & 0x3f);
			Str += Base64Chars.charAt((I32 >> 12) & 0x3f);
			Str += Base64Chars.charAt((I32 >> 6) & 0x3f);
			Str += Base64Chars.charAt((I32) & 0x3f);
		}
		if (pData.length - I == 1) {
			int I32 = (pData[I] << 16);
			Str += Base64Chars.charAt((I32 >> 18) & 0x3f);
			Str += Base64Chars.charAt((I32 >> 12) & 0x3f);
			Str += '=';
			Str += '=';
		} else if (pData.length - I == 2) {
			int I32 = (pData[I] << 16) + (pData[I + 1] << 8);
			Str += Base64Chars.charAt((I32 >> 18) & 0x3f);
			Str += Base64Chars.charAt((I32 >> 12) & 0x3f);
			Str += Base64Chars.charAt((I32 >> 6) & 0x3f);
			Str += '=';
		}
		return Str;
	}

	public static byte[] FromBase64(String Base64String) {
		byte[] StrBytes = Base64String.getBytes();
		if (Base64Inverted['B'] != 1) {
			for (byte I = 0; I < 64; I++) {
				Base64Inverted[Base64Chars.getBytes()[I]] = I;
			}
		}
		int OutDataLen = 0;
		byte[] OutData = new byte[StrBytes.length];
		for (int I = 0; I < (int) Base64String.length(); I += 4) {
			if (Base64String.charAt(I + 3) != '=') {
				int I32 = (Base64Inverted[StrBytes[I]] << 18) +
								(Base64Inverted[StrBytes[I + 1]] << 12) +
								(Base64Inverted[StrBytes[I + 2]] << 6) +
								Base64Inverted[StrBytes[I + 3]];
				OutData[OutDataLen++] = (byte) ((I32 >> 16) & 0xff);
				OutData[OutDataLen++] = (byte) ((I32 >> 8) & 0xff);
				OutData[OutDataLen++] = (byte) ((I32) & 0xff);
			} else if (Base64String.charAt(I + 2) != '=') {
				int I32 = (Base64Inverted[StrBytes[I]] << 18) +
								(Base64Inverted[StrBytes[I + 1]] << 12) +
								(Base64Inverted[StrBytes[I + 2]] << 6);
				OutData[OutDataLen++] = (byte) ((I32 >> 16) & 0xff);
				OutData[OutDataLen++] = (byte) ((I32 >> 8) & 0xff);
			} else {
				int I32 = (Base64Inverted[StrBytes[I]] << 18) +
								(Base64Inverted[StrBytes[I + 1]] << 12);
				OutData[OutDataLen++] = (byte) ((I32 >> 16) & 0xff);
			}
		}
		byte[] RetVal = new byte[OutDataLen];
		System.arraycopy(OutData, 0, RetVal, 0, OutDataLen);
		return RetVal;
	}
}
