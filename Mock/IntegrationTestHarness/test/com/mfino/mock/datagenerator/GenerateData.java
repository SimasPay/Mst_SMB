/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.mock.datagenerator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import org.junit.Test;

/**
 *
 * @author sunil
 */
public class GenerateData {
     @Test
    public void ReadWriteMultipleFileOfSameSize(){
        try {
            String subFile = System.getProperty("user.dir") + "/subscriber_report.csv";
            String merFile = System.getProperty("user.dir") + "/merchant_report.csv";
            FileReader subFileReader = new FileReader(subFile);
            FileReader merFileReader = new FileReader(merFile);

            BufferedReader subBr = new BufferedReader(subFileReader);
            BufferedReader merBr = new BufferedReader(merFileReader);
            String sublineContent = "";
            String merlineContent = "";
            int eachFileSize=250;
            int inputFileSize=1500;

            int totalFiles=inputFileSize/eachFileSize;
            int actualFileSize=eachFileSize/2;
            String filepath="";

            ArrayList<File> subFileList=new ArrayList(totalFiles);
            ArrayList<File> merFileList=new ArrayList(totalFiles);
            for(int i=0;i<totalFiles;i++){
                filepath=System.getProperty("user.dir") + "/subscriber_report"+i+".csv";
                subFileList.add(new File(filepath));
                filepath=System.getProperty("user.dir") + "/merchant_report"+i+".csv";
                merFileList.add(new File(filepath));
            }

            ArrayList<PrintWriter> subPrintWriterList=new ArrayList(totalFiles);
            ArrayList<PrintWriter> merPrintWriterList=new ArrayList(totalFiles);
            for(int i=0;i<totalFiles;i++){
                subPrintWriterList.add(new PrintWriter(new FileWriter(subFileList.get(i))));
                subPrintWriterList.get(i).println(actualFileSize);
                merPrintWriterList.add(new PrintWriter(new FileWriter(merFileList.get(i))));
                merPrintWriterList.get(i).println(actualFileSize);
            }

            sublineContent = subBr.readLine();
            merlineContent=merBr.readLine();
            int count=0;
            int currentFile=0;
            while (((sublineContent = subBr.readLine()) != null)&& ((merlineContent = merBr.readLine()) != null)) {

                if (count >= actualFileSize) {
                    currentFile++;
                    count=0;
                } else if (count < actualFileSize) {
                    subPrintWriterList.get(currentFile).println(sublineContent);
                    merPrintWriterList.get(currentFile).println(merlineContent);
                }
                count++;
            }
            for(int i=0;i<totalFiles;i++){
                subPrintWriterList.get(i).close();
                merPrintWriterList.get(i).close();
            }
            subBr.close();
            merBr.close();

        } catch (IOException e) {
        }

    }
   
    public void ReadWriteFile(){
        try {
            String subFile = System.getProperty("user.dir") + "/subscriber_report.csv";
            String merFile = System.getProperty("user.dir") + "/merchant_report.csv";
            FileReader subFileReader = new FileReader(subFile);
            FileReader merFileReader = new FileReader(merFile);

            BufferedReader subBr = new BufferedReader(subFileReader);
            BufferedReader merBr = new BufferedReader(merFileReader);
            String sublineContent = "";
            String merlineContent = "";
            
            File subFile100 = new File(System.getProperty("user.dir") + "/subscriber_report100.csv");
            File subFile250 = new File(System.getProperty("user.dir") + "/subscriber_report250.csv");
            File subFile500 = new File(System.getProperty("user.dir") + "/subscriber_report500.csv");
            File subFile750 = new File(System.getProperty("user.dir") + "/subscriber_report750.csv");
            File subFile1000 = new File(System.getProperty("user.dir") + "/subscriber_report1000.csv");
            File subFile1500 = new File(System.getProperty("user.dir") + "/subscriber_report1500.csv");

            File merFile100 = new File(System.getProperty("user.dir") + "/merchant_report100.csv");
            File merFile250 = new File(System.getProperty("user.dir") + "/merchant_report250.csv");
            File merFile500 = new File(System.getProperty("user.dir") + "/merchant_report500.csv");
            File merFile750 = new File(System.getProperty("user.dir") + "/merchant_report750.csv");
            File merFile1000 = new File(System.getProperty("user.dir") + "/merchant_report1000.csv");
            File merFile1500 = new File(System.getProperty("user.dir") + "/merchant_report1500.csv");

            PrintWriter outputSub100 = new PrintWriter(new FileWriter(subFile100));
            PrintWriter outputSub250 = new PrintWriter(new FileWriter(subFile250));
            PrintWriter outputSub500 = new PrintWriter(new FileWriter(subFile500));
            PrintWriter outputSub750 = new PrintWriter(new FileWriter(subFile750));
            PrintWriter outputSub1000 = new PrintWriter(new FileWriter(subFile1000));
            PrintWriter outputSub1500 = new PrintWriter(new FileWriter(subFile1500));

            PrintWriter outputMer100 = new PrintWriter(new FileWriter(merFile100));
            PrintWriter outputMer250 = new PrintWriter(new FileWriter(merFile250));
            PrintWriter outputMer500 = new PrintWriter(new FileWriter(merFile500));
            PrintWriter outputMer750 = new PrintWriter(new FileWriter(merFile750));
            PrintWriter outputMer1000 = new PrintWriter(new FileWriter(merFile1000));
            PrintWriter outputMer1500 = new PrintWriter(new FileWriter(merFile1500));

            outputMer100.println("100");
            outputMer250.println("250");
            outputMer500.println("500");
            outputMer750.println("750");
            outputMer1000.println("1000");
            outputMer1500.println("1500");

            outputSub100.println("100");
            outputSub250.println("250");
            outputSub500.println("500");
            outputSub750.println("750");
            outputSub1000.println("1000");
            outputSub1500.println("1500");

            
            sublineContent = subBr.readLine();
            merlineContent=merBr.readLine();
            int count=0;

            while (((sublineContent = subBr.readLine()) != null)&& ((merlineContent = merBr.readLine()) != null)) {

                if (count < 50) {
                    outputMer100.println(merlineContent);
                    outputMer250.println(merlineContent);
                    outputMer500.println(merlineContent);
                    outputMer750.println(merlineContent);
                    outputMer1000.println(merlineContent);
                    outputMer1500.println(merlineContent);

                    outputSub100.println(sublineContent);
                    outputSub250.println(sublineContent);
                    outputSub500.println(sublineContent);
                    outputSub750.println(sublineContent);
                    outputSub1500.println(sublineContent);
                    outputSub1000.println(sublineContent);

                } else if (count < 125) {
                    outputMer250.println(merlineContent);
                    outputMer500.println(merlineContent);
                    outputMer750.println(merlineContent);
                    outputMer1000.println(merlineContent);
                    outputMer1500.println(merlineContent);

                    outputSub250.println(sublineContent);
                    outputSub500.println(sublineContent);
                    outputSub750.println(sublineContent);
                    outputSub1500.println(sublineContent);
                    outputSub1000.println(sublineContent);

                } else if (count < 250) {
                    outputMer500.println(merlineContent);
                    outputMer750.println(merlineContent);
                    outputMer1000.println(merlineContent);
                    outputMer1500.println(merlineContent);

                    outputSub500.println(sublineContent);
                    outputSub750.println(sublineContent);
                    outputSub1500.println(sublineContent);
                    outputSub1000.println(sublineContent);

                } else if (count < 375) {
                    outputMer750.println(merlineContent);
                    outputMer1000.println(merlineContent);
                    outputMer1500.println(merlineContent);

                    outputSub750.println(sublineContent);
                    outputSub1500.println(sublineContent);
                    outputSub1000.println(sublineContent);

                }else if(count < 500){
                    outputMer1000.println(merlineContent);
                    outputMer1500.println(merlineContent);

                    outputSub1500.println(sublineContent);
                    outputSub1000.println(sublineContent);

                }else if(count < 750){
                    outputMer1500.println(merlineContent);

                    outputSub1500.println(sublineContent);

                }
                count++;
            }
            outputMer100.close();
            outputMer250.close();
            outputMer500.close();
            outputMer750.close();
            outputMer1500.close();
            outputMer1000.close();


            outputSub100.close();
            outputSub250.close();
            outputSub500.close();
            outputSub750.close();
            outputSub1500.close();
            outputSub1000.close();
            subBr.close();
            merBr.close();

        } catch (IOException e) {
        }

    }

}
