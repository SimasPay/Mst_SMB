/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.isorequests.listener.util;

import java.net.Socket;

import com.solab.iso8583.IsoMessage;
import com.solab.iso8583.MessageFactory;

import test.IsoArtaClient;

/**
 *
 * @author admin
 */
public class Util {

    public static final int timeout = 30000;
//    public static IsoArtaClient client = null;
	    public static Socket socket = null;
	    public static Socket mobile8Socket = null;
	    public static Socket xlinkSocket = null;
    
    public static MessageFactory mfact = new MessageFactory();
    
    
    	
    public static String[] topupRequestParams =        {"2","3","4","7","11","12","13","15","18","32","33","35","37","39","40","41","42","43","48","49","60","61","62","63","70","90","98","100"};
    												//{"2",   "3",    "4",    "11",    "18",   "32",  "37",         "42",              "48",               "49","63","90"};
                                                  //   {"0","180000","5000","601122","6011",   "881","108551910200","000000000000000","1101088911111111 000000050000","360","110","672356562357"};
    public static String[] topupRequestDefaultvalues = {"1111111111111111111","180000","5000","7","601122","12","13","15","6011","881","33","35","108551910200","39","40","41","000000000000000","43","1101088911111111 000000050000","360","60","61","62","110","70","672356562357","98","100"};
    
    public static String[] paymentRequestDefaultvalues = {"0","280000","5000","6011","881","1085B1910200","000000000000000","1101088911111111 000000050000","360","110"};
    public static String[] inquiryRequestDefaultvalues = {"0","180000","5000","6011","881","1085B1910200","000000000000000","1101088911111111 000000050000","360","110"};
    public static String[] topupReversalRequestDefaultvalues = {"0","180000","5000","6011","881","1085B1910200","000000000000000","1101088911111111 000000050000","360","110","1101088911111111"};
    public static String[] paymentReversalDefaultvalues = {"0","180000","5000","6011","881","1085B1910200","000000000000000","1101088911111111 000000050000","360","110","1101088911111111"};

    public static String[] inquiryRequestParams = {"2","3","4","18","32","37","42","48","49","63"};
    public static String[] paymentRequestParams = {"2","3","4","18","32","37","42","48","49","63","90"};
    public static String[] topReversalRequestParams = {"2","3","4","18","32","37","42","48","49","63","90"};
    public static String[] paymentReversalRequestParams = {"2","3","4","18","32","37","42","48","49","63","90"};

}
