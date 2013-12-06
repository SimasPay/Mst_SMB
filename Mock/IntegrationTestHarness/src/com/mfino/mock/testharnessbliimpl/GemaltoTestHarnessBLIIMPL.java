/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.mock.testharnessbliimpl;

import com.mfino.mock.integrationtestharness.commons.ITHLogger;
import com.mfino.mock.testharnessbli.CallMfinoServerBLI;
import com.mfino.mock.testharnessbli.TestHarnessBLI;
import org.slf4j.Logger;


/**
 *
 * @author sunil
 */
public class GemaltoTestHarnessBLIIMPL implements TestHarnessBLI {

    public boolean subsricption_activation(TestHarnessValueObject input){

        Logger logger=ITHLogger.getLogger();
        logger.info("Subcription Activation Called");
        String outgoingURL=new String();
        outgoingURL= "/WinFacadeWeb/SmsServicesServlet?SMS_serviceName=subscriber_activation";
                
        if(!(input.getSMS_pin().equals("")))
        {
            outgoingURL+="&SMS_pin="+input.getSMS_pin();
        }
        if(!(input.getSMS_sourceMsisdn().equals("")))
        {

            outgoingURL+="&SMS_sourceMsisdn="+input.getSMS_sourceMsisdn();

        }
        if(!(input.getSMS_secretAnswer().equals("")))
        {
            outgoingURL+="&SMS_secretAnswer="+input.getSMS_secretAnswer();
        }
        if(!(input.getSMS_contactNumber().equals("")))
        {
            outgoingURL+="&SMS_contactNumber="+input.getSMS_contactNumber();
        }
        input.setOutgoingUrl(outgoingURL);
        callMfinoServer(input);
        return true;

    }


    public boolean change_pin ( TestHarnessValueObject input){
        Logger logger=ITHLogger.getLogger();
        logger.info("change_pin Called");
        String outgoingURL=new String();
        outgoingURL="/WinFacadeWeb/SmsServicesServlet?SMS_serviceName=change_pin";
        if(!(input.getSMS_newPin().equals("")))
        {
            outgoingURL+="&SMS_newPin="+input.getSMS_newPin();
        }
        if(!(input.getSMS_oldPin().equals("")))
        {

            outgoingURL+="&SMS_oldPin="+input.getSMS_oldPin();

        }
        if(!(input.getSMS_sourceMsisdn().equals("")))
        {
            outgoingURL+="&SMS_sourceMsisdn="+input.getSMS_sourceMsisdn();
        }
        input.setOutgoingUrl(outgoingURL);
        callMfinoServer(input);

        return true;

    }
    public boolean merchant_change_pin ( TestHarnessValueObject input){
        Logger logger=ITHLogger.getLogger();
        logger.info("change_pin Called");
        String outgoingURL=new String();
        outgoingURL="/WinFacadeWeb/SmsServicesServletMA?SMS_serviceName=change_pin";
        if(!(input.getSMS_newPin().equals("")))
        {
            outgoingURL+="&SMS_newPin="+input.getSMS_newPin();
        }
        if(!(input.getSMS_oldPin().equals("")))
        {

            outgoingURL+="&SMS_oldPin="+input.getSMS_oldPin();

        }
        if(!(input.getSMS_sourceMsisdn().equals("")))
        {
            outgoingURL+="&SMS_sourceMsisdn="+input.getSMS_sourceMsisdn();
        }
        input.setOutgoingUrl(outgoingURL);
        callMfinoServer(input);

        return true;

    }

    public boolean reset_pin ( TestHarnessValueObject input){            
        Logger logger=ITHLogger.getLogger();
        logger.info("reset_pin Called");
        String outgoingURL=new String();
        outgoingURL= "/WinFacadeWeb/SmsServicesServlet?SMS_serviceName=reset_pin";
        if(!(input.getSMS_newPin().equals("")))
        {
            outgoingURL+="&SMS_newPin="+input.getSMS_newPin();
        }
        if(!(input.getSMS_sourceMsisdn().equals("")))
        {

            outgoingURL+="&SMS_sourceMsisdn="+input.getSMS_sourceMsisdn();

        }
        if(!(input.getSMS_secretAnswer().equals("")))
        {
            outgoingURL+="&SMS_secretAnswer="+input.getSMS_secretAnswer();
        }
        input.setOutgoingUrl(outgoingURL);
        callMfinoServer(input);

        return true;

    }

    public boolean get_transactions ( TestHarnessValueObject input){
            //String SMS_Pin, String SMS_serviceName, String SMS_sourceMsisdn){
        Logger logger=ITHLogger.getLogger();
        logger.info("get_transactions Called");
        String outgoingURL=new String();
        outgoingURL= "/WinFacadeWeb/SmsServicesServlet?SMS_serviceName=get_transactions";
        if(!(input.getSMS_pin().equals("")))
        {
            outgoingURL+="&SMS_pin="+input.getSMS_pin();
        }
        if(!(input.getSMS_sourceMsisdn().equals("")))
        {

            outgoingURL+="&SMS_sourceMsisdn="+input.getSMS_sourceMsisdn();

        }
        input.setOutgoingUrl(outgoingURL);
        callMfinoServer(input);
        return true;

    }

    public boolean merchant_get_transactions ( TestHarnessValueObject input){
            //String SMS_Pin, String SMS_serviceName, String SMS_sourceMsisdn){
        Logger logger=ITHLogger.getLogger();
        logger.info("get_transactions Called");
        String outgoingURL=new String();
        outgoingURL= "/WinFacadeWeb/SmsServicesServletMA?SMS_serviceName=get_transactions";
        if(!(input.getSMS_pin().equals("")))
        {
            outgoingURL+="&SMS_pin="+input.getSMS_pin();
        }
        if(!(input.getSMS_sourceMsisdn().equals("")))
        {

            outgoingURL+="&SMS_sourceMsisdn="+input.getSMS_sourceMsisdn();

        }
        input.setOutgoingUrl(outgoingURL);
        callMfinoServer(input);
        return true;

    }

    public boolean mobile_Agent_Recharge ( TestHarnessValueObject input){
            // String SMS_serviceName, String SMS_destMsisdn,String SMS_bucketType, String SMS_rechargeAmount, int SMS_pin, String SMS_sourceMsisdn){
        Logger logger=ITHLogger.getLogger();
        logger.info("mobile_Agent_Recharge Called");
        String outgoingURL=new String();
        outgoingURL= "/WinFacadeWeb/SmsServicesServletMA?SMS_serviceName=mobileAgentRecharge";
        if(!(input.getSMS_pin().equals("")))
        {
            outgoingURL+="&SMS_pin="+input.getSMS_pin();
        }
        if(!(input.getSMS_rechargeAmount().equals("")))
        {

            outgoingURL+="&SMS_rechargeAmount="+input.getSMS_rechargeAmount();

        }
        if(!(input.getSMS_bucketType().equals("")))
        {
            if (input.getSMS_bucketType().equalsIgnoreCase("reg") ||
                    input.getSMS_bucketType().equalsIgnoreCase("Cal") ||
                    input.getSMS_bucketType().equalsIgnoreCase("Dat") ||
                    input.getSMS_bucketType().equalsIgnoreCase("SPL")) {
                outgoingURL += "&SMS_bucketType=" + input.getSMS_bucketType();
            } else {
                input.setHtmlResponse("Invalid Bucket Type :: Bucket Type has to be either \"reg\" or \"cal\" or \"dat\" or \"spl\"");
                return false;
            }
        }
        if(!(input.getSMS_sourceMsisdn().equals("")))
        {
            outgoingURL+="&SMS_sourceMsisdn="+input.getSMS_sourceMsisdn();
        }
        if(!(input.getSMS_destMsisdn().equals("")))
        {
            outgoingURL+="&SMS_destMsisdn="+input.getSMS_destMsisdn();
        }
        input.setOutgoingUrl(outgoingURL);
        callMfinoServer(input);

        return true;
    }

    public boolean mcash_topup ( TestHarnessValueObject input){
            //( String SMS_serviceName, String SMS_destMsisdn,String SMS_distributeAmount, String SMS_pin,String SMS_sourceMsisdn){
        String outgoingURL=new String();
        outgoingURL="/WinFacadeWeb/SmsServicesServletMA?SMS_serviceName=mcash_topup";
        if(!(input.getSMS_sourceMsisdn().equals("")))
        {
            outgoingURL+="&SMS_sourceMsisdn="+input.getSMS_sourceMsisdn();
        }
        if(!(input.getSMS_destMsisdn().equals("")))
        {

            outgoingURL+="&SMS_destMsisdn="+input.getSMS_destMsisdn();

        }
        if(!(input.getSMS_bucketType().equals("")))
        {
            if (input.getSMS_bucketType().equalsIgnoreCase("reg") ||
                    input.getSMS_bucketType().equalsIgnoreCase("Cal") ||
                    input.getSMS_bucketType().equalsIgnoreCase("Dat") ||
                    input.getSMS_bucketType().equalsIgnoreCase("SPL")) {
                outgoingURL += "&SMS_bucketType=" + input.getSMS_bucketType();
            } else {
                input.setHtmlResponse("Invalid Bucket Type :: Bucket Type has to be either \"reg\" or \"cal\" or \"dat\" or \"spl\"");
                return false;
            }
        }
        if(!(input.getSMS_Amount().equals("")))
        {
            outgoingURL+="&SMS_amount="+input.getSMS_Amount();
        }
        Logger logger=ITHLogger.getLogger();
        logger.info("mcash_topup Called");
        input.setOutgoingUrl(outgoingURL);
        callMfinoServer(input);

        return true;

    }

    public boolean check_balance ( TestHarnessValueObject input){
            //( String SMS_serviceName, String SMS_pin,String SMS_sourceMsisdn)
        Logger logger=ITHLogger.getLogger();
        logger.info("check_balance Called");
        String outgoingURL=new String();
        outgoingURL="/WinFacadeWeb/SmsServicesServlet?&SMS_serviceName=check_balance";
        if(!(input.getSMS_pin().equals("")))
        {
            outgoingURL+="&SMS_pin="+input.getSMS_pin();
        }
        if(!(input.getSMS_sourceMsisdn().equals("")))
        {

            outgoingURL+="&SMS_sourceMsisdn="+input.getSMS_sourceMsisdn();

        }
        input.setOutgoingUrl(outgoingURL);
        callMfinoServer(input);
        return true;

    }
    public boolean merchant_check_balance ( TestHarnessValueObject input){
            //( String SMS_serviceName, String SMS_pin,String SMS_sourceMsisdn)
        Logger logger=ITHLogger.getLogger();
        logger.info("check_balance Called");
        String outgoingURL=new String();
        outgoingURL="/WinFacadeWeb/SmsServicesServletMA?&SMS_serviceName=check_balance";
        if(!(input.getSMS_pin().equals("")))
        {
            outgoingURL+="&SMS_pin="+input.getSMS_pin();
        }
        if(!(input.getSMS_sourceMsisdn().equals("")))
        {

            outgoingURL+="&SMS_sourceMsisdn="+input.getSMS_sourceMsisdn();

        }
        input.setOutgoingUrl(outgoingURL);
        callMfinoServer(input);
        return true;

    }

    public boolean change_mcash_Pin ( TestHarnessValueObject input){
            //( String SMS_serviceName, int SMS_oldPin, String SMS_newPin, String SMS_sourceMsisdn){
        Logger logger=ITHLogger.getLogger();
        logger.info("change_mcash_Pin Called");
        String outgoingURL=new String();
        outgoingURL="/WinFacadeWeb/SmsMCashServlet?SMS_serviceName=change_mCash_pin";

        if(!(input.getSMS_newPin().equals("")))
        {
            outgoingURL+="&SMS_newPin="+input.getSMS_newPin();
        }
        if(!(input.getSMS_oldPin().equals("")))
        {

            outgoingURL+="&SMS_oldPin="+input.getSMS_oldPin();

        }
        if(!(input.getSMS_sourceMsisdn().equals("")))
        {

            outgoingURL+="&SMS_sourceMsisdn="+input.getSMS_sourceMsisdn();

        }
        input.setOutgoingUrl(outgoingURL);
        callMfinoServer(input);
        return true;

    }

    public boolean merchant_mpin_reset ( TestHarnessValueObject input){
            //( int SMS_secretAnswer, String SMS_newPin, String SMS_serviceName, String SMS_sourceMsisdn){
        Logger logger=ITHLogger.getLogger();
        logger.info("merchant_mpin_reset Called");
        String outgoingURL=new String();
        outgoingURL="/WinFacadeWeb/SmsServicesServletMA?SMS_newPin=&SMS_serviceName=reset_pin";
        if(!(input.getSMS_newPin().equals("")))
        {
            outgoingURL+="&SMS_newPin="+input.getSMS_newPin();
        }
        if(!(input.getSMS_secretAnswer().equals("")))
        {

            outgoingURL+="&SMS_secretAnswer="+input.getSMS_secretAnswer();

        }
        if(!(input.getSMS_sourceMsisdn().equals("")))
        {

            outgoingURL+="&SMS_sourceMsisdn="+input.getSMS_sourceMsisdn();

        }
        input.setOutgoingUrl(outgoingURL);
        callMfinoServer(input);
        return true;

    }
    public boolean share_load (TestHarnessValueObject input){
        Logger logger=ITHLogger.getLogger();
        logger.info("merchant_mpin_reset Called");
        String outgoingURL=new String();
        outgoingURL="/WinFacadeWeb/SmsServicesServlet?SMS_serviceName=share_load";
        if(!(input.getSMS_pin().equals("")))
        {
            outgoingURL+="&SMS_pin="+input.getSMS_pin();
        }
        if(!(input.getSMS_rechargeAmount().equals("")))
        {

            outgoingURL+="&SMS_rechargeAmount="+input.getSMS_rechargeAmount();

        }
        if(!(input.getSMS_sourceMsisdn().equals("")))
        {

            outgoingURL+="&SMS_sourceMsisdn="+input.getSMS_sourceMsisdn();

        }
        if(!(input.getSMS_destMsisdn().equals("")))
        {

            outgoingURL+="&SMS_destMsisdn="+input.getSMS_destMsisdn();

        }
       input.setOutgoingUrl(outgoingURL);
        callMfinoServer(input);

        return true;
    }

    public boolean mcash_to_mcash (TestHarnessValueObject input){

        Logger logger=ITHLogger.getLogger();
        logger.info("merchant_mpin_reset Called");
        String outgoingURL=new String();
        outgoingURL="/WinFacadeWeb/SmsMCashServlet?SMS_serviceName=mcash_to_mcash";
        if(!(input.getSMS_mCashPin().equals("")))
        {
            outgoingURL+="&SMS_mCashPin="+input.getSMS_mCashPin();
        }
        if(!(input.getSMS_secretAnswer().equals("")))
        {
            outgoingURL+="&SMS_secretAnswer="+input.getSMS_secretAnswer();

        }
        if(!(input.getSMS_Amount().equals("")))
        {
            outgoingURL+="&SMS_amount="+input.getSMS_Amount();

        }
        if(!(input.getSMS_sourceMsisdn().equals("")))
        {

            outgoingURL+="&SMS_sourceMsisdn="+input.getSMS_sourceMsisdn();

        }
        if(!(input.getSMS_destMsisdn().equals("")))
        {

            outgoingURL+="&SMS_destMsisdn="+input.getSMS_destMsisdn();

        }
         if(!(input.getSMS_mCashMessage().equals("")))
        {

            outgoingURL+="&SMS_mCashMessage="+input.getSMS_mCashMessage();

        }
        input.setOutgoingUrl(outgoingURL);
        callMfinoServer(input);

        return true;
    }

    public boolean mcash_balance_inquiry (TestHarnessValueObject input){

        Logger logger=ITHLogger.getLogger();
        logger.info("merchant_mpin_reset Called");
        String outgoingURL=new String();
        outgoingURL="/WinFacadeWeb/SmsMCashServlet?&SMS_serviceName=mCash_balance_inquiry";
        if(!(input.getSMS_mCashPin().equals("")))
        {
            outgoingURL+="&SMS_mCashPin="+input.getSMS_mCashPin();
        }
        if(!(input.getSMS_sourceMsisdn().equals("")))
        {

            outgoingURL+="&SMS_sourceMsisdn="+input.getSMS_sourceMsisdn();

        }
        input.setOutgoingUrl(outgoingURL);
        callMfinoServer(input);
        return true;
    }
    public boolean get_mcash_transactions (TestHarnessValueObject input){
        Logger logger=ITHLogger.getLogger();
        logger.info("merchant_mpin_reset Called");
        String outgoingURL=new String();
        outgoingURL="/WinFacadeWeb/SmsMCashServlet?SMS_serviceName=get_mCash_transactions";
        if(!(input.getSMS_mCashPin().equals("")))
        {
            outgoingURL+="&SMS_mCashPin="+input.getSMS_mCashPin();
        }
        if(!(input.getSMS_sourceMsisdn().equals("")))
        {

            outgoingURL+="&SMS_sourceMsisdn="+input.getSMS_sourceMsisdn();

        }
        input.setOutgoingUrl(outgoingURL);
        callMfinoServer(input);
        return true;
    }

    public boolean mobile_agent_distribute (TestHarnessValueObject input){
        Logger logger=ITHLogger.getLogger();
        logger.info("merchant_mpin_reset Called");
        String outgoingURL=new String();
        outgoingURL="/WinFacadeWeb/SmsServicesServletMA?SMS_serviceName=mobileAgentDistribute";
        if(!(input.getSMS_pin().equals("")))
        {
            outgoingURL+="&SMS_pin="+input.getSMS_pin();
        }
        if(!(input.getSMS_sourceMsisdn().equals("")))
        {

            outgoingURL+="&SMS_sourceMsisdn="+input.getSMS_sourceMsisdn();

        }
        if(!(input.getSMS_destMsisdn().equals("")))
        {

            outgoingURL+="&SMS_destMsisdn="+input.getSMS_destMsisdn();

        }
         if(!(input.getSMS_distributeAmount().equals("")))
        {

            outgoingURL+="&SMS_distributeAmount="+input.getSMS_distributeAmount();

        }
        input.setOutgoingUrl(outgoingURL);
        callMfinoServer(input);
        return true;

    }
    public boolean frequency_test (int selectionList, int totalNoOfRequests){
        Logger logger=ITHLogger.getLogger();
        logger.info("Frequency_test Called");
        FreqTestController ftc=new FreqTestController(selectionList, totalNoOfRequests);
        ftc.freqTestCtrlInit();
       return true;

    }

    private void callMfinoServer(TestHarnessValueObject input) {
        Logger logger=ITHLogger.getLogger();
        logger.info("Calling mFinoServer");
        CallMfinoServerBLI mFinoServer_req=TestHarnessBLIFactoryIMPL.getMfinoServerRequest(TestHarnessBLIFactoryIMPL.MfinoServer.HttpServer);
 
        try{
            mFinoServer_req.invokeMfinoServer(input);
        }
        catch(Exception e)
        {
            e.printStackTrace();

        }
        
    }
    

}
