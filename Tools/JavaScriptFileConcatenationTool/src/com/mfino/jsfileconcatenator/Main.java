/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.jsfileconcatenator;

/**
 *
 * @author sandeepjs
 */
public class Main {

    public static void main(String[] args) throws Exception {
        try {
            JSFileConcatenator jspCont = new JSFileConcatenator(args[0], args[1], args[2]);
            jspCont.concatenateFiles();
            System.exit(0);
        } catch (Exception ex) {
            System.out.print(ex.toString());
            ex.printStackTrace(System.out);
            System.exit(1);
        }
    }
}
