/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.fix;

import java.math.BigDecimal;

import com.mfino.constants.GeneralConstants;
import com.mfino.fix.serialization.FixMessageSerializer;
import com.mfino.hibernate.Timestamp;
import org.junit.Ignore;

/**
 *
 * @author Raju
 */
@Ignore //this relys on a running multix
public class LOPTest {

    public static void main(String[] args) {
        String URL = GeneralConstants.HTTP_PROTOCOL_PREFIX + CmFinoFIX.FixServerDParam_FixServerDHost + GeneralConstants.COLON_STRING + CmFinoFIX.FixServerDParam_FixServerDPort + GeneralConstants.SLASH_STRING;
        FixMessageSerializer fms = new FixMessageSerializer(URL);

        CFIXMsg.SetFIXMsgCreator(new CmFinoFIX.CMessageCreator());
        CmFinoFIX.CMH2HGenerateLOP genLOP = new CmFinoFIX.CMH2HGenerateLOP();
        genLOP.m_pHeader.setMsgSeqNum(0);
        genLOP.m_pHeader.setSendingTime(new Timestamp());

        genLOP.setLOPActualAmountPaid(new BigDecimal(Long.MAX_VALUE));
        genLOP.setLOPGiroRefID("1234");
        genLOP.setLOPTransferDate("20080715");
        genLOP.setPin("1234");
        genLOP.setSourceApplication(CmFinoFIX.SourceApplication_Web);
        genLOP.setMSPID(Long.MAX_VALUE);
        genLOP.setSourceMDN("dsdas");

	

         //genLOP.setCode(0);

        fms.send(genLOP);

        // Note:
        // There is no need to send Async request to MultiX
        // All the requests are sync and will get response immediately from MultiX.
        // fms.sendAsync(subNotification, new FixMessageSerializationHandler());

    }
}
