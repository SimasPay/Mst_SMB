/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.fix;

import com.mfino.constants.GeneralConstants;
import com.mfino.fix.serialization.FixMessageSerializer;
import com.mfino.hibernate.Timestamp;
import org.junit.Ignore;

/**
 *
 * @author sandeepjs
 */
@Ignore
public class SubscriberNotificationTest {


    //public void sendTestEmail()
    public static void main(String[] args)
    {
        String URL = GeneralConstants.HTTP_PROTOCOL_PREFIX+CmFinoFIX.FixServerDParam_FixServerDHost +GeneralConstants.COLON_STRING+ CmFinoFIX.FixServerDParam_FixServerDPort +GeneralConstants.SLASH_STRING;
        FixMessageSerializer fms = new FixMessageSerializer(URL);

        CFIXMsg.SetFIXMsgCreator(new CmFinoFIX.CMessageCreator());
        CmFinoFIX.CMSubscriberNotification subNotification = new CmFinoFIX.CMSubscriberNotification();

        subNotification.setSenderEmail("backend@mfino.com");
        subNotification.setReceiverEmail("sandeep@mfino.com");
        subNotification.setText("Hello There !!!");
        subNotification.setSourceApplication(CmFinoFIX.SourceApplication_Web);
        subNotification.m_pHeader.setMsgSeqNum(0);
        subNotification.m_pHeader.setSendingTime(new Timestamp());
        subNotification.setMethod(CmFinoFIX.NotificationMethod_Email);
        subNotification.setCode(0);

        fms.send(subNotification);

     // Note: 
     // There is no need to send Async request to MultiX
     // All the requests are sync and will get response immediately from MultiX.
     // fms.sendAsync(subNotification, new FixMessageSerializationHandler());
       
    }
}
