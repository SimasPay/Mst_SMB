package com.mfino.stk;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Random;

import com.mfino.crypto.CryptographyService;

public class TestSTK {

	private static byte[] sillyShiftCDMA(byte[] resultantBytes) {

		byte temp = resultantBytes[resultantBytes.length - 1];
		for (int i = resultantBytes.length - 1; i > 0; i--) {
			resultantBytes[i] = (byte) ((resultantBytes[i] >>> 3)&0x1F);
			byte t = (byte)((resultantBytes[i - 1] << 5)& 0x000000E0);
			resultantBytes[i] = (byte) (resultantBytes[i] + t);
		}
		resultantBytes[0] = (byte) (resultantBytes[0] >>> 3);
		resultantBytes[0] = (byte) (resultantBytes[0] + (((temp << 5)) & 0x000000E0));

		return resultantBytes;
	}

	public static void main(String[] args) throws IOException {
		//		String str= "C^RÂ¨Ã¿Â°mÃS1W<90>Âµ)U7<86>c^ZÃ§t!)Â";
		//		System.out.println(str);

		
//		System.out.println((0x31<<5)&0x000000E0);
//		
//		String str1 = "0167FE17FC7D9EE263B902F829253041B34D5C105D03AA656BC7D6614FE0E21D627B6E091AA22730DC6901F231E8";
//		String str2 = "002CFFC2FF8FB3DC4C77205F0524A6083669AB820BA0754CAD78FACC29FC1C43AC4F6DC1235444E61B8D203E463D";
//		//		byte[] b = CryptographyService.hexToBin(str.toCharArray());
//
//		System.out.println(str1.length() + " " + str2.length());
//		String binStr1 = "";
//		for (int i = 0; i < str1.length(); i++) {
//			binStr1=binStr1+hexChartoBin(str1.charAt(i));
//		}
//		for(int i=0;i<binStr1.length();i++) {
//			if(i%8==0&&i!=0)
//				System.out.print(" ");
//			System.out.print(binStr1.charAt(i));
//		}
//		System.out.println();
//		
//		System.out.println("Shifting string");
//		String binStr3="";
//		byte[] arr = sillyShiftCDMA(CryptographyService.hexToBin(str1.toCharArray()));
//		String str3 = new String(CryptographyService.binToHex(arr));
//		for (int i = 0; i < str3.length(); i++) {
//			binStr3=binStr3+hexChartoBin(str3.charAt(i));
//			if(i%2==1)
//				binStr3 = binStr3+" ";
//		}
//		System.out.println(binStr3);
//
//		System.out.println("actual shifter string");
//		String binStr2 = "";
//		for (int i = 0; i < str2.length(); i++) {
//			binStr2=binStr2+hexChartoBin(str2.charAt(i));
//			if(i%2==1)
//				binStr2 = binStr2+" ";
//		}
//		System.out.println(binStr2);
//		
//		if(binStr2.equals(binStr3)) {
//			System.out.println("Yah0000!");
//		}
		
		ByteArrayInputStream bais = new ByteArrayInputStream(new byte[] {15,12,127,111,121,17,8,0,-127});
		InputStreamReader isr = new InputStreamReader(bais,"UTF-8");
		int ch=-1;
		while((ch=isr.read())!=-1)
			System.out.println(ch);
		
		System.out.println(Charset.defaultCharset());
		
		

	}
//   00000001 01100111 1111111 000010111111111000111110110011110111000100110001110111001000000101111100000101001001001010011000001000001101100110100110101011100000100000101110100000011101010100110010101101011110001111101011001100001010011111110000011100010000111010110001001111011011011100000100100011010101000100010011100110000110111000110100100000001111100100011000111101000
//00000000001 01100110 1111111 000010110111111000111110010011101111000100110001010111001000000011111100000101001001001010011000001000000101100110100110101011100000100000101110100000010101010100110010101101010110001101101011001100001010011101110011111100010000111010110001001111011011011100000100100011001101000100010011100110111110111000110100100000000111100100011000011101
//00000000001 01100111 1111111 000010111111111000111110110011110111000100110001110111001000000101111100000101001001001010011000001000001101100110100110101011100000100000101110100000011101010100110010101101011110001111101011001100001010011111110000011100010000111010110001001111011011011100000100100011010101000100010011100110000110111000110100100000001111100100011000111101


	public static String getAHexString() {
		final String HEXCHARS = "0123456789ABCDEF";
		Random rand = new Random();
		int len = rand.nextInt() % 99 + 1;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < len; i++) {
			int pos = rand.nextInt() % 16;
			sb.append(HEXCHARS.charAt(pos));
		}
		return sb.toString();
	}

	public static String hexChartoBin(char c) {
		switch (c) {
			case '0':
				return "0000";
			case '1':
				return "0001";
			case '2':
				return "0010";
			case '3':
				return "0011";
			case '4':
				return "0100";
			case '5':
				return "0101";
			case '6':
				return "0110";
			case '7':
				return "0111";
			case '8':
				return "1000";
			case '9':
				return "1001";
			case 'A':
				return "1010";
			case 'B':
				return "1011";
			case 'C':
				return "1100";
			case 'D':
				return "1101";
			case 'E':
				return "1110";
			case 'F':
				return "1111";

		}
		return null;
	}

}
