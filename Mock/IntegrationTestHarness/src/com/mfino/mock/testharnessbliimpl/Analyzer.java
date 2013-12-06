/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.mock.testharnessbliimpl;

import com.mfino.mock.integrationtestharness.commons.ITHLogger;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 *
 * @author sunil
 */
public class Analyzer {
        public String analyze() {
        String logAnalysis="";
        String sucessfulString="Response: HTTP";
        String failureString="ERROR";
        String socketException="SocketException";
        String noHttpResponseException="NoHttpResponseException";
        String startTime="StartTime::";
        String endTime="EndTime::";
        String totalTimeTaken="TotalTimeTakenInMilliSeconds::";

        try {
            FileReader logFile = new FileReader("C:/ITH.log");
            BufferedReader br = new BufferedReader(logFile);
            String lineContent = "";
            int successfullRecords = 0;
            int failureRecords=0;
            int socketExceptionCount=0;
            int noHttpResponseExceptionCount=0;
            String startTimeString="";
            String endTimeString="";
            String totalTimeTakenString="";
            int transactionPerSec=0;

            while ((lineContent = br.readLine()) != null) {
                if(lineContent.contains(sucessfulString)){
                    successfullRecords++;
                }else if(lineContent.contains(failureString)){
                    failureRecords++;
                }else if(lineContent.contains(socketException)){
                    socketExceptionCount++;
                }else if(lineContent.contains(noHttpResponseException)){
                    noHttpResponseExceptionCount++;
                }else if(lineContent.contains(startTime)){
                    startTimeString=lineContent.substring(lineContent.indexOf(startTime));
                }else if(lineContent.contains(endTime)){
                    endTimeString=lineContent.substring(lineContent.indexOf(endTime));
                }else if(lineContent.contains(totalTimeTaken)){
                    totalTimeTakenString=lineContent.substring(lineContent.indexOf(totalTimeTaken));
                    transactionPerSec+=Integer.parseInt(lineContent.substring(lineContent.indexOf("::")+2));
                }
            }

            logAnalysis=String.format("%s\n%s\n%s\nSucessful Transactions Processed Per Second::%s" +
                                "\nTotal Transactions Sent Per Second::%s\nTotal Records Processed::%s\nSuccessfull Recrords::%s\n"+
                                "Failure Records::%s\nSocketException Count::%s\n"+
                                "NoHttpResponseException Count::%s\nOther Error Count::%s\n ",
                                startTimeString,
                                endTimeString,
                                totalTimeTakenString,
                                (transactionPerSec==0)? 0:((successfullRecords+failureRecords-socketExceptionCount)*1000)/transactionPerSec,
                                (transactionPerSec==0)? 0:((successfullRecords+failureRecords)*1000)/transactionPerSec,
                                (successfullRecords+failureRecords),
                                successfullRecords,
                                failureRecords,
                                socketExceptionCount,
                                noHttpResponseExceptionCount,
                                ((failureRecords-socketExceptionCount)-noHttpResponseExceptionCount));
            ITHLogger.getLogger().info(logAnalysis);
        } catch (IOException e) {
            ITHLogger.getLogger().error("Exception in opening the file" + e.getMessage());
        }
        
        return logAnalysis;
    }


}
