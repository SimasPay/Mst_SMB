package com.mfino.testbsim.sms.impl;

import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.ProducerTemplate;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.NotificationLogDetailsDAO;
import com.mfino.domain.NlogDetails;
import com.mfino.fix.CmFinoFIX;
import com.mfino.mce.notification.SMSNotification;
import com.mfino.mce.notification.SMSNotificationService;

public class TestBSIMSMSServiceImpl
  implements SMSNotificationService
{
  public static final String RESPONSE_CODE_SUCCESS = "00";
  private Log log;
  private String url;
  private String mobiles;
  private String message;
  private String authkey;
  private String sender;
  private String route;
  private String response;
  private SMSNotificationService bsimSMSService;

  public TestBSIMSMSServiceImpl()
  {
    this.log = LogFactory.getLog(TestBSIMSMSServiceImpl.class);
  }

  public String getAuthkey()
  {
    return this.authkey;
  }

  public void setAuthkey(String authkey) {
    this.authkey = authkey;
  }

  public String getSender() {
    return this.sender;
  }

  public void setSender(String sender) {
    this.sender = sender;
  }

  public String getRoute() {
    return this.route;
  }

  public void setRoute(String route) {
    this.route = route;
  }

  public String getResponse() {
    return this.response;
  }

  public void setResponse(String response) {
    this.response = response;
  }

  public SMSNotificationService getBsimSMSService()
  {
    return this.bsimSMSService;
  }

  public void setBsimSMSService(SMSNotificationService bsimSMSService) {
    this.bsimSMSService = bsimSMSService;
  }

  @Transactional(readOnly=false, propagation=Propagation.REQUIRED)
  public void process(Exchange httpExchange)
  {
    this.log.debug("BSIMSMSServiceImpl :: process() BEGIN");
    SMSNotification smsNotification = (SMSNotification)httpExchange.getIn().getBody(SMSNotification.class);
    this.message = smsNotification.getContent();

    if ((this.message.startsWith("Kode Simobi Anda")) || (this.message.startsWith("Your Simobi Code")) 
    		|| (this.message.startsWith("Kode OTP Simaspay anda :")) || (this.message.startsWith("Your Simaspay code is ")))
    {
      Long notificationLogDetailsID = smsNotification.getNotificationLogDetailsID();
      NotificationLogDetailsDAO notificationLogDetailsDao = DAOFactory.getInstance().getNotificationLogDetailsDao();
      NlogDetails notificationLogDetails = null;
      if (notificationLogDetailsID != null)
      {
        notificationLogDetails = (NlogDetails)notificationLogDetailsDao.getById(notificationLogDetailsID.longValue());
      }

      Object obj = null;

      String url = getUrl() + "?";
      CamelContext camelContext = httpExchange.getContext();
      String responseCode;
      try
      {
        String queryString = getQueryString();
        this.log.info("BSIMSMSServiceImpl :: process() url=" + url + ", Exchange.HTTP_QUERY=" + queryString);
        httpExchange.getIn().setHeader("CamelHttpQuery", queryString);
        ProducerTemplate template = camelContext.createProducerTemplate();
        template.start();
        obj = template.sendBodyAndHeader(url, ExchangePattern.InOut, "", "CamelHttpQuery", queryString);
        template.stop();

        this.log.info("BSIMSMSServiceImpl DEBUG obj=" + obj + ", obj.getclass=" + obj.getClass());
        StringWriter writer = new StringWriter();
        InputStream inputStream = (InputStream)obj;
        IOUtils.copy(inputStream, writer, "UTF-8");
        String content = writer.toString();
        this.log.info("Response from web call = " + content);
        responseCode = getResponseCode(content);
      }
      catch (Exception e) {
        this.log.error("BSIMSMSServiceImpl catch block, Error communicating with BSIM SMS Service. Error message:" + e.getMessage());
        responseCode = "9999";
      }

      if ((null != responseCode) && (responseCode.equals("00")))
      {
        if (notificationLogDetails != null)
        {
          notificationLogDetails.setStatus(CmFinoFIX.SendNotificationStatus_Success);
          notificationLogDetailsDao.save(notificationLogDetails);
          this.log.info("SMS with NotificationLogDetailsID " + notificationLogDetailsID + " was successfully sent");
        }

      }
      else if (notificationLogDetails != null)
      {
        notificationLogDetails.setStatus(CmFinoFIX.SendNotificationStatus_Failed);
        notificationLogDetailsDao.save(notificationLogDetails);
        this.log.info("Failed to send sms with notificationLogDetailsID " + notificationLogDetailsID);
      }

      this.log.debug("BSIMSMSServiceImpl :: process() END");
    }
    else
    {
      try {
        this.bsimSMSService.process(httpExchange);
      }
      catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  private String getResponseCode(String response) {
    if (response.startsWith("{"))
    {
      return "00";
    }

    return "01";
  }

  private String getQueryString()
    throws UnsupportedEncodingException
  {
    String queryString = "authkey={1}&mobiles={2}&message={3}&sender={4}&route={5}&response={6}";

    String message = URLEncoder.encode(getMessage(), "UTF-8");
    queryString = queryString.replace("{1}", getAuthkey());
    queryString = queryString.replace("{2}", getMobiles());
    queryString = queryString.replace("{3}", message);
    queryString = queryString.replace("{4}", getSender());
    queryString = queryString.replace("{5}", getRoute());
    queryString = queryString.replace("{6}", getResponse());
    return queryString;
  }

  public String getMobiles()
  {
    return this.mobiles;
  }

  public void setMobiles(String mobiles) {
    this.mobiles = mobiles;
  }

  public String getMessage() {
    return this.message;
  }

  public String getUrl() {
    return this.url;
  }

  public void setUrl(String url) {
    this.url = url;
  }
}