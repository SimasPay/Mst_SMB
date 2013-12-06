/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.fix;

import com.mfino.exceptions.InvalidDataException;

import junit.framework.TestCase;

/**
 *
 * @author xchen
 */
public class CFIXMsgTest extends TestCase {

    public void testMutilpleEntries() {
        CFIXMsg.SetFIXMsgCreator(new CmFinoFIX.CMessageCreator());

        CmFinoFIX.CMJSDistributionChainLevel origMsg = new CmFinoFIX.CMJSDistributionChainLevel();
        origMsg.allocateEntries(2);
        for (int i = 0; i < 2; i++) {
            CmFinoFIX.CMJSDistributionChainLevel.CGEntries e = new CmFinoFIX.CMJSDistributionChainLevel.CGEntries();
            origMsg.getEntries()[i] = e;
            e.setDistributionChainTemplateID(31l);
            e.setDistributionLevel(i + 1);

            //NOTE: comment out this line the test will fail.
            //currently we are relying on the ID to
            e.setID((long)i);
        }

        assertEquals(origMsg.getEntries().length, 2);
        assertNotNull(origMsg.getEntries()[0]);
        assertNotNull(origMsg.getEntries()[1]);

        CMultiXBuffer buffer = new CMultiXBuffer();
        try {
			origMsg.toFIX(buffer);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        System.out.println(new String(buffer.DataPtr()));
        CFIXMsg msg = CFIXMsg.fromFIX(buffer);

        CmFinoFIX.CMJSDistributionChainLevel realMsg = (CmFinoFIX.CMJSDistributionChainLevel) msg;

        assertEquals(realMsg.getEntries().length, 2);
        assertNotNull(realMsg.getEntries()[0]);
        assertNotNull(realMsg.getEntries()[1]);
    }
}
