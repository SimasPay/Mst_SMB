/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.util;

import java.util.Random;

import org.apache.commons.lang.StringUtils;

/**
 *
 * @author Siddhartha Chinthapally
 */
public class PasswordGenUtil {
    private static Random myGen = new Random(System.currentTimeMillis());
    private static final String DEFAULT_SOURCE = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstwxyz01234567890";//!#
    private static final int DEFAULT_PWD_LEN = 6;
    private static final String PASSWORD_PATTERN = "((?=.*\\d)(?=.*[A-Z]).{"+DEFAULT_PWD_LEN+",})";

    public static String generate(){
        return generate(DEFAULT_SOURCE, DEFAULT_PWD_LEN);
    }

    public static String generate(String source, int len){
        StringBuffer buf = new StringBuffer(len);
        for (int i = 0; i < len; i++)
            buf.append(source.charAt(myGen.nextInt(Integer.MAX_VALUE)%source.length()));
        String password = buf.toString();
        if(!checkPassword(password)){
        	password = generate(source,len);
        }
        return password;
    }
    
    public static boolean checkPassword(String password){
		if(StringUtils.isBlank(password)){
    		return false;
    	}
    	if(password.length()<DEFAULT_PWD_LEN){
    		return false;
    	}
    	if(!password.matches(PASSWORD_PATTERN)){
    		return false;
    	}
       return true;
    }
    
   /* public static void main(String a[]){
    	String password ="122A";
    	System.out.print(checkPassword(password));
    	System.out.print(generate());
    	
    }*/
    
    
 }
