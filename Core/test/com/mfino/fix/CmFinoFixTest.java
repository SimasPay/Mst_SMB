/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.fix;

import junit.framework.TestCase;
import org.junit.Test;

import com.mfino.exceptions.InvalidDataException;

/**
 *
 * @author xchen
 */
public class CmFinoFixTest extends TestCase {

    @Test
    public void testEncodeAndDecode() throws InvalidDataException {
        CFIXMsg.SetFIXMsgCreator(new CmFinoFIX.CMessageCreator());
        CmFinoFIX.CMChangePin Msg = new CmFinoFIX.CMChangePin();
        Msg.setNewPin("123456");
        Msg.setOldPin("456789");
        Msg.setSourceMDN("rtete");
        CMultiXBuffer Buf = new CMultiXBuffer();
        Msg.toFIX(Buf);
        System.out.println(new String(Buf.DataPtr(), 0, Buf.Length()));
        CFIXMsg pMsg = CFIXMsg.fromFIX(Buf);
        CMultiXBuffer Buf1 = new CMultiXBuffer();
        pMsg.toFIX(Buf1);
        System.out.println("Actual class deserialized is :" + pMsg.getClass().getName());
        System.out.println(new String(Buf1.DataPtr(), 0, Buf1.Length()));
    }
}