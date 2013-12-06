/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.mb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Scanner;

/**
 *
 * @author Mfino
 */
public class Stocks {

    //snl1d1cpobat8mwvrj3y
    static String summaryFormat = "snld1c1pobat8mwvrj3y";
    static String valueFormat = "l1";

    static String strUrl = "http://finance.yahoo.com/d/quotes.csv?s=";
    static URL url = null;

    public static String getQuote(String sym)
            throws IOException {
        url = new URL(strUrl + sym + "&f=" + summaryFormat);
        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

        String inputLine;
        StringBuilder buffer = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            buffer.append(inputLine);
        }

        in.close();

        return buffer.toString();
    }

    public static String getQuoteValue(String sym)
            throws IOException {
        url = new URL(strUrl + sym + "&f=" + valueFormat);
        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

        String inputLine;
        StringBuilder buffer = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            buffer.append(inputLine);
        }

        in.close();

        return buffer.toString();
    }

    public static String getQuoteDetails(String sym, String fmt)
            throws IOException {
        url = new URL(strUrl + sym + "&f=" + fmt);
        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

        String inputLine;
        StringBuilder buffer = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            buffer.append(inputLine);
        }

        in.close();

        return buffer.toString();
    }


    public static void main(String args[])
            throws Exception {
        Scanner in = new Scanner(System.in);
        String line = in.nextLine();

        while (!line.equalsIgnoreCase("Q")) {
            String quoteContent = getQuote(line);
            System.out.println(quoteContent);

            line = in.nextLine();
        }

        in.close();
    }
}