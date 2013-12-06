/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.service;

import java.util.HashMap;

import junit.framework.TestCase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.mfino.fix.CmFinoFIX;

/**
 *
 * @author xchen
 */
public class EnumTextServiceTest extends TestCase {
	@Autowired
	@Qualifier("EnumTextServiceImpl")
	private EnumTextService enumTextService;
    @Override
    public void setUp() {
    }

    @Override
    public void tearDown() {
    }


    public void testGetOneEnum(){
    
        HashMap map = enumTextService.getEnumTextSet(CmFinoFIX.TagID_Language, CmFinoFIX.Language_English);

        assertNotNull(map);
        assertTrue(map.size() > 0);
    }

    public void testGetSecEnum(){

        HashMap map = enumTextService.getEnumTextSet(CmFinoFIX.TagID_SubscriberType, CmFinoFIX.Language_English);
        assertNotNull(map);
        assertTrue(map.size() > 0);
    }


    public void testGetEnumTextValue()
    {
        String enumTextValue = enumTextService.getEnumTextValue(CmFinoFIX.TagID_SubscriberType, CmFinoFIX.Language_English, "0");
        assertEquals(enumTextValue, "Subscriber");
    }

    public void testGetEnumTextValueHex()
    {
        String enumTextValue = enumTextService.getEnumTextValueHex(CmFinoFIX.TagID_DistributionPermissions, CmFinoFIX.Language_English, CmFinoFIX.DistributionPermissions_DistributeAll);
        assertEquals(enumTextValue, "DistributeAll");
    }

    public void testGetRestrcitions()
    {
        String restriction = enumTextService.getRestrictionsText(CmFinoFIX.TagID_SubscriberRestrictions, null, "15");

        System.out.println(restriction);

        assertTrue(restriction.contains("Suspended"));
        assertTrue(restriction.contains("SelfSuspended"));
        assertTrue(restriction.contains("SecurityLocked"));
        assertTrue(restriction.contains("AbsoluteLocked"));
        

    }


    public void testMsgType()
    {
        String msgType = enumTextService.getEnumTextValue(CmFinoFIX.TagID_MsgType, null, "1000");
        System.out.println(msgType);

    }



}
