package com.mfino.service;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashSet;

import junit.framework.TestCase;

import com.mfino.domain.Pocket;
import com.mfino.domain.PocketTemplate;
import com.mfino.domain.SubscriberMdn;
import com.mfino.fix.CmFinoFIX;

/**
 *
 * @author xchen
 */
public class SubscriberServiceTest extends TestCase {

    @Override
    public void setUp() {
    }

    @Override
    public void tearDown() {
    }

    private SubscriberMdn getMockSubscriber() {
        SubscriberMdn sub = mock(SubscriberMdn.class);
        Pocket p = mock(Pocket.class);
        PocketTemplate template = mock(PocketTemplate.class);
        when(p.getPocketTemplateByPockettemplateid()).thenReturn(template);
        when(p.getStatus()).thenReturn(CmFinoFIX.PocketStatus_Active);
        when(template.getType()).thenReturn(CmFinoFIX.PocketType_BOBAccount);
        HashSet<Pocket> pockets = new HashSet<Pocket>();
        pockets.add(p);
        when(sub.getPockets()).thenReturn(pockets);

        return sub;
    }

    public void testHasBobAccount() {
        SubscriberMdn sub = getMockSubscriber();
//        assertTrue(SubscriberService.hasBOBPocket(sub));
    }
    
    public static void main(String[] args){
    	/*String PIN = SubscriberServiceExtended.calculateDigestPin("23410000000000", "mFino260");
    	System.out.println(PIN);*/
    	
    }
}
