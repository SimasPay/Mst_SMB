/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.mock.testharnessbliimpl;

import com.mfino.mock.integrationtestharness.commons.ITHConstants;
import com.mfino.mock.integrationtestharness.commons.ITHLogger;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import org.slf4j.Logger;

/**
 *
 * @author sunil
 */
public class FreqTestProducer implements Runnable {

    private BlockingQueue queuePool;
    Logger logger;
    final int subscriberList[] = {ITHConstants.CHANGE_PIN_INT, ITHConstants.MCASH_TOPUP_INT,
        ITHConstants.CHECK_BALANCE_INT, ITHConstants.RESET_PIN_INT,
        ITHConstants.SHARE_LOAD_INT, ITHConstants.SUBSRICPTION_ACTIVATION_INT,
        ITHConstants.GET_TRANSACTIONS_INT
    };
    final int merchantList[] = {ITHConstants.MOBILE_AGENT_RECHARGE_INT, ITHConstants.MERCHANT_CHECK_BALANCE_INT,
        ITHConstants.MERCHANT_MPIN_RESET_INT, ITHConstants.MOBILE_AGENT_DISTRIBUTE_INT,
        ITHConstants.MERCHANT_GET_TRANSACTIONS_INT, ITHConstants.MERCHANT_CHANGE_PIN_INT};
    final int mcashList[] = {ITHConstants.MCASH_TO_MCASH_INT, ITHConstants.MCASH_BALANCE_INQUIRY_INT,
        ITHConstants.MCASH_BALANCE_INQUIRY_INT,
        ITHConstants.GET_MCASH_TRANSACTIONS_INT, ITHConstants.CHANGE_MCASH_PIN_INT};

    FreqTestProducer(BlockingQueue queue) {
        logger = ITHLogger.getLogger();
        queuePool = queue;
        logger.info("Freq Prod:: Initialized");
    }

    public void run() {

        logger.info("Freq Prod:: Run Initialized");

//        if(inputVO==null){
//            logger.error("Input VO Null");
//            return;
//        }
//
//        Vector<String> srvName=inputVO.getService_Name();
//
//
//        int freqCounter=0;
//
//        int frequency=inputVO.getFrequency();
//        while(freqCounter<frequency)
//        {
//            Random randomGenerator = new Random();
//
//
//            int serviceIndex=randomGenerator.nextInt(inputVO.getService_Name().size());
//            inputVO.setSMS_serviceName(srvName.get(serviceIndex));
//            //incrementVO(inputVO);
//            intializeURL(inputVO);
//
//            try{
//                logger.info("Outgoing URL in Producer is ::"+inputVO.getOutgoingUrl());
//                queuePool.put(inputVO.getOutgoingUrl());
//            }
//            catch(InterruptedException e){
//                logger.warn( "Exception while adding to Blocking Queue with error Message:"+e.getMessage());
//
//            }
//            freqCounter++;
//
//        }
    }

    private void intializeURL(TestHarnessValueObject input, int service) {

        switch (service) {
            case ITHConstants.CHANGE_PIN_INT:
                input.setOutgoingUrl("/WinFacadeWeb/SmsServicesServlet?SMS_newPin=" + input.getSMS_newPin() +
                        "&SMS_oldPin=" + input.getSMS_oldPin() + "&SMS_serviceName=change_pin" +
                        "&SMS_sourceMsisdn=" + input.getSMS_sourceMsisdn());
                break;
            case ITHConstants.MOBILE_AGENT_RECHARGE_INT:
                input.setOutgoingUrl("/WinFacadeWeb/SmsServletMA?SMS_pin=" + input.getSMS_pin() +
                        "&SMS_rechargeAmount=" + input.getSMS_rechargeAmount() +
                        "&SMS_serviceName=mobileAgentRecharge&SMS_bucketType=" + input.getSMS_bucketType() +
                        "&SMS_sourceMsisdn=" + input.getSMS_sourceMsisdn() + "&SMS_destMsisdn=" + input.getSMS_destMsisdn());
                break;
            case ITHConstants.MCASH_TOPUP_INT:
                input.setOutgoingUrl("/WinFacadeWeb/SmsMCashServlet?SMS_serviceName=mcash_topup&SMS_sourceMsisdn=" +
                        input.getSMS_sourceMsisdn() + "&SMS_destMsisdn=" + input.getSMS_destMsisdn() +
                        "&SMS_amount=" + input.getSMS_Amount() + "&SMS_bucketType=" + input.getSMS_bucketType() +
                        "&SMS_mCashPin=" + input.getSMS_mCashPin());
                break;
            case ITHConstants.CHECK_BALANCE_INT:
                input.setOutgoingUrl("/WinFacadeWeb/SmsServicesServlet?SMS_pin=" + input.getSMS_pin() +
                        "&SMS_serviceName=check_balance&SMS_sourceMsisdn=" + input.getSMS_sourceMsisdn());
                break;
            case ITHConstants.MERCHANT_CHECK_BALANCE_INT:
                input.setOutgoingUrl("/WinFacadeWeb/SmsServicesServletMA?SMS_pin=" + input.getSMS_pin() +
                        "&SMS_serviceName=check_balance&SMS_sourceMsisdn=" + input.getSMS_sourceMsisdn());
                break;
            case ITHConstants.RESET_PIN_INT:
                input.setOutgoingUrl("/WinFacadeWeb/SmsServicesServlet?SMS_newPin=" + input.getSMS_newPin() +
                        "&SMS_secretAnswer=" + input.getSMS_secretAnswer() + "&SMS_serviceName=reset_pin&SMS_sourceMsisdn=" + input.getSMS_sourceMsisdn());
                break;
            case ITHConstants.MERCHANT_MPIN_RESET_INT:
                input.setOutgoingUrl("/WinFacadeWeb/SmsServicesServletMA?SMS_newPin=" + input.getSMS_newPin() +
                        "&SMS_secretAnswer=" + input.getSMS_secretAnswer() + "&SMS_serviceName=reset_pin&SMS_sourceMsisdn=" +
                        input.getSMS_sourceMsisdn());
                break;
            case ITHConstants.SHARE_LOAD_INT:
                input.setOutgoingUrl("/WinFacadeWeb/SmsServicesServlet?SMS_pin=" + input.getSMS_pin() +
                        "&SMS_rechargeAmount==" + input.getSMS_rechargeAmount() + "&SMS_serviceName=share_load&SMS_sourceMsisdn=" + input.getSMS_sourceMsisdn() + "&SMS_destMsisdn=" + input.getSMS_destMsisdn());
                break;
            case ITHConstants.MCASH_TO_MCASH_INT:
                input.setOutgoingUrl("/WinFacadeWeb/SmsMCashServlet?SMS_mCashPin=" + input.getSMS_mCashPin() +
                        "&SMS_amount=" + input.getSMS_Amount() + "&SMS_serviceName=mcash_to_mcash&SMS_sourceMsisdn=" + input.getSMS_sourceMsisdn() + "&SMS_destMsisdn=" + input.getSMS_destMsisdn() + "&SMS_mCashMessage=" + input.getSMS_Message());
                break;

            case ITHConstants.MCASH_BALANCE_INQUIRY_INT:
                input.setOutgoingUrl("/WinFacadeWeb/SmsMCashServlet?SMS_mCashPin=" + input.getSMS_mCashPin() +
                        "&SMS_serviceName=mCash_balance_inquiry&SMS_sourceMsisdn=" + input.getSMS_sourceMsisdn());
                break;

            case ITHConstants.GET_MCASH_TRANSACTIONS_INT:
                input.setOutgoingUrl("/WinFacadeWeb/SmsMCashServlet?SMS_mCashPin=" + input.getSMS_mCashPin() +
                        "&SMS_serviceName=get_mCash_transactions&SMS_sourceMsisdn=" + input.getSMS_sourceMsisdn());
                break;
            case ITHConstants.MOBILE_AGENT_DISTRIBUTE_INT:
                input.setOutgoingUrl("/WinFacadeWeb/SmsServicesServletMA?SMS_pin=" + input.getSMS_pin() +
                        "&SMS_serviceName=mobileAgentDistribute&SMS_sourceMsisdn=" + input.getSMS_sourceMsisdn() +
                        "&SMS_destMsisdn=" + input.getSMS_destMsisdn() + "&SMS_distributeAmount=" + input.getSMS_distributeAmount());
                break;

            case ITHConstants.SUBSRICPTION_ACTIVATION_INT:
                input.setOutgoingUrl("/WinFacadeWeb/SmsServicesServlet?SMS_pin=" + input.getSMS_pin() +
                        "&SMS_serviceName=subscriber_activation&SMS_sourceMsisdn=" + input.getSMS_sourceMsisdn() +
                        "&SMS_secretAnswer=" + input.getSMS_secretAnswer() + "&SMS_contactNumber=" + input.getSMS_contactNumber());
                break;

            case ITHConstants.GET_TRANSACTIONS_INT:
                input.setOutgoingUrl("/WinFacadeWeb/SmsServicesServlet?SMS_pin=" + input.getSMS_pin() +
                        "&SMS_serviceName=get_transactions&SMS_sourceMsisdn=" + input.getSMS_sourceMsisdn());
                break;

            case ITHConstants.MERCHANT_GET_TRANSACTIONS_INT:
                input.setOutgoingUrl("/WinFacadeWeb/SmsServicesServletMA?SMS_pin=" + input.getSMS_pin() +
                        "&SMS_serviceName=get_transactions&SMS_sourceMsisdn=" + input.getSMS_sourceMsisdn());
                break;

            case ITHConstants.MERCHANT_CHANGE_PIN_INT:
                input.setOutgoingUrl("/WinFacadeWeb/SmsServicesServletMA?SMS_newPin=" + input.getSMS_newPin() +
                        "&SMS_oldPin=" + input.getSMS_oldPin() + "&SMS_serviceName=change_pin&SMS_sourceMsisdn=" +
                        input.getSMS_sourceMsisdn());
                break;
            case ITHConstants.CHANGE_MCASH_PIN_INT:
                input.setOutgoingUrl("/WinFacadeWeb/SmsMCashServlet?SMS_newPin=" + input.getSMS_newPin() +
                        "&SMS_oldPin=" + input.getSMS_oldPin() + "&SMS_serviceName=change_mCash_pin&SMS_sourceMsisdn=" +
                        input.getSMS_sourceMsisdn());
                break;
            default:
                input.setOutgoingUrl("Invalid Service Name Entered");

        }
        logger.info("Freq Prod:: URL Initialized to :::" + input.getOutgoingUrl());

    }

    public ArrayList readFile(String reportName, int typeOfList) {
        FileReader fileName;
        BufferedReader br;
        ArrayList<Integer> validUTKList = new ArrayList();
        int modValue = 1;
        if (typeOfList == ITHConstants.SUBSCRIBER_LIST) {
            modValue = subscriberList.length;
            for (int s : subscriberList) {
                validUTKList.add(s);
            }

        } else if (typeOfList == ITHConstants.MERCHANT_LIST) {
            modValue = merchantList.length;
            for (int s : merchantList) {
                validUTKList.add(s);
            }

        } else if (typeOfList == ITHConstants.MCASH_LIST) {
            modValue = mcashList.length;
            for (int s : mcashList) {
                validUTKList.add(s);
            }
        }

        try {
            String filePath = System.getProperty("user.dir");
            fileName = new FileReader(filePath+ "/"+ reportName + ".csv");
            br = new BufferedReader(fileName);
            String lineContent = "";
            TestHarnessValueObject input = new TestHarnessValueObject();
            lineContent = br.readLine();
            int no_of_records = Integer.parseInt(lineContent);
            ArrayList<String> urlList = new ArrayList(no_of_records);
            int count = 0;
            while ((lineContent = br.readLine()) != null) {
                int val = count % modValue;
                count++;
                intializeURL(input, validUTKList.get(val));
                urlList.add(input.getOutgoingUrl());
                processLine(input,lineContent);
            }
            return urlList;
        } catch (IOException e) {
            ITHLogger.getLogger().error("Exception in opening the file" + e.getMessage());
        }
        return null;

    }

    private void processLine(TestHarnessValueObject input, String line){
        int count=0;
        int beginIndex = 0;
        int  endIndex = line.indexOf(',');
        //TODO: Need to check the type of ReportName we are processing and based on it we needto process the request
        //currently we are assuming it is for reset_pin
        while (endIndex > beginIndex) {
            addValue(input, line.substring(beginIndex, endIndex), count);
            beginIndex = endIndex + 1;
            endIndex = line.indexOf(',', beginIndex);
            count++;
            if (endIndex == -1) {
                addValue(input, line.substring(beginIndex), count);
                break;
            }
        }
    }

    private void addValue(TestHarnessValueObject input, String str, int count ){
        switch(count){
            case 0:
                input.setSMS_pin(str);
                break;
            case 1:
                input.setSMS_oldPin(str);
                break;
            case 2:
                input.setSMS_newPin(str);
                break;
            case 3:
                input.setSMS_Amount(str);
                break;
            case 4:
                input.setSMS_mCashPin(str);
                break;
            case 5:
                input.setSMS_contactNumber(str);
                break;
            case 6:
                input.setSMS_sourceMsisdn(str);
                break;
            case 7:
                input.setSMS_secretAnswer(str);
                break;
            case 8:
                input.setSMS_destMsisdn(str);
                break;
            case 9:
                input.setSMS_bucketType(str);
                break;
            case 10:
                input.setSMS_rechargeAmount(str);
                break;
            case 11:
                input.setSMS_distributeAmount(str);
                break;
            case 12:
                input.setSMS_Message(str);
                break;
            case 13:
                input.setSMS_mCashMessage(str);
                break;
            default:
                break;

        }
        return;

    }
}
