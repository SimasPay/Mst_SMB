/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.isorequests.listener.util;

import test.IsoArtaClient;

/**
 *
 * @author admin
 */
public class Util {

    public static final int timeout = 30000;
    public static IsoArtaClient client = null;
    public static String[] topupRequestParams = {"2","3","4","11","18","32","37","42","48","49","63","90"};
    public static String[] topupRequestDefaultvalues = {"0","180000","5000","601122","6011","881","108551910200","000000000000000","1101088911111111 000000050000","360","110","672356562357"};
    public static String[] paymentRequestDefaultvalues = {"0","280000","5000","6011","881","1085B1910200","000000000000000","1101088911111111 000000050000","360","110"};
    public static String[] inquiryRequestDefaultvalues = {"0","180000","5000","6011","881","1085B1910200","000000000000000","1101088911111111 000000050000","360","110"};
    public static String[] topupReversalRequestDefaultvalues = {"0","180000","5000","6011","881","1085B1910200","000000000000000","1101088911111111 000000050000","360","110","1101088911111111"};
    public static String[] paymentReversalDefaultvalues = {"0","180000","5000","6011","881","1085B1910200","000000000000000","1101088911111111 000000050000","360","110","1101088911111111"};

    public static String[] inquiryRequestParams = {"2","3","4","18","32","37","42","48","49","63"};
    public static String[] paymentRequestParams = {"2","3","4","18","32","37","42","48","49","63","90"};
    public static String[] topReversalRequestParams = {"2","3","4","18","32","37","42","48","49","63","90"};
    public static String[] paymentReversalRequestParams = {"2","3","4","18","32","37","42","48","49","63","90"};

}
