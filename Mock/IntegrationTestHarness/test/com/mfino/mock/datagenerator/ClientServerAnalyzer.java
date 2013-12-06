/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.mock.datagenerator;

import com.mfino.mock.integrationtestharness.commons.ITHLogger;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;

/**
 *
 * @author sunil
 */
public class ClientServerAnalyzer {
@Before
    public void setUp() {

    }

    @After
    public void tearDown() {
    }

    @Test
    public void test(){
        analyze();
    }
    public String analyze() {
        Logger logger=ITHLogger.getLogger();
        String logAnalysis="";
        String failureString="ERROR";
        String mdnString="SMS_sourceMsisdn=";
        String linkIdString="Link ID=";
        String mdn="";
        int nullDataCount=0;
        int nullDataCountServer=0;
        String dataReceivedString="WinFacadeWeb";
        String responseMessage="HTTP/1.1 200 OK";
        String respondedHttpCount="UTK Resonse Sent";
        String receivedHttpCount="UTK Request Received";
        String sourceMdnIDTag="5078=";
        String sessionId="Session ID=";
        String backEndResponseMessage="Begin sending reply to message";
        
        int totalFailedRecords=0;
        int totalRecordsReceivedByHTTPSrvr=0;
        int totalRcordsRespondedByHTTPSrvr=0;
        int failureRecordsReceivedByHTTPServer=0;
        int failureRecordsRespondedByHttpServer=0;
        int failureRecordsReceivedByBckEndSrvr=0;
        int failureRecordsRespondedByBckEndSrvr=0;
        int respondedHttpCountInt=0;
        int receivedHttpCountInt=0;
        
        HashMap<Long,Boolean> failedMdnListClient=new HashMap();
        HashMap<Integer,Boolean> failedMdnListHTTPServer=new HashMap();
        HashMap<Long,Boolean> failedMdnListBackEnd=new HashMap();

        HashMap<Integer,Long> linkIdMap=new HashMap();
        HashMap<String,String> sessionIdMap=new HashMap();
        HashMap<String,Boolean> mdnsRespondedByBkndSrvr=new HashMap();

        try {
            FileReader ithLogFile = new FileReader("C:/ITH.log");
            BufferedReader br = new BufferedReader(ithLogFile);
            String lineContent = "";
            int mdnLength=mdnString.length();
            int linkIdLength=linkIdString.length();
            //Code to process Client Log
            while ((lineContent = br.readLine()) != null) {
                if (lineContent.contains(failureString) && lineContent.contains(mdnString)) {
                    totalFailedRecords++;
                    mdn = lineContent.substring(lineContent.indexOf(mdnString) + mdnLength, lineContent.indexOf(mdnString) + mdnLength + 10);
                    if (!mdn.contains("null")) {
                        failedMdnListClient.put(Long.parseLong(mdn), Boolean.FALSE);
                    } else {
                        nullDataCount++;
                    }
                    mdn = "";
                }
            }
            br.close();
            ithLogFile.close();
            
            FileReader httpLogFile = new FileReader("C:/HTTP.log");
            BufferedReader httpbr = new BufferedReader(httpLogFile);
            int linkId=0;
            //Code to process HTTP Log
            while ((lineContent = httpbr.readLine()) != null) {
                if (lineContent.contains(mdnString) && lineContent.contains(dataReceivedString)) {
                    totalRecordsReceivedByHTTPSrvr++;
                    mdn = lineContent.substring(lineContent.indexOf(mdnString) + mdnLength, lineContent.indexOf(mdnString) + mdnLength + 10);
                    if (!mdn.contains("null")) {
                        if (failedMdnListClient.containsKey(Long.parseLong(mdn))) {
                            //logger.info(mdn);
                            if (!failedMdnListClient.get(Long.parseLong(mdn))) {
                                failureRecordsReceivedByHTTPServer++;
                                failedMdnListClient.put(Long.parseLong(mdn), Boolean.TRUE);
                                linkIdMap.put(linkId, Long.parseLong(mdn));
                                failedMdnListHTTPServer.put(linkId, Boolean.FALSE);
                            }
                        }
                    } else {
                        nullDataCountServer++;
                    }
                    mdn = "";
                } else if (lineContent.startsWith(responseMessage)) {
                    if (failedMdnListHTTPServer.containsKey(linkId)) {
                        if(!failedMdnListHTTPServer.get(linkId)){
                            failedMdnListHTTPServer.put(linkId, Boolean.TRUE);
                            failureRecordsRespondedByHttpServer++;
                        }
                    }
                    totalRcordsRespondedByHTTPSrvr++;
                }else if (lineContent.contains(receivedHttpCount)) {
                    receivedHttpCountInt++;
                } else if (lineContent.contains(respondedHttpCount)) {
                    respondedHttpCountInt++;
                }else if (lineContent.contains(linkIdString)) {
                    String temp = lineContent.substring(lineContent.indexOf(linkIdString) + linkIdLength, lineContent.indexOf(linkIdString) + linkIdLength + 6);
                    temp = temp.substring(0, temp.indexOf(','));
                    linkId = Integer.parseInt(temp);
                }
            }
            
            httpbr.close();
            httpLogFile.close();
            
            Set<Entry<Integer,Boolean>> c = failedMdnListHTTPServer.entrySet();
            Iterator itr = c.iterator();
            while (itr.hasNext()) {
                Entry<Integer, Boolean> collEntry = (Entry<Integer, Boolean>) itr.next();
                if (!collEntry.getValue()) {
                    String str=String.format("Link ID=%s  MDN=%s didReplySent=%s\n", collEntry.getKey(),linkIdMap.get(collEntry.getKey()), collEntry.getValue());
                    logger.info(str);
                    failedMdnListBackEnd.put(linkIdMap.get(collEntry.getKey()),Boolean.FALSE);
                }
            }
            
            FileReader backendLogFile = new FileReader("C:/backend.log");
            BufferedReader backendbr = new BufferedReader(backendLogFile);
            int backendReceivedCount=0;
            int sourceMdnIDTagLen=sourceMdnIDTag.length();
            int mdnsFldByHttpButPrsBkend=0;
            //Code to process BackEndServer Log
            String currentSessionId="";
            while ((lineContent = backendbr.readLine()) != null) {
                if (lineContent.startsWith(sourceMdnIDTag)) {
                    backendReceivedCount++;
                    mdn = lineContent.substring(sourceMdnIDTagLen, lineContent.lastIndexOf('.'));
                    if (!mdn.contains("null")) {
                        if (failedMdnListBackEnd.containsKey(Long.parseLong(mdn))) {
                            if (!failedMdnListBackEnd.get(Long.parseLong(mdn))) {
                                mdnsFldByHttpButPrsBkend++;
                                failedMdnListBackEnd.put(Long.parseLong(mdn), Boolean.TRUE);
                                mdnsRespondedByBkndSrvr.put(mdn,Boolean.FALSE);
                                failureRecordsReceivedByBckEndSrvr++;
                                sessionIdMap.put(currentSessionId,mdn);
                            }

                        }

                    }
                    mdn = "";
                }else if(lineContent.contains(sessionId)){
                    currentSessionId=lineContent.substring(lineContent.indexOf(sessionId) + sessionId.length(), lineContent.indexOf(")"));
                } else if (lineContent.contains(backEndResponseMessage)) {
                    if (sessionIdMap.containsKey(currentSessionId)) {
                        mdnsRespondedByBkndSrvr.put(sessionIdMap.get(currentSessionId), Boolean.TRUE);
                        failureRecordsRespondedByBckEndSrvr++;
                    }
                }
            }

            backendbr.close();
            backendLogFile.close();            

            logAnalysis=String.format("\nTotal Number of FailureRecords On Client ::%s\nTotal Records Received by the HTTP server::%s\n" +
                    "Total Records Responded by the HTTP server::%s\nFailure Records Received by HTTP Server::%s\n" +
                    "Failure Records Resonded by HTTP Server::%s\nBackendServer.....\nFailure Records Received by BACKEND Server::%s\n" +
                    "Failure Records Resonded by BACKEND Server::%s\n" +
                    "Null Data Sent::%s\nNull Data Received by the server::%s\nFailure at HTTP But Processessed by backEnd=%s" +
                    "\n My Analysis HTTP Server...........................\nReceived Count::%s\n Responded Count::%s",
                    totalFailedRecords,
                    totalRecordsReceivedByHTTPSrvr,
                    totalRcordsRespondedByHTTPSrvr,
                    failureRecordsReceivedByHTTPServer,
                    failureRecordsRespondedByHttpServer,
                    failureRecordsReceivedByBckEndSrvr,
                    failureRecordsRespondedByBckEndSrvr,
                    nullDataCount,                    
                    nullDataCountServer,
                    mdnsFldByHttpButPrsBkend,
                    receivedHttpCountInt,
                    respondedHttpCountInt);

            logger.info(logAnalysis);

        } catch (IOException e) {
            ITHLogger.getLogger().error("Exception in opening the file" + e.getMessage());
        }

        return logAnalysis;
    }
}

