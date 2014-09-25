package com.mfino.hsm.thales7.util;

import java.util.LinkedHashMap;
import java.util.Map;

import org.jpos.iso.packager.DummyPackager;


/**
 * Thales Key schemes
 * @author POCHADRI
 *
 */
public class HThalesKeyScheme 
{
    public static char U = 'U';
    public static char Z = 'Z';
    public static char X = 'X';
    public static char Y = 'Y';
    public static char T = 'T';

    static Map keySchemes = new LinkedHashMap();
   
    static 
    {
        keySchemes.put(Z, 16);
        keySchemes.put(U, 32);
        keySchemes.put(X, 32);
        keySchemes.put(Y, 48);
        keySchemes.put(T, 48);
    }
    
     public  static int getKeyLength(char b) throws Exception
     {
    	 int     ret =   ((Integer) keySchemes.get(b)).intValue();
    	 return ret;
     }
}
