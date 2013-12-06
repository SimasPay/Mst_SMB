package com.mfino.hsm.test;
import java.security.MessageDigest;

public class Hashing 
{
	private static final char[] DIGITS_LOWER =
			         {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
	
	private static final char[] DIGITS_UPPER =
			         {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
	
	public static String calculateDigestPin(String mdn, String pin) {
		MessageDigest md = null;

		try {
			md = MessageDigest.getInstance("SHA-256");
			md.update(pin.getBytes());
			md.update(mdn.getBytes());
			byte[] bytes = md.digest();
			char[] encodeHex = encodeHex(bytes);
			String calcPIN = new String(encodeHex);
			calcPIN = calcPIN.toUpperCase();
			return calcPIN;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static char[] encodeHex(byte[] data) 
	{
	    return encodeHex(data, true);
	}
	
	public static char[] encodeHex(byte[] data, boolean toLowerCase) 
	{
	        return encodeHex(data, toLowerCase ? DIGITS_LOWER : DIGITS_UPPER);
	}
	
	protected static char[] encodeHex(byte[] data, char[] toDigits) 
	{
	        int l = data.length;
	        char[] out = new char[l << 1];
	        // two characters form the hex value.
	        for (int i = 0, j = 0; i < l; i++) {
	            out[j++] = toDigits[(0xF0 & data[i]) >>> 4];
	           out[j++] = toDigits[0x0F & data[i]];
	        }
	        return out;
	}
	
	public static boolean isHexString(String s)
	{
		for(int i=0;i<s.length()-1;i++)
		{
			System.out.println("validating "+s.charAt(i));
			try
			{
				int c = Integer.parseInt(s.charAt(i)+"");
			}
			catch(NumberFormatException e)
			{
				if(!(s.charAt(i)=='A'||s.charAt(i)=='B'||s.charAt(i)=='C'||s.charAt(i)=='D'||s.charAt(i)=='E'
						||s.charAt(i)=='F'))
				{
					System.out.println(" tryinf for "+s.charAt(i));
					return false;
				}
			}
			
		}
		return true;
	}
	
	public static String convertPinForValidation(String pin, int len)
	{
		if(pin==null||pin.trim().equals("")) return pin;
		//lets find the last N digits from the given PIN
		final StringBuilder sb = new StringBuilder(len*2);
		int count = 0;
	    for(int i= pin.length()-1; i >= 0; i--)
	    {
	        final char c = pin.charAt(i);
	        if(c > 47 && c < 58){
	            sb.append(c);
	            count++;
	        }
	        if(count==len)
	        	break;
	    }
	    
	    if(count<len)
	    {
	    	int extraLen = len-count;
	    	for(int i=0;i<extraLen;i++)
	    	{
	    		sb.append("1");
	    	}
	    }
	    return sb.toString();
	}
	
	public static void main(String args[])
	{
		String mdn = args[0];
		String pin = args[1];
		String s = calculateDigestPin(mdn, pin);		
		System.out.println("hashed pin:"+s );
		//System.out.println("to hex string "+Integer.toHexString(123456));
		//System.out.println("isHexString(012E3):"+isHexString("012G3"));
		/*String res = convertPinForValidation("abagsggs765hhsa1",10);
		System.out.println("hashed to pin res: "+res);*/
	}
}
