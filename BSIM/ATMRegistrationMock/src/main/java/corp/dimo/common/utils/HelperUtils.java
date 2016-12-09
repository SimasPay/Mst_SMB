package corp.dimo.common.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

public class HelperUtils {
private static String salt = "Random$SaltValue#WithSpecialCharacters12@$@4&#%^$*";
	
	public static String getSalt() {
		return salt;
	}
	
	public static String getCurrentSession1() {
		String userID = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("userID").toString();
		return userID;
	}
	
	public static void showMessage(String header, String message) {
		FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, new FacesMessage(header, message));
	}
	
	@SuppressWarnings("static-access")
	public void setSalt(String salt) {
		this.salt = salt;
	}
	
	public static String generateUUID() {
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		Date date = new Date();
		String now = dateFormat.format(date);
		
		UUID idRandom = UUID.randomUUID();
		String id = idRandom.toString()+now;
		////System.out.println(id);
		return id;
	}
	
	public static boolean strNotNullNotEmpty(String str){
		boolean returnVal = false;
			if(str != null && !str.equals("")){
				returnVal = true;
			}
		return returnVal;
	}
	
	public static String md5(String input){
		String md5 = null;
		if(input == null) return null;
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			digest.update(input.getBytes(), 0, input.length());
			md5 = new BigInteger(1, digest.digest()).toString(16);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return md5;
	}
	
	public static Date getCurrentDateTime(){
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		try {
			String currentDateTimeString = dateFormat.format(date);
			return dateFormat.parse(currentDateTimeString);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}
	
	public static Date commonCurrentDateTime(String formatDate){
		DateFormat dateFormat = new SimpleDateFormat(formatDate);
		Date date = new Date();
		try {
			String currentDateTimeString = dateFormat.format(date);
			return dateFormat.parse(currentDateTimeString);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}
	
	public static String getCurrentDateString(){
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		return dateFormat.format(date);
	}
	
	public static String commonCurrentDateString(String formatDate){
		DateFormat dateFormat = new SimpleDateFormat(formatDate);
		Date date = new Date();
		return dateFormat.format(date);
	}
	
	public static String generateUniqueString(){
		String uuid = generateUUID();
		long l = ByteBuffer.wrap(uuid.toString().getBytes()).getLong();
        return Long.toString(l, Character.MAX_RADIX).toUpperCase();
	}
	
	public static int generateUniqueInteger(){
		return (int) (Math.random());
	}
	
	public static byte[] serialize(Object object) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(object);
        oos.close();
        return baos.toByteArray();
    }
	
	public static Object deserialize(InputStream stream) throws Exception {

        ObjectInputStream ois = new ObjectInputStream(stream);
        try {
            return ois.readObject();
        } finally {
            ois.close();
        }
    }
	
	public static String arrayToDelimitedString(String[] s, String d){
		String r = "";
		for (int i =  0; i < s.length ; i++){
			if (i != s.length - 1){
				r = r + s[i] + d;
			}else{
				r = r + s[i];
			}
		}
		return r;
	}
	
	public static String[] delimitedStringToArray(String s, String d){
		String[] r = null;
		r = s.split(d);
		return r;
	}
	
	public static String unPadRight(String s, char c) {
		   
		int end = s.length();
	    if (end == 0) return s;
	    
	    while ((0 < end) && (s.charAt(end - 1) == c)) end--;
	    return 0 < end ? s.substring(0, end) : s.substring(0, 1);
	}
	  
	  
	public static String unPadLeft(String s, char c){
	    int fill = 0;int end = s.length();
	    if (end == 0) return s;
	    
	    while ((fill < end) && (s.charAt(fill) == c)) fill++;
	    
	    return fill < end ? s.substring(fill, end) : s.substring(fill - 1, end);
	}
	  
	public static String padleft(String s, int len, char c){
		try {
			s = s.trim();
			if (s.length() > len) throw new Exception("invalid len " + s.length() + "/" + len);
				    
			StringBuffer d = new StringBuffer(len);
			int fill = len - s.length();
			
			while (fill-- > 0) d.append(c);
			d.append(s);
			return d.toString();
		} catch (Exception e) {
			return s;
		}
	}
			  
	public static String padright(String s, int len, char c){
		try {
			s = s.trim();
			if (s.length() > len) throw new Exception("invalid len " + s.length() + "/" + len);
			
			StringBuffer d = new StringBuffer(len);
			int fill = len - s.length();
			d.append(s);
			
			while (fill-- > 0) d.append(c);
			return d.toString();
		} catch (Exception e) {
			return s;
		}
		
	}
	
	public static void main(String[] args){
		System.out.println(generateUniqueInteger());
	}

}
